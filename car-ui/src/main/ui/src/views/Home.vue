<template>
  <div
    v-if="true"
    style="
      height: 100%;
      width: 100%;
      display: flex;
      flex-direction: column;
      color: black;
    "
  >
    <app-bar orientation="row"></app-bar>
    <dset-search-view
      style="padding-top: 35px; background-color: #fafafa; flex-grow: 1;"
      @picked="openDs"
    ></dset-search-view>
  </div>
</template>

<script>
import AppBar from "./AppBar.vue";
import DsetSearchView from "./DsetSearchView.vue";

export default {
  components: { DsetSearchView, AppBar },
  name: "Home",
  methods: {
    openDs(ds) {
      console.log(ds);
      this.$router.push({
        name: "datasetdetail",
        params: {
          dsetid: ds.id,
        },
      });
    },
  },
};
</script>

<style scoped>
.side-link {
  cursor: pointer;
  width: 100%;
  padding: 5px 20px;
}
.side-link:hover {
  background: #fafafa;
}
</style>