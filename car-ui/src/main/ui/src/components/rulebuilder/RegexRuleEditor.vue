<template>
  <div>
    <div v-for="(example, eidx) in data.exampleTexts" :key="eidx">
      <regex-builder label="Example text" :txt="example.txt" :handWrittenRegex="data.pattern" :exSelections="data.selections" @regex-changed="regexChanged"></regex-builder>
    </div>
  </div>
</template>
<script>
//import FieldEditor from "../utils/FieldEditor.vue";
import RegexBuilder from "./RegexBuilder.vue";
export default {
  components: { RegexBuilder },
  name: "regex-rule-editor",
  props: ["data"],
  computed: {
    randomColor() {
      return "hsl(" + Math.random() * 360 + ", 100%, 75%)";
    },
  },
  methods: {
    removeExample(idx) {
      this.data.examples.splice(idx, 1);
      if (this.data.examples.length == 0)
        this.data.examples.push({ txt: "", groups: [] });
    },
    regexChanged(x) {
      this.data.pattern = x.regex
      this.data.groupNameMap = x.groupNameMap;
    }
  },
};
</script>

<style scoped>
.regex-example {
  display: flex;
  padding: 0px;
  align-items: center;
}

.regex-example > * {
  margin: 0px 0px;
}
</style>
