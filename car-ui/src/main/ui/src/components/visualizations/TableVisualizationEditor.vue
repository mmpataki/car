<template>
  <div class="form-col form">
    <div class="form-col form">
      <v-checkbox v-model="visualization.keyValueDisplay" label="Is a key value display?"></v-checkbox>
    </div>
  </div>
</template>

<script>
export default {
  name: "TableVisualizationEditor",
  props: ['visualization']
};
</script>
