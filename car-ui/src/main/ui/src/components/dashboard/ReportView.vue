<template>
  <div style="background: rgb(46, 159, 37, 0.25); overflow: auto">
    <div v-if="dashboards.length > 0">
      <div
        class="form form-col"
        style="
          border-bottom: 1px solid #eaeaea;
          background: white;
          padding-left: 20px;
        "
      >
        <div
          v-if="pickedValue && pickedValue.value && pickedValue.value.attr"
          class="form-row"
          style="align-items: baseline"
        >
          <div style="flex-grow: 0">
            <i>{{ pickedValue.value.attr.logTypeGroup }} / </i>
            <i style="padding-right: 5px"
              >{{ pickedValue.value.attr.logType }} /
            </i>
          </div>
          <input
            v-if="editmode"
            v-model="dashboard.name"
            style="
              flex-grow: 1;
              margin: 0px 20px 0px 0px;
              border: none;
              border-bottom: solid 2px purple;
              border-radius: 0px;
              font-size: 1.2em;
            "
          />
          <div
            style="
              flex-grow: 1;
              margin: 0px 20px 0px 0px;
              border-bottom: solid 2px transparent;
            "
          >
            <select
              v-if="!editmode"
              v-model="dashboard"
              @change="dashboardSelected"
              style="border: none; font-size: 1.2em"
            >
              <option v-for="dash in dashboards" :value="dash" :key="dash.name">
                {{ dash.name }}
              </option>
              <option>New dashboard</option>
            </select>
          </div>
          <div style="flex-grow: 0; font-size: 0.9em; margin: 0px 10px">
            <a v-if="!editmode" href="#" @click="editmode = true">Edit</a>
            <div v-if="editmode">
              <a href="#" @click="save()">Save</a>
              <a href="#" @click="cancelEdit">Cancel</a>
            </div>
          </div>
        </div>
        <div v-if="editmode" class="form form-row">
          <label>Pick a visualization:</label>
          <select v-model="visSelected">
            <option
              v-for="ctyp in charttypes"
              :value="ctyp.name"
              :key="ctyp.name"
            >
              {{ ctyp.displayName }}
            </option>
          </select>
          <button style="margin-left: 5px" @click="addVisualization">
            Add
          </button>
        </div>
      </div>

      <splitpanes>
        <pane>
          <grid-layout
            v-model="layout"
            :col-num="50"
            :row-height="20"
            :col-width="100"
            :is-draggable="editmode"
            :is-resizable="true"
            :is-mirrored="false"
            :vertical-compact="true"
            :use-css-transforms="true"
            :margin="[30, 30]"
          >
            <grid-item
              v-for="item in layout"
              :x="item.x"
              :y="item.y"
              :w="item.w"
              :h="item.h"
              :i="item.i"
              :key="item.i"
              :margin="20"
              @resized="containerResized"
            >
              <div
                :style="{
                  border: editmode
                    ? 'dashed ' +
                      (selecteditem == item ? '1px red' : '0px lightgray')
                    : 'solid 0px #f0f0f0',
                  background: 'white',
                  width: '100%',
                  height: '100%',
                  'border-radius': '5px',
                  position: 'relative',
                }"
                @click="selecteditem = item"
              >
                <chart-view
                  v-if="item.view.data"
                  :visualization="item"
                ></chart-view>
                <i
                  v-if="editmode"
                  @click="removeVisualization(item)"
                  style="
                    position: absolute;
                    top: 2px;
                    right: 2px;
                    font-size: 0.7em;
                    color: gray;
                  "
                  class="fa fa-trash"
                ></i>
              </div>
            </grid-item>
          </grid-layout>
        </pane>
        <pane>
          <collapsible-pane
            style="height: 100%; background: #eaeef280; padding: 0px 10px"
            type="right"
          >
            <visualization-editor
              style="min-width: 400px"
              :visualization="selecteditem"
            ></visualization-editor>
          </collapsible-pane>
        </pane>
      </splitpanes>
    </div>
    <div
      v-else
      style="
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
      "
    >
      No dashboards found. Create a&nbsp;
      <button @click="newDashboard">new</button> &nbsp;one?
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
import {
  visConstructors,
  Visualization,
} from "@/models/visualizations/Visualization.js";
import { Dashboard } from "@/models/visualizations/Dashboard.js";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

