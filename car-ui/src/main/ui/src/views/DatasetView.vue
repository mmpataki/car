<template>
  <div
    style="
      min-height: 100vh;
      width: 100%;
      overflow: auto;
      padding: 30px;
      position: relative;
    "
  >
    <div v-if="dataset && dataset.loaded" style="height: 100%; width: 100%">
      <div class="d-flex my-2" style="align-items: flex-end">
        <h2 class="d-inline-block text-primary">
          {{ dataset.name }}
        </h2>
        <h4 class="d-inline-block ml-4" style="margin-bottom: 5px">
          ({{ dataset.timeZoneOffset || getMyTimeZone() }})
        </h4>
      </div>
      <div>
        <v-chip
          :key="tag"
          v-for="tag in dataset.types"
          class="mr-2"
          color="#1E88E5"
          x-small
          text-color="white"
        >
          <v-icon left> mdi-label </v-icon>
          {{ tag }}
        </v-chip>

        <v-chip class="mr-2" color="secondary" x-small text-color="white">
          <v-icon left> mdi-account </v-icon>
          {{ dataset.owner }}
        </v-chip>

        <div style="float: right">
          <v-btn
            @click="openEditPage"
            x-small
            color="secondary"
            dark
            class="mr-2"
            >Edit</v-btn
          >
          <v-btn
            @click="runExtract"
            :loading="extractRunning"
            x-small
            color="primary"
            dark
            class="mr-2"
            >Extract</v-btn
          >
          <v-btn
            @click="cleanup"
            :loading="cleaupRunning"
            x-small
            color="error"
            dark
            >Cleanup</v-btn
          >
        </div>
      </div>
      <div class="mt-8">
        <p class="text-body-1">{{ dataset.description }}</p>
      </div>
      <div v-if="dataset.localDataset" class="my-5">
        <span class="text-h6">Local dataset path</span>
        <p class="text-body-1">{{ dataset.localPath }}</p>
      </div>

      <v-tabs v-model="tab">
        <v-tab>Files</v-tab>
        <v-tab>Analysis</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item>
          <v-card flat>
            <file-upload-zone :dataset="dataset.id">
              <div style="padding: 0px 20px 40px 20px">
                <small
                  style="
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    color: lightgray;
                  "
                  >drag a file here to add it to this dataset</small
                >
                <div class="my-5 py-3">
                  <span class="text-h6"
                    >Files ({{ dataset.files.length }})</span
                  >
                  <div style="float: right; display: block; margin-top: 20px">
                    <input
                      ref="filePicker"
                      style="display: none"
                      type="file"
                      @change="uploadFile"
                    />
                    <v-text-field
                      v-model="search"
                      label="Search files"
                      class="mx-4"
                      style="display: inline-block"
                      append-icon="mdi-magnify"
                    ></v-text-field>
                    <v-btn
                      color="blue-grey"
                      small
                      class="white--text"
                      @click="openFilePicker"
                    >
                      {{ uploading ? "Uploading " : "Upload" }}
                      <v-icon v-if="!uploading" right dark>
                        mdi-cloud-upload
                      </v-icon>
                      <v-progress-circular
                        size="15"
                        indeterminate
                        color="white"
                        class="ml-2"
                        v-if="uploading"
                      >
                      </v-progress-circular>
                    </v-btn>
                    <v-btn
                      color="blue-grey"
                      small
                      class="white--text ml-1"
                      @click="downloadAll()"
                    >
                      Download all
                      <v-icon v-if="!uploading" right dark>
                        mdi-cloud-download
                      </v-icon>
                    </v-btn>
                  </div>
                </div>

                <v-data-table
                  dense
                  :headers="filelistheaders"
                  :items="pathList"
                  item-key="name"
                  class="elevation-1"
                  :calculate-widths="true"
                  :custom-filter="filter"
                  :search="search"
                  style="margin-top: 50px"
                >
                  <template v-slot:item="item">
                    <table-row :item="item"></table-row>
                  </template>
                </v-data-table>
              </div>
            </file-upload-zone>
          </v-card>
        </v-tab-item>
        <v-tab-item>
          <analysis-view
            :analyticskey="dataset.name"
            :dset="dataset"
          ></analysis-view>
        </v-tab-item>
      </v-tabs-items>
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
import AnalysisView from "../components/analysis/AnalysisView.vue";
import TableRow from "@/components/utils/TableRow.vue";
import FileUploadZone from "../components/utils/FileUploadZone.vue";
export default {
  name: "DatasetView",
  components: { TableRow, FileUploadZone, AnalysisView },
  data() {
    return {
      tab: 0,
      dataset: null,
      logtypegroups: [],
      uploading: false,
      uploadProgress: "",
      timeZones: [],
      search: "",
      nameDisabled: false,
      extractRunning: false,
      cleaupRunning: false,
      filelistheaders: [
        {
          text: "Path",
          align: "start",
          sortable: true,
          value: "path",
          sort: (a, b) => a.linktext.localeCompare(b.linktext),
        },
        {
          text: "Reports",
          align: "start",
          sortable: false,
          value: "reports",
          filterable: false,
        },
        {
          text: "Size",
          align: "start",
          sortable: true,
          value: "size",
          sort: (a, b) => a.val - b.val,
          filterable: false,
        },
        {
          text: "File type",
          align: "start",
          sortable: true,
          value: "fileType",
          filterable: true,
        },
        {
          text: "Status",
          align: "start",
          sortable: true,
          value: "analyzed",
          filterable: false,
        },
        {
          text: "Actions",
          align: "start",
          sortable: false,
          value: "actions",
          filterable: false,
        },
      ],
    };
  },
  created() {
    if (this.$attrs.dataset) {
      this.dataset = this.$attrs.dataset;
      if (!this.dataset.loaded) {
        this.dataset.loadDetails();
      }
    } else {
      car.getDataset(this.$attrs.dsetid).then((ds) => {
        this.dataset = ds;
      });
    }
    this.startStatusFetchThread();
  },
  computed: {
    pathList() {
      function humanFileSize(size) {
        const units = ["B ", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];
        let i = 0;
        while (size >= 1024) {
          size /= 1024;
          i++;
        }
        return `${size.toFixed(2)}${units[i]}`;
      }

      let fsm = this.dataset.fileStatus;
      let enc = encodeURIComponent;
      return [...new Set([...Object.keys(fsm), ...this.dataset.files])].map(
        (k) => {
          let status = (
            fsm[k].state +
            (fsm[k].state == "EXTRACTING"
              ? ` (${(100 * fsm[k].processPercent).toFixed(0)}%)`
              : "")
          ).toLowerCase();
          return {
            fileType: {
              text:
                fsm[k] && fsm[k].logTypeGroup && fsm[k].logType
                  ? `${fsm[k].logTypeGroup || ""} / ${fsm[k].logType || ""}`
                  : "",
              style: "white-space: nowrap; overflow-wrap: nowrap",
            },
            size: {
              text: humanFileSize(fsm[k].size),
              val: fsm[k].size,
              style: { textAlign: "right", width: "100%" },
            },

            path: fsm[k]
              ? {
                  link:
                    fsm[k].state == "EXTRACTED"
                      ? `/ui/datasets/${enc(
                          this.dataset.id
                        )}/browse?_file=is,${enc(k)}`
                      : "",
                  linktext: k,
                  truncateText: true,
                }
              : k,
            reports: fsm[k]
              ? {
                  link:
                    fsm[k].state == "EXTRACTED"
                      ? `/ui/datasets/${enc(
                          this.dataset.id
                        )}/reports/default/${enc(k)}`
                      : "",
                  linktext: "reports",
                }
              : "",
            actions: [
              {
                btntext: "Analyze",
                btnicon: "mdi-refresh",
                color: "gray",
                func: () => {
                  car.analyzeFile(this.dataset.id, k);
                },
              },
              {
                btntext: "Download",
                btnicon: "mdi-arrow-collapse-down",
                color: "gray",
                func: () => {
                  car.downloadFiles(this.dataset.id, [k]);
                },
              },
            ],
            analyzed: {
              statustext:
                fsm[k].jobUrl && !["NOTDONE", "NEW"].includes(fsm[k].state)
                  ? `<a target="_blank" href='${fsm[k].jobUrl}'>${status}</a>`
                  : status,
              value: (fsm[k].state || "").toLowerCase().endsWith("ing"),
              color: "success",
              size: 12,
              width: 3,
            },
          };
        }
      );
    },
    taskRunning() {
      if (!this.dataset) return false;
      return (
        Object.values(this.dataset.fileStatusMap).filter((s) =>
          s.state.toLowerCase().endsWith("ing")
        ).length != 0
      );
    },
  },
  methods: {
    filter(value, text) {
      return ("" + JSON.stringify(value)).includes(text);
    },
    getMyTimeZone() {
      var offset = new Date().getTimezoneOffset(),
        o = Math.abs(offset);
      return (
        (offset < 0 ? "+" : "-") +
        ("00" + Math.floor(o / 60)).slice(-2) +
        ":" +
        ("00" + (o % 60)).slice(-2)
      );
    },
    uploadFile(e) {
      this.uploading = true;
      car
        .upload(this.dataset.id, e.target.files[0], (e) => {
          if (e.lengthComputable) {
            this.uploadProgress = Math.round((e.loaded * 100) / e.total);
          }
        })
        .then(() => this.refresh())
        .then(() => {
          this.uploading = false;
        });
    },
    openFilePicker() {
      if (!this.uploading) this.$refs.filePicker.click();
    },

    runExtract() {
      this.extractRunning = true;
      car.analyze(this.dataset.id).finally(() => {
        this.extractRunning = false;
      });
    },
    startStatusFetchThread() {
      this.refreshTimer = window.setTimeout(() => this.getStatus(), 3000);
    },
    getStatus() {
      this.dataset.updateStatus().then(() => this.startStatusFetchThread());
    },
    cleanup() {
      this.cleaupRunning = true;
      car.cleanup(this.dataset.id).finally(() => {
        this.cleaupRunning = false;
      });
    },
    openEditPage() {
      this.$router.push(`/ui/datasets/${this.dataset.id}/edit`);
    },
    downloadAll() {
      car.downloadFiles(this.dataset.id, []);
    },
  },
  destroyed() {
    if (this.refreshTimer) window.clearTimeout(this.refreshTimer);
  },
};
</script>

<style>
</style>