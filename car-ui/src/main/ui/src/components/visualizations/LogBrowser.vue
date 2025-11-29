<template>
  <div style="height: 100%; width: 100%">
    <div style="height: 100%">
      <div style="display: flex; margin: 2px; align-items: start">
        <textarea
          rows="1"
          v-if="filters[0]"
          v-model="filters[0].val"
          class="searchbar"
          placeholder="search messages"
        ></textarea>
        <textarea
          rows="1"
          v-else
          class="searchbar"
          placeholder="search messages"
        ></textarea>
        <date-picker
          v-if="tsfilter"
          v-model="temptsfilter"
          type="datetime"
          range
          confirm
          clearable
          confirm-text="Done"
          :show-second="false"
          @change="tsFilterChanged"
        ></date-picker>
        <button @click="filterChanged" class="searchbutton">
          <v-icon dark small>mdi-refresh</v-icon> refresh
        </button>
      </div>

      <div style="display: flex; align-items: center">
        <div
          style="
            display: block;
            padding: 2px 0px;
            align-items: center;
            flex-grow: 1;
          "
        >
          <v-icon small style="float: left; margin: 3px 5px"
            >mdi-filter-variant</v-icon
          >

          <filter-input
            style="float: left; margin-bottom: 3px"
            v-for="(filter, idx) in filters.slice(1, filters.length)"
            v-show="idx"
            :key="idx"
            :filter="filter"
            :filtervalues="filterValues"
            :field="
              viz.customView.fields.filter((f) => f.name == filter.key)[0]
            "
            @filterchange="filterChanged"
            @deleted="removeFilter($event)"
            @editing="filterChanged"
          ></filter-input>

          <v-btn
            text
            x-small
            color="primary"
            style="float: left"
            @click="addEmptyFilter"
            ><strong>add filter</strong></v-btn
          >

          <v-spacer></v-spacer>

          <!-- <v-btn x-small text color="primary"><strong>save</strong></v-btn>
          <v-btn x-small text color="primary"><strong>clear</strong></v-btn> -->
        </div>

        <div
          style="
            display: flex;
            padding: 2px 0px;
            margin-left: 30px;
            align-items: center;
          "
        >
          <v-btn
            x-small
            color="primary"
            v-if="charts.length"
            @click="openShareDialog()"
            text
            ><v-icon small>mdi-share-variant</v-icon>

            <v-dialog v-model="showShare" max-width="450">
              <v-card>
                <v-card-text style="padding: 40px">
                  <strong style="margin-bottom: 10px; display: block">
                    Share URL
                  </strong>
                  <div style="display: flex; align-items: start">
                    <div
                      style="
                        padding: 5px 10px;
                        border: solid 1px gray;
                        width: 350px;
                        overflow: auto;
                        height: 32px;
                        display: inline-block;
                        white-space: nowrap;
                        color: black;
                      "
                    >
                      {{ url }}
                    </div>
                    <div
                      style="
                        background: seagreen;
                        color: white;
                        padding: 5px 10px;
                        cursor: pointer;
                        height: 32px;
                        border: solid 1px gray;
                      "
                      @click="copyFormatted(url)"
                    >
                      <span v-if="copyDone" style="display: flex"
                        ><v-icon small color="white" style="margin-right: 5px"
                          >mdi-checkbox-marked-circle</v-icon
                        >Copied</span
                      >
                      <span v-else>Copy</span>
                    </div>
                  </div>
                </v-card-text>
              </v-card>
            </v-dialog>
          </v-btn>
          <v-btn
            x-small
            color="primary"
            v-if="charts.length"
            @click="showChartSelector()"
            text
            ><v-icon small>mdi-chart-bar</v-icon></v-btn
          >
          <v-btn
            x-small
            color="primary"
            text
            v-if="viz && viz.customView"
            @click="showFieldsSelector = true"
            :title="viz.customView.name"
            ><v-icon small>mdi-table-edit</v-icon></v-btn
          >

          <v-dialog
            persistent
            v-model="openChartSelector"
            width="500"
            style="height: 500px"
          >
            <v-card>
              <v-card-title class="my-2"> Pick a visualization </v-card-title>
              <v-card-subtitle
                >These visualizations are proposed by plugins in the
                car.</v-card-subtitle
              >
              <v-card-text>
                <v-select
                  dense
                  :items="charts.map((c) => ({ name: c.name, chart: c }))"
                  v-model="currentChart"
                  item-text="name"
                  item-value="chart"
                  @change="openChartSelector = false"
                ></v-select>
              </v-card-text>
            </v-card>
          </v-dialog>

          <SearchViewBuilder
            v-if="showFieldsSelector"
            :queriedFields="allFields"
            :views="views"
            :searchView="viz.customView"
            @closed="searchViewBuilderClosed($event)"
          >
          </SearchViewBuilder>
        </div>
      </div>

      <div
        style="
          height: calc(100% - 70px);
          width: calc(100% - 5px);
          overflow: scroll;
          padding-top: 10px;
          margin-left: 5px;
          display: flex;
          flex-direction: column;
        "
      >
        <div
          v-if="currentChart"
          :style="{
            minHeight: currentChart.pheight,
            height: currentChart.pheight,
            width: currentChart.pwidth,
            position: 'relative',
          }"
        >
          <ChartView
            :visualization="currentChart"
            :dashboard="dashbrd"
            :hidecontrols="true"
            style="position: relative"
            :key="currentChart.key"
          ></ChartView>
        </div>
        <div style="flex-grow: 1; overflow-y: auto">
          <logmessages-visualization
            v-if="viz"
            :visualization="viz"
            :dsetid="dsetid"
            :view="viz.view"
            :dataWatch="dataVersion"
            :metaWatch="metaVersion"
            @log-message-selected="$emit('log-message-selected', $event)"
            @text-selected="textSelected($event)"
            @right-clicked="rightClicked($event)"
          ></logmessages-visualization>
        </div>
      </div>
      <menu-loader
        v-if="showMenu"
        viewname="logview"
        event="textselect"
        :x="selDetails.menuX - 60"
        :y="selDetails.menuY"
        @menuclosed="closeMenu()"
        :arg="{ ...selDetails, component: component }"
      ></menu-loader>
      <menu-loader
        v-if="showContextMenu"
        viewname="logview"
        event="rightclick"
        :x="contextMenuDetails.menuX - 60"
        :y="contextMenuDetails.menuY"
        @menuclosed="showContextMenu = false"
        :arg="{ ...contextMenuDetails, component: component }"
      ></menu-loader>
    </div>
  </div>
