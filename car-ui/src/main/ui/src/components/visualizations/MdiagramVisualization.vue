<template>
  <div :key="idx" style="height: 100%; width: 100%">
    <div style="height: 100%; width: 100%" ref="xoutput"></div>
  </div>
</template>

<script>
// import { eventHandlers } from "@/models/visualizations/Visualization.js";
import svgPanZoom from "svg-pan-zoom";
import mermaidAPI from "mermaid";
export default {
  props: ["view", "visualization"],
  data() {
    return {
      idx: "xmddiagram" + +new Date(),
    };
  },
  mounted() {
    mermaidAPI.initialize({
      startOnLoad: true,
    });
    setTimeout(() => this.update(), 5000);
  },
  watch: {
    dataWatch: {
      handler() {
        this.update();
      },
    },
    metaWatch: {
      handler() {
        this.update();
      },
    },
  },
  methods: {
    update() {
      let that = this;
      if (!this.view.data || this.view.data.length < 1) {
        this.diagram = "";
        return;
      }
      let diagram =
        this.visualization.diagramType +
        "\n" +
        this.view.data.filter((x) => x != undefined).join("\n");
      mermaidAPI.render(this.idx, diagram, (cb) => {
        that.$refs.xoutput.innerHTML = cb;
        that.$refs.xoutput.childNodes[0].style.height = that.$refs.xoutput.childNodes[0].style.width = "100%";
        svgPanZoom(that.$refs.xoutput.childNodes[0], {
          minZoom: 0.02,
          maxZoom: 50,
          contain: false,
          center: true,
          dblClickZoomEnabled: false,
        });
      });
    },
  },
};
</script>
