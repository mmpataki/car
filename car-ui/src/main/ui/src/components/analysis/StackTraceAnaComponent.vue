<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Stack traces</b></small>
    <pre v-for="(st, idx) in objs" :key="idx">{{st.info}}</pre>
  </div>
</template>

<script>
export default {
  name: "StackTraceAnaComponent",
  props: ["objs"],
};
</script>

<style>
</style>