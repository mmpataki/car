<template>
  <div>
    <div style="border-bottom: 1px solid lightgray">
      <span
        @click="tab = 'props'"
        style="color: black; padding-left: 5px"
        :class="{
          'tab-lbl': true,
          'tab-lbl-selected-darkblue': tab == 'props',
        }"
        >Properties</span
      >
      <span
        v-if="!visualization.dataIndependent"
        @click="tab = 'data'"
        style="color: black; padding-left: 5px"
        :class="{ 'tab-lbl': true, 'tab-lbl-selected-darkblue': tab == 'data' }"
        >Data source</span
      >
      <span
        v-if="
          visualization &&
          visualization.eventNames &&
          visualization.eventNames.length > 0
        "
        @click="tab = 'evnts'"
        style="color: black; padding-left: 5px"
        :class="{
          'tab-lbl': true,
          'tab-lbl-selected-darkblue': tab == 'evnts',
        }"
        >Events</span
      >
    </div>
    <div style="padding: 10px 10px 0px 10px; height: 100%">
      <div class="tabcontent" v-show="tab == 'props'">
        <v-text-field
          label="Title"
          dense
          outlined
          class="my-2"
          v-model="visualization.title"
        />
        <v-textarea
          outlined
          class="my-2"
          v-model="visualization.description"
          label="Description"
        ></v-textarea>
        <v-text-field
          label="Message when there is no data"
          dense
          outlined
          class="my-2"
          v-model="visualization.noDataMessage"
        />
        <color-picker
          :color="visualization.backgroundColor"
          @picked="visualization.backgroundColor = $event"
          label="Background color"
        ></color-picker>
        <div
          class="my-2"
          v-if="
            visualization.dataIndependent ||
            (visualization.view.data && visualization.view.data.length > 0)
          "
        >
          <component
            :visualization="visualization"
            :is="chartEditorName(visualization.type)"
          ></component>
        </div>
      </div>
      <div
        class="tabcontent"
        v-if="!visualization.dataIndependent"
        v-show="tab == 'data'"
      >
        <DatasetEditor :visualization="visualization"></DatasetEditor>
      </div>
      <div
        class="tabcontent"
        v-show="
          tab == 'evnts' &&
          visualization.eventNames &&
          visualization.eventNames.length > 0
        "
      >
        Define envent handlers for your visualization events
        <div style="padding: 20px 0px">
          <v-card
            class="mx-auto"
            v-for="(handler, idx) in visualization.eventHandlers"
            :key="idx"
            style="
              border-bottom: 1px solid lightgray;
              margin-bottom: 10px;
              padding: 5px 15px;
            "
            elevation="2"
            outlined
            tile
          >
            <v-select
              label="On: "
              :hint="'These are the events defined by ' + visualization.type"
              v-model="handler.on"
              :items="visualization.eventNames"
            ></v-select>

            <v-select
              label="Do: "
              :hint="'These are the events defined by ' + visualization.type"
              v-model="handler.type"
              :items="
                handlers.map(([key, val]) => ({
                  val: key,
                  text: val.label,
                }))
              "
              item-text="text"
              item-value="val"
            >
            </v-select>
            <component
              :is="eventHandlerEditorName(handler.type)"
              :visualization="visualization"
              :ehandler="handler"
              :dashboard="dashboard"
            ></component>
            <v-card-actions>
              <v-btn
                color="red lighten-2"
                text
                small
                @click="deleteEventHandler(idx)"
              >
                delete
              </v-btn>
            </v-card-actions>
          </v-card>
          <v-btn x-small color="primary" @click="addEventHandler">
            <v-icon x-small>mdi-plus</v-icon>
            Add a event handler</v-btn
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { eventHandlers } from "@/models/visualizations/Visualization.js";
import DatasetEditor from "@/components/dashboard/DatasetEditor.vue";
import upperFirst from "lodash/upperFirst";
import ColorPicker from "../utils/ColorPicker.vue";

export default {
  name: "VisualizationEditor",
  props: ["visualization", "defaultTab", "dashboard"],
  components: { DatasetEditor, ColorPicker },
  data() {
    return { tab: this.defaultTab || "props", showingPreProcessor: false };
  },
  computed: {
    keys() {
      return Object.keys(this.visualization.view.data[0]);
    },
    handlers() {
      return Object.entries(eventHandlers);
    },
  },
  methods: {
    eventHandlerEditorName(type) {
      return upperFirst(type + "EventHandlerEditor");
    },
    chartEditorName(name) {
      return upperFirst(name + "VisualizationEditor");
    },
    addEventHandler() {
      if (
        !this.visualization.eventNames ||
        this.visualization.eventNames.length == 0
      ) {
        return;
      }
      this.visualization.eventHandlers.push({
        type: "setvariable",
        on: this.visualization.eventNames[0],
        parameters: { variablename: "newvar1" },
      });
    },
    deleteEventHandler(idx) {
      this.visualization.eventHandlers.splice(idx, 1);
    },
  },
};
</script>

<style scoped>
.tab-lbl-selected-darkblue {
  border-bottom: solid 3px darkblue !important;
}
.tab-lbl {
  padding: 5px 15px 3px 5px;
  display: inline-block;
  border-bottom: solid 3px transparent;
}
.tabcontent {
  height: 100%;
  overflow: scroll;
}
</style>