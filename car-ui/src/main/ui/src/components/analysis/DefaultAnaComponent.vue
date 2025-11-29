<template>
  <pre v-if="debug">{{ JSON.stringify(obj) }}</pre>
</template>

<script>
export default {
  name: "DefaultAnaComponent",
  props: ["obj"],
  data() {
    return {
      debug: window.debug,
    };
  },
};
</script>

<style>
</style>