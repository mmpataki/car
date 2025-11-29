<template>
  <div
    style="display: flex; flex-direction: column; height: 100%; overflow: auto"
  >
    total: {{ total }}<br />
    conversion attempted: {{ converts }}<br />
    updated: {{ updated }}<br />
    failed: {{ failed }}<br />
    skipped: {{ skipped }}<br />
    regexUpdate: {{ regexUpdate }}<br />
  </div>
</template>

<script>
import { car } from "@/car.js";
export default {
  data() {
    return {
      total: 0,
      failed: 0,
      converts: 0,
      skipped: 0,
      updated: 0,
      regexUpdate: 0,
    };
  },
  created() {
    console.log("starting up");
    car.getRegexTokens().then((regexes) => {
      Object.values(regexes).forEach((grp) =>
        Object.values(grp).forEach((r) => (r.reg = new RegExp(r.regex, "g")))
      );
      car.getLogGroupTypes().then((lgts) => {
        lgts.forEach((lgt) => {
          car.getLogTypes(lgt.name).then((lts) => {
            lts.forEach((lt) => {
              car.getRules(lgt.name, lt.name).then((rules) => {
                rules.forEach((rule) => {
                  this.total++;
                  if (rule.type != "regexv2") {
                    this.converts++;
                    this.convert([rule]).then((newRule) => {
                      let state = this.verify(newRule, rule);
                      this.failed += state ? 0 : 1;
                      car.saveRule(newRule).then(() => this.updated++);
                    });
                  } else {
                    if (
                      rule.exampleTexts.some((ex) =>
                        ex.selections.some((sel) => sel.regexTokenGroup)
                      )
                    ) {
                      this.skipped++;
                    } else {
                      console.log(rule);
                      rule.exampleTexts.forEach((ex) => {
                        ex.selections.forEach((sel) => {
                          if (sel.regexTokenGroup && sel.regexTokenName) return;
                          let txt = ex.txt.substr(sel.start, sel.length);
                          (sel.regexTokenGroup = "string"),
                            (sel.regexTokenName = "string");
                          Object.entries(regexes).forEach((e) => {
                            let tag = e[0];
                            Object.entries(e[1]).forEach((tnr) => {
                              let r = tnr[1],
                                tagType = tnr[0],
                                res;
                              if (
                                r.skipForScan ||
                                r.type + "ftype" != sel.type.type
                              )
                                return;
                              console.log(r.type, r.regex, txt);

                              if ((res = r.reg.exec(sel.txt))) {
                                if (res[0] != txt) return;
                                sel.regexTokenGroup = tag;
                                sel.regexTokenName = tagType;
                              }
                            });
                          });
                        });
                      });
                    }
                  }
                });
              });

              this.convert(lt.readRules).then((newRule) => {
                lt.readRules = [newRule];
                car.saveLogType(lt);
              });
            });
          });
        });
      });
    });
  },
  methods: {
    verify(x, y) {
      let xassert = (b, m) => {
        if (!b) {
          console.log(b);
          throw m;
        }
      };
      let passert = (key, obj, expected, debugKey) => {
        xassert(
          obj[key] != expected,
          `obj (${debugKey || ""}) has no ${key} defined`
        );
      };

      try {
        passert("name", x);
        passert("description", x);
        passert("type", x);
        passert("exampleTexts", x);

        x.exampleTexts.forEach((ex) => {
          passert("txt", ex);
          ex.selections.forEach((sel) => {
            passert("name", sel);
            passert("type", sel);
            passert("regexTokenNames", sel);
            passert("regexTokenGroup", sel);
            passert("start", sel);
            passert("length", sel);
          });
        });
      } catch (e) {
        console.log(`verifying failed with error [${e}]`, x, y);
        return false;
      }
      return true;
    },
    convert(xins) {
      let proms = xins.map((xin) =>
        car.regexMatchV2(xin.pattern, xin.exampleTexts).then((x) => {
          console.log(x);
          let out = {
            name: xin.name,
            description: xin.description,
            lgroup: xin.lgroup,
            ltype: xin.ltype,
            exampleTexts: [],
            type: "regexv2",
          };
          x.forEach((ex) => {
            out.exampleTexts.push({
              txt: ex.txt,
              selections: ex.groups.map((match, idx) => {
                let sel = xin.groupNameMap[idx + 1];
                return {
                  name: sel.name,
                  start: match.begin,
                  length: match.end - match.begin,
                  type: sel.type,
                  ...this.deduceRTGN(sel),
                };
              }),
            });
          });
          return out;
        })
      );
      return Promise.allSettled(proms).then((outs) => {
        console.log(outs);
        outs.forEach((out, idx) => {
          if (idx == 0) return;
          out = out.value;
          outs[0].value.exampleTexts.push(out.exampleTexts[0]);
        });
        return outs[0].value;
      });
    },
    deduceRTGN(sel) {
      let x = (a, b) => ({ regexTokenNames: b, regexTokenGroup: a });
      switch (sel.type.type) {
        case "stringftype":
          return x("string", ["string"]);
        case "dateftype":
          return x("ts", [sel.type.format]);
        case "numberftype":
          return x("number", ["integer", "float"]);
        case "jsonftype":
          return x("json", ["json"]);
      }
    },
  },
};
</script>

<style>
</style>