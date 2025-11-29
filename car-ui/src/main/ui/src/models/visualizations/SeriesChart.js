import Visualization from "./Visualization.js";
export default class SeriesChart extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = ["bar-click", "range-select"];
  }
}