</template>

<script>
import { car, Field } from "@/car.js";
import DatePicker from "vue2-datepicker";
import "vue2-datepicker/index.css";
import FilterInput from "./logmsgvis/FilterInput.vue";
import LogmessagesVisualization from "./LogmessagesVisualization.vue";
import createViz from "@/models/visualizations/VisFactory.js";
import SearchViewBuilder from "./logmsgvis/SearchViewBuilder.vue";
import MenuLoader from "../utils/MenuLoader.vue";
import ChartView from "../dashboard/ChartView.vue";
import { Dashboard } from "../../models/visualizations/Dashboard";

export default {
  name: "LogBrowser",
  props: [
    "dsetid",
    "blackListedFields",
    "getAllFields",
    "sortFields",
    "query",
    "searchView",
  ],

  components: {
    DatePicker,
    FilterInput,
    LogmessagesVisualization,
    SearchViewBuilder,
    MenuLoader,
    ChartView,
  },

  watch: {
    "$route.query"() {
      this.parseFilters();
      this.newsearch();
    },
  },

  data() {
    return {
      viz: undefined,

      timerange: null,
      filters: [],
      filterValues: {},

      selected: null,

      allFields: [],
      showFieldsSelector: false,

      dataVersion: 0,
      metaVersion: 0,

      showMenu: false,
      selDetails: undefined,

      component: this,
      views: [],

      openChartSelector: false,
      charts: [],
      currentChart: undefined,
      dashbrd: new Dashboard({}),

      tsField: undefined,
      tsfilter: undefined,
      temptsfilter: [],

      url: "",
      copyDone: false,
      showShare: false,

      showContextMenu: false,
      contextMenuDetails: undefined,

      viewName: undefined,
    };
  },

  created() {
    let { dsetid, filename } = this,
      that = this;

    let fields = [
      new Field("_line", true),
      new Field("_msg", true, true),
      new Field("_file", false),
    ];

    let viz = createViz("logmessages", {
      ctxt: {
        _file: filename,
        dsetid: dsetid,
      },
      datastore: {
        type: "logs",
        query: JSON.stringify(this.buildQuery()),
        mode: "logs",
        pageSize: 200,
        dsetid: dsetid,
        sortFields: this.sortFields,
        queriedFields: this.getAllFields
          ? []
          : fields
              .map((f) => f.name)
              .filter((x) => !(this.blackListedFields || []).includes(x)),
      },
      customView: { name: "custom", fields },
      viewName: this.searchView || "custom",
    });

    let inferTypes = (plugins, schema) => {
      plugins.forEach((plugin) => {
        if (!plugin.handlers.logview) return;
        let computers = plugin.handlers.logview.infertypes;
        if (!computers || !computers.length) return;
        computers.forEach((computer) => {
          computer(schema);
        });
      });
    };

    let setUpTimestampFilter = (plugins, schema) => {
      let tsf = schema.filter((f) => f.itype == "ts")[0];
      if (tsf) {
        this.tsField = tsf.name;
        this.tsfilter = this.filters[1];
      }
    };

    let setUpSort = (plugins, schema) => {
      let sortfs = schema
        .filter((f) => f.isort)
        .map((f) => ({ first: f.name, second: f.isort }));
      if (sortfs.length) {
        viz.datastore.sortFields = sortfs;
      }
    };

    let setUpViews = (plugins, schema) => {
      plugins.forEach((plugin) => {
        if (!plugin.handlers.logview) return;
        let computers = plugin.handlers.logview.computeview;
        if (!computers || !computers.length) return;
        computers.forEach((computer) => {
          let view = computer(schema);
          if (view) {
            viz.customView = view;
            viz.datastore.queriedFields = view.fields.map((f) => f.name);
            viz.datastore.statFields = view.fields
              .filter((f) => f.stats)
              .map((f) => f.name);
            this.views.push({ text: view.name, value: view });
          }
        });
      });
    };

    Promise.all([
      car.getSearchViews(),
      viz.datastore.getStoreSchema(),
      car.loadPlugins(),
    ]).then(([views, { schema }, plugins]) => {
      //
      this.schema = schema;
      views
        .map((v) => ({
          text: v.name,
          value: {
            name: v.name,
            fields: v.fields.map(
              (x) =>
                new Field(
                  x.name,
                  x.visible,
                  x.wrap,
                  x.align,
                  x.decodeAs,
                  x.decodeFunc,
                  undefined,
                  undefined,
                  x.queried
                )
            ),
          },
        }))
        .forEach((v) => this.views.push(v));

      inferTypes(plugins, schema);
      setUpTimestampFilter(plugins, schema);
      setUpSort(plugins, schema);
      setUpViews(plugins, schema);

      this.parseFilters();
      if (
        this.viewName &&
        this.views.filter((v) => v.name == this.viewName).length
      ) {
        viz.customView = this.views.filter((v) => v.name == this.viewName)[0];
      } else {
        viz.datastore.suggestView = true;
      }
      if (this.query) {
        this.query.forEach((q) => this.addFilter(q, true));
      }
      viz.datastore.query = JSON.stringify(this.buildQuery());
      that.viz = viz;
      this.allFields = schema
        .map((f) => f.name)
        .filter(
          (x) =>
            !["_version_", "id", "dset", "_logtypgrp", "_logtyp"].includes(x)
        );
      this.newsearch().then(() => (viz.datastore.suggestView = false));
    });
  },

  methods: {
    parseFilters() {
      this.filters = [
        { key: "_msg", op: "contains", val: "*" },
        { key: this.tsField, op: "inrange", val: [] },
      ];
      this.tsfilter = this.filters[1];
      let d = (x) => decodeURIComponent(x);
      Object.entries(this.$route.query)
        .filter(([k]) => k != "_view")
        .forEach(([k, vals]) => {
          vals = Array.isArray(vals) ? vals : [vals];
          vals.forEach((v) => {
            let op = v.split(",")[0];
            let val = v
              .substr(v.indexOf(",") + 1)
              .split(",")
              .map(d);
            this.addFilter(
              {
                key: k,
                val: val,
                op: op,
              },
              true
            );
          });
        });
      let searchView = Object.entries(this.$route.query).filter(
        ([k]) => k == "_view"
      )[0];
      if (searchView) {
        this.viewName = searchView[1];
      }
    },
    tsFilterChanged(ts) {
      this.filters[1].val = [+ts[0], +ts[1]];
      this.filterChanged();
    },

    removeFilter(filt) {
      this.filters.splice(this.filters.indexOf(filt), 1);
      this.filterChanged();
    },

    addEmptyFilter() {
      if (this.filters.filter((f) => !this.valid(f)).length != 0) return;
      this.addFilter({
        key: "_file",
        op: "is",
        val: this.filename,
        editing: true,
      });
    },

    valid(f) {
      return f.key && f.val && f.op;
    },

    filterChanged() {
      this.$router.push({
        path: window.location.pathname,
        query: this.makeSearchParams(),
      });
    },

    newsearch() {
      this.viz.datastore.cursor = "0";
      this.viz.datastore.query = JSON.stringify(this.buildQuery());
      return this.viz.update().finally(() => {
        if (this.viz.datastore.suggestedView) {
          let viewName = this.viz.datastore.suggestedView;
          console.log('views', this.views);
          if (viewName && this.views.filter((v) => v.text == viewName).length) {
            this.viz.customView = this.views.filter(
              (v) => v.text == viewName
            )[0].value;
            this.viz.datastore.queriedFields = this.viz.customView.fields.map((f) => f.name);
          }
        }
        this.filterValues = this.viz.datastore.facets;
        this.dataVersion++;

        /* compute possible visualization */
        this.charts = [];
        car.loadPlugins().then((plugins) => {
          plugins.forEach((plugin) => {
            if (!plugin.handlers.logview) return;
            let computers = plugin.handlers.logview.computecharts;
            if (!computers || !computers.length) return;
            computers.forEach((computer) => {
              let chart = computer(this.schema, this, this.viz.datastore.stats);
              if (chart) {
                chart.name = chart.title;
                chart.title = "";
                let v = createViz(chart.type, {
                  ...chart,
                  key: +new Date(),
                  ctxt: { ...this.viz.ctxt },
                });
                this.charts.push(v);
              }
            });
          });

          if (this.currentChart) {
            this.currentChart = this.charts.filter(
              (c) => c.name == this.currentChart.name
            )[0];
          } else {
            if (this.charts.length == 1) this.currentChart = this.charts[0];
          }
        });
      });
    },

    buildQuery() {
      let makeSQ = (v) => {
        let val = "",
          key = v.key;
        switch (v.op) {
          case "is":
            val = Array.isArray(v.val)
              ? v.val.map((e) => `"${e}"`).join(" OR ")
              : `"${v.val}"`;
            break;
          case "contains":
            val = `${v.val}`;
            break;
          case "isnot":
            val = Array.isArray(v.val)
              ? v.val.map((e) => `${e}`).join(" ")
              : `${v.val}`;
            key = "-" + key;
            break;
          case ":":
            val = v.val;
            break;
          case "inrange":
            val =
              v.val.length && v.val[0] != v.val[1]
                ? `[${+v.val[0]} TO ${+v.val[1]}]`
                : undefined;
            break;
        }
        return { key, val };
      };

      let qMap = this.filters
        .filter((x) => this.valid(x) && !x.editing)
        .reduce((m, v) => {
          let { key, val } = makeSQ(v);
          if (!val) return m;
          if (!m[key]) m[key] = [];
          m[key].push(val);
          return m;
        }, {});

      return Object.entries(qMap).reduce((m, [key, val]) => {
        if (key != "dsetid")
          m[key] = val.join(key.startsWith("-") ? " OR " : " AND ");
        return m;
      }, {});
    },

    searchViewBuilderClosed(evt) {
      if (evt) {
        this.viz.customView = evt;
        this.viz.datastore.queriedFields = evt.fields
          .filter((f) => f.queried)
          .map((x) => x.name);
        this.viz.datastore.statFields = evt.fields
          .filter((f) => f.stats)
          .map((f) => f.name);
        this.newsearch();
      }
      this.showFieldsSelector = false;
    },

    textSelected(e) {
      this.selDetails = e;
      this.showMenu = true;
    },

    rightClicked(e) {
      this.contextMenuDetails = e;
      this.showContextMenu = true;
    },

    closeMenu() {
      this.showMenu = false;
    },

    showChartSelector() {
      if (this.charts.length == 1) {
        this.currentChart = this.currentChart ? undefined : this.charts[0];
      } else {
        this.openChartSelector = true;
      }
    },

    addFilter(f, doNotRefresh) {
      if (f.key == this.tsField) {
        f.val = [+f.val[0], +f.val[1]];
        this.temptsfilter = [new Date(+f.val[0]), new Date(+f.val[1])];
        this.$set(this.filters, 1, f);
        this.tsfilter = f;
      } else if (f.key == "_msg") {
        this.$set(this.filters, 0, f);
      } else {
        this.filters.push(f);
      }
      if (!doNotRefresh) this.filterChanged();
    },

    makeSearchParams() {
      let e = (x) => x; //encodeURIComponent(x);
      return this.filters
        .filter((f) => !["dsetid"].includes(f.key))
        .filter(
          (f) =>
            (Array.isArray(f.val)
              ? f.val.length && f.val[0] != f.val[1]
              : f.val) && !(f.key == "_msg" && f.val == "*")
        )
        .reduce((m, f) => {
          if (!m[f.key]) m[f.key] = [];
          m[f.key].push(
            [e(f.op), ...(Array.isArray(f.val) ? f.val : [f.val]).map(e)].join(
              ","
            )
          );
          return m;
        }, {});
    },

    openShareDialog() {
      let w = window.location;
      let q = Object.entries(this.makeSearchParams())
        .reduce((a, [k, v]) => {
          a.push(`${k}=${v}`);
          return a;
        }, [])
        .join("&");
      this.url = w.origin + w.pathname + (q ? `?${q}` : "");
      this.showShare = true;
    },

    copyFormatted(url) {
      car.copyFormatted(url);
      this.copyDone = true;
      setTimeout(() => (this.copyDone = false), 2000);
    },
  },
};
</script>

