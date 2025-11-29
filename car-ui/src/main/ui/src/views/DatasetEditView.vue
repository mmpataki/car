<template>
  <div
    style="
      height: 100vh;
      width: 100%;
      overflow: auto;
      padding: 10px 20px;
      position: relative;
    "
  >
    <div v-if="dataset && dataset.loaded" style="height: 100%; width: 100%">
      <h2>{{ newDs ? "Create" : "Edit" }} dataset</h2>
      <div class="my-4">
        <v-text-field
          v-model="dataset.name"
          :counter="24"
          :rules="nameRules"
          label="Name"
          required
          outlined
          dense
          :disabled="!newDs"
          class="my-4"
        ></v-text-field>
        <v-textarea
          outlined
          label="Description"
          dense
          v-model="dataset.description"
          class="my-4"
        ></v-textarea>
        <v-combobox
          v-model="dataset.types"
          :items="logtypegroups"
          label="Pick log type groups"
          multiple
          small-chips
          outlined
          dense
          class="my-4"
        ></v-combobox>
        <v-select
          dense
          outlined
          label="Time zone offset"
          v-model="dataset.timeZoneOffset"
          :items="timeZones"
          class="my-4"
        ></v-select>
        <div class="d-flex my-2" style="align-items: center">
          <v-checkbox
            class="mr-8"
            v-model="dataset.localDataset"
            label="Local dataset"
          ></v-checkbox>
          <v-text-field
            v-if="dataset.localDataset"
            v-model="dataset.localPath"
            label="Path to log files directory"
            outlined
            dense
          ></v-text-field>
        </div>
      </div>
      <div class="mt-5">
        <v-btn color="primary" @click="save" small>Save</v-btn>
        <v-btn color="error" class="mx-2" text @click="cancelEdit()" small
          >Cancel</v-btn
        >
      </div>
    </div>
    <v-overlay
      :value="!dataset || !dataset.loaded"
      color="white"
      :absolute="true"
    >
      <v-progress-circular
        indeterminate
        size="32"
        color="primary"
      ></v-progress-circular>
    </v-overlay>
  </div>
</template>

<script>
import { car } from "@/car.js";
import Dataset from "@/models/Dataset.js";

export default {
  data() {
    return {
      dataset: undefined,
      timeZones: [],
      logtypegroups: [],
      newDs: this.$route.path.endsWith("/new"),
    };
  },
  created() {
    if (this.newDs) {
      this.dataset = new Dataset({
        name: "new dataset",
        loaded: true,
      });
    } else {
      car.getDataset(this.$attrs.dsetid).then((ds) => {
        this.dataset = ds;
      });
    }

    this.timeZones.push({ text: `-12:00`, value: "-1200" });
    for (var i = -11; i < 12; i++) {
      let j =
        (i > -1 ? "+" : "-") + (Math.abs(i) < 10 ? "0" : "") + Math.abs(i);
      this.timeZones.push({ text: `${j}:30`, value: `${j}30` });
      this.timeZones.push({ text: `${j}:00`, value: `${j}00` });
    }
    this.timeZones.push({ text: `+12:00`, value: `+1200` });

    car
      .getLogGroupTypes()
      .then((lgt) => (this.logtypegroups = lgt.map((g) => g.name)));
  },
  methods: {
    cancelEdit() {
      this.$router.go(-1);
    },
    save() {
      car.createDataset(this.dataset).then((ds) => {
        car.success(
          `dataset [${this.dataset.name}] ${this.newDs ? "created" : "saved"}`
        );
        this.$router.push({
          name: `datasetdetail`,
          params: {
            dsetid: ds.id,
          },
        });
      });
    },
  },
};
</script>

<style>
</style>