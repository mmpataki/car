<template>
  <div>
    <strong
      ><small>{{ label }}</small></strong
    >
    <div
      style="padding: 10px; border: solid 1px lightgrey; margin: 0px"
      v-if="txt"
    >
      <div>
        <div
          @mouseup="selectionDone"
          style="
            white-space: pre-wrap;
            font-family: monospace;
            word-break: break-all;
          "
          v-html="highlighted"
        ></div>
      </div>
    </div>
    <div style="margin: 10px 0px">
      <v-switch
        v-model="showregex"
        dense
        :label="`${showregex ? 'Hide' : 'Show'} regex`"
      ></v-switch>
      <div style="display: flex" v-if="!editRegex && showregex">
        <pre
          class="wrapped"
          style="
            border: solid 1px lightgray;
            padding: 10px;
            flex-grow: 1;
            width: calc(100% - 25px);
          "
          >{{ regex }}</pre
        >
        <v-btn small icon
          ><v-icon small @click="(editRegex = true), (handWrittenRegex = regex)"
            >mdi-pencil</v-icon
          ></v-btn
        >
      </div>
      <div style="display: flex" v-if="showregex && editRegex">
        <v-textarea
          outlined
          :value="handWrittenRegex"
          @input="regexUpdated"
        ></v-textarea>
        <v-btn small icon
          ><v-icon small @click="removeRegexEditor"
            >mdi-close-circle-outline</v-icon
          ></v-btn
        >
      </div>
    </div>
    <div style="align-items: baseline; height: 350px; overflow: auto">
      <field-editor
        v-for="(v, idx) in selections"
        :key="idx + v.color"
        class="my-5"
        :v="v"
        :hidetxt="false"
        @ignored="ignored($event, v)"
        @removed="remove(idx)"
      ></field-editor>
    </div>
  </div>
</template>

<script>
import upperFirst from "lodash/upperFirst";
import { car } from "@/car.js";
import FieldEditor from "../utils/FieldEditor.vue";

let type2RegexGroups = {
  numberftype: "[-+]?[0-9]*[.]?[0-9]+",
  stringftype: ".*?",
  dateftype: ".*?",
};

let regexes = {
  ts: [
    {
      reg: /(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3})/g,
      props: [{ key: "format", value: "yyyy-MM-dd HH:mm:ss,SSS" }],
      label: "yyyy-dd-mm hh:mm:ss,zzz",
      type: "date",
    },
    {
      reg: /(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3})/g,
      props: [{ key: "format", value: "yyyy-MM-dd HH:mm:ss,SSS" }],
      label: "yyyy-dd-mm hh:mm:ss,zzz",
      type: "date",
    },
  ],
  loglevel: [
    {
      reg: /(INFO|ALL|WARN|DEBUG|TRACE|ERROR|FATAL)/g,
      label: "Log level",
      type: "string",
    },
  ],
  filepath: [
    {
      reg: /([a-zA-Z0-9_]?(\/[a-zA-Z0-9_]+)+\/?)/g,
      label: "Unix path",
      type: "string",
    },
  ],
  classname: [
    {
      reg: /((?:[a-zA-Z0-9]*\.)+[$_a-zA-Z0-9]+)/g,
      label: "Class name",
      type: "string",
    },
  ],
};

