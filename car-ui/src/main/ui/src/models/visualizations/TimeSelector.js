import Visualization from "./Visualization.js";
export default class TimeSelector extends Visualization {
  constructor(a) {
    super(a);
    this.eventNames = ["timeframe-select"];
  }
}
