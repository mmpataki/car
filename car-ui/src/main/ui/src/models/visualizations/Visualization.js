import { storeFactory } from "@/models/stores/StoreFactory.js";
import { llog } from "@/car.js";
import Vue from "vue";
import Router from '../../router';
import dayjs from "dayjs";
import { car } from "@/car.js"
export let eventHandlers = {
  "setvariable": {
    label: "Set a variable",
    handle(datum, ehandler, visualization) {
      Vue.set(
        visualization.ctxt,
        ehandler.parameters.variablename,
        ehandler.parameters.dataKey != "Full record"
          ? datum[ehandler.parameters.dataKey]
          : datum
      );
    },
    init() {
      //nothing
    },
  },
  "navigateto": {
    label: "Navigate to",
    handle: function (datum, event, visualization) {
      console.log(datum, event, visualization);
      let url = car.evalTemplateStringWithCtxt(event.parameters.url, { ...visualization.ctxt, ...datum })
      console.log(url);
      Router.push(url)
    },
    init(ehandler, visualization) {
      console.log(ehandler, visualization);
    },
  },

  "custom": {
    label: "Custom code",
    handle: function (datum, event, visualization) {
      console.log(datum, event, visualization);
      if (event.parameters.func) {
        event.parameters.func(datum, event, visualization)
      } else if (event.parameters.funcCode) {
        eval(event.parameters.funcCode)(datum, event, visualization)
      }
    },
    init(ehandler, visualization) {
      console.log(ehandler, visualization);
    },
  },
};

export default class Visualization {
  constructor(v) {
    this.dataLoading = v.dataLoading == undefined ? true : v.dataLoading;
    this.isGenerated = false;
    this.dataIndependent = false;
    this.preProcessor = "(cur, idx, state)=>cur";
    Object.keys(v).forEach((k) => (this[k] = v[k]));
    if (!v.datastore) {
      this.datastore = storeFactory("sql", { dsetid: this.ctxt ? this.ctxt.dsetid : "" });
    } else {
      this.datastore = storeFactory(this.datastore.type, { ...v.datastore, dsetid: this.ctxt.dsetid });
    }

    this.dataVersion = -1;
    this.error = undefined;

    /* a field called `data1 will be defined once we fetch it. its delayed to avoid to skip the 
     * overhead by vue creating reactive getter and setters for every piece of data.
     */
    this.view = {};
    this.backgroundColor = v.backgroundColor;
    this.eventHandlers = v.eventHandlers || [];
    this.eventHandlers.forEach((eh) => {
      if (eventHandlers[eh.type].init) eventHandlers[eh.type].init(eh, this);
    });
    this.filters = {}
  }

  eventOccured(eventName, datum) {
    console.info('event!', eventName, datum)
    this.eventHandlers
      .filter((eh) => eh.on == eventName && eventHandlers[eh.type])
      .forEach((eh) => eventHandlers[eh.type].handle(datum, eh, this));
  }

  toJSON() {
    return {
      ...this,
      view: undefined,
    };
  }

  init() {
    /* do some init, set variables to default etc. */
    dayjs('2000-01-01')
  }

  userFilter(row) {
    for (let key in this.filters) {
      let filter = this.filters[key];
      if (!filter.accepts(row)) return false;
    }
    return true;
  }

  update(reset) {
    llog(`Updating visualization : ${this.title}`)
    car.setCurrentUpdatingVisualization(this)
    Vue.set(this, 'dataLoading', true)
    let that = this;
    that.error = undefined
    if (that.isGenerated) {
      Vue.set(this, 'dataLoading', false)
      return Promise.resolve()
    }
    return this.datastore.getData(this.ctxt, reset).then((results) => {
      try {
        that.unfilteredData = results
        that.defnUpdated()
        that.dataVersion++;
        console.log(that.title, that.view)
      } catch (e) {
        console.log(e);
        throw e;
      }
    }).catch(e => {
      console.error(e)
      that.error = e.message
    }).finally(() => {
      car.setCurrentUpdatingVisualization()
      Vue.set(this, 'dataLoading', false)
    });
  }

  defnUpdated() {
    // if (!this.unfilteredData || this.unfilteredData.length == 0)
    //   return;
    let output = [], ctxt = this.ctxt;
    window.dayjs = dayjs;
    window.car = car;

    let js = `
      (function(row) {
        ${Object.keys(ctxt)
          .map((k) => `let ${k} = ctxt.${k}`)
          .join("\n")}
          try {
            car.enterVAccessBlock()
            return ${this.preProcessor}
          } finally{
            car.exitVAccessBlock()
          }
      })()
    `
    let preproc = eval(js);

    if (this.unfilteredData) {
      let state = { length: this.unfilteredData.length }
      for (let i = 0; i < this.unfilteredData.length; i++) {
        let obj = this.unfilteredData[i];
        obj = preproc(obj, i, state)
        if (obj == undefined)
          continue;
        if (Array.isArray(obj)) {
          obj.filter(re => this.userFilter(re)).forEach(o => output.push(o))
        } else {
          if (!this.userFilter(obj))
            continue;
          output.push(obj)
        }
      }

      // stores can send extra metadata through props
      output.props = this.unfilteredData.props
    }

    this.setData(output)
  }

  setData(newData) {
    this.view.data = newData
  }

  applyUserFilter() {
    if (this.view.data)
      this.view.data.splice(0, this.view.data.length);
    else
      this.view.data = []
    this.unfilteredData.filter(r => this.userFilter(r))
      .forEach((r) => this.view.data.push(r));
  }
}
