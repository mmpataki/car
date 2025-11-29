<template>
  <div
    @dragenter="($refs.dropzone.style.display = 'flex'), (hover = true)"
    style="border: dashed 1px lightgray; position: relative"
  >
    <div style="position: absolute; top: 10px; right: 10px; color: lightgray">
      <slot name="message"></slot>
    </div>

    <slot></slot>
    <div
      ref="dropzone"
      @dragleave="($refs.dropzone.style.display = 'none'), (hover = false)"
      @drop="fileDropped($event)"
      @dragover="$event.preventDefault()"
      style="
        display: none;
        position: absolute;
        flex-direction: column;
        top: 0px;
        right: 0px;
        left: 0px;
        bottom: 0px;
        align-items: center;
        justify-content: center;
        border: dashed 2px seagreen;
        background: white;
      "
    >
      <strong>Drop the file here</strong>
    </div>

    <v-dialog v-model="showDialog" persistent max-width="400">
      <v-card>
        <v-card-title class="text-h5">
          Upload a file in {{ dataset }}</v-card-title
        >
        <v-card-text>
          <div
            style="
              display: flex;
              flex-direction: column;
              height: 100%;
              width: 100%;
              margin-top: 10px;
            "
          >
            <div
              v-if="!uploading"
              style="
                height: 80px;
                display: flex;
                justify-content: center;
                margin: 10px 0px;
              "
            >
              <div style="position: relative; margin: 0px 20px">
                <v-icon size="72px" color="brown">mdi-file</v-icon>
                <v-icon
                  style="position: absolute; bottom: 10px; right: 5px"
                  size="30px"
                  :color="validFile ? 'success' : 'error'"
                  >{{
                    validFile
                      ? "mdi-checkbox-marked-circle"
                      : "mdi-close-circle"
                  }}</v-icon
                >
              </div>
              <div
                style="
                  flex-grow: 1;
                  display: flex;
                  flex-direction: column;
                  justify-content: center;
                "
              >
                <div>
                  <v-icon small :color="fileChoosen ? 'success' : 'error'">{{
                    fileChoosen ? "mdi-check" : "mdi-close"
                  }}</v-icon>
                  <small style="margin-left: 5px">File is choosen</small>
                </div>
                <div>
                  <v-icon small :color="validFile ? 'success' : 'error'">{{
                    validFile ? "mdi-check" : "mdi-close"
                  }}</v-icon>
                  <small style="margin-left: 5px"
                    >File is smaller than 20MB</small
                  >
                </div>
              </div>
            </div>

            <div
              v-if="uploading"
              style="
                height: 80px;
                display: flex;
                flex-direction: column;
                align-items: center;
                margin: 10px 0px;
              "
            >
              <v-progress-circular
                color="primary"
                indeterminate
                size="32"
              ></v-progress-circular>
              <small style="margin: 10px 0px">{{
                uploading ? "Uploading file" : ""
              }}</small>
            </div>
            <strong style="margin: 0px 20px">
              File<br /><small>{{ fileChoosen.name }}</small></strong
            >
            <div v-if="notArchive" style="height: 160px; margin: 20px 10px">
              <strong style="display: block; margin: 10px 10px"
                >Do you want to hint us on type of file</strong
              >
              <div
                style="
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                "
              >
                <v-select
                  :items="['autodetect', ...logTypeGroups]"
                  label="Log-type group"
                  class="my-2"
                  v-model="logTypeGroup"
                  color="primary"
                  style="width: 310px"
                  :disabled="uploading"
                  @change="lgtChanged"
                ></v-select>
                <v-select
                  :items="['autodetect', ...logTypes]"
                  label="Log type"
                  color="primary"
                  v-model="logType"
                  :disabled="uploading"
                  class="my-2"
                  style="width: 310px"
                ></v-select>
              </div>
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="red darken-1" small text @click="showDialog = false">
            Cancel
          </v-btn>
          <v-btn
            color="primary"
            :disabled="!validFile"
            small
            @click="fileUploaded()"
          >
            Upload
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { car } from "@/car.js";
export default {
  props: ["dataset"],
  data() {
    return {
      uploading: false,
      hover: false,
      fileChoosen: false,
      logTypes: [],
      logTypeGroups: [],
      logTypeGroup: "autodetect",
      logType: "autodetect",
      showDialog: false,
    };
  },
  created() {
    car.getLogGroupTypes().then((x) => {
      this.logTypeGroups = x.map((lgt) => lgt.name);
    });
  },
  computed: {
    validFile() {
      return this.fileChoosen && this.fileChoosen.size < 1024 * 1024 * 20;
    },
    notArchive() {
      return (
        this.fileChoosen &&
        [".zip", ".gz", ".tar", ".tgz", ".rar"].filter((x) =>
          this.fileChoosen.name.includes(x)
        ).length == 0
      );
    },
  },
  methods: {
    lgtChanged() {
      this.logTypes = [];
      this.logType = "autodetect";
      car.getLogTypes(this.logTypeGroup).then((x) => {
        this.logTypes = x.map((lt) => lt.name);
      });
    },
    fileDropped(ev) {
      ev.preventDefault();
      if (ev.dataTransfer.items) {
        if (ev.dataTransfer.items[0].kind === "file")
          this.fileChoosen = ev.dataTransfer.items[0].getAsFile();
      } else {
        this.fileChoosen = ev.dataTransfer.files[0];
      }
      this.$refs.dropzone.style.display = "none";
      this.hover = false;
      this.showDialog = true;
      console.log(this.fileChoosen);
    },
    fileUploaded() {
      this.uploading = true;
      car
        .upload(
          this.dataset,
          this.fileChoosen,
          this.logTypeGroup == "autodetect" ? undefined : this.logTypeGroup,
          this.logType == "autodetect" ? undefined : this.logType,
          (e) => {
            console.log(e);
          }
        )
        .then((resp) => {
          this.showDialog = false;
          if (resp.response) {
            this.$router.push(
              `/ui/datasets/${
                this.dataset
              }/reports/default/${encodeURIComponent(
                JSON.parse(resp.response)
              )}`
            );
          } else {
            this.$router.push(`/ui/datasets/${this.dataset}`);
          }
        })
        .finally(() => {
          this.uploading = false;
        });
    },
  },
};
</script>
