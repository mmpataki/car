import { car } from "@/car.js";

let marshallers = {
  regex: function (rule) {
    rule.gnm = Object.values(rule.groupNameMap || {}).reduce((map, v, i) => {
      map[i] = v;
      return map;
    }, {});
    rule.examples = (rule.exampleTexts || []).map((t) => ({
      txt: t,
      html: "",
      groups: [],
    }));
  },

  regexv2: function () {

  }
};

let unMarshallers = {
  regex: function (rule) {
    rule.groupNameMap = Object.values(rule.gnm).reduce((map, grp, i) => {
      map[i + 1] = grp;
      return map;
    }, {});
    rule.exampleTexts = rule.examples.map((e) => e.txt);
  },
  regexv2: function () {

  }
};

function check(name, obj) {
  xassert(obj[name], `${name} is not set`);
}
function xassert(x, msg) {
  if (!x) {
    car.warn(msg);
    throw new Error(msg);
  }
}

export default class Rule {
  constructor(lgroup, ltype, rule) {
    this.lgroup = lgroup;
    this.ltype = ltype;
    this.name = rule.name;
    this.description = rule.description;

    this.expandedIcon = "tools";
    this.collapsedIcon = "tools";
    this.getHrefParams = () => {
      return {
        logtypegroup: lgroup,
        logtype: ltype,
        rulename: this.name,
        rule: this
      }
    }
    this.href = {
      name: "ruleeditor",
    };

    Object.keys(rule).forEach((k) => (this[k] = rule[k]));
    marshallers[rule.type](this);
  }
  toJSON() {
    return { ...this, href: undefined }
  }
  save() {
    unMarshallers[this.type](this);
    // verify
    check("name", this);
    check("description", this);
    check("length", this.exampleTexts);
    car.saveRule(this).then(() => car.success(`rule [${this.name}] is saved`));
  }
}
