import Visualization from "@/models/visualizations/Visualization.js";

export default class TimelineChart extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = ["bar-click", "range-select"];
  }
}
