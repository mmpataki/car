<template>
  <splitpanes class="default-theme" style="height: calc(100vh - 37px)">
    <pane
      style="
        padding: 10px 10px 10px 10px;
        max-width: 450px;
        border-right: solid 1px lightgray;
        justify-content: space-between;
        background: #eaeef280;
        display: inline-block;
        position: relative;
        overflow: auto;
      "
    >
      <div>
        <div class="form-col">
          <label>Case: </label>
          <select @change="adHocOpened" v-model="currentcase">
            <option :key="c.name" v-for="c in cases" :value="c">
              {{ c.name }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div v-if="currentcase" class="form-col">
            <label>Tags: </label>
            <div style="padding: 5px 0px">
              <span
                v-for="type in currentcase.types"
                :key="type"
                style="
                  fontsize: 0.85em;
                  padding: 1px 5px;
                  flex-grow: 0;
                  background-color: tomato;
                  color: white;
                  border-radius: 3px;
                  border: solid 1px transparent;
                  margin-right: 1px;
                "
                >{{ type.toLowerCase() }}</span
              >
            </div>
          </div>

          <div v-if="currentcase" class="form-col">
            <label>Analysis: </label>
            <div style="padding: 5px">
              <img
                v-if="
                  currentcase.detailStatus &&
                  currentcase.detailStatus.status.endsWith('ING')
                "
                src="loading.gif"
                style="height: 15px; width: 15px"
              />
              <b
                :style="{
                  fontSize: '0.85em',
                  background:
                    currentcase.detailStatus &&
                    currentcase.detailStatus.status == 'FAILED'
                      ? 'lightred'
                      : 'lightgreen',
                  padding: '2px 5px',
                  borderRadius: '3px',
                  border: 'solid 1px transparent',
                }"
                >{{
                  currentcase.detailStatus
                    ? currentcase.detailStatus.status.toLowerCase()
                    : "not done"
                }}</b
              >
            </div>
          </div>
        </div>

        <div v-if="currentcase" class="form-row">
          <button @click="analyze(currentcase)">Analyze</button>
          <button @click="cleanup(currentcase)">Cleanup</button>
        </div>

        <hr />

        <div v-if="currentcase && currentcase.detailStatus">
          <tree-view
            :nodes="files"
            :pickedItem="pickedValue"
            :expanded="true"
            :key="currentcase.name"
            :treeid="currentcase.name"
          ></tree-view>
        </div>
      </div>
    </pane>
    <pane>
      <div>
        <el-menu
          :default-active="'reports'"
          class="nav"
          mode="horizontal"
          :router="true"
        >
          <el-menu-item index="reports">Reports</el-menu-item>
          <el-menu-item index="explore">Explore</el-menu-item>
        </el-menu>
        <router-view></router-view>
      </div>
      <div>
        <div
          v-if="adhocviz"
          v-show="tab == 'adhoc'"
          style="padding: 0px; display: flex; flex-direction: column"
        >
          <div
            style="
              border-bottom: 1px solid lightgrey;
              background: ghostwhite;
              border-bottom: solid 3px rgb(255, 69, 0, 0.4);
            "
          >
            <collapsible-pane type="top" :text="'data'">
              <dataset-editor
                style="padding: 10px 20px"
                :visualization="adhocviz"
              ></dataset-editor>
            </collapsible-pane>
          </div>

          <div
            v-if="adhocviz.view.data && adhocviz.view.data.length > 0"
            style="
              border-bottom: 1px solid lightgrey;
              background: ghostwhite;
              border-bottom: solid 3px rgb(46, 139, 87, 0.4);
            "
          >
            <collapsible-pane type="top" :text="'visualization'">
              <div style="padding: 10px 20px; min-height: 40px">
                <div class="form-row form">
                  <label>Choose a visualization</label>
                  <select v-model="adhocviz.type">
                    <option
                      v-for="ctyp in charttypes"
                      :value="ctyp.name"
                      :key="ctyp.name"
                    >
                      {{ ctyp.displayName }}
                    </option>
                  </select>
                </div>
                <component
                  :visualization="adhocviz"
                  :is="adhocviz.type + '-charteditor'"
                ></component>
              </div>
            </collapsible-pane>
          </div>

          <div
            style="padding: 20px"
            v-if="adhocviz.view.data && adhocviz.view.data.length > 0"
          >
            <component
              v-if="adhocviz.view.data"
              :is="adhocviz.type + '-chart-component'"
              :view="adhocviz.view"
              :visualization="adhocviz"
            ></component>
          </div>
        </div>
      </div>
    </pane>
  </splitpanes>
</template>

<script>
import { car } from "@/car.js";
import Visualization from "@/models/visualizations/Visualization.js";
import createViz from "@/models/visualizations/VisFactory.js";
import TreeView from "@/components/utils/TreeView.vue";
import { Dashboard } from "@/models/visualizations/Dashboard.js";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

export default {
  name: "AnalyzeView",
  components: { Splitpanes, Pane, TreeView },
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
      this.selecteditem = createViz(this.visSelected, args);
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

<style scoped>
.el-menu-item {
  height: 35px;
  line-height: 35px;
}
.splitpanes.default-theme .splitpanes__pane {
  background-color: transparent;
}
</style>
