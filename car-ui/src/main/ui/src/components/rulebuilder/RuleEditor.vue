<template>
  <div style="width: 100%; height: 100%">
    <v-form v-if="rule" class="pa-5">
      <div style="display: flex">
        <h2 style="flex-grow: 1">{{ rule.name }}</h2>
        <v-btn color="primary" icon  @click="saveRule">
          <v-icon>mdi-content-save</v-icon>
        </v-btn>
        <v-btn color="error" icon  @click="deleteRule">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </div>
      <v-textarea
        class="my-4"
        rows="3"
        outlined
        dense
        label="Description"
        v-model="rule.description"
      ></v-textarea>

      <div class="mt-10">
        <v-select
          :items="ruleTypes"
          label="Rule type"
          v-model="rule.type"
          dense
          outlined
        ></v-select>
        <component
          :is="compname"
          :data="rule"
          :key="rule.lgroup + rule.ltype + rule.name"
          style="margin: 40px 5px 10px 5px"
        ></component>
      </div>
    </v-form>
    <v-overlay :value="rule == null" :absolute="false">
      <v-progress-circular indeterminate size="32"></v-progress-circular>
    </v-overlay>
  </div>
</template>
<script>
import upperFirst from "lodash/upperFirst";
import { car } from "@/car.js";
export default {
  name: "RuleEditor",
  props: ["data"],
  data() {
    return { rule: null };
  },
  created() {
    if (this.$attrs.rule) {
      this.rule = this.$attrs.rule;
    } else {
      car
        .getRules(this.$route.params.logtypegroup, this.$route.params.logtype)
        .then(
          (rules) =>
            (this.rule = rules.filter(
              (rule) => rule.name == this.$route.params.rulename
            )[0])
        );
    }
  },
  computed: {
    ruleTypes() {
      return car.getTypes().rule.subTypes;
    },
    compname() {
      return upperFirst(this.rule.type + "RuleEditor");
    },
  },
  methods: {
    deleteRule() {
      car.deleteRule(this.rule).then(() => car.info(`Deleted ${this.rule.name}`));
    },
    saveRule() {
      this.rule.save();
    },
  },
};
</script>
