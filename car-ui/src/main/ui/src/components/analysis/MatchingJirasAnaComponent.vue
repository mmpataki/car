<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Matching KBs</b></small>
    <ul>
      <li v-for="(info, idx) in jiras" :key="idx">
        <b
          ><a target="_blank" :href="info.url">{{ info.desc }}</a
          >: {{ info.title }}</b
        >
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "MatchingJirasAnaComponent",
  props: ["objs"],
  data() {
    return {
      jiras: this.objs.map((o) => o.info),
    };
  },
};
</script>

<style>
</style>