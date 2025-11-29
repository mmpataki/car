<template>
  <div style="display: flex; align-items: center; margin: 2px 5px">
    <input
      v-model="text"
      @change="emitEvent"
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
        case "contains":
          return row[this.datakey]
            .toLowerCase()
            .includes(this.text.toLowerCase());
        case "doesn't contain":
          return !row[this.datakey]
            .toLowerCase()
            .includes(this.text.toLowerCase());
        case "equal to":
          return row[this.datakey] == this.text;
        case "not equal to":
          return row[this.datakey] != this.text;
        case "starts with":
          return row[this.datakey].startsWith(this.text);
        case "doesn't start with":
          return !row[this.datakey].startsWith(this.text);
        case "ends with":
          return row[this.datakey].endsWith(this.text);
        case "doesn't end with":
          return !row[this.datakey].endsWith(this.text);
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