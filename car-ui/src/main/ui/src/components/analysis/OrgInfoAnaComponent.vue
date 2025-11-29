<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Org ID</b></small>
    <div>{{info.orgId}} ({{info.podId}}, {{info.orgName}})</div>
  </div>
</template>

<script>
export default {
  name: "OrgIdAnaComponent",
  props: ["objs"],
  data() {
    return {
      info: this.objs[0].info,
    };
  },
};
</script>

<style>
</style>