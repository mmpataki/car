<template>
  <div>
    <div style="margin-bottom: 20px">
      <div class="d-flex" style="align-items: center">
        <b
          @click="schemaOpen = !schemaOpen"
          style="cursor: pointer; flex-grow: 1"
        >
          <i
            :class="{
              fa: true,
              'fa-chevron-right': !schemaOpen,
              'fa-chevron-down': schemaOpen,
            }"
            style="font-size: 0.8em; margin-right: 5px"
          ></i>
          Schema browser</b
        >
        <v-text-field
          @input="searchTriggered"
          v-if="schemaOpen"
          append-icon="mdi-magnify"
          style="width: 100px"
          dense
          placeholder="search tables / columns"
          v-model="searchedTable"
        >
        </v-text-field>
      </div>
      <div
        v-if="schema && schemaOpen"
        style="
          max-height: 250px;
          min-height: 100px;
          padding: 10px;
          overflow: auto;
          border: solid 1px lightgray;
          position: relative;
        "
      >
        <v-overlay
          color="white"
          opacity="1"
          absolute
          :value="!unfilteredSchema"
        >
          <v-progress-circular
            color="primary"
            indeterminate
            size="32"
          ></v-progress-circular>
        </v-overlay>
        <div v-for="(columns, table) in schema" :key="table">
          <i
            @click="columns.show = !columns.show"
            :class="{
              fa: true,
              'fa-chevron-right': !columns.show,
              'fa-chevron-down': columns.show,
            }"
            style="font-size: 12px; margin-right: 5px; cursor: pointer"
          ></i>
          <span
            @click="columns.show = !columns.show"
            style="cursor: pointer"
            class="primary--text"
            >{{ table }}</span
          >
          <div v-if="columns.show" style="margin-left: 30px">
            <span
              v-for="(c, idx) in columns.cols"
              :key="idx"
              style="display: block"
              >{{ c.name }} <i>({{ c.type.toLowerCase() }})</i></span
            >
          </div>
        </div>
      </div>
    </div>

    <div style="margin-bottom: 0px">
      <div class="d-flex" style="align-items: center">
        <helpful-label help="SQL query to fetch data">
          <b style="cursor: pointer" @click="sqlOpen = !sqlOpen">
            <i
              :class="{
                fa: true,
                'fa-chevron-right': !sqlOpen,
                'fa-chevron-down': sqlOpen,
              }"
              style="font-size: 0.8em; margin-right: 5px"
            ></i>
            SQL query</b
          >
        </helpful-label>
      </div>
      <codemirror
        v-if="sqlOpen"
        style="border: solid 1px lightgray; padding: 2px"
        v-model="store.sql"
        :options="cmOption"
      />
    </div>
  </div>
</template>

<script>
import { codemirror } from "vue-codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/mode/sql/sql.js";

export default {
  name: "SqlStoreEditor",
  props: ["store"],
  components: { codemirror },
  data() {
    return {
      unfilteredSchema: null,
      schema: {},
      schemaOpen: false,
      sqlOpen: true,
      searchedTable: "",
      cmOption: {
        tabSize: 4,
        styleActiveLine: true,
        line: true,
        lineNumbers: true,
        mode: "text/x-sql",
        theme: "default",
      },
    };
  },
  methods: {
    searchTriggered() {
      if (!this.unfilteredSchema) return {};
      this.schema = Object.entries(this.unfilteredSchema).reduce((map, kvp) => {
        if (
          kvp[0].toLowerCase().includes(this.searchedTable.toLowerCase()) ||
          kvp[1].filter((c) =>
            c.name.toLowerCase().includes(this.searchedTable.toLowerCase())
          ).length
        )
          map[kvp[0]] = { cols: kvp[1], show: false };
        return map;
      }, {});
    },
  },
  created() {
    this.store.getStoreSchema().then((s) => {
      this.unfilteredSchema = s;
      this.searchTriggered();
    });
  },
};
</script>

<style scoped>
</style>
