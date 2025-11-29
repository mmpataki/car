import Visualization from "@/models/visualizations/Visualization.js";

export default class DropdownVisualization extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = ["change"];
  }
  init() {
    //this.ctxt[this.ctxtKey] = this.defaultValue || "*";
  }
}
