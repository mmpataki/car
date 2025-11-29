<template>
  <div>
    <v-expansion-panels accordion v-if="msg" v-model="expandModel">
      <v-expansion-panel
        v-for="([key, msg], idx) in Object.entries(msg).filter(([k, v]) => !['___selected'].includes(k))"
        :key="idx"
      >
        <v-expansion-panel-header>
          <template>
            <div style="display: flex; align-items: center">
              <div style="display: flex; flex-direction: column; flex-grow: 1">
                <strong class="primary--text">{{ key }}</strong>
                <pre
                  v-if="idx != expandModel"
                  style="
                    display: block;
                    padding: 5px 0px;
                    white-space: pre-wrap;
                    font-family: monospace;
                    word-break: break-all;
                  "
                  v-text="msg"
                ></pre>
              </div>
              <div style="margin: 0px 20px; display: flex; white-space: nowrap">
                <label style="margin: 0px 20px">
                  <input
                    type="checkbox"
                    v-model="example.fieldConfigs[key].ignored"
                    hide-details="true"
                  />
                  <span style="margin: 5px">ignore</span>
                </label>

                <label>
                  <input
                    :disabled="example.fieldConfigs[key].ignored"
                    type="checkbox"
                    v-model="example.fieldConfigs[key].pickFullValue"
                    hide-details="true"
                  />
                  <span style="margin: 5px">extract full field</span>
                </label>
              </div>
            </div>
          </template>
        </v-expansion-panel-header>
        <v-expansion-panel-content v-if="!example.fieldConfigs[key].ignored" :key="key">
          <regex-builder
            v-if="!example.fieldConfigs[key].pickFullValue && msg"
            :key="msg"
            :txt="msg"
            :editing="false"
            :exSelections="example.selections[key]"
            @regex-changed="setRegex(key, $event)"
          ></regex-builder>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script>
import RegexBuilder from "./RegexBuilder.vue";
export default {
  name: "ExampleEditor",
  components: { RegexBuilder },
  props: ["example"],
  data() {
    return {
      expandModel: 0,
    };
  },
  computed: {
    msg() {
      return this.example.txt;
    },
  },
  created() {
    let msg = this.msg,
      sels = this.example.selections,
      fc = this.example.fieldConfigs;
    if(!fc) {
      this.example.fieldConfigs = fc = {}
    }
    Object.keys(msg).forEach((key) => {
      sels[key] = sels[key] || [];
      fc[key] = fc[key] || { ignored: true, pickFullValue: true };
    });
  },
  methods: {
    setRegex(key, e) {
      this.example.selections[key] = e.selections;
    }
  },
};
</script>

<style>
</style>