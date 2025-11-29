<template>
  <div className="case-detail-card">
    <b
      ><a
        :href="`https://test.lightning.force.com/lightning/r/Case/${c.SFDC_CASE_ID}/view`"
        >{{ c.SR_NUMBER }}</a
      >
      : {{ c.SUMMARY }}&nbsp;
      <small>
        (
        <a :href="`mailto:${c.OWNER_EMAIL}`">{{ c.OWNER }}</a>
        &nbsp;|&nbsp; {{ c.STATUS }}, {{ c.SUB_STATUS }} )
      </small>
    </b>
    <div>
      <ShowMoreComponent :text="c.DESCRIPTION"></ShowMoreComponent>
    </div>
    <ShowMoreComponent
      v-if="c.CLOSING_NOTES"
      :text="c.CLOSING_NOTES"
    ></ShowMoreComponent>
    <div>
      Product / Component: {{ c.PRODUCT }} ({{ c.PRODUCT_VERSION }}) /
      {{ c.COMPONENT }}, {{ c.SUBCOMPONENT }}
    </div>
  </div>
</template>

<script>
import ShowMoreComponent from "./ShowMoreComponent.vue";
export default {
  name: "CasedetailAnaComponent",
  components: { ShowMoreComponent },
  props: ["objs"],
  data() {
    return {
      c: this.objs[0].info,
    };
  },
};
</script>

<style>
</style>