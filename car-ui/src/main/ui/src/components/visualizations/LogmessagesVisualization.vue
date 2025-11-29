<template>
  <div
    v-if="visualization.datastore.mode != 'facet'"
    style="height: 100%; display: flex; flex-direction: column"
  >
    <div
      style="flex-grow: 1; position: relative; height: 100%; overflow: auto"
      ref="scrolldiv"
      @scroll="scrolled"
    >
      <table
        v-show="!noResults"
        class="log-table"
        ref="outputtable"
        style="
          width: 100%;
          table-layout: auto;
          margin: 0px;
          border-collapse: collapse;
        "
        @mouseup="mouseUp($event)"
      ></table>
      <div
        v-show="noResults"
        style="
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
        "
      >
        No results found. Try different keywords
      </div>
      <v-overlay
        color="white"
        absolute
        opacity="1"
        :value="visualization.dataLoading"
      >
        <v-progress-circular
          color="primary"
          indeterminate
          size="32"
        ></v-progress-circular>
      </v-overlay>
    </div>
    <div v-if="visualization.view.data" style="display: flex">
      <div style="flex-grow: 1"></div>
      <strong
        ><code
          ><small style="margin-left: 20px"
            >{{ view.data.length }} / {{ view.data.totalMessages }}</small
          ></code
        ></strong
      >
    </div>
    <v-dialog
      v-if="expandedMsg"
      :value="expandedMsg != undefined"
      @click:outside="expandedMsg = undefined"
    >
      <v-card>
        <v-card-title>All fields</v-card-title>
        <v-card-text>
          <v-data-table
            :headers="[
              { text: 'Key', value: 'Key' },
              { text: 'Value', value: 'Value' },
            ]"
            :items="
              Object.keys(expandedMsg).sort().map(k => ({
                Key: k,
                Value: expandedMsg[k],
              }))
            "
          ></v-data-table>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
  <div v-else style="height: 100%; display: flex">
    <router-link
      :to="getHref(facet)"
      v-for="(facet, idx) in visualization.view.data"
      :key="idx"
      style="
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
      "
    >
      <svg viewBox="0 0 56 18">
        <text
          x="50%"
          text-anchor="middle"
          y="15"
          :style="{
            fill: visualization.colorField
              ? facet[visualization.colorField]
              : 'black',
            fontWeight: 'bold',
          }"
        >
          {{ facet.count }}
        </text>
      </svg>
      <div style="">{{ facet.name }}</div>
    </router-link>
  </div>
</template>