<style>
.menu {
  position: absolute;
  display: flex;
  flex-direction: column;
  background: white;
  border: solid 1px lightgray;
  padding: 5px 0px;
  box-shadow: 0 10px 10px rgb(0 0 0 / 0.5);
}

.menuitem {
  padding: 3px 5px;
  cursor: pointer;
}

.menuitem:hover {
  background: lightgray;
}

.filter {
  border-radius: 5px;
  padding: 1px 6px;
  font-size: 0.9em;
  margin: 0px 5px;
}
.filtertext {
  margin: 0px 4px;
}

.searchbar {
  border: solid 1px lightgray;
  flex-grow: 1;
  margin: 0px 3px;
  padding: 4px 10px;
  border-radius: 3px;
  font-family: monospace;
}

.searchbutton {
  margin: 0px 3px;
  padding: 4px 10px;
  border-radius: 3px;
  background: seagreen;
  color: white;
  border: solid 1px seagreen;
}

.wrapped {
  white-space: pre-wrap; /* Since CSS 2.1 */
  white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
  white-space: -pre-wrap; /* Opera 4-6 */
  white-space: -o-pre-wrap; /* Opera 7 */
  word-wrap: break-word; /* Internet Explorer 5.5+ */
}

.unselectable {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

.v-stepper__wrapper {
  height: 100%;
}
</style>
