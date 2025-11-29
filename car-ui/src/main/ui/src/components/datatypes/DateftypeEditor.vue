<template>
  <div style="display: inline-block">
    <custom-regex-builder :field="$attrs.v" :type="'date'"></custom-regex-builder>
  </div>
</template>

<script>
import CustomRegexBuilder from "./CustomRegexBuilder.vue";
export default {
  components: { CustomRegexBuilder },
  name: "DateftypeEditor",
  props: ["type"],
};
</script>
