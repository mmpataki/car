<template>
  <v-stepper v-model="markerStep" height="100%" class="ma-3">
    <v-stepper-header>
      <v-stepper-step editable :complete="markerStep > 1" step="1">
        Pick a dataset
      </v-stepper-step>

      <v-divider></v-divider>

      <v-stepper-step
        :editable="markerStep > 2"
        :complete="markerStep > 2"
        step="2"
      >
        Pick a log message
      </v-stepper-step>

      <v-divider></v-divider>

      <v-stepper-step
        :editable="markerStep > 3"
        :complete="markerStep > 3"
        step="3"
      >
        Define the rule
      </v-stepper-step>
    </v-stepper-header>

    <v-stepper-items style="height: calc(100% - 72px)">
      <v-stepper-content style="height: 100%" :step="1">
        <dset-search-view
          @picked="
            (ds) => {
              dataset = ds.id;
              markerStep = 2;
            }
          "
        ></dset-search-view>
      </v-stepper-content>

      <v-stepper-content style="height: 100%" :step="2">
        <LogBrowser
          v-if="dataset"
          :showsearch="false"
          :query="query"
          @log-message-selected="messageSelected"
          style="height: 100%"
          :dsetid="dataset"
          :key="dataset"
          :black-listed-fields="[
            '_msg',
            '_rule',
            '_line',
            '_logtypgrp',
            '_logtyp',
            '_file',
            '_version_',
          ]"
          :get-all-fields="true"
        ></LogBrowser>
      </v-stepper-content>

      <v-stepper-content style="height: 100%; overflow: scroll" :step="3">
        <div style="height: 100%; overflow: scroll; padding-right: 10px">
          <small>
            Set
            <v-btn color="primary" x-small text @click="addToRule = false"
              ><b>a name</b></v-btn
            >
            for this rule or
            <v-btn color="primary" x-small text @click="addToRule = true"
              ><b>add</b></v-btn
            >
            this message to an existing rule. This rule name will be used to
            query the extracted data. The name should consist of [A-Za-z0-9_]
            chars and can be max 24 chars long in length</small
          >
          <div v-if="addToRule" class="my-3">
            <v-select
              v-model="rule"
              :items="availableRules"
              outlined
              dense
              label="Pick an existing rule"
            ></v-select>
          </div>
          <div v-else class="my-3">
            <v-text-field
              v-model="rule.name"
              :counter="24"
              :rules="nameRules"
              label="Name"
              required
              outlined
              dense
              class="py-2"
            ></v-text-field>
            <v-textarea
              class="my-4"
              outlined
              label="Description"
              rows="3"
              v-model="rule.description"
            ></v-textarea>
          </div>
          <div class="my-5" style="overflow: scroll">
            <strong
              ><small
                >We found below fields in the selected message. You can extract
                the whole field or select parts of it for extraction. Once done,
                click the save button</small
              ></strong
            >
            <example-editor
              :key="exampleEditorKey"
              v-if="Object.keys(example.txt).length"
              :example="example"
            ></example-editor>
          </div>

          <v-divider></v-divider>

          <div class="my-5">
            <v-btn color="primary" @click="save">save</v-btn>
            <v-btn color="error" text @click="markerStep = 2"> Back </v-btn>
          </div>
        </div>
      </v-stepper-content>
    </v-stepper-items>
  </v-stepper>
</template>

<script>
import { car } from "@/car.js";
import LogBrowser from "@/components/visualizations/LogBrowser.vue";
import ExampleEditor from "./ExampleEditor.vue";
import DsetSearchView from "../../views/DsetSearchView.vue";

function check(name, obj) {
  xassert(obj[name], `${name} is not set`);
}
function xassert(x, msg) {
  if (!x) {
    car.warn(msg);
    throw new Error(msg);
  }
}
function validateName(x, fMsg) {
  console.log(x);
  xassert(x.match(/^[A-Za-z_][A-Za-z0-9_]+$/) != null, fMsg);
}

