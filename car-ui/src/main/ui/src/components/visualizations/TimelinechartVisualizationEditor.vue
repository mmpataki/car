<template>
  <div v-if="visualization.view.data" class="form-col">
    <v-select
      v-model="visualization.fromField"
      :items="fields"
      label="Start time field:"
      dense
      outlined
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.toField"
      :items="fields"
      label="End time field:"
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.grpByField"
      :items="fields"
      label="Group by:"
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.colorField"
      :items="['default', ...fields]"
      label="Color field:"
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.labelField"
      :items="['default', ...fields]"
      label="Label field:"
      class="my-2"
    ></v-select>

    <v-checkbox
      v-model="visualization.tooltipEnabled"
      label="Enable tooltip"
      dense
    ></v-checkbox>
    <v-checkbox
      v-model="visualization.xLabelEnabled"
      label="Enable x axis labels"
      dense
    ></v-checkbox>
    <v-checkbox
      v-model="visualization.yLabelEnabled"
      label="Enable y axis labels"
      dense
    ></v-checkbox>
    <v-slider
      class="my-4"
      label="Line thickness"
      v-model="visualization.pointWidth"
      :thumb-size="24"
      thumb-label="always"
      :max="100"
      :min="10"
      :step="10"
      ticks="always"
    ></v-slider>
  </div>
</template>

<script>
export default {
  name: "TimelinechartVisualizationEditor",
  props: ["visualization"],
  computed: {
    fields() {
      return this.visualization.view.data.length > 0
        ? Object.keys(this.visualization.view.data[0])
        : [];
    },
  },
};
</script>
