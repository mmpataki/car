<template>
  <div style="display: flex; align-items: center; flex-grow: 1">
    <div style="flex-grow: 1; align-items: center">
      <code
        v-if="v.txt && !hidetxt"
        class="regex-match"
        :style="{ background: v.color, marginRight: '6px' }"
        >{{ v.txt.substr(0, 20) + (v.txt.length > 20 ? "..." : "") }}</code
      >
      <template v-if="v.txt && !v.ignore">=</template>
      <v-text-field
        v-model="v.name"
        :counter="30"
        :rules="nameRules"
        required
        dense
        outlined
        label="name"
        class="d-inline-block mx-2"
        v-if="!v.ignore"
      ></v-text-field>

      <v-select
        v-model="v.type.type"
        :items="ftypes"
        dense
        outlined
        label="type"
        class="d-inline-block mx-2"
      ></v-select>

      <component
        :is="dataTypeEditor"
        :type="v.type"
        :v="v"
        style="float: left: clear: both"
      ></component>
    </div>
    <v-checkbox
      v-if="!hideignore"
      class="d-inline-block mx-2"
      v-model="v.ignore"
      @change="ignored"
      label="Ignore"
    ></v-checkbox>

    <v-btn
      class="d-inline-block mx-2"
      icon
      small
      @click="remove"
      v-if="!hideRemovebtn"
    >
      <v-icon> mdi-delete </v-icon>
    </v-btn>
  </div>
</template>

<script>
import upperFirst from "lodash/upperFirst";
import { car } from "@/car.js";
export default {
  props: ["v", "hidetxt", "hideRemovebtn", "hideignore"],
  computed: {
    dataTypeEditor() {
      return upperFirst(this.v.type.type + "Editor");
    },
    ftypes() {
      return car.getFTypes().map((ftype) => ({
        value: ftype.name,
        text: ftype.displayName,
      }));
    },
  },
  methods: {
    ignored(event) {
      this.$emit("ignored", event);
    },
    remove() {
      this.$emit("removed");
    },
  },
};
</script>

<style>
</style>