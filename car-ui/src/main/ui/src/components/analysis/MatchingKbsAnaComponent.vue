<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Matching KBs</b></small>
    <ul>
      <li v-for="(info, idx) in kbs" :key="idx">
        <b
          ><a target="_blank" :href="info.url">{{ info.title }}</a></b
        >
        <ShowMoreComponent :text="info.desc"></ShowMoreComponent>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "MatchingKbsAnaComponent",
  props: ["objs"],
  data() {
    return {
      kbs: this.objs.map((o) => o.info),
    };
  },
};
</script>

<style>
</style>