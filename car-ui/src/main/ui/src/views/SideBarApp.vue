<template>
  <div style="height: 100%; width: 100%; display: flex; color: black">
    <app-bar v-if="true" orientation="column"></app-bar>
    <router-view style="flex-grow: 1;" />
  </div>
</template>

<script>
import AppBar from "./AppBar.vue";
export default {
  components: { AppBar },
  name: "SideBarApp",
  data() {
    return {};
  },
};
</script>
