<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Case type: </b></small>
    {{ ct.type }} ({{ ct.score }}% sure)
  </div>
</template>

<script>
export default {
  name: "CaseTypeAnaComponent",
  props: ["objs"],
  data() {
    return {
      ct: this.objs[0].info,
    };
  },
};
</script>

<style>
</style>