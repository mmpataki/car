<template>
  <div
    ref="container"
    class="xcontainer"
    :style="{
      display: 'flex',
      flexDirection: 'column',
      height: '100%',
      background: isFullScreen ? 'white' : 'transparent',
      position: 'relative',
    }"
    @fullscreenchange="onFullscreenChange"
  >
    <div
      v-if="visualization.title"
      :style="{
        height: '30px',
        display: 'flex',
        margin: '0px 0px 0px 0px',
        padding: '10px',
        'align-items': 'center',
      }"
    >
      <helpful-label
        :color="dashboard.visTitleColor || '#2f63a0'"
        style="flex-grow: 1"
        :help="visualization.description"
      >
        <span
          v-if="collapsible"
          style="
            font-size: 0.7em;
            display: inline-block;
            text-transform: uppercase;
            font-weight: 900;
            border-radius: 5px;
            padding: 5px 20px 5px 0px;
            height: min-content;
            cursor: pointer;
          "
          @click="vizcollapsed = !vizcollapsed"
        >
          <v-icon x-small>{{
            vizcollapsed ? "mdi-plus-box" : "mdi-minus-box"
          }}</v-icon>
          {{ resolvedTitle }}
        </span>
        <span
          v-if="!collapsible"
          :style="{
            color: dashboard.visTitleColor || '#2f63a0',
            fontSize: `${dashboard.titleFontSize}px`,
            display: 'inline-block',
            textTransform: dashboard.textTransform || 'uppercase',
            fontWeight: 900,
          }"
        >
          {{ resolvedTitle }}
        </span>
      </helpful-label>
    </div>

    <div
      class="showonhover"
      v-if="
        !hidecontrols &&
        visualization.unfilteredData &&
        visualization.unfilteredData.length > 0
      "
    >
      <v-icon
        v-if="!isFullScreen"
        style="font-size: 20px"
        class="mx-2"
        @click="showTableView = !showTableView"
        >{{ showTableView ? "mdi-chart-bar" : "mdi-table" }}</v-icon
      >
      <v-icon v-if="!isFullScreen" style="font-size: 20px" @click="goFullScreen"
        >mdi-fullscreen</v-icon
      >

      <FilterMenu
        :data="visualization.unfilteredData"
        :filters="visualization.filters"
        @change="visualization.applyUserFilter()"
      ></FilterMenu>
      <slot name="controls"></slot>
    </div>

    <div
      v-if="!vizcollapsed"
      ref="chartdiv"
      :style="{
        overflow: 'auto',
        height: visualization.title ? 'calc(100% - 30px)' : '100%',
        width: '100%',
        margin: visualization.title ? '10px 0px 5px 0px' : '0px 0px 5px 0px',
        padding: visualization.title ? '0px 10px' : '0px',
      }"
    >
      <table
        v-if="showTableView && !visualization.error"
        style="width: 100%; border-collapse: collapse"
        border="1"
      >
        <tr>
          <th v-for="(key, idx) in headers" :key="idx">{{ key }}</th>
        </tr>
        <tr v-for="(row, idx) in visualization.view.data" :key="idx">
          <td
            style="padding: 0px 10px"
            v-for="(key, idx) in headers"
            :key="idx"
          >
            {{ row[key] }}
          </td>
        </tr>
      </table>
      <component
        :is="vizName"
        v-if="!showTableView && dataVersion > -1 && !visualization.error"
        :view="visualization.view"
        :visualization="visualization"
        :dataWatch="dataVersion"
        :metaWatch="metaVersion"
        :dashboard="dashboard"
        :vw="vw"
        :vh="vh"
      ></component>
      <pre
        v-if="visualization.error"
        class="error--text"
        style="
          width: 100%;
          height: 100%;
          white-space: pre-wrap; /* Since CSS 2.1 */
          white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
          white-space: -pre-wrap; /* Opera 4-6 */
          white-space: -o-pre-wrap; /* Opera 7 */
          word-wrap: break-word; /* Internet Explorer 5.5+ */
        "
        >{{ visualization.error.trim() }}</pre
      >
    </div>
    <div
      v-if="
        !visualization.dataIndependent &&
        !vizcollapsed &&
        !visualization.dataLoading &&
        !visualization.error &&
        (!visualization.view.data || visualization.view.data.length == 0)
      "
      style="
        display: flex;
        justify-content: center;
        align-items: center;
        position: absolute;
        top: 30px;
        left: 0px;
        right: 0px;
        bottom: 0px;
      "
    >
      <span style="margin: 0px 25px">{{
        visualization.noDataMessage || "No data available"
      }}</span>
    </div>
    <v-overlay
      :color="
        visualization.backgroundColor
          ? visualization.backgroundColor
          : dashboard.visBackground
          ? dashboard.visBackground == 'transparent'
            ? dashboard.background
            : dashboard.visBackground
          : 'white'
      "
      style="margin-top: 30px"
      opacity="1"
      :value="visualization.dataLoading"
      absolute
      v-if="false"
    >
      <v-progress-circular
        color="primary"
        indeterminate
        size="32"
      ></v-progress-circular>
    </v-overlay>
    <slot></slot>
  </div>
