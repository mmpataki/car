<template>
  <v-expansion-panels v-if="!plain" class="elevation-0" v-model="expandModel">
    <v-expansion-panel class="elevation-0">
      <v-expansion-panel-header v-slot:default="{ open }">
        <template>
          <div style="display: flex; align-items: center">
            <strong>{{ label }}</strong>
            <v-btn
              x-small
              text
              color="green"
              v-if="open"
              @click.stop="array.push(ctor())"
            >
              add
            </v-btn>
          </div>
        </template>
      </v-expansion-panel-header>
      <v-expansion-panel-content :key="key">
        <div
          v-for="(elem, idx) in array"
          :key="idx"
          style="display: flex; margin: 0px 0px 0px 10px"
        >
          <slot v-bind:idx="idx"></slot>
          <v-btn text x-small @click="remove(elem)">
            <v-icon small>mdi-close</v-icon>
          </v-btn>
          <v-btn title="Move up" @click="moveUp(array, idx)" text x-small>
            <v-icon small> mdi-chevron-up </v-icon>
          </v-btn>
          <v-btn title="Move down" @click="moveDown(array, idx)" text x-small>
            <v-icon small> mdi-chevron-down </v-icon>
          </v-btn>
        </div>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
  <div v-else style="margin: 20px 0px">
    <div style="display: flex; align-items: center">
      <strong>{{ label }}</strong>
      <v-btn
        x-small
        text
        color="green"
        @click.stop="array.push(ctor())"
      >
        add
      </v-btn>
    </div>
    <div
      v-for="(elem, idx) in array"
      :key="idx"
      style="display: flex; margin: 10px -10px 10px 20px"
    >
      <slot v-bind:idx="idx"></slot>
      <v-btn text x-small @click="remove(elem)">
        <v-icon small>mdi-close</v-icon>
      </v-btn>
      <v-btn title="Move up" @click="moveUp(array, idx)" text x-small>
        <v-icon small> mdi-chevron-up </v-icon>
      </v-btn>
      <v-btn title="Move down" @click="moveDown(array, idx)" text x-small>
        <v-icon small> mdi-chevron-down </v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script>
export default {
  props: ["array", "label", "ctor", "expanded", "plain"],
  data() {
    return {
      key: 0,
      expandModel: this.expanded ? [0] : [],
    };
  },
  methods: {
    set(i, x) {
      this.$set(this.array, i, x);
    },
    remove(x) {
      this.array.splice(this.array.indexOf(x), 1);
      this.key++;
      this.$forceUpdate();
    },
    moveDown(arr, idx) {
      if (idx + 1 < arr.length) {
        let tmp = arr[idx];
        this.$set(arr, idx, arr[idx + 1]);
        this.$set(arr, idx + 1, tmp);
      }
    },
    moveUp(arr, idx) {
      if (idx - 1 > -1) {
        let tmp = arr[idx];
        this.$set(arr, idx, arr[idx - 1]);
        this.$set(arr, idx - 1, tmp);
      }
    },
  },
};
</script>

<style>
</style>