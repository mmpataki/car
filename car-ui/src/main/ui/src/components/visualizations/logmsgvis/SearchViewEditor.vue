<template>
  <v-dialog value="true" width="500" style="height: 500px" persistent>
    <v-card style="position: relative">
      <v-btn
        @click="cancelAndClose()"
        rounded
        icon
        style="position: absolute; right: 10px; top: 10px"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
      <v-card-title>Edit "{{ view.name }}"</v-card-title>
      <v-card-text>
        <div style="margin-top: 10px; max-height: 300px; overflow: auto">
          <v-text-field v-model="view.name" label="View name" outlined style="margin: 10px" dense></v-text-field>
          <div v-for="(field, idx) in view.fields" :key="idx">
            <div
              style="
                padding: 15px 0px 15px -10px;
                display: flex;
                align-items: baseline;
              "
            >
              <v-btn
                icon
                small
                @click="field.showDesc = !field.showDesc"
                style="margin-right: 10px"
              >
                <v-icon small>{{
                  "mdi-chevron-" + (field.showDesc ? "down" : "right")
                }}</v-icon>
              </v-btn>
              <input
                type="checkbox"
                dense
                v-model="field.queried"
                @change="fieldVisibilityChanged(field)"
                style="margin: 0px 20px 0px 0px"
              />
              <strong style="flex-grow: 1">
                {{ field.name }}
              </strong>
              <div>
                <v-btn icon small @click="displace(idx, -1)">
                  <v-icon small>mdi-chevron-up</v-icon>
                </v-btn>
                <v-btn icon small @click="displace(idx, +1)">
                  <v-icon small>mdi-chevron-down</v-icon>
                </v-btn>
              </div>
            </div>
            <div
              v-if="field.showDesc"
              style="
                padding: 10px 10px 10px 40px;
                display: flex;
                flex-direction: column;
              "
            >
              <div style="display: flex">
                <div style="flex-grow: 1; align-items: start">
                  <label style="margin: 0px 10px">Visible</label>
                  <input type="checkbox" v-model="field.visible" />
                </div>
                <div>
                  <label>Decode value as</label>
                  <select v-model="field.decodeAs" style="margin: 0px 10px">
                    <option
                      v-for="func in Object.keys(decodeFunctions)"
                      :key="func"
                    >
                      {{ func }}
                    </option>
                  </select>
                </div>
              </div>
              <div style="display: flex">
                <div style="flex-grow: 1">
                  <label style="margin: 0px 10px">Wrap text</label>
                  <input type="checkbox" v-model="field.wrap" />
                </div>
                <div style="flex-grow: 1">
                  <label style="margin: 0px 10px">Align text</label>
                  <select v-model="field.align">
                    <option>left</option>
                    <option>center</option>
                    <option>right</option>
                  </select>
                </div>
              </div>
              <div
                v-if="field.decodeAs == 'custom'"
                style="display: flex; flex-direction: column; margin: 10px 10px"
              >
                <label>Mapping function</label>

                <textarea
                  style="border: solid 1px black"
                  v-model="field.decodeFunc"
                >
                </textarea>
              </div>
            </div>
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn small color="primary" @click="saveAndClose()">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { decodeFuncs } from "@/car.js";
export default {
  name: "SearchViewEditor",
  props: ["view"],
  data() {
    return {
      show: true,
      decodeFunctions: decodeFuncs,
      showSearchViewSaver: false,
    };
  },

  methods: {
    saveAndClose() {
      this.$emit("closed", this.view);
    },

    cancelAndClose() {
      this.$emit("closed", false);
    },

    fieldVisibilityChanged() {},

    displace(idx, direction) {
      let fields = this.view.fields;
      let item = fields[idx];
      fields.splice(idx, 1);
      fields.splice(idx + direction, 0, item);
    },
  },
};
</script>

<style>
</style>