<template>
  <div class="form-col form">
    <div class="form-col form">
      <v-checkbox v-model="visualization.isGenerator" label="Is generator"></v-checkbox>
    </div>
    <div v-if="!generatedVisualization" class="form-col form">
      <label>Choose a visualization</label>
      <select @change="updateChildComponent" v-model="visualization.subtype">
        <option :key="ctyp.name" v-for="ctyp in charttypes" :value="ctyp.name">
          {{ ctyp.displayName }}
        </option>
      </select>
    </div>
    <div class="form-col form">
      <label>Choose a layout</label>
      <select v-model="visualization.flowType">
        <option value="row">Row</option>
        <option value="column">Column</option>
      </select>
      <component
        v-if="!generatedVisualization"
        :visualization="getChildVisualization"
        :is="visualization.subtype + 'VisualizationEditor'"
      ></component>
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
import createViz from "@/models/visualizations/VisFactory.js";
export default {
  name: "VisgeneratorVisualizationEditor",
  props: ["visualization"],
  data() {
    return { charttypes: car.getChartTypes() };
  },
  computed: {
    generatedVisualization() {
      return this.visualization.isGenerator;
    },
  },
  methods: {
    updateChildComponent(e) {
      this.visualization.childvisualization = createViz(e.target.value, {});
    },
    getChildVisualization() {
      let type = this.visualization.subtype;
      return createViz(type, {});
    },
  },
};
</script>
