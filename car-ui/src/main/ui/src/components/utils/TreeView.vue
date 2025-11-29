<template>
  <div style="font-size: 0.9rem">
    <TreeNode
      :node="root"
      :create-new="createNew"
      :picked-item="pickedItem"
      :treeid="treeid"
      :styled="styled"
      :maxchars="maxchars"
    ></TreeNode>
  </div>
</template>

<script>
import TreeNode from "./TreeNode.vue";
export default {
  name: "TreeView",
  components: { 'TreeNode': TreeNode },
  props: ["root", "expanded", "createNew", "treeid", "pickedItem", "styled", "maxchars"],
};
</script>
