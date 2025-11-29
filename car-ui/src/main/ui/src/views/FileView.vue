<template>
  <!-- rgb(46, 159, 37, 0.25); -->
  <div
    :style="{
      background: dashboard ? dashboard.background : 'white',
      overflow: 'auto',
      height: '100%',
      width: '100%',
      position: 'relative',
    }"
  >
    <div
      v-if="dataset && dashboardsLoaded && fd && dashboards.length > 0"
      style="height: 100%"
      class="dashboarrdset"
    >
      <div
        style="
          border-bottom: 1px solid #eaeaea;
          background: white;
          padding-left: 20px;
          height: 40px;
          display: flex;
          align-items: center;
        "
      >
        <div style="align-items: baseline; display: flex; flex-grow: 1">
          <div style="flex-grow: 0; font-size: 0.7em">
            <b>
              <router-link
                :to="`/ui/datasets/${enc(dataset.id)}`"
                :title="dataset.name"
                >{{ dataset.name }}</router-link
              >
              /
              <router-link
                :to="`/ui/datasets/${enc(dataset.id)}/browse?_file=is,${enc(
                  filename
                )}`"
                :title="filename"
                >{{ cutFileName }}</router-link
              ></b
            >
            <b style="padding: 0px 15px">|</b>
            <i>{{ fd.logTypeGroup }} / </i>
            <i style="padding-right: 5px">{{ fd.logType }} / </i>
          </div>
          <v-text-field
            dense
            v-if="editmode"
            v-model="dashboard.name"
            style="flex-grow: 1"
          />
          <div
            style="
              flex-grow: 0;
              margin: 0px 20px 0px 0px;
              border-bottom: solid 2px transparent;
              display: inline-block;
            "
          >
            <v-select
              v-if="!editmode"
              v-model="dashboard"
              @change="dashboardSelected"
              :outlined="false"
              dense
              :full-width="false"
              :items="
                dashboards
                  .map((d) => ({ text: d.name, value: d }))
                  .concat([{ text: 'New dashboard', value: 'New dashboard' }])
              "
              style="border: none"
            >
            </v-select>
          </div>
          <div style="flex-grow: 1"></div>
          <div style="flex-grow: 0; font-size: 0.9em; margin: 0px 10px">
            <v-btn
              v-if="!editmode"
              @click="setEditMode"
              color="primary"
              small
              icon
            >
              <v-icon x-small>mdi-pencil</v-icon>
            </v-btn>
            <div style="display: none"></div>
            <v-btn
              v-if="!editmode"
              @click="openShareDialog"
              color="primary"
              small
              icon
            >
              <v-icon x-small>mdi-share-variant</v-icon>
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
            <div v-if="editmode">
              <v-menu class="mx-3" offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn x-small color="primary" dark v-bind="attrs" v-on="on">
                    Add visualization
                  </v-btn>
                </template>
                <v-list dense>
                  <v-list-item
                    v-for="(ctyp, index) in charttypes"
                    :key="index"
                    style="cursor: pointer"
                    @click="addVisualization(ctyp.name)"
                  >
                    <v-list-item-title>{{
                      ctyp.displayName
                    }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>

              <span
                style="border-left: 1px solid black; margin: -10px 10px"
              ></span>

              <v-btn
                @click="showDashBoardSettings = true"
                x-small
                text
                color="primary"
                ><b>settings</b>
              </v-btn>
              <v-btn @click="save" x-small text color="success"
                ><b>save</b></v-btn
              >
              <v-btn @click="cancelEdit" x-small text color="error"
                ><b>cancel</b></v-btn
              >
            </div>
          </div>
        </div>
      </div>

      <splitpanes style="height: calc(100vh - 40px)">
        <pane style="overflow: auto">
          <grid-layout
            :key="dataset.id + filename + dashboard.name"
            :layout="layout"
            :col-num="50"
            :row-height="5"
            :col-width="100"
            :is-draggable="editmode"
            :is-resizable="editmode"
            :is-mirrored="false"
            :vertical-compact="true"
            :use-css-transforms="true"
            :margin="[10, 10]"
          >
            <grid-item
              v-for="item in layout"
              :x="item.x"
              :y="item.y"
              :w="item.w"
              :h="item.h"
              :i="item.i"
              :key="`${dataset.id}-${filename}-${dashboard.name}-${item.i}`"
            >
              <div
                :style="{
                  border: editmode
                    ? 'dashed ' +
                      (selecteditem == item ? '1px red' : '0px lightgray')
                    : 'solid 0px #ededed',
                  background: item.backgroundColor || dashboard.visBackground,
                  width: '100%',
                  height: '100%',
                  position: 'relative',
                }"
                :class="{ visualization: item.transparent }"
                @click="selecteditem = item"
              >
                <ChartView :visualization="item" :dashboard="dashboard">
                  <template v-if="editmode" v-slot:controls>
                    <v-icon
                      @click="removeVisualization(item.i)"
                      style="font-size: 17px"
                      >mdi-delete</v-icon
                    >
                  </template>
                </ChartView>
              </div>
            </grid-item>
          </grid-layout>
        </pane>
        <pane v-if="editmode && selecteditem" size="35  " max-size="55%">
          <VisualizationEditor
            style="min-width: 400px; background: white; height: 100%"
            :visualization="selecteditem"
            :dashboard="dashboard"
          ></VisualizationEditor>
        </pane>
      </splitpanes>
    </div>
    <div
      v-if="
        dashboardsLoaded &&
        fd &&
        ((fd.state == 'EXTRACTED' && dashboards.length == 0) ||
          fd.state == 'NOT_DETECTED' ||
          fd.state == 'DETECTION_FAILED' ||
          fd.state == 'FAILED' ||
          fd.state == 'NOTDONE' ||
          fd.state == 'NEW')
      "
      style="
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
      "
    >
      <span v-if="fd.state == 'EXTRACTED' && dashboards.length == 0">
        No dashboards found. Create a&nbsp;
        <v-btn color="primary" x-small @click="newDashboard">new</v-btn>
        &nbsp;one?
      </span>
      <span v-if="fd.state == 'NOT_DETECTED'">
        This log file was not detected by CAR. Please add a log type in the
        "Rules" tab
      </span>
      <span
        v-if="
          fd.state == 'DETECTION_FAILED' ||
          fd.state == 'FAILED' ||
          fd.state == 'NOTDONE' ||
          fd.state == 'NEW'
        "
      >
        This log file was not analyzed by CAR. Please visit the dataset page and
        click "analyze"
      </span>
    </div>

    <v-overlay absolute v-if="!dashboardsLoaded" color="white">
      <v-progress-circular
        indeterminate
        size="32"
        color="primary"
      ></v-progress-circular>
    </v-overlay>
    <div
      v-if="dashboardsLoaded && !fd"
      style="
        height: 100%;
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
      "
    >
      <span>Looks like this a directory / unknown file</span>
    </div>
    <v-dialog v-model="showDashBoardSettings" width="900px" persistent>
      <dashboard-settings-editor
        :dashboard="dashboard"
        @close="showDashBoardSettings = false"
      ></dashboard-settings-editor>
    </v-dialog>
  </div>
</template>

<script>
import { car } from "@/car.js";
import { Dashboard } from "@/models/visualizations/Dashboard.js";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import VueGridLayout from "vue-grid-layout";
import ChartView from "@/components/dashboard/ChartView.vue";
import createViz from "@/models/visualizations/VisFactory.js";
import VisualizationEditor from "@/components/dashboard/VisualizationEditor.vue";
import DashboardSettingsEditor from "@/components/dashboard/DashboardSettingsEditor.vue";

export default {
  name: "FileView",
  props: ["ds", "filename"],
  components: {
    Splitpanes,
    Pane,
    ChartView,
    GridLayout: VueGridLayout.GridLayout,
    GridItem: VueGridLayout.GridItem,
    VisualizationEditor,
    DashboardSettingsEditor,
  },
  data() {
    return {
      dataset: this.ds,

      fd: this.ds ? this.ds.fileStatus[this.filename] : undefined,
      q: "",
      charttyp: "timelinechart",
      tab: "dashboard",
      preProcessor: "x=>x",
      dashboards: [],
      dashboardsLoaded: false,
      dashboard: undefined,
      editmode: false,
      dbkey: 0,

      showDashBoardSettings: false,
      settingsTab: 0,

      visSelected: "",
      selecteditem: undefined,
      charttypes: [],

      url: window.location.href,
      showShare: false,
      copyDone: false,
    };
  },
  computed: {
    cutFileName() {
      let fn = this.filename;
      return fn.length > 40
        ? [fn.substring(0, 20), fn.substring(fn.length - 20, fn.length)].join(
            "..."
          )
        : fn;
    },
    layout() {
      return this.dashboard ? this.dashboard.visualizations : [];
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
      let fsm = this.dataset.fileStatus;
      let fs = new fsnode("", true);
      Object.keys(fsm).forEach((p) => {
        let node = fs,
          path = p.substring(p.indexOf(this.dataset.name));
        path.split(/\\|\//).forEach((pc) => {
          node = node.addChild(pc, new fsnode(pc, !p.endsWith(pc)));
        });
        node.attr = fsm[p];
        node.attr.path = path;
      });
      return Object.values(fs.children);
    },
  },
  created() {
    this.tab = "dashboard";
    let prom = Promise.resolve(1);
    let that = this;
    if (!that.fd) {
      prom = car.getDataset(that.$attrs.dsetid).then((ds) => {
        that.dataset = ds;
        that.fd = ds.fileStatus[that.filename];
      });
    }

    prom.then(() => {
      if (!that.fd || that.fd.state == "NOT_DETECTED") {
        that.dashboardsLoaded = true;
        return;
      }
      let attr = that.dataset.fileStatus[that.filename];

      that.charttypes = car.getChartTypes();

      car
        .getDashboards((attr || {}).logTypeGroup, (attr || {}).logType)
        .then((dbs) => {
          return dbs.map((db) => {
            let ctxt = {
              file: that.filename,
              dsetid: that.dataset.id,
            };
            this.setupTrackers(ctxt, db);
            return new Dashboard({ ...db, ctxt });
          });
        })
        .then((x) => {
          that.dashboards = x;
          this.dashboardsLoaded = true;
          if (x.length > 0) {
            let report = this.$route.params.report;
            console.log(report);

            that.dashboard =
              report == "default" ? x[0] : x.filter((d) => d.name == report)[0];
            if (report == "Untitled") {
              this.newDashboard();
            }
            car.fileOpened(
              that.$attrs.dsetid,
              that.dashboard.name,
              that.filename
            );
          }
        });
    });
  },
  methods: {
    enc(x) {
      return encodeURIComponent(x);
    },
    setupTrackers(ctxt, db) {
      db.variables.forEach((v) => {
        try {
          ctxt[v.name] = JSON.parse(v.defaultValue);
        } catch (e) {
          console.error(e);
        }
      });
      ctxt.__vals = {};
      let watchers = (ctxt.__watchers = {});
      db.variables
        .map((v) => v.name)
        .forEach(function (prop) {
          ctxt.__vals[prop] = ctxt[prop];
          watchers[prop] = watchers[prop] || new Set();
          Object.defineProperty(ctxt, prop, {
            get: function () {
              if (car.isVAccessGoingOn()) {
                let viz = car.getCurrentUpdatingVisualization();
                if (viz) {
                  console.error(`${prop} is being read by ${viz.title}`);
                  watchers[prop].add(viz);
                }
              }
              return ctxt.__vals[prop];
            },
            set: function (val) {
              ctxt.__vals[prop] = val;
              watchers[prop].forEach((viz) => {
                console.log(
                  `Update for ${viz.title} because [${prop}] changed`
                );
                viz.update();
              });
            },
          });
        });
    },
    dashboardKey() {
      return this.dbkey++;
    },
    dashboardSelected(value) {
      if (value == "New dashboard") {
        this.dashboard = new Dashboard();
        this.dashboards.push(this.dashboard);
        this.editmode = true;
      }
      this.$router.push({
        name: "fileview",
        params: {
          dsetid: this.$attrs.dsetid,
          report: this.dashboard.name,
          filename: this.filename,
        },
      });
    },
    setEditMode() {
      this.editmode = true;
      this.dashboardCopy = JSON.stringify(this.dashboard);
    },
    cancelEdit() {
      this.editmode = false;
    },
    sql() {
      let preproc = eval(`
                        (function(row) {
                            return ${this.preProcessor}
                        })()
                    `);
      this.results.splice(0, this.results.length);
      car.sql(this.dataset.id, this.q).then((results) => {
        this.results.splice(0, this.results.length);
        results
          .map((r) => preproc(r))
          .filter((r) => r != undefined)
          .forEach((r) => this.results.push(r));
      });
    },
    save() {
      this.dashboard.logType = this.fd.logType;
      this.dashboard.logTypeGroup = this.fd.logTypeGroup;
      car.saveDashBoard(this.dashboard);
      this.selecteditem = null;
      this.editmode = false;
    },
    newDashboard() {
      let x = new Dashboard({
        ctxt: {
          file: this.filename,
          dsetid: this.dataset.id,
        },
      });
      this.dashboards.push((this.dashboard = x));
      this.editmode = true;
    },
    addVisualization(vname) {
      let lastRow = this.layout.reduce(
        (prev, current) => (prev.y > current.y ? prev : current),
        { y: 0, h: 1 }
      );
      let args = {
        type: vname,
        title: "New visualization",
        description: "",
        x: 0,
        y: lastRow.y + lastRow.h,
        w: 100,
        h: 5,
        i: +new Date(),
        ctxt: this.dashboard.ctxt,
      };

      /* used by editors */
      this.selecteditem = createViz(vname, args);
      this.layout.push(this.selecteditem);
    },
    removeVisualization(i) {
      const index = this.layout.findIndex((item) => item.i == i);
      this.layout.splice(index, 1);
    },

    openShareDialog() {
      this.showShare = true;
      this.copyDone = false;
    },

    copyFormatted(html) {
      let clipboardDiv = this.$refs.clipboardDiv;
      if (!clipboardDiv) {
        clipboardDiv = document.createElement("div");
        clipboardDiv.style.fontSize = "12pt"; // Prevent zooming on iOS
        // Reset box model
        clipboardDiv.style.border = "0";
        clipboardDiv.style.padding = "0";
        clipboardDiv.style.margin = "0";
        // Move element out of screen
        clipboardDiv.style.position = "fixed";
        clipboardDiv.style["right"] = "-9999px";
        clipboardDiv.style.top =
          (window.pageYOffset || document.documentElement.scrollTop) + "px";
        // more hiding
        clipboardDiv.setAttribute("readonly", "");
        clipboardDiv.style.opacity = 0;
        clipboardDiv.style.pointerEvents = "none";
        clipboardDiv.style.zIndex = -1;
        clipboardDiv.setAttribute("tabindex", "0"); // so it can be focused
        clipboardDiv.innerHTML = "";
        document.body.appendChild(clipboardDiv);
      }
      clipboardDiv.innerHTML = html;
      var focused = document.activeElement;
      clipboardDiv.focus();

      window.getSelection().removeAllRanges();
      var range = document.createRange();
      range.setStartBefore(clipboardDiv.firstChild);
      range.setEndAfter(clipboardDiv.lastChild);
      window.getSelection().addRange(range);
      try {
        if (document.execCommand("copy")) {
          console.log("copied");
          this.copyDone = true;
          setTimeout(() => (this.copyDone = false), 2000);
        } else console.log("execCommand returned false !");
      } catch (err) {
        console.log("execCommand failed ! exception " + err);
      }

      focused.focus();
    },
  },
};
</script>

<style>
.splitpanes__pane {
  font-family: Helvetica, Arial, sans-serif;
}
.splitpanes.default-theme .splitpanes__pane {
  background-color: transparent;
}
.visualization {
  box-shadow: 0px 0px 5px 1px rgb(0 0 0 / 15%);
  -webkit-box-shadow: 0px 0px 5px 1px rgb(0 0 0 / 15%);
  -moz-box-shadow: 0px 0px 5px 1px rgb(0 0 0 / 15%);
}
.v-text-field__details {
  display: none;
}
</style>
