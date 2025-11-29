import Visualization from "@/models/visualizations/Visualization.js";

export default class TextinputVisualization extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = ["change"];
    this.dataIndependent = true;
  }
  init() {
    this.eventOccured('change', this.defaultValue)
  }
}
