<template>
  <div>
    <div>
      <div class="mb-4">
        <v-select
          @change="storeChanged"
          :items="['sql', 'json', 'logs']"
          v-model="visualization.datastore.type"
          dense
          outlined
          label="Store"
        >
        </v-select>
      </div>
      <div v-if="visualization.datastore.type">
        <component
          :is="storeEditorName(visualization.datastore.type)"
          :store="visualization.datastore"
        ></component>
      </div>
    </div>

    <div class="form-col">
      <helpful-label
        style="flex-grow: 1"
        help="Write some javascript code to modify / reduce / map the data which may not be easily doable in SQL. You can generate fields, delete fields with this. Read more <a href='/ui/help/pre-processor'>here</a>"
      >
        <b
          style="cursor: pointer"
          @click="showingPreProcessor = !showingPreProcessor"
        >
          <i
            style="font-size: 0.8em; margin-right: 5px"
            :class="{
              fa: true,
              'fa-chevron-right': !showingPreProcessor,
              'fa-chevron-down': showingPreProcessor,
            }"
          ></i>
          Pre-processor</b
        >
      </helpful-label>
      <codemirror
        v-show="showingPreProcessor"
        style="margin: 2px; border: solid 1px lightgray; padding: 2px"
        v-model="visualization.preProcessor"
        :options="cmOption"
      />
    </div>

    <div class="form-col">
      <helpful-label style="flex-grow: 1" help="Data you query appear here">
        <b style="cursor: pointer" @click="showingData = !showingData">
          <i
            style="font-size: 0.8em; margin-right: 5px"
            :class="{
              fa: true,
              'fa-chevron-right': !showingData,
              'fa-chevron-down': showingData,
            }"
          ></i>
          Data preview ({{
            visualization.view.data ? visualization.view.data.length : 0
          }}
          rows)</b
        >
      </helpful-label>
      <div
        v-if="showingData"
        style="max-height: 300px; overflow: scroll; margin: 10px 0px"
      >
        <table :key="dataVersion" style="width: 100%; border-collapse: collapse" border="1">
          <tr>
            <th
              style="background: #f1f1f1"
              v-for="(key, idx) in headers"
              :key="idx"
            >
              {{ key }}
            </th>
          </tr>
          <tr v-for="(row, idx) in visualization.view.data" :key="idx">
            <td
              style="padding: 0px 10px"
              v-for="(key, idx) in headers"
              :key="idx"
            >
              {{ row[key] }}
            </td>
          </tr>
        </table>
      </div>
    </div>

    <v-btn small color="primary" class="mt-6" dark @click="query">
      <v-icon small>mdi-cached</v-icon>&nbsp; update</v-btn
    >
  </div>
</template>
<script>
import { storeFactory } from "@/models/stores/StoreFactory.js";
import upperFirst from "lodash/upperFirst";
import { codemirror } from "vue-codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/mode/javascript/javascript.js";

export default {
  name: "DatasetEditor",
  props: ["visualization"],
  components: { codemirror },
  data() {
    return {
      dataVersion: 0,
      showingPreProcessor: false,
      showingData: true,
      cmOption: {
        tabSize: 4,
        styleActiveLine: true,
        line: true,
        lineNumbers: true,
        mode: "text/javascript",
        theme: "default",
      },
    };
  },
  computed: {
    headers() {
      return this.visualization.view.data && this.visualization.view.data.length
        ? Object.keys(this.visualization.view.data[0])
        : [];
    },
  },
  methods: {
    storeEditorName(name) {
      return upperFirst(name + "StoreEditor");
    },
    storeChanged() {
      this.visualization.datastore = storeFactory(
        this.visualization.datastore.type,
        { dsetid: this.visualization.ctxt.dsetid }
      );
    },
    query() {
      this.visualization.update(true);
    },
  },
  watch: {
    "visualization.dataVersion": function() {
      this.dataVersion++
    }
  }
};
</script>
