<template>
  <div>
    <strong
      ><small>{{ label }}</small></strong
    >

    <div style="display: flex; align-items: center">
      <small v-if="edit" style="flex-grow: 1"
        >Pick all the variables parts of this text (using mouse), we generate
        regular expressions based on these selections</small
      >
      <v-btn v-if="edit" x-small dark text color="primary" @click="editDone"
        ><strong>done</strong></v-btn
      >
      <v-btn v-if="edit" x-small dark text color="error" @click="editCancelled"
        ><strong>cancel</strong></v-btn
      >
    </div>

    <div
      style="margin: 0px; display: flex"
      class="my-3"
      v-if="txt"
    >
      <div style="border: solid 1px lightgrey; padding: 10px; width: 100%">
        <div
          @mouseup="selectionDone"
          style="
            white-space: pre-wrap;
            font-family: monospace;
            word-break: break-all;
            max-height: 300px;
            overflow-y: scroll;
          "
          v-html="highlighted"
        ></div>
      </div>
      <div
        v-if="!edit"
        style="display: flex; flex-direction: column; padding: 5px 0px"
      >
        <v-btn color="primary" x-small icon @click="edit = true">
          <v-icon small>mdi-pencil</v-icon>
        </v-btn>
        <v-btn color="primary" x-small icon @click="$emit('deleted')">
          <v-icon small>mdi-close</v-icon>
        </v-btn>
      </div>
    </div>
    <div
      v-if="edit"
      style="
        align-items: baseline;
        height: 350px;
        overflow: auto;
        margin-left: 0px;
      "
    >
      <div
        v-for="(v, idx) in selections"
        :key="idx + v.color"
        style="display: flex; align-items: center"
      >
        <popper
          v-if="v.inferred && !v.accepted"
          :options="{ placement: 'top' }"
        >
          <div
            trigger="hover"
            class="popper"
            style="
              text-align: left;
              max-width: 450px;
              padding: 5px;
              width: 250px;
            "
          >
            <span style="display: block"
              >This field was detected by <strong>CAR</strong>. You can accept
              it</span
            >
            <v-btn @click="accept(v)" color="success" small>Accept</v-btn>
          </div>
          <v-icon
            slot="reference"
            style="width: 15px; display: block"
            small
            color="yellow"
            >{{ v.inferred ? "mdi-lightbulb" : "" }}</v-icon
          >
        </popper>

        <field-editor
          class="my-5"
          :v="v"
          :hidetxt="false"
          @ignored="ignored($event, v)"
          @removed="remove(idx)"
          :style="{
            paddingLeft: '10px',
          }"
        ></field-editor>
      </div>
    </div>
  </div>
</template>

<script>
import upperFirst from "lodash/upperFirst";
import { car } from "@/car.js";
import FieldEditor from "../utils/FieldEditor.vue";

import Popper from "vue-popperjs";
import "vue-popperjs/dist/vue-popper.css";

let regexes = {};

