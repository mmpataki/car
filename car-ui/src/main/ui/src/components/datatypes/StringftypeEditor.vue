<template>
  <custom-regex-builder :field="$attrs.v" type="string"></custom-regex-builder>
</template>

<script>
import CustomRegexBuilder from "./CustomRegexBuilder.vue";
export default {
  name: "StringftypeEditor",
  props: ["field"],
  components: {
    CustomRegexBuilder,
  },
};
</script>
