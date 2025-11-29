<template>
  <div style="height: 100vh; width: 100%">
    <LogBrowser
      style="height: calc(100%); width: calc(100%); padding-top: 5px"
      :dsetid="$route.params.dsetid"
      :sortFields="[{ first: '_line', second: 'asc' }]"
    ></LogBrowser>
  </div>
</template>

<script>
import LogBrowser from "../components/visualizations/LogBrowser.vue";
export default {
  components: { LogBrowser },
  methods: {
    enc(x) {
      return encodeURIComponent(x);
    },
  },
};
</script>

<style>
.splitpanes__pane {
  overflow: none !important;
}
</style>