<template>
  <div
    :style="{
      margin: '5px',
      overflow: 'hidden',
      'font-size': `${this.visualization.fontSize || 14}px`,
      'font-family': this.visualization.monospace ? 'monospace' : 'inherit',
      'white-space': this.visualization.monospace ? 'pre-wrap' : '',
    }"
    v-html="html"
  ></div>
</template>

<script>
export default {
  name: "TextVisualization",
  props: ["view", "visualization", "metaWatch", "dataWatch"],
  data() {
    return {
      html: "",
    };
  },
  mounted() {
    this.update();
  },
  watch: {
    vw: {
      handler: function () {
        this.update();
      },
    },
    vh: {
      handler: function () {
        this.update();
      },
    },
    dataWatch: {
      handler() {
        console.debug("dataWatch", this.visualization.title, this.view.data);
        this.update();
      },
    },
    metaWatch: {
      handler() {
        console.debug("metaWatch", this.visualization.title, this.view.data);
        this.update();
      },
    },
  },
  methods: {
    update() {
      console.debug("update", this.visualization.title, this.view.data);
      if (!this.view.data || this.view.data.length < 1) {
        this.html = "";
        return;
      }
      this.html = Object.entries(this.view.data[0])[0][1];
    },
  },
};
</script>
