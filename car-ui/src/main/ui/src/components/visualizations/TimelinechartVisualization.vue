<template>
  <highcharts
    v-if="dataLoaded"
    ref="chartbox"
    :options="options"
    :constructor-type="'chart'"
  ></highcharts>
</template>

<script>
import Highcharts from "highcharts";
import xrangeInit from "highcharts/modules/xrange";
import "highcharts/modules/debugger";

xrangeInit(Highcharts);

export default {
  name: "TimelinechartVisualization",
  props: ["view", "visualization", "dashboard", "vh", "vw", "dataWatch", "metaWatch"],
  data() {
    return {
      options: {},
      dataLoaded: false,
    };
  },
  watch: {
    vh: function (vh) {
      if (this.view.data && this.view.data.length) {
        this.$refs.chartbox.chart.update({
          chart: {
            height: vh,
          },
        });
      }
    },
    vw: function (vw) {
      if (this.view.data && this.view.data.length) {
        this.$refs.chartbox.chart.update({
          chart: {
            width: vw,
          },
        });
      }
    },
    dataWatch: function () {
      console.log("dataWatch");
      this.update();
    },
    metaWatch: function () {
      console.log("metaWatch");
      this.update();
    },
  },
  mounted() {
    console.log("mounted");
    this.update();
  },
  methods: {
    update() {
      console.log("time line chart update", this.visualization.title);
      if (!this.view.data || this.view.data.length < 1) {
        this.dataLoaded = false;
        return;
      }
      this.dataLoaded = true;
      let cats = Object.keys(
        this.view.data.reduce((a, d) => {
          a[d[this.visualization.grpByField]] = 1;
          return a;
        }, {})
      );
      let cdata = this.view.data.map((d) => ({
        x: d[this.visualization.fromField],
        x2: d[this.visualization.toField],
        y: cats.indexOf(d[this.visualization.grpByField]),
        label: d[this.visualization.labelField],
        color: d[this.visualization.colorField]
      }));
      let viz = this.visualization;
      this.options = {
        tooltip: {
          enabled: viz.tooltipEnabled,
        },
        chart: {
          type: "xrange",
          zoomType: "xy",
          height: this.vh,
          width: this.vw,
          displayErrors: true,
          panning: true,
          panKey: 'ctrl',
          backgroundColor: this.dashboard.visBackground || "transparent",
          events: {
            selection: function (event) {
              if (event.resetSelection) return;
              let edata = {
                x_min: event.xAxis.length > 0 ? event.xAxis[0].min : 0,
                x_max: event.xAxis.length > 0 ? event.xAxis[0].max : 0,
                y_min: event.yAxis.length > 0 ? event.yAxis[0].min : 0,
                y_max: event.yAxis.length > 0 ? event.yAxis[0].max : 0,
              };
              viz.eventOccured("range-select", edata);
            },
          },
        },
        plotOptions: {
          series: {
            turboThreshold: 999999999,
            borderRadius: 0,
            colorByPoint: false,
            point: {
              events: {
                click: function () {
                  viz.eventOccured("bar-click", viz.view.data[this.index]);
                },
              },
            },
          },
        },
        credits: {
          enabled: false,
        },
        title: {
          text: "",
        },
        accessibility: {
          point: {
            descriptionFormatter: function (point) {
              var ix = point.index + 1,
                from = new Date(point.x),
                to = new Date(point.x2);
              return (
                ix +
                ". " +
                point.label +
                ", " +
                from.toDateString() +
                " to " +
                to.toDateString() +
                "."
              );
            },
          },
        },
        xAxis: {
          type: "datetime",
          labels: {
            enabled: viz.xLabelEnabled
          },
        },
        yAxis: {
          title: {
            text: "",
          },
          labels: {
            enabled: viz.yLabelEnabled
          },
          categories: cats,
          reversed: true,
        },
        series: [
          {
            name: "",
            borderColor: "gray",
            pointWidth: this.visualization.pointWidth,
            data: cdata,
            showInLegend: false,
            dataLabels: {
              enabled: true,
            },
            tooltip: {
              pointFormat: `<b>{point.label}</b><br/>`,
            },
          },
        ],
      };
    },
  },
};
</script>

<style>
</style>