<script>
import { car, render, decodeFuncs } from "@/car.js";
export default {
  name: "LogmessagesVisualization",
  props: ["view", "visualization", "dataWatch", "metaWatch"],
  data() {
    return {
      noResults: false,
      expandedMsg: undefined,
      selectedMsgs: [],
    };
  },
  created() {
    let viz = this.visualization;
    if (viz.viewName != "custom") {
      car.getSearchViews().then((svs) => {
        this.visualization.customView = svs.filter(
          (sv) => sv.name == viz.viewName
        )[0];
      });
    }
    this.updateView();
  },
  computed: {
    fields() {
      return this.visualization.customView.fields;
    },
  },
  watch: {
    metaWatch: {
      handler() {
        console.log("updating logvis mw");
        this.updateView();
      },
    },
    dataWatch: {
      handler() {
        console.log("updating logvis dw");
        this.updateView();
      },
    },
  },
  methods: {
    _expandedMsg(row) {
      console.log(row)
      this.visualization.datastore.doSearch({
        query: {
          id: row.id
        },
        pageSize: 1
      }).then(resp => {
        this.expandedMsg = resp.msgs[0]
      })
    },
    updateView() {
      console.log("update view");
      this.selectedMsgs = [];
      let data = this.view.data;
      if (!data || !data.length) {
        this.noResults = true;
        return;
      }
      this.noResults = false;
      let output = this.$refs.outputtable;

      if (!output) return;

      let viz = this.visualization; //, sortIcons = ['&#9660;', ];

      output.innerHTML = "";
      output.appendChild(
        render("log", {
          ele: "tr",
          children: [
            { ele: "th", text: "", classList: "header" },
            ...this.fields
              .filter((f) => f.visible)
              .map((field) => {
                let sf = viz.datastore.sortFields.filter(
                  (sf) => sf.first == field.name
                )[0];

                let sortFunc = () => {
                  let sfs = viz.datastore.sortFields;
                  let sf = sfs.filter((sf) => sf.first == field.name);
                  if (!sf.length) {
                    sfs.push((sf = { first: field.name, second: "" }));
                  } else {
                    sf = sfs[0];
                  }
                  let states = { "": "asc", asc: "desc", desc: "" };
                  sf.second = states[sf.second];
                  if (sf.second == "") {
                    sfs.splice(sfs.indexOf(sf), 1);
                  }
                  viz.datastore.cursor = "0";
                  viz.update().then(() => this.updateView());
                };

                return {
                  ele: "th",
                  classList: "header",
                  children: [
                    { ele: "span", text: field.name },
                    {
                      ele: "b",
                      html: sf && sf.second == "desc" ? "&#9660;" : "&#9650;",
                      evnts: {
                        click: sortFunc,
                      },
                      styles: {
                        color: !sf ? "lightgray" : "black",
                        marginLeft: "3px",
                      },
                    },
                  ],
                  styles: {
                    "text-align": field.align,
                    borderRight: "solid 1px #ddd",
                  },
                  evnts: {
                    click: sortFunc,
                  },
                };
              }),
          ],
        })
      );
      data
        .map((row, i) => this.makeRows(row, i))
        .forEach((el) => {
          output.appendChild(el);
        });
    },
    makeRows(row, idx) {
      let comp = this;
      return render("log", {
        ele: "tr",
        classList: "row",
        evnts: {
          click: function () {
            comp.rowSelected(row, idx);
            this.style.background = row.___selected ? "#c3e6fc" : "transparent";
          },
          contextmenu: function (e) {
            e.preventDefault();
            comp.$emit("right-clicked", {
              msgs: comp.selectedMsgs
                .map((row) =>
                  comp.fields
                    .filter((f) => f.visible)
                    .reduce((m, f) => {
                      m[f.name] = comp.decode(row, f);
                      return m;
                    }, {})
                )
                .reduce((a, r) => {
                  a.push(r);
                  return a;
                }, []),
              menuX: e.pageX,
              menuY: e.pageY,
            });
          },
        },
        children: [
          {
            ele: "td",
            classList: "td expander",
            styles: {
              padding: "0px 3px 0px 5px",
              verticalAlign: "top",
              cursor: "pointer",
            },
            html: "+",
            evnts: {
              click: () => {
                comp._expandedMsg(row);
              },
            },
          },
          ...comp.fields
            .filter((f) => f.visible)
            .map((field) => ({
              ele: "td",
              classList: "td",
              attribs: {
                style: `${
                  field.wrap ? "overflow-wrap: anywhere" : ""
                }; white-space: ${
                  field.wrap ? "pre-wrap" : "nowrap"
                }; text-align: field.align; padding: 0px 6px; vertical-align: top`,
              },
              text: comp.decode(row, field),
            })),
        ],
      });
    },

    decode(message, field) {
      return decodeFuncs[field.decodeAs || "default"](message, field);
    },

    valChanged(e) {
      this.visualization.eventOccured("change", this.view.data[e]);
    },

    rowSelected(message, idx) {
      this.selected = message;
      this.$set(message, "___selected", !message.___selected);
      if (message.___selected) this.selectedMsgs[idx] = message;
      else this.selectedMsgs.splice(idx);
      this.$emit("log-message-selected", message);
    },

    scrolled() {
      if (this.updating) return;
      let listElm = this.$refs.scrolldiv;
      if (listElm.scrollTop + listElm.clientHeight >= listElm.scrollHeight) {
        console.log("scrolled");
        this.updating = true;
        let idx = listElm.querySelectorAll(".log-row").length;
        this.visualization.scroll().then(() => {
          this.updateView();
          this.$refs.scrolldiv
            .querySelectorAll(".log-row")
            [idx - 1].scrollIntoView(true);
          this.updating = false;
        });
      }
    },

    getHref(facet) {
      console.log(facet);
      let viz = this.visualization,
        e = (_) => _; //encodeURIComponent;
      return `/ui/datasets/${viz.ctxt.dsetid}/browse?${e(
        viz.datastore.facetFields[0]
      )}=is,${e(facet.name)}&_file=is,${e(viz.ctxt.file)}`;
    },

    mouseUp(e) {
      let sel = document.all
        ? document.selection.createRange().text
        : document.getSelection();
      if (sel.anchorNode != sel.extentNode) return;
      if (sel.toString() == "") return;
      let node = sel.anchorNode;

      while (node && node.nodeName.toLowerCase() != "td") {
        node = node.parentNode;
      }
      console.log(node);
      this.$emit("text-selected", {
        fname: this.fields.filter((f) => f.visible)[node.cellIndex - 1].name,
        txt: sel.toString(),
        menuX: e.pageX,
        menuY: e.pageY,
      });
    },
  },
};
</script>

<style>
.logtd {
  padding: 0px 3px;
  vertical-align: top;
}

.log-header {
  text-align: left;
  padding: 0px 3px;
  position: sticky;
  top: -1px;
  background: lightgray;
  white-space: nowrap;
}

.log-table {
  font-family: monospace;
  font-size: 0.8em;
  margin: 5px;
}

.log-expander {
  margin-top: 3px;
  color: lightgray;
  cursor: pointer;
}

.log-row:hover .log-expander {
  color: black;
}
</style>