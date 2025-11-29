<template>
  <div>
    <div class="form form-row">
      <label>Font size:</label>
      <select v-model="visualization.fontSize">
        <option
          :key="s"
          v-for="s in [8, 10, 12, 14, 16, 24, 36, 48, 64, 72]"
          :value="s"
        >
          {{ s }}px
        </option>
      </select>
    </div>
    <div class="form form-row">
      <label>Monospace:</label>
      <input type="checkbox" v-model="visualization.monospace">
    </div>
  </div>
</template>

<script>
export default {
  name: "TextVisualizationEditor",
  props: ["visualization"],
};
</script>