export default {
  name: "RuleBuildingWizard",
  props: ["logtype"],
  components: {
    LogBrowser,
    ExampleEditor,
    DsetSearchView,
  },
  data() {
    return {
      msg: null,
      name: "",
      description: "",
      markerStep: 1,
      datasets: [],
      dataset: undefined,

      addToRule: false,
      availableRules: [],

      rule: this.newRule(),

      example: { txt: {}, selections: {}, fieldConfigs: {} },
      exampleEditorKey: 0,

      query: [
        {
          key: "_logtyp",
          val: this.logtype.name,
          op: "is",
          readonly: true,
        },
        {
          key: "_logtypgrp",
          val: this.logtype.group,
          op: "is",
          readonly: true,
        },
      ],
    };
  },
  created() {
    car.getDatasets().then((x) => (this.datasets = x));
    car
      .getRules(this.logtype.group, this.logtype.name)
      .then(
        (rules) =>
          (this.availableRules = rules.map((x) => ({ text: x.name, value: x })))
      );
  },
  methods: {
    newRule() {
      return {
        type: "regexv2",
        lgroup: this.logtype.group,
        ltype: this.logtype.name,
        name: "",
        description: "",
        exampleTexts: [],
      };
    },

    save() {
      let rule = { ...this.rule };

      // verify
      check("name", rule);
      check("description", rule);
      [this.example].forEach((ex) => {
        xassert(Object.keys(ex.txt).length, `example has no fields`);
        Object.values(ex.selections).forEach((sels) => {
          if (!Array.isArray(sels)) return;
          sels.forEach((f) => {
            xassert(f.name, `${f.txt} is not named`);
            validateName(
              f.name,
              `selection name can be made up of alphanumeric characters and _ and should not begin with numbers`
            );
          });
        });
      });
      validateName(
        rule.name,
        `rule name can be made up of alphanumeric characters and _ and should not begin with numbers`
      );
      rule.exampleTexts.push(this.example);
      car
        .saveRule(rule)
        .then(() => car.success("saved the rule"))
        .finally(() => {
          this.markerStep = 2;
          this.example = { txt: {}, selections: {}, fieldConfigs: {} };
          this.rule = this.newRule();
        });
    },

    messageSelected(msg) {
      [
        "_msg",
        "_rule",
        "_line",
        "_logtypgrp",
        "_logtyp",
        "_file",
        "_version_",
        "dset",
        "id",
      ].forEach((f) => {
        delete msg[f];
      });
      this.example.txt = msg;
      this.markerStep = 3;
      this.exampleEditorKey++;

      // car
      //   .searchLog(this.dataset, car.currentUid(), {
      //     pageSize: 10,
      //     query: {
      //       _logtyp: `"${this.logtype.name}"`,
      //       _logtypgrp: `"${this.logtype.group}"`,
      //       _msg: msg.replaceAll(/[^A-Za-z]+/g, " ").trim(),
      //     },
      //     fields: ["_file", "_line", "_msg"],
      //   })
      //   .then((result) => {
      //     console.log(result);
      //     var ed = require("edit-distance");
      //     var insert, remove, update;
      //     insert = remove = function () {
      //       return 1;
      //     };
      //     update = function (stringA, stringB) {
      //       return stringA !== stringB ? 1 : 0;
      //     };

      //     let error = 0.3,
      //       msgs = result.msgs,
      //       related = [msgs[0]];

      //     for (let i = 1; i < msgs.length; i++) {
      //       const x1 = msgs[i - 1],
      //         x2 = msgs[i];
      //       if (Math.pow(x1._score - x2._score, 2) > error) {
      //         break;
      //       }
      //       related.push(x2);
      //     }

      //     console.log(related);

      //     // let out = [];
      //     console.log(msg);
      //     related.forEach((res) => {
      //       let rtxt = res._msg;

      //     });
      //   });
    },
  },
};
</script>

<style scoped>
.v-data-table-header th {
  white-space: nowrap;
}
.v-stepper__wrapper {
  height: 100% !important;
  overflow: auto;
}
.wrapped {
  white-space: pre-wrap; /* Since CSS 2.1 */
  white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
  white-space: -pre-wrap; /* Opera 4-6 */
  white-space: -o-pre-wrap; /* Opera 7 */
  word-wrap: break-word; /* Internet Explorer 5.5+ */
}
.unselectable {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
</style>