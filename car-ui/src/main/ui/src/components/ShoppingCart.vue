<template>
  <v-dialog style="padding: 30px; background: ghostwhite" width="100%" v-model="shopping">
    <v-card style="background: ghostwhite">
      <v-card-title class="text-h5 mb-2"> All datasets</v-card-title>
      <div style="position: absolute; top: 20px; right: 30px">
        <v-text-field
          append-icon="mdi-magnify"
          dense
          hide-details="true"
          v-model="filter"
        ></v-text-field>
      </div>
      <v-card-text style="overflow: auto; height: calc(100vh - 220px)">
        Pick datasets you want to pin
        <div style="height: calc(100% - 150px)">
          <v-row class="ma-2">
            <v-col cols="2" :key="idx" v-for="(ds, idx) in filteredOnes">
              <v-card
                class="card fill-height"
                :style="{
                  cursor: 'pointer',
                  background: ds.picked ? 'gainsboro' : 'ghostwhite',
                }"
                :key="idx"
                @click="
                  (dataset = ds.name),
                    (markerStep = 2),
                    (ds.picked = !ds.picked)
                "
              >
                <v-card-text>
                  <strong class="text--primary">{{ ds.name }}</strong>
                  <div>
                    <small
                      >{{ ds.numFiles }} files
                      <strong style="margin: 0px 6px">|</strong> owner:
                      {{ ds.owner }}<br />
                      {{ ds.desc }}
                    </small>
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
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" small @click="pinDatasets">
          Pick {{ pickedOnes.length ? `(${pickedOnes.length})` : "" }}
        </v-btn>
        <v-btn color="red darken-1" small text @click="$emit('closed')">
          Cancel
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { car } from "@/car.js";
export default {
  data() {
    return {
      shopping: true,
      datasets: [],
      filter: "",
      loading: true,
    };
  },
  created() {
    car.getAllDatasets().then((datasets) => {
      this.loading = false;
      let uid = car.currentUid()
      this.datasets = datasets.filter(ds => ds.owner != uid).map((d) => {
        d.picked = false;
        return d;
      });
    });
  },
  computed: {
    pickedOnes() {
      return this.datasets.filter((ds) => ds.picked);
    },
    filteredOnes() {
      return this.datasets.filter(
        (ds) =>
          ds.name.toLowerCase().includes(this.filter.toLowerCase()) ||
          ds.desc.toLowerCase().includes(this.filter.toLowerCase()) ||
          ds.owner.toLowerCase().includes(this.filter.toLowerCase())
      );
    },
  },
  methods: {
    pinDatasets() {
      car
        .savePinned(this.pickedOnes.map((ds) => ds.id))
        .then(() => {
          car.success("Saved");
          this.$emit("picked", this.pickedOnes);
        });
    },
  },
};
</script>

<style>
</style>