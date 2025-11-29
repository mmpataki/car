<template>
  <div>
    <date-picker
      v-if="visualization.displayMode == 'Time picker'"
      v-model="temptsfilter"
      type="datetime"
      range
      confirm
      clearable
      confirm-text="Done"
      :show-second="false"
      @change="tsChanged"
    ></date-picker>
  </div>
</template>

<script>
import DatePicker from "vue2-datepicker";
import "vue2-datepicker/index.css";

export default {
  name: "TimeselectorVisualization",
  components: { DatePicker },
  props: [
    "view",
    "visualization",
    "dashboard",
    "vh",
    "vw",
    "dataWatch",
    "metaWatch",
  ],
  data() {
    return {
      temptsfilter: {},
      dataLoaded: false,
    };
  },
  watch: {
    dataWatch: function () {
      console.log("dataWatch");
      this.update();
    },
    metaWatch: function () {
      // console.log("metaWatch");
      // this.update();
    },
  },
  mounted() {
    console.log("mounted");
    this.update();
  },
  methods: {
    tsChanged() {
      this.visualization.eventOccured("timeframe-select", {
        start: +this.temptsfilter[0],
        end: +this.temptsfilter[1],
      });
    },  
    update() {
      console.log("time line chart update", this.visualization.title);
      if (!this.view.data || this.view.data.length < 1) {
        this.dataLoaded = false;
        return;
      }
      let obj = this.view.data[0];
      this.temptsfilter = [
        new Date(obj[this.visualization.startIndicatorField]),
        new Date(obj[this.visualization.endIndicatorField]),
      ];
      this.tsChanged();
    },
  },
};
</script>

<style>
</style>