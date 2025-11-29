<template>
  <div style="height: 100%; width: 100%; display: flex">
    <splitpanes class="default-theme">
      <pane :size="25" :max-size="60" class="leftpane">
        <div style="height: 100%; width: 100%; padding: 10px 0px 10px 20px">
          <h3>Rules</h3>
          <rules-tree class="mt-3"></rules-tree>
        </div>
      </pane>
      <pane size="75">
        <div style="height: 100%; overflow: auto; max-width: calc(100%); flex-grow: 1; align-items: start;">
          <router-view :key="$route.path" />
        </div>
      </pane>
    </splitpanes>
  </div>
</template>

<script>
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import RulesTree from "@/components/RulesTree.vue";

export default {
  name: "RuleConfigWithList",
  components: { Splitpanes, Pane, RulesTree },
};
</script>
  
  <style scoped>
.splitpanes__pane {
  display: flex;
  justify-content: center;
  align-items: start;
  font-family: Inter, BlinkMacSystemFont, Helvetica, Arial, sans-serif;
}
.splitpanes.default-theme .splitpanes__pane {
  background-color: transparent;
}
</style>
  
  <style>
.leftpane .v-window__container,
.leftpane .v-window-item,
.leftpane .v-tabs-bar {
  background: ghostwhite !important;
}
</style>