<template>
  <div
    style="
      height: 100%;
      display: flex;
      align-items: start;
      justify-content: center;
    "
  >
    <v-autocomplete
      style="width: calc(100% - 20px); margin: 10px 10px 0px 10px"
      :items="vals"
      outlined
      dense
      item-text="label"
      item-value="key"
      v-model="value"
      @change="valChanged"
      :label="visualization.label"
    >
    </v-autocomplete>
  </div>
</template>

<script>
export default {
  name: "DropdownVisualization",
  props: ["view", "visualization", "dataWatch"],
  data() {
    return {
      value: "",
      vals: [],
    };
  },
  created() {
    this.update();
  },
  watch: {
    dataWatch: {
      handler() {
        this.update();
      },
    },
  },
  methods: {
    update() {
      if (!this.view.data || this.view.data.length < 1) return [];
      this.vals = this.view.data.map((r, i) => {
        return { label: r[this.visualization.labelKey], key: i };
      });
      this.value = this.vals[0].key;
      this.valChanged(0);
    },
    valChanged(e) {
      this.visualization.eventOccured("change", this.view.data[e]);
    },
  },
};
</script>