export default {
  name: "ReportView",
  components: { Splitpanes, Pane },
  data() {
    return {
      cases: [],
      currentcase: undefined,
      q: "",
      charttyp: "timeline",
      tab: "dashboard",
      preProcessor: "x=>x",
      pickedValue: { value: {} },
      dashboards: [],
      dashboard: undefined,
      editmode: false,

      /* adhoc stuff */
      adhocviz: null,

      visSelected: "",
      selecteditem: undefined,
      charttypes: car.getChartTypes(),
    };
  },
  computed: {
    layout() {
      return this.dashboard.visualizations || [];
    },
    files() {
      function fsnode(n, dir) {
        (this.name = n),
          (this.title = n),
          (this.children = {}),
          (this.icon = dir ? "directory.png" : "log-format.png"),
          (this.newChildNode = dir);
        this.addChild = function (name, node) {
          if (this.children[name]) return this.children[name];
          return (this.children[name] = node);
        };
        this.getChildren = function () {
          return Promise.resolve(Object.values(this.children));
        };
      }
      console.log("called");
      let fsm = this.currentcase.detailStatus.fileStatusMap;
      let fs = new fsnode("", true);
      Object.keys(fsm).forEach((p) => {
        let node = fs,
          path = p.substring(p.indexOf(this.currentcase.name));
        path.split(/\\|\//).forEach((pc) => {
          node = node.addChild(pc, new fsnode(pc, !p.endsWith(pc)));
        });
        node.attr = fsm[p];
        node.attr.path = path;
      });
      console.log(fs);
      return Object.values(fs.children);
    },
  },
  created() {
    car.getDatasets().then((cases) => {
      cases.map((c) => {
        this.cases.push(c);
      });
      this.currentcase = cases[0];
    });
  },
  watch: {
    pickedValue: {
      deep: true,
      handler: function (pickedValue) {
        this.tab = "dashboard";
        let attr = pickedValue.value.attr;
        car
          .getDashboards((attr || {}).logTypeGroup, (attr || {}).logType)
          .then((dbs) =>
            dbs.map((db) => {
              return new Dashboard({
                ...db,
                ctxt: {
                  file: this.pickedValue.value.name,
                  dataset: this.currentcase.name,
                },
              });
            })
          )
          .then((x) => {
            this.dashboards = x;
            if (x.length > 0) {
              this.dashboard = x[0];
            }
          });
      },
    },
  },
  methods: {
    adHocOpened() {
      this.adhocviz = new Visualization({
        type: "table",
        ctxt: {
          file: this.pickedValue.value.name,
          dataset: this.currentcase.name,
        },
      });
      this.tab = "adhoc";
    },
    dashboardSelected(e) {
      console.log(e.target.value);
      if (e.target.value == "New dashboard") {
        this.dashboard = new Dashboard();
        this.dashboards.push(this.dashboard);
      }
    },
    setEditMode() {
      this.editmode = true;
      this.dashboard.editmode = true;
      this.dashboardCopy = JSON.stringify(this.dashboard);
    },
    cancelEdit() {
      this.editmode = false;
      this.dashboard.editmode = false;
      this.dashboard = JSON.parse(this.dashboardCopy);
    },
    analyze(c) {
      car.analyze(c.name);
      this.startStatusPoller(c);
    },
    cleanup(c) {
      car.cleanup(c.name).then(() => car.success(`cleaned up ${c.name}`));
      this.startStatusPoller(c);
    },
    startStatusPoller(c) {
      let timer = setInterval(() => {
        car.status(c.name).then((status) => {
          this.currentcase.detailStatus = status;
          if (!status.status.endsWith("ING")) clearInterval(timer);
        });
      }, 3000);
    },
    sql() {
      let preproc = eval(`
                        (function(row) {
                            return ${this.preProcessor}
                        })()
                    `);
      this.results.splice(0, this.results.length);
      car.sql(this.currentcase.name, this.q).then((results) => {
        this.results.splice(0, this.results.length);
        results
          .map((r) => preproc(r))
          .filter((r) => r != undefined)
          .forEach((r) => this.results.push(r));
      });
    },
    save() {
      this.dashboard.logType = this.pickedValue.value.attr.logType;
      this.dashboard.logTypeGroup = this.pickedValue.value.attr.logTypeGroup;
      car.saveDashBoard(this.dashboard);
      this.selecteditem = null;
      this.editmode = false;
    },
    newDashboard() {
      let x = new Dashboard({
        ctxt: {
          file: this.pickedValue.value.name,
          dataset: this.currentcase.name,
        },
      });
      this.dashboards.push((this.dashboard = x));
      this.editmode = true;
    },
    resizedEvent(e) {
      console.log(e);
    },
    addVisualization() {
      let lastRow = this.layout.reduce(
        (prev, current) => (prev.y > current.y ? prev : current),
        { y: 0, h: 1 }
      );
      let args = {
        type: this.visSelected,
        title: "New visualization",
        description: "",
        x: 0,
        y: lastRow.y + lastRow.h,
        w: 100,
        h: 5,
        i: this.layout.length,
        ctxt: this.dashboard.ctxt,
      };

      /* used by editors */
      this.selecteditem = visConstructors[this.visSelected]
        ? visConstructors[this.visSelected](args)
        : new Visualization(args);
      this.layout.push(this.selecteditem);
    },
    removeVisualization({ i }) {
      const index = this.layout.map((item) => item.i).indexOf(i);
      this.layout.splice(index, 1);
    },
    containerResized: function (i, newH, newW, newHPx, newWPx) {
      this.layout[i].vw = newWPx;
      this.layout[i].vh = newHPx;
    },
  },
};
</script>

<style></style>
