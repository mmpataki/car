<template>
  <div>
    <small>
      <strong>{{comment.ACTIVITY_TYPE}} ({{comment.ACT_DATE}})</strong>
    </small>
    <ShowMoreComponent :text="comment.COMMENTS"></ShowMoreComponent>
  </div>
</template>

<script>
import ShowMoreComponent from "./ShowMoreComponent.vue";
export default {
  name: "CaseCommentAnaComponent",
  components: { ShowMoreComponent },
  props: ["objs"],
  data() {
    return {
      comment: this.objs[0].info,
    };
  },
};
</script>

<style>
</style>