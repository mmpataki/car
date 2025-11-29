<template>
  <div>
    <div style="display: flex; width: 100%; justify-content: center">
      <div style="width: 45%; display: flex; align-items: center">
        <v-text-field
          placeholder="search all datasets"
          outlined
          dense
          append-icon="mdi-magnify"
          v-model="filter"
          @input="filterChanged"
          hide-details=""
        ></v-text-field>
        <router-link to="/ui/datasets/untitled/new">
          <v-btn color="primary" class="ml-1">Create</v-btn>
        </router-link>
      </div>
    </div>
    <div
      style="
        width: 100%;
        padding: 0px 20px;
        display: flex;
        align-items: baseline;
      "
    >
      <small>{{ whatIsShown }}</small>
      <span style="flex-grow: 1"></span>
      <small>
        <b style="padding-bottom: 5px">{{ datasets.length }}</b> datasets match
      </small>
    </div>
    <div
      v-if="datasets"
      style="flex-grow: 1; overflow: auto; max-height: 600px"
    >
      <v-row class="ma-2">
        <v-col :cols="getColWidth" :key="idx" v-for="(ds, idx) in datasets">
          <v-card
            class="card fill-height ds-card"
            :key="idx"
            @click="$emit('picked', ds)"
          >
            <span
              class="pin-btn"
              style="position: absolute; top: 3px; right: 3px"
            >
              <v-btn
                icon
                rounded
                fab
                x-small
                @click="togglePin(ds)"
                :color="ds.pinned ? 'warning' : 'gray'"
                :title="ds.pinned ? 'Unpin' : 'Pin'"
              >
                <v-icon small>{{
                  ds.pinned ? "mdi-pin-off" : "mdi-pin"
                }}</v-icon>
              </v-btn>
            </span>
            <v-card-text>
              <strong class="text--primary">{{ ds.name }}</strong>
              <div>
                <span
                  >{{ ds.numFiles }} files
                  <strong style="margin: 0px 6px">|</strong> owner: {{ ds.owner
                  }}<br />
                  <span
                    style="
                      padding: 10px 0px 0px 0px;
                      display: inline-block;
                      word-wrap: break-word;
                      overflow-wrap: break-word;
                      max-width: 100%;
                    "
                    v-html="
                      ds.description.length > maxChars
                        ? ds.description.substr(0, maxChars) + '...'
                        : ds.description
                    "
                  ></span>
                </span>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
      <div
        v-if="loading"
        style="
          height: 100%;
          width: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
        "
      >
        <v-progress-circular
          color="primary"
          indeterminate
          size="32"
        ></v-progress-circular>
      </div>
    </div>
  </div>
</template>
<script>
import { car } from "@/car.js";

export default {
  name: "DsetSearchView",
  data() {
    return {
      datasets: [],
      filter: "",
      loading: true,
      screenWidth: 0,
      pinned: [],
      maxChars: 200,
      whatIsShown: "",
      createDialog: true,
    };
  },
  mounted() {
    this.screenWidth = window.innerWidth; // Get the initial screen width on mount
    window.addEventListener("resize", this.handleResize); // Add event listener for window resize
  },
  created() {
    Promise.all([car.getPinned(), car.getDatasets(this.filter, 0, 24)]).then(
      ([pinned, datasets]) => {
        this.pinned = pinned;
        this.updateDSList(datasets);
      }
    );
  },
  computed: {
    getColWidth() {
      // Calculate the column width based on screen width
      if (this.screenWidth > 1536) {
        return 2; // Set to 4 for larger screens
      } else {
        return 3; // Set to 6 for medium screens
      }
    },
  },
  methods: {
    updateDSList(datasets) {
      this.whatIsShown =
        this.filter.trim() != "" || !this.pinned.length
          ? "Showing search results"
          : "Showing your pinned datasets";
      datasets =
        this.filter.trim() != "" || !this.pinned.length
          ? datasets
          : this.pinned;
      let pinned = this.pinned.reduce((m, p) => ((m[p.id] = true), m), {});
      console.log("pinned", pinned);
      this.loading = false;
      let uid = car.currentUid();
      this.datasets = datasets
        // .filter((ds) => ds.numFiles)
        .map((d) => {
          if (d.owner != uid) d.picked = false;
          d.pinned = pinned[d.id];
          return d;
        });
    },
    filterChanged() {
      car
        .getDatasets(this.filter, 0, 24)
        .then((datasets) => this.updateDSList(datasets));
    },
    togglePin(ds) {
      (ds.pinned ? car.unPinDataset(ds.id) : car.pinDataset(ds.id)).then(() => {
        car.success(`${ds.pinned ? "Unpinned" : "Pinned"} ${ds.id}`);
        this.$set(ds, "pinned", !ds.pinned);
      });
    },
    handleResize() {
      this.screenWidth = window.innerWidth; // Update the screen width on window resize
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize); // Remove event listener on component destroy
  },
};
</script>

<style scoped>
.pin-btn {
  /* display: none; */
}

.ds-card:hover .pin-btn {
  display: block;
}

.side-link {
  cursor: pointer;
  width: 100%;
  padding: 5px 20px;
}
.side-link:hover {
  background: #fafafa;
}

.search-box::placeholder {
  color: white;
}
.search-box {
  background: transparent;
  border-radius: 5px;
  padding: 2px 10px;
  width: 100%;
  border: solid 1px white;
  color: white;
}
.search-box:focus {
  background: white;
  color: black;
}
</style>