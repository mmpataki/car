<template>
  <v-stepper v-model="readRuleStep">
    <v-stepper-header>
      <v-stepper-step editable :complete="sampleText != undefined" step="1">
        Drop a sample log file
      </v-stepper-step>

      <v-divider></v-divider>

      <v-stepper-step
        :editable="readRuleStep > 1"
        :complete="example.txt != undefined"
        step="2"
      >
        Select a complete log message
      </v-stepper-step>

      <v-divider></v-divider>

      <v-stepper-step :editable="readRuleStep > 2" step="3"
        >Define the format of the message</v-stepper-step
      >
    </v-stepper-header>

    <v-stepper-items>
      <v-stepper-content step="1">
        <div
          class="sample-log-file-dropzone"
          @dragleave="$event.target.style.border = 'dashed 1px lightgray'"
          @drop="fileDropped($event)"
          @dragover="
            $event.preventDefault(),
              ($event.target.style.border = 'dashed 1px red')
          "
        >
          Drop a sample file here
        </div>
      </v-stepper-content>

      <v-stepper-content step="2">
        <strong>Select a complete log message</strong>
        <pre
          style="
            overflow-y: auto;
            height: 500px;
            width: 100%;
            white-space: pre-line;
            font-size: 0.85em;
            margin: 10px 0px;
          "
          @mouseup="selectionDone"
          v-text="sampleText"
        ></pre>
      </v-stepper-content>

      <v-stepper-content step="3">
        <regex-builder
          style="height: 500px"
          v-if="example.txt"
          :txt="example.txt._msg"
          :ex-selections="example.selections._msg"
          @regex-changed="setSelections"
          :editing="true"
          @edit-done="editDone"
        ></regex-builder>
      </v-stepper-content>
    </v-stepper-items>
  </v-stepper>
</template>

<script>
import RegexBuilder from "@/components/rulebuilder/RegexBuilder.vue";
export default {
  props: ["example"],
  components: { RegexBuilder },
  data() {
    return {
      sampleText: "",
      readRuleStep: this.example.txt ? 3 : 1,
    };
  },
  methods: {
    editDone() {
      console.log("hey there");
      this.$emit("saved");
    },
    setSelections(event) {
      console.log(event);
      this.example.selections = { _msg: event.selections };
    },
    selectionDone() {
      let selection = document.all
        ? document.selection.createRange().text
        : document.getSelection();
      let text = selection.toString();
      if (text.length == 0 || text.trim().length == 0) return;
      this.example.txt = { _msg: text };
      this.readRuleStep = 3;
    },
    fileDropped(ev) {
      console.log("file dropped");
      ev.preventDefault();
      let file;
      if (ev.dataTransfer.items) {
        if (ev.dataTransfer.items[0].kind === "file")
          file = ev.dataTransfer.items[0].getAsFile();
      } else {
        file = ev.dataTransfer.files[0];
      }
      if (file) {
        let r = new FileReader();
        r.onload = (ex) => {
          this.sampleText = ex.target.result;
          this.readRuleStep = 2;
        };
        r.readAsText(file, "UTF-8");
      }
    },
  },
};
</script>


<style scoped>
.sample-log-file-dropzone {
  display: flex;
  min-height: 200px;
  align-items: center;
  justify-content: center;
  border: dashed 1px lightgray;
}
</style>