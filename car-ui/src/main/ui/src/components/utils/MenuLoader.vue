<template>
  <div
    style="
      background: transparent;
      position: absolute;
      right: 0px;
      bottom: 0px;
      top: 0px;
      left: 0px;
    "
    @click="menuclose()"
  >
    <div
      class="menu"
      :style="{
        left: `${x}px`,
        top: `${y}px`,
      }"
    >
      <small
        v-for="(mitem, idx) in handlers"
        :key="idx"
        class="menuitem"
        @click="mitem.handler(arg)"
        >{{ mitem.menulabel }}</small
      >
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
export default {
  props: ["viewname", "event", "x", "y", "arg"],
  data() {
    return {
      handlers: [],
    };
  },
  created() {
    let that = this;
    car.loadPlugins().then((plugins) => {
      plugins.forEach((plugin) => {
        let view = plugin.handlers[that.viewname];
        if (!view) return;
        (view[that.event] || []).forEach((mitem) => {
          that.handlers.push(mitem);
        });
      });
    });
  },
  methods: {
    menuclose() {
      console.log("closing");
      this.$emit("menuclosed");
    },
  },
};
</script>

<style>
</style>