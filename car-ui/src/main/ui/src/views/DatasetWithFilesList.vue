<template>
  <div style="height: 100%; width: 100%; display: flex">
    <splitpanes class="default-theme" style="width: calc(100%)">
      <pane :size="25" :max-size="60" class="leftpane">
        <div
          style="
            height: 100%;
            padding: 10px 0px 10px 20px;
            overflow: auto;
            flex-grow: 1;
          "
        >
          <div style="display: flex; overflow-wrap: anywhere; align-items: center;">
            <small>
              <router-link
                :to="`/ui/datasets/${$route.params.dsetid}`"
                v-text="$route.params.dsetid"
              ></router-link>
            </small>
            <h3>&nbsp;/&nbsp;Files</h3>
          </div>
          <FileTree class="mt-3"></FileTree>
        </div>
      </pane>
      <pane size="75">
        <div style="width: calc(100% - 10px); flex-grow: 1">
          <router-view :key="$route.path" />
        </div>
      </pane>
    </splitpanes>
  </div>
</template>

<script>
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import FileTree from "@/components/FileTree";

export default {
  name: "DatasetWithFilesList",
  components: { Splitpanes, Pane, FileTree },
  data() {
    return {
      widdd: 25,
    };
  },
};
</script>
  
<style scoped>
.splitpanes__pane {
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Inter, BlinkMacSystemFont, Helvetica, Arial, sans-serif;
}
.splitpanes.default-theme .splitpanes__pane {
  background-color: transparent;
}
.v-application {
  height: 100%;
}
.tab-link {
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: seagreen;
}
.leftpane .v-window__container,
.leftpane .v-window-item,
.leftpane .v-tabs-bar {
  background: ghostwhite !important;
}

.leftpane {
  justify-content: start;
}
</style>