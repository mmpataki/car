import Visualization from "@/models/visualizations/Visualization.js";
import createViz from "@/models/visualizations/VisFactory.js";

export default class VisGeneratorVisualization extends Visualization {
  constructor(a = {}) {
    super(a);
    this.subtype = a.subtype || "table";
    if (!a.isGenerator) {
      this.childvisualization = createViz(this.subtype || "text", a.childvisualization || {});
    }
  }
}
