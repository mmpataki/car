<template>
  <div :key="updateKey">
    <div
      v-if="!generatedVisualization"
      :style="{ display: 'flex', 'flex-direction': visualization.flowType }"
    >
      <component
        :style="{
          margin:
            visualization.flowType == 'row'
              ? '0px 10px 0px 0px'
              : '0px 0px 10px 0px',
          'flex-grow': 1,
        }"
        v-for="(row, idx) in view.data"
        :key="idx"
        :is="vizName(visualization.subtype)"
        :view="builddata(row)"
        :visualization="getChildVisualization(row)"
      ></component>
    </div>
    <div
      v-if="generatedVisualization"
      :style="{
        display: 'flex',
        'flex-direction': visualization.flowType,
      }"
    >
      <chart-view
        :collapsible="subview.collapsible"
        :collapsed="subview.collapsed"
        v-for="(subview, xidx) in view.data"
        :key="xidx"
        :visualization="getChildVisualization(subview)"
        :dashboard="dashboard"
      ></chart-view>
    </div>
  </div>
</template>

<script>
import createViz from "@/models/visualizations/VisFactory.js";
import upperFirst from "lodash/upperFirst";
import ChartView from "@/components/dashboard/ChartView.vue";

export default {
  name: "VisgeneratorVisualization",
  props: ["view", "visualization", "dataWatch", "metaWatch", "dashboard"],
  components: { ChartView },
  data() {
    return {
      updateKey: 0,
    };
  },
  computed: {
    generatedVisualization() {
      return this.visualization.isGenerator;
      // return (
      //   this.view.data &&
      //   this.view.data.length > 0 &&
      //   this.view.data[0].length > 0 &&
      //   this.view.data[0][0].viz != undefined &&
      //   this.view.data[0][0].data != undefined
      // );
    },
  },
  watch: {
    dataWatch: {
      handler() {
        console.log(
          `updating visgen (dw): ${this.visualization.title}`,
          this.view
        );
        this.updateKey++;
        //this.$forceUpdate();
      },
    },
    metaWatch: {
      handler() {
        console.log(`updating visgen (mw): ${this.visualization.title}`);
        this.updateKey++;
        //this.$forceUpdate();
      },
    },
  },
  mounted() {
    this.updateKey++;
  },
  methods: {
    vizName(name) {
      return upperFirst(name + "Visualization");
    },
    builddata(row) {
      return { data: row };
    },
    getChildVisualization(row) {
      if (this.generatedVisualization) {
        let viz = createViz(row.viz.type, row.viz);
        viz.view = { data: row.data };
        viz.isGenerated = true;
        viz.overrideDataVersion = 0;
        viz.ctxt = {};
        viz.dataVersion = 0;
        viz.dataLoading = false;
        return viz;
      } else {
        let type = this.visualization.subtype;
        return createViz(type, {});
      }
    },
  },
};
</script>
