<template>
  <v-card height="600px">
    <v-card-title class="grey lighten-2" style="position: relative">
      Dashboard settings
      <v-btn
        icon
        @click="$emit('close')"
        style="position: absolute; top: 10px; right: 10px"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-card-title>
    <v-card-text
      style="padding: 10px 20px; height: calc(100% - 60px); overflow: auto"
    >
      <v-tabs
        v-model="settingsTab"
        background-color="transparent"
        color="basil"
        dense
      >
        <v-tab> Colors </v-tab>
        <v-tab> Fonts </v-tab>
        <v-tab> Variables </v-tab>
      </v-tabs>

      <v-tabs-items v-model="settingsTab">
        <v-tab-item>
          <v-card color="basil" flat>
            <v-card-text>
              <div class="my-2"></div>

              <color-picker
                label="Dashboard background"
                :color="dashboard.background"
                @picked="dashboard.background = $event"
              ></color-picker>

              <div class="my-5"></div>

              <color-picker
                label="Visualization background"
                :color="dashboard.visBackground"
                @picked="dashboard.visBackground = $event"
              ></color-picker>

              <div class="my-5"></div>

              <color-picker
                label="Visualization title color"
                :color="dashboard.visTitleColor"
                @picked="dashboard.visTitleColor = $event"
              ></color-picker>
            </v-card-text>
          </v-card>
        </v-tab-item>

        <v-tab-item>
          <v-card color="basil" flat>
            <v-card-text>
              <div class="my-2"></div>

              <strong>Visualization title font size</strong>
              <div
                class="mt-5 px-5"
                style="display: flex; flex-direction: column"
              >
                <v-slider
                  v-model="dashboard.titleFontSize"
                  :thumb-size="20"
                  thumb-label="always"
                  min="8"
                  max="36"
                  label="Font size"
                ></v-slider>
                <v-select
                  label="Text transform"
                  v-model="dashboard.textTransform"
                  :items="textTransforms"
                ></v-select>
              </div>
            </v-card-text>
          </v-card>
        </v-tab-item>

        <v-tab-item>
          <v-card color="basil" flat>
            <v-card-text>
              <div class="my-2 mb-5">
                Variables can be used for controlling what is shown in the
                screen by interactive visualizations like select / bar charts /
                timeline charts. Click
                <v-btn @click="addVariable" color="primary" x-small
                  ><b>here</b></v-btn
                >
                to add a variable
              </div>

              <div class="my-2"></div>
              <div
                v-for="(v, idx) in dashboard.variables"
                :key="idx"
                style="display: flex; align-items: end"
              >
                <v-text-field
                  class="mx-5"
                  v-model="v.name"
                  label="Name"
                ></v-text-field>
                <span class="pb-2">=</span>
                <v-text-field
                  class="mx-5"
                  v-model="v.defaultValue"
                  label="Default value"
                ></v-text-field>
                <v-btn icon>
                  <v-icon
                    @click="
                      dashboard.variables.splice(
                        dashboard.variables.findIndex((x) => x == v),
                        1
                      )
                    "
                    small
                    >mdi-close</v-icon
                  >
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-tab-item>
      </v-tabs-items>
    </v-card-text>
  </v-card>
</template>

<script>
import ColorPicker from "../utils/ColorPicker.vue";
export default {
  components: { ColorPicker },
  props: ["dashboard"],
  data() {
    return {
      backgroundColors: [
        "#fff",
        "#eee",
        "#FFEBEE",
        "#FCE4EC",
        "#F3E5F5",
        "#EDE7F6",
        "#E8EAF6",
        "#E3F2FD",
        "#E1F5FE",
        "#E0F7FA",
        "#E0F2F1",
        "#E8F5E9",
        "#F1F8E9",
        "#F9FBE7",
        "#FFFDE7",
        "#FFF8E1",
        "#FFF3E0",
        "#FBE9E7",
        "#EFEBE9",
        "#ECEFF1",
        "#FAFAFA",
      ],
      visColors: [
        "transparent",
        "#fff",
        "#eee",
        "#FFEBEE",
        "#FCE4EC",
        "#F3E5F5",
        "#EDE7F6",
        "#E8EAF6",
        "#E3F2FD",
        "#E1F5FE",
        "#E0F7FA",
        "#E0F2F1",
        "#E8F5E9",
        "#F1F8E9",
        "#F9FBE7",
        "#FFFDE7",
        "#FFF8E1",
        "#FFF3E0",
        "#FBE9E7",
        "#EFEBE9",
        "#ECEFF1",
        "#FAFAFA",
      ],
      visTitleColors: [
        "#F44336",
        "#E91E63",
        "#9C27B0",
        "#673AB7",
        "#3F51B5",
        "#2196F3",
        "#03A9F4",
        "#00BCD4",
        "#009688",
        "#4CAF50",
        "#8BC34A",
        "#CDDC39",
        "#FFEB3B",
        "#FFC107",
        "#FF9800",
        "#FF5722",
        "#795548",
        "#607D8B",
        "#9E9E9E",
      ],
      textTransforms: ["uppercase", "lowercase", "none"],
      settingsTab: 0,
    };
  },
  methods: {
    addVariable() {
      if (!this.dashboard.variables) this.dashboard.variables = [];
      this.dashboard.variables.push({
        name: "var" + this.dashboard.variables.length,
        defaultValue: "",
      });
    },
  },
};
</script>

<style>
.color-item {
  width: 60px;
  height: 30px;
  border: solid 2px #ccc;
  border-radius: 5px;
  margin: 5px;
  display: inline-block;
  cursor: pointer;
}
.color-item-highlighted {
  border: solid 2px #888;
}

.color-item:hover {
  border: solid 2px #888;
}
</style>
