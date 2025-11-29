<template>
  <div v-if="visualization.view.data && visualization.view.data.length > 0">
    <v-select
      dense
      outlined
      label="Chart type"
      class="my-4"
      :items="['Line', 'Area', 'Column', 'Bar']"
      v-model="visualization.chartType"
    >
    </v-select>

    <v-expansion-panels accordion>
      <v-expansion-panel>
        <v-expansion-panel-header>X-axis</v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-select
            dense
            outlined
            label="Field"
            class="my-1"
            :items="Object.keys(visualization.view.data[0])"
            v-model="visualization.xfield"
          >
          </v-select>
          <v-checkbox
            v-model="visualization.xIsDateTime"
            label="Is x a datetime axis?"
          ></v-checkbox>
        </v-expansion-panel-content>
      </v-expansion-panel>
      <v-expansion-panel>
        <v-expansion-panel-header>Y-axis</v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-combobox
            v-model="visualization.groupBy"
            :items="Object.keys(visualization.view.data[0])"
            label="Group by"
            chips
            dense
            small-chips
            outlined
            class="my-0"
          ></v-combobox>

          <v-combobox
            v-model="visualization.yfields"
            :items="Object.keys(visualization.view.data[0])"
            label="Pick fields to be plotted on y axis"
            :multiple="visualization.groupBy == undefined"
            chips
            outlined
            dense
            small-chips
            class="my-4"
          ></v-combobox>

          <div style="display: flex">
            <v-checkbox
              v-model="visualization.yIsDateTime"
              label="Date-time field"
              class="mr-4"
            ></v-checkbox>
            <v-checkbox
              v-model="visualization.yAxisLogarithmic"
              label="Logarithmic scale"
            ></v-checkbox>
          </div>
          <v-text-field
            dense
            outlined
            v-model="visualization.yAxisTickInterval"
            label="Tick interval"
          ></v-text-field>
        </v-expansion-panel-content>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-header>Chart properties</v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-switch
            class="my-1"
            :label="`Show legend`"
            v-model="visualization.showLegend"
          ></v-switch>

          <v-slider
            class="my-1"
            label="Line thickness"
            v-model="visualization.lineWidth"
            :thumb-size="20"
            thumb-label="always"
            :max="10"
            :min="1"
            ticks="always"
          ></v-slider>

          <v-select
            dense
            outlined
            label="Label field"
            class="my-1"
            :items="Object.keys(visualization.view.data[0])"
            v-model="visualization.labelField"
          >
          </v-select>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script>
export default {
  name: "SerieschartVisualizationEditor",
  props: ["visualization"],
};
</script>
