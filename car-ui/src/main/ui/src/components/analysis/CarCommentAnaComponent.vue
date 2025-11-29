<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Car comment</b></small>
    <div>{{ info }}</div>
  </div>
</template>

<script>
export default {
  name: "CarCommentAnaComponent",
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