export default {
  props: ["txt", "label", "handWrittenRegex", "groupNames"],
  components: { FieldEditor },
  data() {
    return {
      selections: [],
      showregex: false,
      regex: ".*",
      editRegex: this.handWrittenRegex != undefined,
    };
  },
  computed: {
    highlighted() {
      if (this.selections.length == 0) return this.txt;
      let ret = "",
        last = 0;
      let esc = (x) => x.replace(/</g, "&lt;").replace(/>/g, "&gt;");
      this.selections.forEach((sel) => {
        ret += esc(this.txt.substring(last, sel.start));
        ret += `<span style="background: ${
          sel.color
        }; padding: 0px 2px; border-radius: 3px">${esc(sel.txt)}</span>`;
        last = sel.start + sel.length;
      });
      ret += esc(this.txt.substring(last));
      return ret;
    },
    ftypes() {
      return car.getFTypes().map((ftyp) => ({
        value: ftyp.name,
        text: ftyp.displayName,
      }));
    },
  },
  created() {
    if (this.handWrittenRegex) {
      let findIfExists = (i) => this.groupNames[i];
      car
        .regexMatch(this.handWrittenRegex, [this.txt])
        .then((x) => {
          x[0].groups.forEach((grp, i) => {
            let old = findIfExists(i + 1);
            let sel = {
              start: grp.begin,
              length: grp.end - grp.begin,
              color:
                old && old.color
                  ? old.color
                  : "hsl(" + Math.random() * 360 + ", 100%, 85%)",
              type: old ? old.type : { type: "stringftype" },
              ignore: false,
              txt: grp.txt,
              name: old ? old.name : "",
            };
            this.selections.push(sel);
          });
        })
        .catch((e) => {
          console.log(e);
        });
    } else {
      this.update();
    }
  },
  watch: {
    txt: function () {
      this.update();
    },
  },
  methods: {
    regexUpdated(x, groupNames) {
      this.regex = x;
      let oldSelections = groupNames || this.selections;
      this.selections = [];
      console.log("cleared");
      let findIfExists = (start) =>
        oldSelections.filter((sel) => sel.start == start)[0];
      car.regexMatch(this.handWrittenRegex, [this.txt]).then((x) => {
        x[0].groups.forEach((grp) => {
          let old = findIfExists(grp.begin);
          let sel = {
            start: grp.begin,
            length: grp.end - grp.begin,
            color: old
              ? old.color
              : "hsl(" + Math.random() * 360 + ", 100%, 85%)",
            type: old ? old.type : { type: "stringftype" },
            ignore: false,
            txt: grp.txt,
            name: old ? old.name : "",
          };
          this.selections.push(sel);
        });
        this.emitRegex(this.regex);
      });
    },
    update() {
      this.selections = [];
      let txt = this.txt;
      Object.entries(regexes).forEach((e) => {
        let tag = e[0];
        let regexset = e[1];
        console.log(tag, regexset);
        regexset.forEach((r) => {
          let res;
          while ((res = r.reg.exec(txt))) {
            console.log(res);
            res = res[1];
            let sel = {
              start: r.reg.lastIndex - res.length,
              length: res.length,
              color: "hsl(" + Math.random() * 360 + ", 100%, 85%)",
              type: { type: r.type + "ftype" },
              ignore: false,
              txt: res,
              name: tag,
              match: {
                ...r,
                type: tag,
              },
            };
            console.log(r.props);
            if (r.props) {
              r.props.forEach((prop) => {
                console.log(prop);
                sel.type[prop.key] = prop.value;
              });
            }
            this.selections.push(sel);
          }
        });
      });
      this.regex = this.buildRegex();
    },
    removeRegexEditor() {
      if (
        this.regex != this.handWrittenRegex &&
        !confirm("The regex has been modified, do you want to continue")
      ) {
        return;
      }
      this.editRegex = false;
    },
    typeEditor(type) {
      return upperFirst(type + "Editor");
    },
    ignored(e, sel) {
      sel.color = e
        ? "lightgray"
        : "hsl(" + Math.random() * 360 + ", 100%, 75%)";
      this.regex = this.buildRegex();
    },
    selectionDone() {
      let selection = document.all
        ? document.selection.createRange().text
        : document.getSelection();
      let text = selection.toString();
      if (text.length == 0 || text.trim().length == 0) return;
      let start = selection.baseOffset,
        nodes = selection.baseNode.parentElement.childNodes;
      for (let i = 0; i < nodes.length && nodes[i] != selection.baseNode; i++) {
        start += nodes[i].textContent.length;
      }
      if (this.txt.substr(start, text.length) != selection.toString()) {
        start -= text.length;
      }

      let type = { type: "stringftype" },
        name = undefined,
        match = undefined;

      /* figure out the type */
      console.log(`>>${text}<< ${text.length}`);
      Object.entries(regexes).forEach((e) => {
        let tag = e[0];
        let regexset = e[1];
        regexset.forEach((r) => {
          if (r.reg.test(text)) {
            type.type = r.type + "ftype";
            name = tag;
            if (r.props) {
              r.props.forEach((prop) => {
                type[prop.key] = prop.value;
              });
            }
            match = { type: tag, ...r };
          }
          r.reg.lastIndex = 0;
        });
      });

      let sel = {
        name,
        txt: text,
        ignore: false,
        type,
        start: start,
        length: selection.toString().length,
        color: "hsl(" + Math.random() * 360 + ", 100%, 75%)",
        match,
      };
      this.selections.push(sel);
      this.regex = this.buildRegex();
    },
    buildRegex() {
      this.selections.sort((a, b) => a.start - b.start);
      let txt = this.txt;
      if (this.selections.length == 0) return this.escape(txt);
      let ret = "",
        last = 0;
      this.selections.forEach((sel) => {
        ret += this.escape(txt.substring(last, sel.start));
        let grpRegex = sel.match
          ? sel.match.reg.source
          : sel && sel.type && sel.type.type && type2RegexGroups[sel.type.type]
          ? sel.start + sel.length != txt.length
            ? type2RegexGroups[sel.type.type]
            : ".*"
          : ".*";
        ret += sel.ignore || sel.match ? grpRegex : `(${grpRegex})`;
        last = sel.start + sel.length;
        if (txt[last] == "\n" || txt[last] == "\r") {
          last++;
        }
      });
      ret += this.escape(txt.substring(last));
      this.emitRegex(ret);
      return ret;
    },
    emitRegex(regex) {
      this.$emit("regex-changed", {
        regex,
        groupNameMap: this.selections
          .filter((v) => !v.ignore)
          .reduce((map, sel, i) => {
            map[i + 1] = sel;
            return map;
          }, {}),
      });
    },
    remove(i) {
      this.selections.splice(i, 1);
      this.buildRegex();
    },
    escape(s) {
      let ret = "";
      for (let i = 0; i < s.length; i++) {
        let nchars =
          "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 \t\n";
        if (!nchars.includes(s[i])) ret += "\\";
        if (s[i] == "\n" || s[i] == "\r") {
          ret += "$";
        } else if (s[i] == " " || s[i] == "\t") {
          if ((i != 0 && s[i - 1] == " ") || s[i - 1] == "\t") continue;
          ret += "\\s+";
        } else {
          ret += s[i];
        }
      }
      return ret;
    },
  },
};
</script>

<style>
</style>