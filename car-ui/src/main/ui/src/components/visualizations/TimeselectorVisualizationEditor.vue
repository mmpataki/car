<template>
  <div v-if="visualization.view.data" class="form-col">
    <v-select
      v-model="visualization.startIndicatorField"
      :items="fields"
      label="Start time field:"
      dense
      outlined
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.endIndicatorField"
      :items="fields"
      label="End time field:"
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.displayMode"
      :items="['Chart', 'Time picker']"
      label="Display mode"
      class="my-2"
    ></v-select>
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
