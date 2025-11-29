<template>
  <div style="margin: 0px" class="white--text">
    <tree-view
      v-for="node in docroots"
      :key="node.name"
      :root="node"
      :styled="true"
    ></tree-view>
  </div>
</template>

<script>
import TreeView from "@/components/utils/TreeView.vue";
function node(title, url, children) {
  return {
    name: title,
    title,
    link: `/ui/help/${url}`,
    children,
    getChildren:
      children && children.length > 0 ? () => Promise.resolve(1) : undefined,
    expandIcon: true,
    expanded(path) {
      return path.startsWith(`/ui/help/${this.name}`)
    }
  };
}
export default {
  name: "HelpTree",
  components: { TreeView },
  data() {
    return {
      docroots: [
        node("Introduction", "introduction", [
          node("#1. Task timelines", "introduction/example-task-timeline"),
          node(
            "#2. Browsing subtasks of a task",
            "introduction/example-browse-subtask-of-a-task"
          ),
        ]),
        node("Installation", "installation"),
        node("Getting started", "getting-started"),
        node("CAR from inside", "concepts", [
          node("Log type group", "concepts/log-type-group"),
          node("Log type", "concepts/log-type"),
          node("Rule", "concepts/rule"),
        ]),
        node("Visualizations", "visualizations", [
          node("Table", "visualizations/table")
        ])
      ],
    };
  },
};
</script>