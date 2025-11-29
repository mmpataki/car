import { car } from "@/car.js";
import LogType from "./LogType";

export default class LogTypeGroup {
  constructor(grp) {
    this.name = grp.name;
    this.description = grp.description;
    this.expandedIcon = "folder-open";
    this.collapsedIcon = "folder";
    this.getHrefParams = () => {
      return {
        logtypegroup: this.name,
        group: this
      }
    }
    this.href = {
      name: "lgroupeditor"
    };
    this.editorComponent = "lgroup-editor";
    this.children = [];
    this.getChildren = function () {
      this.children.splice(0, this.children.length);
      return car.getLogTypes(this.name).then(children => children.forEach(child => this.children.push(child)));
    };
    this.newChildNode = function () {
      return new LogType(this.name, {
        name: "new logtype",
        description: "new log type",
        detectors: [],
        recordReader: { type: "singlelinerecordreader" },
      });
    };
    this.expanded = function (path) {
      return path.startsWith(`/ui/logtypegroup/${this.name}`)
    }
  }
  toJSON() {
    return { ...this, href: undefined }
  }
}
