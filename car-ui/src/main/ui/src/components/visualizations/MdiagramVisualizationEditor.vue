<template>
  <v-select label="Diagram type" :items="mermaidTypes" v-model="visualization.diagramType"></v-select>
</template>

<script>
// import { eventHandlers } from "@/models/visualizations/Visualization.js";
export default {
  props: ["view", "visualization"],
  data() {
    return {
      mermaidTypes: [
        "flowchart TD",
        "flowchart LR",
        "sequenceDiagram",
        "pie"
      ]
    };
  },
};
</script>
