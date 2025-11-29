<template>
  <div>
    <div style="margin: 10px 0px">
      <v-select
        v-model="store.mode"
        :items="['facet', 'logs', 'stats']"
        outlined
        hint="Query stats or logs?"
        label="Query mode"
        dense
      ></v-select>
      <v-text-field
        v-if="store.mode == 'logs'"
        v-model="store.pageSize"
        type="number"
        label="Page size"
        outlined
        dense
        class="my-3"
      ></v-text-field>
    </div>

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

    <div v-if="store.mode == 'logs'" class="my-2">
      <v-combobox
        v-model="store.queriedFields"
        :items="store.fields"
        label="Pick fields to query"
        multiple
        chips
        outlined
        dense
        small-chips
      ></v-combobox>
    </div>

    <div v-if="store.mode == 'stats'" class="my-2">
      <v-combobox
        v-model="store.statFields"
        :items="store.fields"
        label="Pick fields to get stats for"
        multiple
        chips
        outlined
        dense
        small-chips
      ></v-combobox>
    </div>

    <div v-if="store.mode == 'facet'" class="my-2">
      <v-combobox
        v-model="store.facetFields"
        :items="store.fields"
        label="Facet fields"
        multiple
        chips
        outlined
        small-chips
        dense
      ></v-combobox>
    </div>

    <div class="my-2">
      <v-select
        v-if="store.mode == 'logs'"
        v-model="store.sortFields"
        :items="possibleSortFields"
        :item-text="x => `${x.first} ${x.second}`"
        :item-value="x => ({first: x.first, second: x.second})"
        label="Sort fields"
        dense
        outlined
        multiple
        chips
        small-chips
      ></v-select>
    </div>

    <div style="margin-bottom: 0px">
      <div class="d-flex" style="align-items: center">
        <helpful-label help="Query in json form to fetch logs">
          <b style="cursor: pointer" @click="queryOpen = !queryOpen">
            <i
              :class="{
                fa: true,
                'fa-chevron-right': !queryOpen,
                'fa-chevron-down': queryOpen,
              }"
              style="font-size: 0.8em; margin-right: 5px"
            ></i>
            Log query</b
          >
        </helpful-label>
      </div>
      <codemirror
        v-if="queryOpen"
        style="border: solid 1px lightgray; padding: 2px"
        v-model="store.query"
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
  name: "LogsStoreEditor",
  props: ["store"],
  components: { codemirror },
  data() {
    return {
      unfilteredSchema: null,
      schema: {},
      schemaOpen: false,
      queryOpen: true,
      searchedTable: "",
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
    }
  },
  created() {
    this.store.getStoreSchema().then((s) => {
      this.unfilteredSchema = s;
      this.searchTriggered();
    });
  },
  computed: {
    possibleSortFields() {
      let ret = []
      this.store.fields.forEach((f) => {
        ret.push({ first: f, second: 'asc' })
        ret.push({ first: f, second: 'desc' })
      })
      return ret
    }
  }
};
</script>

<style scoped>
</style>
