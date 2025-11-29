<template>
  <div style="margin-left: 25px">
    <h4>Configure JSON fields to pick {</h4>
    <div style="margin-left: 25px">
      <div
        style="display: flex; align-items: center"
        v-for="(td, key, i) in fields"
        :key="i"
      >
        <b>{{ key }}: </b>
        <small style="margin: 0px 5px">
          {{ (typeof td.txt === 'string' ? td.txt.substr(0, 20) : td.txt) + (td.txt.length > 20 ? "..." : "") }}</small
        >
        <field-editor :hidetxt="true" :v="td" @ignored="td.ignored = true" :hideRemovebtn="true"></field-editor>
      </div>
    </div>
    <h4>}</h4>
  </div>
</template>

<script>
import FieldEditor from "../utils/FieldEditor.vue";

export default {
  components: { FieldEditor },
  props: ["v"],
  created() {
    if (!this.v.type.fields) {
      this.v.type.fields = {};
    }
    let obj = JSON.parse(this.v.txt);
    Object.keys(obj)
      .filter((k) => {
        let v = obj[k];
        return typeof v !== "object" && !Array.isArray(v) && v !== null;
      })
      .map((key) => [
        key,
        {
          name: key,
          txt: obj[key],
          type: { type: "stringftype" },
          ignore: false,
        },
      ])
      .forEach((kv) => {
        let v = this.v.type.fields;
        if (!v[kv[0]]) v[kv[0]] = kv[1];
      });
  },
  computed: {
    fields() {
      return this.v.type.fields;
    },
  },
};
</script>

<style>
</style>
