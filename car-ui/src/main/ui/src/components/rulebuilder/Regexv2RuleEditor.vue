<template>
  <div>
    <h4 style="margin: 15px 0px">Example texts</h4>
    <v-expansion-panels>
      <template
        style="margin: 20px 5px"
        v-for="(example, eidx) in data.exampleTexts"
      >
        <v-expansion-panel :key="eidx">
          <v-expansion-panel-header>
            Example {{ eidx + 1 }}
            <div style="display: flex">
              <v-spacer></v-spacer>
              <v-btn
                x-small
                text
                color="error"
                @click="data.exampleTexts.splice(eidx, 1)"
                >remove</v-btn
              >
            </div>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <example-editor :example="example"></example-editor>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </template>
    </v-expansion-panels>
    <v-textarea outlined class="my-4" label="Rule match actions" v-model="data.action"></v-textarea>
  </div>
</template>
<script>
import ExampleEditor from "./ExampleEditor.vue";
export default {
  components: { ExampleEditor },
  name: "regexv2-rule-editor",
  props: ["data"],
  methods: {
    removeExample(example) {
      this.data.exampleTexts.splice(
        this.data.exampleTexts.findIndex((x) => x == example),
        1
      );
    },
  },
};
</script>

