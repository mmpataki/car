<template>
  <div style="display: inline">
    <div
      v-if="isPrimitive"
      style="display: inline-flex; clear: right; align-items: center"
    >
      <label style="margin: 0px 20px">
        <input
          type="checkbox"
          :checked="ignored"
          @change="ignoreToggled($event)"
        />
        <span style="margin: 5px">ignore</span>
      </label>
      <field-editor
        v-if="!ignored"
        :hidetxt="true"
        :v="mappingdict[jspath]"
        :hideignore="true"
        @ignored="ignoreToggled({ target: { checked: $event } })"
        :hideRemovebtn="true"
      ></field-editor>
    </div>
    <div v-if="isObject" style="display: inline">
      <span @click="expand = !expand" style="border: solid 1px lightgray; font-family: monospace">{{
        expand ? "-" : "+"
      }}</span>
      {
      <div style="margin-left: 25px" v-if="expand">
        <div v-for="(val, key) in jsonobj" :key="key">
          <span :title="val">{{ key }}</span>:
          <component
            :is="'JsonObjectMapper'"
            :mappingdict="mappingdict"
            :jsonobj="val"
            :jspath="`${jspath}/${key}`"
            :jsname="key"
          ></component>
        </div>
      </div>
      <span v-else>...</span>
      }
    </div>
  </div>
</template>

<script>
import FieldEditor from "../utils/FieldEditor.vue";
export default {
  name: "JsonObjectMapper",
  props: ["jspath", "jsname", "mappingdict", "jsonobj"],
  components: { FieldEditor },
  data() {
    return {
      ignored: this.mappingdict[this.jspath] == undefined,
      expand: true,
    };
  },
  computed: {
    isPrimitive() {
      return !this.isArray && !this.isObject;
    },
    isArray() {
      return Array.isArray(this.jsonobj);
    },
    isObject() {
      return (
        typeof this.jsonobj === "object" &&
        !Array.isArray(this.jsonobj) &&
        this.jsonobj !== null
      );
    },
  },
  methods: {
    ignoreToggled(e) {
      this.ignored = e.target.checked;
      if (!e.target.checked) {
        this.$set(this.mappingdict, this.jspath, {
          name: this.jsname.replaceAll(/[^a-zA-Z0-9]+/g, "_"),
          ignore: false,
          regexTokenNames: ["string/string"],
          type: {
            type: "stringftype",
          },
        });
      } else {
        delete this.mappingdict[this.jspath];
      }
    },
  },
};
</script>

<style>
</style>