<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Missing info</b></small>
    <ul>
      <li v-for="(mi, idx) in objs" :key="idx">{{ mi.info }}</li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "MissingInfoAnaComponent",
  props: ["objs"]
};
</script>

<style>
</style>