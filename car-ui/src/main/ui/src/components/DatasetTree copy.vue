<template>
  <div
    style="
      height: calc(100vh - 48px);
      margin: 0px 0px 0px 15px;
      position: relative;
    "
  >
    <v-text-field
      v-model="filter"
      append-icon="mdi-magnify"
      class="mr-2 mb-4"
      dense
      v-if="showsearchbar"
      hide-details="true"
    ></v-text-field>

    <div
      style="height: 100%; margin: 0; overflow: auto; height: calc(100% - 20px)"
      v-if="datasets.length"
    >
      <TreeView
        v-for="(ds, idx) in filteredDatasets"
        :root="ds"
        :key="ds.name"
        :createNew="false"
        :treeid="'idx / ' + idx"
        style="overflow: none"
        :styled="true"
      ></TreeView>
    </div>
    <div
      v-if="!datasets.length"
      style="
        height: 100%;
        margin: 0;
        overflow: auto;
        height: calc(100% - 20px);
        display: flex;
        align-items: center;
        padding: 30px;
      "
    >
      <div>
        Looks like, you don't have any datasets, either create one or
        <v-btn x-small @click="shopping = true" color="primary">pick</v-btn>
        from existing ones.
      </div>
    </div>
    <v-btn
      color="primary"
      dark
      x-small
      style="position: absolute; bottom: 50px; right: 10px"
      title="Create new dataset"
      fab
      v-if="loggedIn"
      @click="createNewDataset"
    >
      <v-icon>mdi-plus</v-icon>
    </v-btn>
    <v-btn
      color="primary"
      dark
      x-small
      style="position: absolute; bottom: 10px; right: 10px"
      fab
      title="Find datasets"
      @click="openShopping"
    >
      <v-icon>mdi-shopping</v-icon>
    </v-btn>
    <v-icon
      v-if="!showsearchbar"
      @click="showsearchbar = !showsearchbar"
      style="position: absolute; right: 5px; top: 5px"
      >mdi-magnify</v-icon
    >
    <ShoppingCart
      v-if="shopping"
      @closed="shopping = false"
      @picked="datasetsPicked($event)"
    ></ShoppingCart>
  </div>
</template>

<script>
import { car } from "@/car.js";
import Dataset from "@/models/Dataset.js";
import TreeView from "@/components/utils/TreeView.vue";
import ShoppingCart from "./ShoppingCart.vue";
export default {
  name: "DatasetTree",
  components: { TreeView, ShoppingCart },
  data() {
    return {
      datasets: [],
      shopping: false,
      filter: "",
      showsearchbar: false,
    };
  },
  computed: {
    loggedIn() {
      return car.isLoggedIn();
    },
    filteredDatasets() {
      return this.datasets.filter((x) =>
        x.name.toLowerCase().includes(this.filter.toLowerCase())
      );
    },
  },
  created() {
    car.getDatasets().then((dss) => {
      dss.map((ds) => {
        this.datasets.push(ds);
      });
    });
  },
  methods: {
    createNewDataset() {
      this.$router.push({
        name: `newdataset`,
        params: {
          dsetid: "create",
        },
      });
    },
    openShopping() {
      this.shopping = true;
    },
    datasetsPicked(picked) {
      picked.forEach((ds) => {
        this.datasets.push(new Dataset(ds));
      });
    },
  },
};
</script>
