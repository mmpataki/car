<template>
  <div>
    <div v-if="!editingExample">
      <v-card
        v-for="(ex, idx) in type.readConfig.readRules[0].exampleTexts"
        :key="idx"
        style="padding: 10px; position: relative; margin: 10px 0px"
      >
        <div style="position: absolute; top: 0px; right: 0px; display: flex">
          <v-btn small icon @click="setEditable(ex)"
            ><v-icon x-small>mdi-pencil</v-icon></v-btn
          >
          <v-btn small icon @click="removeExample(ex)"
            ><v-icon x-small>mdi-delete</v-icon></v-btn
          >
        </div>
        <div
          style="
            white-space: pre-wrap;
            font-family: monospace;
            padding: 10px 0px;
            font-size: 0.85em;
          "
        >
          <span
            v-for="[k, v] in Object.entries(ex.txt)"
            :key="k"
            v-text="v"
            :title="k"
          ></span>
        </div>
      </v-card>
      <v-btn color="primary" @click="addExample" x-small text
        ><b>Add record format</b></v-btn
      >
    </div>
    <div v-if="editingExample">
      <read-rule-editor
        :example="editingExample"
        :key="ruleKey"
        @saved="setEditable(undefined)"
        @cancelled="setEditable(undefined)"
      ></read-rule-editor>
    </div>
  </div>
</template>

<script>
import ReadRuleEditor from "@/components/rulebuilder/ReadRuleEditor.vue";
export default {
  name: "RulebasedReadConfig",
  components: { ReadRuleEditor },
  props: ["type"],
  data() {
    return {
      ruleKey: 0,
      editingExample: undefined,
      fileType: "CSV",
    };
  },
  created() {
    if (this.type.readConfig.readRules.length == 0 && this.type.structured) {
      this.type.readConfig.readRules.push({
        type: "regexv2",
        exampleTexts: [],
      });
    }
  },
  methods: {
    setEditable(ex) {
      this.editingExample = ex;
    },
    addExample() {
      let ex = { txt: undefined, selections: [], fieldConfigs: {} };
      if (
        !this.type.readConfig.readRules ||
        this.type.readConfig.readRules.length == 0
      ) {
        let rule = {
          type: "regexv2",
          exampleTexts: [ex],
        };
        this.type.readConfig.readRules.push(rule);
      }
      this.type.readConfig.readRules[0].exampleTexts.push(ex);
      this.setEditable(ex);
    },
    removeExample(ex) {
      let idx = this.type.readConfig.readRules[0].exampleTexts.findIndex((x) => x === ex);
      this.type.readConfig.readRules[0].exampleTexts.splice(idx, 1);
    },
  },
};
</script>

<style>
</style>