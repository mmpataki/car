<template>
  <div style="height: 100%; position: relative">
    <div v-if="files.length" style="height: 100%; margin: 0; overflow: auto; height: calc(100%)">
      <TreeView
        v-for="(file, idx) in files"
        :root="file"
        :key="file.name"
        :createNew="false"
        :treeid="'idx / ' + idx"
        style="overflow: none"
        :styled="true"
        :expanded="true"
        :maxchars="500"
      ></TreeView>
    </div>
    <div
      v-if="!files.length"
      style="
        height: 100%;
        margin: 0;
        overflow: auto;
        display: flex;
        align-items: center;
        padding: 30px;
      "
    >
      <v-progress-circular></v-progress-circular>
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
import TreeView from "@/components/utils/TreeView.vue";
export default {
  name: "FileTree",
  components: { TreeView },
  data() {
    return {
      files: [],
    };
  },
  created() {
    car
      .getDataset(this.$route.params.dsetid)
      .then((ds) => (this.files = ds.children));
  },
};
</script>
