<template>
  <div>
    <div style="margin: 10px">
      <el-table
        size="small"
        :data="
          datasets.filter(
            (dataset) =>
              !search ||
              dataset.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        style="width: 100%"
      >
        <el-table-column label="Name">
          <template slot-scope="scope">
            <b>{{ scope.row.name }}</b>
          </template>
        </el-table-column>
        <el-table-column label="Types">
          <template slot-scope="scope">
            <el-tag size="mini" type="info" style="margin: 0px 2px"
              >{{ scope.row.files.length }} files</el-tag
            >
            <el-tag
              v-for="tag in scope.row.types"
              :key="tag"
              size="mini"
              style="margin: 0px 2px"
              >{{ tag }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="Description" prop="description">
        </el-table-column>
        <el-table-column label="Status">
          <template slot-scope="scope">
            <el-tag
              size="mini"
              effect="light"
              :type="calcDSState(scope.row.detailStatus.status)"
              >{{ scope.row.detailStatus.status.toLowerCase() }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column align="right">
          <template slot="header">
            <el-input
              v-model="search"
              size="mini"
              placeholder="Type to search"
            />
          </template>
          <template slot-scope="scope">
            <el-link
              style="margin: 0px 5px"
              type="info"
              :href="'/ui/explore/' + scope.row.name"
              >explore</el-link
            >
            <el-link
              style="margin: 0px 5px"
              type="danger"
              :href="'/ui/explore/' + scope.row.name"
              >delete</el-link
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div>
      <!-- <div class="dataset" :key="dataset.name" v-for="dataset in datasets">
        <span style="font-size: 1.5em">{{ dataset.name }}</span>
        <div>
          <div
            :key="type"
            v-for="type in dataset.types"
            style="
              display: inline-block;
              background: #fafae0;
              padding: 2px 5px;
              border: solid 1px gray;
              border-radius: 4px;
              margin: 2px;
              font-size: 0.8em;
            "
          >
            <span>{{ type }}</span>
          </div>
          |
          <span
            v-if="dataset.files && dataset.files.length > 0"
            style="font-size: 0.9em"
            >{{ dataset.files.length }} files</span
          >
        </div>
        <div>
          <a
            href="#"
            @click="editDataSet(dataset)"
            style="font-size: 0.9em; margin: 0px 3px"
            >Edit</a
          >
          <a href="#" @click="analyze" style="font-size: 0.9em; margin: 0px 3px"
            >Analyze</a
          >
        </div>
        <input type="file" @change="upload($event, dataset.name)" />
        <span>{{ dataset.description }}</span>
      </div>
      <div class="dataset" @click="createDataset">
        <span style="font-size: 72px; color: skyblue">+</span>
        <span>New dataset</span>
      </div> -->
    </div>
    <el-dialog title="Tips" :visible="newdataset != null" width="30%">
      <span>This is a message</span>
    </el-dialog>
    <div class="modal" v-if="newdataset">
      <h2>New dataset</h2>
      <div class="form">
        <div class="form-col">
          <label>Name</label>
          <input v-model="newdataset.name" />
        </div>
        <div class="form-row">
          <label>Log types:</label>
          <br />
          <div>
            <div
              :key="type"
              style="
                display: inline-block;
                background: #fafae0;
                padding: 2px 5px;
                border: solid 1px gray;
                border-radius: 4px;
                margin: 2px;
              "
              v-for="type in newdataset.types"
            >
              <span>{{ type }}</span>
              <i
                style="font-size: 0.8em"
                @click="deleteTypeFromNewDS(type)"
                class="fa fa-minus-circle"
              ></i>
            </div>
          </div>
          <div class="form-row">
            <select ref="logTypeGroup">
              <option :key="lgrp" v-for="lgrp in logTypeGroups">
                {{ lgrp }}
              </option>
            </select>
            <button @click="addTypeToNewDS">add</button>
          </div>
        </div>
        <div class="form-col">
          <label>Description</label>
          <textarea v-model="newdataset.description"></textarea>
        </div>
        <div class="form-row">
          <button @click="postDataset">Save</button>
          <button @click="newdataset = null">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { car } from "@/car.js";
import Dataset from "@/models/Dataset.js";
export default {
  name: "Datasets",
  data() {
    return {
      datasets: [],
      newdataset: null,
      logTypeGroups: [],
      search: "",
    };
  },
  props: ["navigator"],
  created() {
    this.refresh();
    car
      .getLogGroupTypes()
      .then((lgts) => lgts.forEach((lgt) => this.logTypeGroups.push(lgt.name)));
  },
  methods: {
    calcDSState(status) {
      switch (status.toLowerCase()) {
        case "done":
          return "success";
        case "failed":
          return "danger";
        case "detection_done":
          return "info";
        case "indexing_failed":
          return "danger";
        case "extracting":
          return "info";
      }
    },
    editDataSet(ds) {
      this.newdataset = JSON.parse(JSON.stringify(ds));
      this.newdataset.oldds = ds;
    },
    refresh() {
      car.getDatasets().then((datasets) => {
        datasets.forEach((dataset) => this.datasets.push(new Dataset(dataset)));
      });
    },
    createDataset() {
      this.newdataset = new Dataset();
    },
    postDataset() {
      car.createDataset(this.newdataset);
      if (this.newdataset.oldds) {
        Object.keys(this.newdataset).forEach(
          (k) => (this.newdataset.oldds[k] = this.newdataset[k])
        );
        delete this.newdataset.oldds["oldds"];
      }
      this.newdataset = null;
    },
    addTypeToNewDS() {
      if (!this.newdataset.types.includes(this.$refs.logTypeGroup.value))
        this.newdataset.types.push(this.$refs.logTypeGroup.value);
    },
    deleteTypeFromNewDS(typ) {
      this.newdataset.types.splice(this.newdataset.types.indexOf(typ), 1);
    },
    analyze() {
      this.navigator("analyze");
    },
    upload(e, dsetname) {
      car.upload(dsetname, e.target.files[0]);
    },
  },
};
</script>

<style scoped>
.dataset {
  border: solid 2px lightgray;
  padding: 10px;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  margin: 10px;
  cursor: pointer;
  width: 250px;
  height: 300px;
}
</style>
