<template>
  <div>
    <div>
      <strong>{{ label }}</strong>
      <v-btn
        color="success"
        text
        x-small
        @click="kvps.push({ key: '', value: '' })"
      >
        add</v-btn
      >
    </div>
    <div v-if="kvps.length > 0" style="margin: 10px -10px 0px 10px">
      <div
        v-for="(kvp, idx) in kvps"
        :key="idx"
        style="
          display: flex;
          align-items: center;
          margin: 10px 0px;
          border-left: solid 5px lightseagreen;
          padding: 10px 0px 0px 10px;
        "
      >
        <v-text-field
          @change="keyChanged(kvp.key, $event, idx)"
          :value="kvp.key"
          :label="keyLabel || 'Key'"
          dense
        ></v-text-field>
        <strong style="padding: 0px 15px"> = </strong>
        <v-text-field
          @change="valueChanged(kvp.key, $event, idx)"
          :value="kvp.value"
          :label="valueLabel || 'Value'"
          dense
        ></v-text-field>
        <v-btn fab icon rounded x-small @click="remove(idx)">
          <v-icon small>mdi-minus-box</v-icon>
        </v-btn>
      </div>
    </div>
    <div v-else style="margin: 20px">No key value pairs added.</div>
  </div>
</template>

<script>
export default {
  props: ["obj", "label", "keyLabel", "valueLabel"],
  data() {
    return {
      kvps: [],
    };
  },
  created() {
    Object.entries(this.obj).forEach((kvp) => {
      this.kvps.push({ key: kvp[0], value: kvp[1] });
    });
  },
  methods: {
    keyChanged(oldKey, newKey, idx) {
      delete this.obj[oldKey];
      this.obj[newKey] = this.kvps[idx].value;
      this.kvps[idx].key = newKey;
    },
    valueChanged(key, newValue, idx) {
      this.obj[key] = this.kvps[idx].value = newValue;
    },
    remove(idx) {
      delete this.obj[this.kvps[idx].key];
      this.kvps.splice(idx, 1);
      this.$forceUpdate();
    },
  },
};
</script>

<style>
</style>