export default {
  props: ["txt", "label", "handWrittenRegex", "exSelections", "editing"],
  components: { FieldEditor, popper: Popper },
  data() {
    return {
      selections: [],
      showregex: false,
      regex: ".*",
      edit: this.editing,
      editRegex: this.handWrittenRegex != undefined,
    };
  },
  computed: {
    highlighted() {
      let esc = (x) => x.replace(/</g, "&lt;").replace(/>/g, "&gt;");
      if (this.selections.length == 0) return esc(this.txt);
      let ret = "",
        last = 0;
      this.selections.forEach((sel) => {
        ret += esc(this.txt.substring(last, sel.start));
        if (this.edit || !sel.inferred) {
          ret += `<span title="${
            sel.ignore ? "ignored text" : sel.name
          }" style="background: ${
            sel.ignore ? "transparent" : sel.color
          }; text-decoration: ${
            sel.ignore ? "line-through" : ""
          }; padding: 0px 2px; border-radius: 3px">${esc(sel.txt)}</span>`;
        } else {
          ret += esc(sel.txt);
        }
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
    car.getRegexTokens().then((toks) => {
      regexes = toks;
      Object.values(regexes).forEach((x) =>
        Object.values(x).forEach((y) => (y.reg = new RegExp(y.regex, "g")))
      );
      this.setup();
    });
  },
  watch: {
    txt: function () {
      this.setup();
    },
  },
  methods: {
    accept(v) {
      v.accepted = true;
      this.emitRegex();
    },
    setup() {
      let txt = this.txt;
      this.selections = [];
      (this.exSelections || []).forEach((exsel) => {
        this.selections.push({
          ...exsel,
          txt: txt.substr(exsel.start, exsel.length),
          color: "hsl(" + Math.random() * 360 + ", 100%, 85%)",
        });
      });
      Object.entries(regexes).forEach((e) => {
        let tag = e[0];
        Object.entries(e[1]).forEach((tnr) => {
          let r = tnr[1],
            tagType = tnr[0],
            res;
          if (r.skipForScan) return;
          while ((res = r.reg.exec(txt))) {
            if (res.length < 2) break;
            res = res[1];
            let sel = {
              start: r.reg.lastIndex - res.length,
              length: res.length,
              color: "hsl(" + Math.random() * 360 + ", 100%, 85%)",
              type: { type: r.type + "ftype" },
              ignore: false,
              txt: res,
              name: r.varName,
              match: {
                ...r,
                type: tag,
              },
              regexTokenNames: [`${tag}/${tagType}`],
              inferred: true,
            };
            if (r.props) {
              r.props.forEach((prop) => {
                sel.type[prop.key] = prop.value;
              });
            }
            let intersections = this.intersect(sel);
            if (intersections.length == 0) {
              this.selections.push(sel);
            } else {
              let removed = 0;
              intersections.forEach((isect) => {
                if (isect.length < sel.length) {
                  this.selections.splice(this.selections.indexOf(isect), 1);
                  removed++;
                }
              });
              if (removed) {
                this.selections.push(sel);
              }
            }
          }
        });
      });

      if (this.selections.some((x) => x.inferred)) {
        car.info(
          "We have some field suggestions for you, accept them by hovering on the lighbulb icon beside them"
        );
      }
      this.emitRegex();
    },
    intersect(sel) {
      let isect = (sel1, sel2) => {
        sel1.end = sel1.start + sel1.length;
        sel2.end = sel2.start + sel2.length;
        console.log(
          `sel1: {${sel1.start} ${sel1.end}}, sel2: {${sel2.start}, ${sel2.end}}`
        );
        return (
          (sel2.start <= sel1.end && sel1.start <= sel2.start) ||
          (sel1.start <= sel2.end && sel2.start <= sel1.start)
        );
      };
      let x = this.selections.filter((x) => isect(x, sel));
      console.log(x);
      return x;
    },
    typeEditor(type) {
      return upperFirst(type + "Editor");
    },
    ignored(e, sel) {
      sel.color = e
        ? "lightgray"
        : "hsl(" + Math.random() * 360 + ", 100%, 75%)";
      this.emitRegex();
    },
    selectionDone() {
      if (!this.edit) return;
      let selection = document.all
        ? document.selection.createRange().text
        : document.getSelection();

      if (selection.anchorNode.parentNode.tagName.toLowerCase() != "div") {
        car.warn("Don't select text which is already a part of selection");
        return;
      }

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
        match = undefined,
        regexTokenNames = ["string/string"],
        inferred = false;

      /* figure out the type */
      Object.entries(regexes).forEach((e) => {
        let tag = e[0];
        Object.entries(e[1]).forEach((trn) => {
          let r = trn[1],
            tagName = trn[0];
          if (r.skipForScan) return;
          let res = r.reg.exec(text);
          if (res != null && res[0] == text) {
            regexTokenNames = [`${tag}/${tagName}`];
            type.type = r.type + "ftype";
            name = r.varName;
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
        regexTokenNames,
        inferred,
      };
      if (!this.intersect(sel).length > 0) {
        this.selections.push(sel);
        this.emitRegex();
      }
    },
    emitRegex() {
      this.selections.sort((a, b) => a.start - b.start);
      this.$emit("regex-changed", {
        selections: this.selections.filter((x) => {
          return !x.inferred || x.accepted;
        }),
      });
    },
    remove(i) {
      this.selections.splice(i, 1);
      this.emitRegex();
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
    editDone() {
      this.$emit("edit-done");
      this.edit = false;
      this.emitRegex();
    },
    editCancelled() {
      this.$emit("edit-done");
      if (this.exSelections) {
        this.selections = this.exSelections;
      }
      this.edit = false;
      this.emitRegex();
    },
  },
};
</script>

<style>
</style>