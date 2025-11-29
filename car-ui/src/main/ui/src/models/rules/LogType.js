import { car } from "@/car.js";
import Rule from "./Rule.js";

let marshallers = {
  messagefinder: function (detector) {
    detector.msgs = detector.msgs.map((msg) => ({ msg }));
  },
};

let unMarshallers = {
  messagefinder: function (detector) {
    detector.msgs = detector.msgs.map((msg) => msg.msg);
  },
};

export default class LogType {
  constructor(group, typ) {
    this.group = group;
    this.name = typ.name || "";
    this.description = typ.description || "";
    this.mergeMultipleFiles = typ.mergeMultipleFiles || false;
    this.mergedFileName = typ.mergedFileName || "";
    this.structured = typ.structured;
    this.readConfig = typ.readConfig || { type: 'rulebased', readRules: [] };
    this.defaultSearchView = typ.defaultSearchView

    this.expandedIcon = "folder-open";
    this.collapsedIcon = "folder";
    this.iconcolor = "orange";
    this.getHrefParams = () => {
      return {
        logtypegroup: this.group,
        logtype: this.name,
        type: this
      }
    }
    this.href = {
      name: "logtypeeditor",
    };

    this.recordReader = typ.recordReader || { type: "singlelinerecordreader" };
    this.detectors = typ.detectors || [];
    this.detectors.forEach((detector) => {
      if (marshallers[detector.type]) marshallers[detector.type](detector);
    });

    this.children = [];

    this.getChildren = function () {
      this.children.splice(0, this.children.length);
      return car.getRules(group, typ.name).then(children => children.forEach(child => this.children.push(child)));
    };

    this.newChildNode = function () {
      return new Rule(group, typ.name, {
        name: "new rule",
        description: "new rule",
        type: car.getTypes()["rule"].subTypes[0],
      });
    };

    this.expanded = function (path) {
      path = decodeURI(path)
      let p = `/ui/logtypegroup/${this.group}/logtype/${this.name}`
      return path.startsWith(p) && path != p
    }
  }
  toJSON() {
    return { ...this, href: undefined }
  }
  save() {
    let x = JSON.parse(JSON.stringify(this));
    x.detectors.forEach((detector) => {
      if (unMarshallers[detector.type]) unMarshallers[detector.type](detector);
    });
    car.saveLogType(x);
  }
}
