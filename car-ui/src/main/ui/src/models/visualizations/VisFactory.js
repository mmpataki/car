import Visualization from "@/models/visualizations/Visualization.js"
import SeriesChart from "@/models/visualizations/SeriesChart.js";
import TimelineChart from "@/models/visualizations/TimelineChart.js";
import TimeSelector from "@/models/visualizations/TimeSelector.js";
import DropdownVisualization from "@/models/visualizations/DropdownVisualization.js";
import VisGeneratorVisualization from "@/models/visualizations/VisGeneratorVisualization.js";
import TextinputVisualization from "@/models/visualizations/TextinputVisualization";
import LogMessages from "@/models/visualizations/LogMessages";

let visConstructors = {
  timelinechart: (a) => new TimelineChart({ ...a, type: "timelinechart" }),
  visgenerator: (a) => new VisGeneratorVisualization({ ...a, type: "visgenerator" }),
  dropdown: (a) => new DropdownVisualization({ ...a, type: "dropdown" }),
  serieschart: (a) => new SeriesChart({ ...a, type: "serieschart" }),
  textinput: (a) => new TextinputVisualization({ ...a, type: "textinput" }),
  logmessages: (a) => new LogMessages({...a, type: "logmessages"}),
  timeselector: (a) => new TimeSelector({...a, type: "timeselector"}),
};

export default function createViz(type, arg) {
  return visConstructors[type] ? visConstructors[type](arg) : new Visualization({ ...arg, type });
}
