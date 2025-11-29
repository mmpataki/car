<template>
  <div style="position: relative">
    <div style="position: absolute; top: -35px; right: 0px">
      <v-btn
        v-if="!editingExample"
        small
        text
        @click="(editingExample = true), (readRuleStep = 1)"
        >Add example</v-btn
      >
      <v-btn v-if="editingExample" small text @click="editingExample = false"
        >Done</v-btn
      >
    </div>

    <div v-if="!editingExample">
      <b>Field mapping from examples</b>
      <div
        v-for="(row, idx) in this.type.readConfig.examples"
        :key="idx"
        style="
          width: 100%;
          position: relative;
          border: solid 2px beige;
          margin: 10px 0px;
        "
      >
        <div
          style="position: absolute; top: 0px; right: 0px; background: beige"
        >
          <v-btn
            text
            x-small
            @click="
              (example = row), (editingExample = true), (readRuleStep = 3)
            "
            >edit</v-btn
          >
          <v-btn text x-small @click="type.readConfig.examples.splice(idx, 1)"
            >delete</v-btn
          >
        </div>
        <table style="width: 100%">
          <template v-for="(v, k) in type.readConfig.fieldMappings">
            <tr
              v-if="rowContainsField(row, k)"
              :key="k"
              style="margin: 0px; padding: 0px; font-size: 0.8em"
            >
              <td>
                <b>{{ k }}</b>
              </td>
              <td>{{ v.name }} ({{ v.type.type }})</td>
            </tr>
          </template>
        </table>
      </div>
    </div>
    <div v-if="editingExample || uninitialized">
      <v-stepper v-model="readRuleStep">
        <v-stepper-header>
          <v-stepper-step
            :editable="uninitialized"
            :complete="file != undefined || !uninitialized"
            step="1"
          >
            Drop a sample log file
          </v-stepper-step>

          <v-divider></v-divider>

          <v-stepper-step
            :editable="uninitialized"
            :complete="this.type.readConfig.examples.length > 0"
            step="2"
          >
            Configure JSON reader
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
            <strong>Configure the JSON reader</strong>
            <div class="my-10">
              <div>
                <v-text-field
                  type="number"
                  v-model="numLinesRead"
                  label="Number of lines to read to infer schema"
                ></v-text-field>
              </div>
            </div>
            <v-btn @click="parse"> Next </v-btn>
          </v-stepper-content>

          <v-stepper-content step="3">
            <div v-if="this.example">
              <b>Map the fields</b>
              <json-object-mapper
                jspath=""
                jsname=""
                :mappingdict="this.type.readConfig.fieldMappings"
                :jsonobj="this.example"
              ></json-object-mapper>
            </div>
          </v-stepper-content>
        </v-stepper-items>
      </v-stepper>
    </div>
  </div>
</template>

<script>
// import { car } from "@/car.js";
import JsonObjectMapper from "./JsonObjectMapper.vue";
export default {
  name: "JsonReadConfig",
  components: { JsonObjectMapper },
  props: ["type"],
  data() {
    return {
      ruleKey: 0,
      editingExample: false,
      readRuleStep: 1,
      file: undefined,
      numLinesRead: 5,
      example: undefined,
    };
  },

  created() {
    if (!this.type.readConfig || !this.type.readConfig.fieldMappings) {
      this.$set(this.type, "readConfig", {
        type: "json",
        fieldMappings: {},
        examples: [],
      });
      this.editingExample = true;
    }
  },

  computed: {
    keys() {
      return Object.keys(this.type.readConfig.fieldMappings).filter(
        (k) => k && k != ""
      );
    },

    uninitialized() {
      return !Object.keys(this.type.readConfig.fieldMappings).length;
    },
  },

  methods: {
    rowContainsField(ex, k) {
      console.log(ex, k);
      for (let ke of k.split("/").slice(1)) {
        if (!ex[ke]) return false;
        ex = ex[ke];
      }
      return true;
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
      const reader = new FileReader();

      function isObject(tree) {
        return (
          typeof tree === "object" && !Array.isArray(tree) && tree !== null
        );
      }

      function dfs(tree, oldschema) {
        if (Array.isArray(tree)) return;
        if (!isObject(tree)) {
          return tree;
        }
        let schema = oldschema && isObject(oldschema) ? oldschema : {};
        Object.entries(tree).forEach(([key, obj]) => {
          let ret = dfs(obj, schema[key]);
          if (ret) {
            if (schema[key]) {
              if (!isObject(schema[key]) && isObject(ret)) {
                schema[key] = ret;
              }
            } else {
              schema[key] = ret;
            }
          }
        });
        return schema;
      }

      let a = { a: 1, b: { c: 2, d: { e: 3 } }, f: { g: 1 } };
      let b = { a: 1, b: 3, c: 5, f: { h: 5 } };
      let f = {};
      dfs(a, f);
      dfs(b, f);
      console.log(f);

      reader.onload = (ex) => {
        let sampleText = ex.target.result;
        console.log(sampleText);
        let lines = sampleText.split("\n"),
          schema = {};

        for (let i = 0; i < this.numLinesRead; i++) {
          try {
            dfs(JSON.parse(lines[i]), schema);
          } catch (e) {
            console.debug(`${lines[i]} is not valid json`);
          }
        }

        rc.examples.push(schema);
        this.example = schema;
        //this.$set(rc, "examples", [schema]);
        this.readRuleStep = 3;
      };
      reader.readAsText(this.file);
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