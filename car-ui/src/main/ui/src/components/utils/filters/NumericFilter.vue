<template>
  <div style="display: flex; align-items: center; margin: 2px 5px">
    <input
      v-model="text"
      @input="emitEvent"
      style="
        flex-grow: 1;
        border: solid 1px black;
        margin-left: 2px;
        padding: 0px 2px;
        border-radius: 3px;
        background: white;
        font-size: 12px;
        outline: none;
      "
    />
  </div>
</template>

<script>
export default {
  name: "ContainsFilter",
  props: ["data", "datakey", "filter"],
  data() {
    return {
      text: "",
    };
  },
  created() {
    this.filter._accepts = (row) => {
      if (this.text.trim() == "") return true;
      switch (this.filter.type) {
        case "greater than":
          return +row[this.datakey] > +this.text;
        case "less than":
          return +row[this.datakey] < +this.text;
        case "greater than or equal to":
          return +row[this.datakey] >= +this.text;
        case "less than or equal to":
          return +row[this.datakey] <= +this.text;
      }
    };
  },
  methods: {
    emitEvent() {
      this.filter.enabled = this.text.trim() != "";
      this.$emit("change");
    },
  },
};
</script>

<style>
</style>