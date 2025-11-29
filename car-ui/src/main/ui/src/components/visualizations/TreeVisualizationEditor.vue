<template>
  <div v-if="visualization.view.data" class="form-col">
    <v-select
      v-model="visualization.pathField"
      :items="fields"
      label="Path field:"
      dense
      outlined
      class="my-2"
    ></v-select>
    <v-text-field
      dense
      outlined
      v-model="visualization.separator"
      label="Delimiter"
      class="my-2"
    ></v-text-field>
    <v-select
      dense
      outlined
      v-model="visualization.labelField"
      :items="['none', ...fields]"
      label="Label field:"
      class="my-2"
    ></v-select>
    <v-select
      dense
      outlined
      v-model="visualization.urlField"
      :items="['none', ...fields]"
      label="URL field:"
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
