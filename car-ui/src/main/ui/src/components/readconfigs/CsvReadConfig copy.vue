<template>
  <div style="position: relative">
    <div style="display: flex; width: 100%">
      <span style="flex-grow: 1"></span>
      <v-btn
        v-if="!editingExample"
        small
        text
        @click="(editingExample = true), (readRuleStep = 3)"
        >Edit</v-btn
      >
      <v-btn v-if="editingExample" small text @click="editingExample = false"
        >Done</v-btn
      >
      <v-btn small text @click="resetReadConfig()">Reset</v-btn>
    </div>

    <div v-if="!editingExample">
      <v-card
        v-if="type.readConfig.examples"
        style="padding: 10px; position: relative; margin: 10px 0px"
      >
        <div
          style="
            white-space: pre-wrap;
            font-family: monospace;
            padding: 10px 0px;
            font-size: 0.85em;
          "
          v-text="formattedMsg"
        ></div>
      </v-card>
    </div>
    <div v-if="editingExample || !type.readConfig.fieldMappings.length">
      <v-stepper v-model="readRuleStep">
        <v-stepper-header>
          <v-stepper-step
            :editable="type.readConfig.fieldMappings.length == 0"
            :complete="
              file != undefined || type.readConfig.fieldMappings.length != 0
            "
            step="1"
          >
            Drop a sample log file
          </v-stepper-step>

          <v-divider></v-divider>

          <v-stepper-step
            :editable="type.readConfig.fieldMappings.length == 0"
            :complete="this.type.readConfig.examples.length > 0"
            step="2"
          >
            Configure CSV reader
          </v-stepper-step>

          <v-divider></v-divider>

          <v-stepper-step :editable="readRuleStep > 2" step="3"
            >Map the fields</v-stepper-step
          >
        </v-stepper-header>

        <v-stepper-items>
          <v-stepper-content step="1">
            <div
              class="sample-log-file-dropzone"
              @dragleave="$event.target.style.border = 'dashed 1px lightgray'"
              @drop="fileDropped($event)"
              @dragover="
                $event.preventDefault(),
                  ($event.target.style.border = 'dashed 1px red')
              "
            >
              Drop a sample file here
            </div>
          </v-stepper-content>

          <v-stepper-content step="2">
            <strong>Configure the CSV reader</strong>

            <v-checkbox
              label="First row header"
              v-model="type.readConfig.firstRowHeader"
            ></v-checkbox>

            <v-text-field
              outlined
              dense
              label="Separator"
              v-model="type.readConfig.separator"
              class="ma-2"
            ></v-text-field>

            <v-text-field
              outlined
              dense
              label="Quote character"
              v-model="type.readConfig.quoteChar"
              class="ma-2"
            ></v-text-field>

            <v-btn @click="parse"> Next </v-btn>
          </v-stepper-content>

          <v-stepper-content step="3">
            <div v-if="this.type.readConfig.examples.length > 0">
              <b>Map the fields</b>
              <div style="width: 100%; overflow: auto">
                <table style="border-collapse: collapse" border="1">
                  <tr>
                    <th style="background: #eee" v-for="key in keys" :key="key">
                      {{ key }}
                    </th>
                  </tr>
                  <tr
                    v-for="(row, idx) in this.type.readConfig.examples"
                    :key="idx"
                  >
                    <td v-for="key in keys" :key="key">
                      {{ row[key] }}
                    </td>
                  </tr>
                </table>
              </div>
              <div style="margin: 10px 0px">
                <b>Formatted text (this will be used to mark the fields)</b>
                <div
                  style="
                    white-space: pre-wrap;
                    font-family: monospace;
                    word-break: break-all
                  "
                  v-text="formattedMsg"
                ></div>
              </div>
              <div>
                <div
                  v-for="(fm, idx) in type.readConfig.fieldMappings"
                  :key="idx"
                  style="display: flex; align-items: center"
                >
                  <b style="display: block; min-width: 250px">{{ fm.first }}</b>
                  <field-editor
                    class="my-1"
                    :v="fm.second"
                    :hidetxt="false"
                    :hideRemovebtn="true"
                  ></field-editor>
                  <div>
                    <v-btn icon small @click="move(idx, -1)"
                      ><v-icon>mdi-chevron-up</v-icon></v-btn
                    >
                    <v-btn icon small @click="move(idx, +1)"
                      ><v-icon>mdi-chevron-down</v-icon></v-btn
                    >
                  </div>
                </div>
              </div>
            </div>
          </v-stepper-content>
        </v-stepper-items>
      </v-stepper>
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
import Papa from "papaparse";
import FieldEditor from "../utils/FieldEditor.vue";
export default {
  name: "CsvReadConfig1",
  components: { FieldEditor },
  props: ["type"],
  data() {
    return {
      ruleKey: 0,
      editingExample: false,
      readRuleStep: 1,
      file: undefined,
    };
  },

  created() {
    if (!this.type.readConfig || this.type.readConfig.type != "csv")
      this.$set(this.type, "readConfig", {
        type: "csv",
        fieldMappings: [],
        separator: ",",
        quoteChar: '"',
        firstRowHeader: true,
      });
  },

  computed: {
    formattedMsg() {
      return this.type.readConfig.examples
        .map((msg) =>
          this.type.readConfig.fieldMappings
            .filter((fm) => !fm.second.ignore)
            .map((fm) => msg[fm.first] + " ")
            .join(" ")
        )
        .join("\n\n");
    },

    keys() {
      return Object.keys(this.type.readConfig.examples[0]).filter(
        (k) => k && k != ""
      );
    },
  },

  //9448955210
  methods: {
    move(idx, dir) {
      let arr = this.type.readConfig.fieldMappings;
      if (idx + dir > -1 && idx + dir < arr.length) {
        let tmp = arr[idx];
        this.$set(arr, idx, arr[idx + dir]);
        this.$set(arr, idx + dir, tmp);
      }
    },
    resetReadConfig() {
      this.type.readConfig.fieldMappings = {};
      this.editingExample = true;
    },
    fileDropped(ev) {
      console.log("file dropped");
      ev.preventDefault();
      if (ev.dataTransfer.items) {
        if (ev.dataTransfer.items[0].kind === "file")
          this.file = ev.dataTransfer.items[0].getAsFile();
      } else {
        this.file = ev.dataTransfer.files[0];
      }
      this.readRuleStep = 2;
    },
    parse() {
      let rc = this.type.readConfig;
      this.parsing = true;
      Papa.parse(this.file, {
        header: rc.firstRowHeader,
        delimiter: rc.delimiter,
        quoteChar: rc.quoteChar,
        escapeChar: '"',
        preview: 2,

        complete: (results) => {
          console.log(results);
          if (!results.data.length) {
            let x = results.errors[0];
            car.error(
              `Error while parsing (${x.type}/{x.code}) ${x.message} at ${x.row}:${x.index}`
            );
            return;
          }

          if (rc.firstRowHeader) {
            this.type.readConfig.examples = results.data;
          } else {
            this.type.readConfig.examples = results.data.map((row) =>
              row.reduce((a, k, i) => {
                a["col-" + i] = k;
                return a;
              }, {})
            );
          }

          this.keys.forEach((key) => {
            if (rc.fieldMappings.filter((fm) => fm.first == key).length) return;

            rc.fieldMappings.push({
              first: key,
              second: {
                name: key.replaceAll(/[^a-zA-Z0-9]+/g, "_"),
                ignore: false,
                regexTokenNames: ["string/string"],
                type: {
                  type: "stringftype",
                },
                regexTokenNames: [],
              },
            });
          });

          this.readRuleStep = 3;
        },
      });
    },
  },
};
</script>

<style scoped>
.sample-log-file-dropzone {
  display: flex;
  min-height: 200px;
  align-items: center;
  justify-content: center;
  border: dashed 1px lightgray;
}
th,
td {
  padding: 5px;
}
</style>
