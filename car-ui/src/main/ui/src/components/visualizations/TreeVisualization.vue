<template>
  <tree-view
    v-if="root"
    :root="root"
    :expanded="true"
    :create-new="false"
    :treeid="99999999999999999999"
    :styled="false"
  >
  </tree-view>
</template>

<script>
import TreeView from "../utils/TreeView.vue";

export default {
  name: "TreeVisualization",
  components: { TreeView },
  props: ["view", "visualization", "vh", "vw", "dataWatch", "metaWatch"],
  data() {
    return {
      root: undefined,
    };
  },
  watch: {
    dataWatch: function () {
      console.log(`dataWatch ${this.visualization.title}`);
      this.update();
    },
    metaWatch: function () {
      console.log(`metaWatch ${this.visualization.title}`);
      this.update();
    },
  },
  mounted() {
    console.log("mounted");
    this.update();
  },
  methods: {
    update() {
      if (!this.view.data || this.view.data.length < 1) {
        return;
      }
      console.log("tree view update", this.visualization.title);

      let viz = this.visualization;
      function node(title, url, children = []) {
        return {
          name: title,
          title,
          link: url,
          children,
          getChildren:
            children && children.length > 0
              ? () => Promise.resolve(1)
              : undefined,
          expandIcon: true,
          expanded: () => true,
        };
      }

      let tmproot = { name: viz.separator, url: undefined, children: {} };

      this.view.data.map((datum) => {
        let path = datum[viz.pathField];
        let url = datum[viz.urlField];
        let lbl = datum[viz.labelField];
        let chunks = path.split(viz.separator);
        let parent = tmproot;
        chunks.forEach((chunk, idx) => {
          if (chunk == "") return;
          if (!parent.children[chunk]) {
            parent.children[chunk] = {
              name: `<b>${chunk}</b>    ${lbl ? "&nbsp;&nbsp;(" + lbl + ")" : ""}`,
              url: idx + 1 == chunks.length ? url : undefined,
              children: {},
            };
          }
          parent = parent.children[chunk];
        });
      });

      function dfs(root) {
        return node(
          root.name,
          root.url,
          root.children ? Object.values(root.children).map(dfs) : undefined
        );
      }

      this.root = dfs(tmproot);
    },
  },
};
</script>

<style>
</style>