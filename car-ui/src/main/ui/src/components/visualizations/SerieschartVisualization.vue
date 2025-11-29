<template>
  <div
    ref="chartcontainer"
    style="margin: 0px; padding: 0px; height: 100%; width: 100%; border: none"
  >
    <highcharts
      v-show="view.data"
      ref="chartbox"
      :options="options"
      :constructor-type="'chart'"
    ></highcharts>
  </div>
</template>

<script>
import boost from "highcharts/modules/boost";
import Highcharts from "highcharts";
//const _ = require("lodash");
boost(Highcharts);

export default {
  name: "SerieschartVisualization",
  props: [
    "view",
    "visualization",
    "vw",
    "vh",
    "dataWatch",
    "metaWatch",
    "dashboard",
  ],
  data() {
    return {
      xfield: undefined,
      yfields: [],
      cdata: undefined,
      options: {},
    };
  },
  watch: {
    vw: {
      handler: function (val) {
        console.log(`setting width to ${val}`)
        this.$refs.chartbox.chart.update({
          chart: {
            width: val,
          },
        });
      },
    },
    vh: {
      handler: function (val) {
        this.$refs.chartbox.chart.update({
          chart: {
            height: val,
          },
        });
      },
    },
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
  mounted() {
    this.update();
  },

  methods: {
    update() {
      if (!this.visualization.xfield || !this.visualization.yfields) return;

      let viz = this.visualization;

      let series,
        seriesLipstick = {
          lineWidth: viz.lineWidth,
          marker: { enabled: false },
        };

      if (this.visualization.groupBy) {
        let dseriesMap = this.view.data.reduce((map, pt) => {
          let key = pt[this.visualization.groupBy];
          if (!map[key]) {
            map[key] = [];
          }
          map[key].push([pt[viz.xfield], pt[viz.yfields]]);
          return map;
        }, {});
        series = Object.entries(dseriesMap).map((kvp) => {
          return {
            name: kvp[0],
            data: kvp[1],
            ...seriesLipstick,
          };
        });
      } else {
        series = viz.yfields.map((yfield) => {
          return {
            name: yfield,
            data: this.view.data.map((row) => [row[viz.xfield], row[yfield]]),
            ...seriesLipstick,
          };
        });
      }

      this.options = {
        chart: {
          type: this.visualization.chartType.toLowerCase(),
          zoomType: "x",
          width: this.vw,
          height: this.vh,
          backgroundColor: "transparent",
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

        credits: {
          enabled: false,
        },

        title: {
          text: "",
        },

        subtitle: {
          text: "",
        },

        tooltip: {
          valueDecimals: 2,
        },

        xAxis: {
          type: viz.xIsDateTime ? "datetime" : undefined,
        },

        yAxis: {
          type: viz.yIsDateTime
            ? "datetime"
            : viz.yAxisLogarithmic
            ? "logarithmic"
            : undefined,
          tickInterval: viz.yAxisTickInterval,
          title: {
            text: "",
          },
        },

        series,

        legend: {
          enabled: viz.showLegend,
        },

        plotOptions: {
          series: {
            animation: false,
            cursor: "pointer",
            pointWidth: viz.pointWidth,
            states: {
              inactive: {
                opacity: 0.8,
              },
            },
            point: {
              events: {
                click: function () {
                  viz.eventOccured("bar-click", viz.view.data[this.index]);
                },
              },
            },
          },
          column: {
            minPointLength: 1,
          },
        },
      };
    },
  },
};
</script>