</template>

<script>
import { car } from "@/car.js";
import upperFirst from "lodash/upperFirst";
import FilterMenu from "@/components/utils/filters/FilterMenu.vue";
const _ = require("lodash");

export default {
  name: "ChartView",
  props: [
    "dashboard",
    "visualization",
    "collapsed",
    "collapsible",
    "hidecontrols",
  ],
  components: { FilterMenu },
  data() {
    return {
      ctxt: undefined,
      observing: false,
      vw: 0,
      vh: 0,
      isFullScreen: false,
      showTableView: false,
      dataVersion:
        this.visualization.overrideDataVersion != undefined
          ? this.visualization.overrideDataVersion
          : -1,
      metaVersion: -1,
      vizcollapsed: this.collapsed,
    };
  },
  computed: {
    vizName() {
      return upperFirst(this.visualization.type + "Visualization");
    },
    headers() {
      return this.visualization.view.data
        ? Object.keys(this.visualization.view.data[0])
        : [];
    },
    resolvedTitle() {
      try {
        return car.evalTemplateStringWithCtxt(
          this.visualization.title,
          this.dashboard.ctxt
        );
      } catch (e) {
        return "";
      }
    },
  },
  created() {
    console.log("update from vis: ", this.dataVersion);
  },
  mounted() {
    console.log("mounted", this.visualization.title);
    this.visualization.update().finally(() => {
      console.log(
        "updated the data",
        this.visualization.title,
        this.dataVersion
      );
    });
    if (!this.observing && this.$refs.chartdiv) {
      this.vw = this.$refs.chartdiv.clientWidth;
      this.vh = this.$refs.chartdiv.offsetHeight;

      console.log(this.vw, this.vh);

      let func = _.debounce((e) => {
        this.vw = e[0].contentRect.width;
        this.vh = e[0].contentRect.height;
        console.log(this.vw, this.vh);
      }, 100);

      new ResizeObserver((e) => {
        func(e);
      }).observe(this.$refs.chartdiv);
      this.observing = true;
    }
  },
  watch: {
    visualization: {
      deep: true,
      handler: function (e) {
        // this is a false alarm
        let loadingAlarm = this.dataLoading != e.dataLoading;
        if (loadingAlarm) this.dataLoading = e.dataLoading;

        /* data change */
        if (e.dataVersion != this.dataVersion) {
          console.log(
            "updated the data from vis: ",
            this.dataVersion,
            " = ",
            e.dataVersion
          );
          this.dataVersion = e.dataVersion;
          return;
        }

        /* variable change detection */
        if (!e.sctxt) {
          e.sctxt = JSON.parse(JSON.stringify(e.ctxt));
          return;
        }

        let o = e.sctxt,
          n = e.ctxt,
          changeList = [];

        if (n.__vals) {
          Object.keys(n.__vals).forEach((k) => {
            if (o[k] !== n[k]) {
              o[k] = n[k];
              changeList.push(k);
            }
          });
        }

        this.visualization.defnUpdated();
        if (!changeList.length && !loadingAlarm) this.metaVersion++;
      },
    },
  },
  methods: {
    goFullScreen() {
      this.$refs.container.requestFullscreen();
    },
    onFullscreenChange() {
      this.isFullScreen = document.fullscreenElement;
    },
  },
};
</script>

<style scoped>
.showonhover {
  display: none;
  position: absolute;
  top: 0px;
  right: 5px;
}
.xcontainer {
}
.xcontainer:hover .showonhover {
  display: flex;
}
</style>