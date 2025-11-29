<template>
  <div
    :title="node.title"
    style="
      display: flex;
      flex-direction: column;
      cursor: pointer;
      color: seagreen;
    "
    @dblclick="toggle"
  >
    <router-link
      :to="
        node.link
          ? node.link
          : node.href
          ? { name: node.href.name, params: getHrefParams(node) }
          : ''
      "
      exact-path
      :style="{
        color: node.txtcolor ? node.txtcolor : 'black',
        textDecoration: 'none',
      }"
    >
      <label style="display: flex; cursor: pointer">
        <input type="radio" :name="treeid" class="tree-node-selector" />
        <div
          tabindex="1"
          style="display: flex; flex-grow: 1"
          :class="{
            'tree-node': true,
            unselectable: true,
            'tree-node-styled': styled,
          }"
          @focus="open"
        >
          <img
            v-if="node.icon"
            :src="node.icon"
            style="height: 15px; width: 15px; padding: 0px; margin: 0px 5px"
          />

          <i
            v-if="node.expandIcon"
            :style="{
              color: node.iconcolor ? node.iconcolor : 'primary',
              fontSize: '13px',
              margin: '0px 7px 0px 3px',
              width: '13px',
            }"
            @click="toggle"
            :class="
              'fa fa-' +
              (node.getChildren
                ? childrenExpanded
                  ? 'chevron-down'
                  : 'chevron-right'
                : '')
            "
          ></i>

          <i
            v-if="!node.expandIcon"
            :style="{
              fontSize: '13px',
              margin: '0px 7px 0px 3px',
            }"
            @click="toggle"
            :class="
              'fa fa-' +
              (node.getChildren && childrenExpanded
                ? node.expandedIcon
                : node.collapsedIcon) +
              ` text--${node.iconcolor ? node.iconcolor : 'primary'}`
            "
          ></i>

          <span
            :maxchars="maxchars"
            style="flex-grow: 1; overflow-wrap: anywhere; display: inline;"
            v-html="node.name"
          >
          </span>

          <i
            v-if="node.newChildNode && createNew"
            class="treenode-icon fa fa-plus add-btn"
            @click="addChild"
          ></i>
        </div>
      </label>
    </router-link>
    <div
      :style="{
        marginLeft: '30px',
        display: childrenExpanded ? 'block' : 'none',
      }"
    >
      <TreeNode
        :key="cname"
        v-for="(child, cname) in node.children"
        :treeid="treeid"
        :node="child"
        :create-new="createNew"
        :picked-item="pickedItem"
        :styled="styled"
        :maxchars="maxchars"
      ></TreeNode>
    </div>
  </div>
</template>

<script>
export default {
  name: "TreeNode",
  props: [
    "node",
    "expanded",
    "createNew",
    "treeid",
    "pickedItem",
    "styled",
    "maxchars",
  ],
  data() {
    return {
      childrenExpanded: this.expanded == undefined ? false : this.expanded,
      expanding: false,
    };
  },
  created() {
    if (
      this.expanded ||
      (this.node.expanded && this.node.expanded(this.$route.path))
    ) {
      this.expandChildrenView(true);
    }
  },
  methods: {
    getHrefParams(node) {
      return node.getHrefParams ? node.getHrefParams() : node.params;
    },
    /* below two functions are reactive, so they wait and set the flag. */
    toggle(e) {
      if (e) e.stopPropagation();
      if (!this.childrenExpanded) this.expandChildrenView(true);
      else this.childrenExpanded = false;
    },
    expandChildrenView(fetchChildren) {
      if (fetchChildren)
        this.bringChildren().then(() => (this.childrenExpanded = true));
      else this.childrenExpanded = true;
    },
    addChild(e) {
      e.stopPropagation();
      let nn = this.node.newChildNode();
      nn.name += ` ${Object.keys(this.node.children).length}`;
      this.node.children.push(nn);
      this.expandChildrenView(false);
      this.$router.push({
        name: nn.href.name,
        params: nn.getHrefParams(),
      });
    },
    open(e) {
      e.stopPropagation();
      if (this.pickedItem) this.pickedItem.value = this.node;
    },
    bringChildren() {
      if (!this.expanding) {
        this.expanding = true;
        this.cgetprom = this.node.getChildren().then(() => {
          this.expanding = false;
        });
      }
      return this.cgetprom;
    },
  },
};
</script>

<style scoped>
.tree-node {
  display: flex;
  margin: 5px 0px;
  padding: 2px 0px;
  align-items: center;
  opacity: 0.8;
}

.tree-node > .add-btn {
  visibility: hidden;
}

.tree-node > .treenode-icon {
  font-size: 0.8em;
  margin-right: 15px;
}

.tree-node:hover > .add-btn {
  visibility: visible;
}

.router-link-exact-active .tree-node-styled {
  background: gainsboro;
  border-right: solid 5px seagreen;
}

.tree-node-styled:hover {
  background: gainsboro;
  opacity: 1;
}

.tree-node-selector {
  display: none;
}
</style>