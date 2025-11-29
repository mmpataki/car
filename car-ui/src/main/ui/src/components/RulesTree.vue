<template>
  <div
    style="
      height: calc(100vh - 48px);
      position: relative;
    "
  >
    <div style="height: 100%; margin: 0px; overflow: auto">
      <TreeView
        v-for="(lgt, idx) in lgts"
        :root="lgt"
        :key="'rule-' + idx"
        :expanded="idx == 0"
        :create-new="true"
        :treeid="'rules' + idx"
        :styled="true"
      >
      </TreeView>
    </div>
    <v-btn
      color="primary"
      dark
      x-small
      style="position: absolute; bottom: 15px; right: 10px"
      title="Create new log type group"
      fab
      @click="createNewLGroup"
    >
      <v-icon>mdi-plus</v-icon>
    </v-btn>
    <!-- <v-btn
      color="primary"
      dark
      x-small
      style="position: absolute; bottom: 10px; right: 10px"
      fab
      title="Sync rules"
      :loading="syncing"
      :disabled="syncing"
      @click="sync"
    >
      <v-icon>mdi-sync</v-icon>
    </v-btn> -->
  </div>
</template>

<script>
import { car } from "@/car.js";
import LogTypeGroup from "@/models/rules/LogTypeGroup.js";
import TreeView from "@/components/utils/TreeView.vue";

export default {
  name: "RulesTree",
  components: { TreeView },
  data() {
    return {
      lgts: [],
      syncing: false,
    };
  },
  created() {
    car
      .getLogGroupTypes()
      .then((lgts) => lgts.forEach((lgt) => this.lgts.push(lgt)));
  },
  methods: {
    createNewLGroup() {
      let grp = new LogTypeGroup({
        name: "new_logtype_group",
        description: "new log type group",
      });
      this.lgts.push(grp);
      this.$router.push({
        name: `lgroupeditor`,
        params: {
          logtypegroup: grp.name,
          group: grp,
        },
      });
    },
    sync() {
      this.syncing = true;
      car.sync().then(() => {
        this.syncing = false;
      });
    },
  },
};
</script>

<style>
</style>