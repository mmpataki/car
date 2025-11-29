<template>
  <div style="width: 100%; height: 100%; overflow: hidden; position: relative">
    <v-tabs v-model="tab" dense>
      <v-tab>Details</v-tab>
      <v-tab>Build rules</v-tab>
    </v-tabs>
    <v-tabs-items
      v-model="tab"
      style="height: calc(100% - 36px); overflow: auto"
    >
      <v-tab-item style="position: relative">
        <v-form v-if="type" class="py-5 px-5">
          <v-text-field
            v-model="type.name"
            :counter="30"
            label="Name"
            required
            outlined
            dense
          ></v-text-field>
          <v-textarea
            class="my-4"
            outlined
            rows="3"
            label="Description"
            v-model="type.description"
          ></v-textarea>

          <v-expansion-panels>
            <v-expansion-panel>
              <v-expansion-panel-header>
                File merging config
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-switch
                  v-model="type.mergeMultipleFiles"
                  label="Merge multiple files in the directory"
                ></v-switch>
                <v-text-field
                  v-if="type.mergeMultipleFiles"
                  dense
                  outlined
                  v-model="type.mergedFileName"
                  label="Merged filename"
                >
                </v-text-field>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header>
                File type detectors
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <div>
                  <label>Detectors</label>
                  <div style="float: right">
                    <v-select
                      v-model="newdetectortyp"
                      :items="dtypes"
                      dense
                      class="d-inline-block mx-2"
                    ></v-select>
                    <v-btn
                      class="d-inline-block"
                      color="secondary"
                      x-small
                      @click="addDetector"
                      >add detector</v-btn
                    >
                  </div>
                </div>
                <div>
                  <div v-for="(detector, idx) in type.detectors" :key="idx">
                    <div
                      style="
                        border: solid 1px lightgray;
                        display: flex;
                        flex-direction: column;
                        margin: 10px 0px;
                        border-radius: 5px;
                      "
                    >
                      <div
                        style="
                          flex-grow: 1;
                          background: #eaeef280;
                          padding: 10px;
                          display: flex;
                          align-items: center;
                        "
                      >
                        <span
                          style="flex-grow: 1"
                          :title="detectorTypeInfo(detector.type).description"
                          >{{
                            detectorTypeInfo(detector.type).displayName
                          }}</span
                        >
                        <i
                          @click="deleteDetector(idx)"
                          class="fa fa-trash"
                          style="float: right; color: gray; font-size: 0.8em"
                        ></i>
                      </div>
                      <component
                        :is="detectorEditorName(detector.type)"
                        :detector="detector"
                        style="padding: 10px"
                      ></component>
                    </div>
                  </div>
                </div>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header>
                File structure
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <small
                  >Tell us about the format of the {{ type.name }} file</small
                >
                <v-checkbox
                  label="Is this a structured file (log file, CSV, JSON etc. are structured files)"
                  v-model="type.structured"
                ></v-checkbox>

                <small style="display: block" class="mt-5"
                  >Tell us how messages / data in {{ type.name }} look like. We
                  use this information to index this data for quick
                  search</small
                >
                <div class="my-4">
                  <div v-if="!type.structured">
                    <v-select
                      v-model="type.recordReader.type"
                      label="Record reader"
                      outlined
                      dense
                      :items="recordreaders"
                    ></v-select>
                    <component
                      :is="recordReaderName(type.recordReader.type)"
                      :recordreader="type.recordReader"
                    ></component>
                  </div>
                  <div v-else>
                    <div class="my-2">
                      <v-select
                        :items="readConfigs"
                        item-value="key"
                        item-text="name"
                        outlined
                        dense
                        label="How to read this file"
                        v-model="type.readConfig.type"
                      ></v-select>
                    </div>

                    <div class="my-2"><b>Read config</b></div>
                    <component :is="readConfigEditor" :type="type"></component>
                  </div>
                </div>
              </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
              <v-expansion-panel-header>
                Log view config
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <i>Select a search view</i>
                <v-select
                  outlined
                  dense
                  :items="searchViews"
                  v-model="type.defaultSearchView"
                ></v-select>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>

          <v-btn class="my-5" color="primary" small @click="save">Save</v-btn>
        </v-form>
        <v-overlay :value="type == null" absolute>
          <v-progress-circular indeterminate size="32"></v-progress-circular>
        </v-overlay>
      </v-tab-item>
      <v-tab-item style="height: 100%">
        <RuleBuildingWizard :logtype="type"></RuleBuildingWizard>
      </v-tab-item>
    </v-tabs-items>
  </div>
</template>
<script>
import { car } from "@/car.js";
import upperFirst from "lodash/upperFirst";
import RuleBuildingWizard from "@/components/rulebuilder/RuleBuildingWizard.vue";
import ReadRuleEditor from "../components/rulebuilder/ReadRuleEditor.vue";
export default {
  name: "LogTypeEditor",
  components: { ReadRuleEditor, RuleBuildingWizard },
  data() {
    return {
      newdetectortyp: "filenamematching",
      tab: "build",
      type: null,
      readConfigs: [
        { name: "CSV", key: "csv" },
        { name: "JSON", key: "json" },
        { name: "Custom", key: "rulebased" },
      ],
      searchViews: [],
    };
  },
  created() {
    if (!this.$attrs.type) {
      car
        .getLogTypes(this.$route.params.logtypegroup)
        .then(
          (lts) =>
            (this.type = lts.filter(
              (lt) => lt.name == this.$route.params.logtype
            )[0])
        );
    } else {
      this.type = this.$attrs.type;
    }
    car.getSearchViews().then((svs) => {
      this.searchViews = svs.map((sv) => sv.name);
    });
  },
  computed: {
    recordreaders() {
      return car.getRecordReaders().map((rr) => ({
        text: `${rr.displayName} : ${rr.description}`,
        value: rr.name,
      }));
    },
    dtypes() {
      return car.getDetectorTypes().map((d) => ({
        text: d.displayName,
        value: d.name,
      }));
    },
    readConfigEditor() {
      return upperFirst(this.type.readConfig.type + "ReadConfig");
    },
  },
  methods: {
    recordReaderName(name) {
      return upperFirst(
        name.substring(0, name.length - "recordreader".length) + "RecordReader"
      );
    },
    detectorEditorName(name) {
      return upperFirst(name + "Detector");
    },
    save() {
      this.type.save();
      this.$router.push({
        name: this.type.href.name,
        params: this.type.getHrefParams(),
      });
    },
    detectorTypeInfo(type) {
      return car.getDetectorTypes().filter((x) => x.name == type)[0];
    },
    addDetector() {
      let detectorCreators = {
        filenamematching: () => ({ type: "filenamematching" }),
        messagefinding: () => ({ type: "messagefinding", msgs: [] }),
      };
      this.type.detectors.push(detectorCreators[this.newdetectortyp]());
    },
    deleteDetector(idx) {
      this.type.detectors.splice(idx, 1);
    },
  },
};
</script>
