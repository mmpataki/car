<template>
  <div>
    <v-text-field
      label="Label"
      v-model="visualization.label"
      class="my-2"
    ></v-text-field>
    <v-select
      label="Label field name"
      v-model="visualization.labelKey"
      :items="keys"
      class="my-2"
    ></v-select>
  </div>
</template>

<script>
export default {
  name: "DropdownVisualizationEditor",
  props: ["visualization"],
  computed: {
    keys() {
      return this.visualization.view.data.length > 0
        ? Object.keys(this.visualization.view.data[0])
        : [];
    },
  },
};
</script>
