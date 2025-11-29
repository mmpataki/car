<template>
  <div style="display: inline-flex">
    <v-select
      multiple
      label="Type"
      dense
      outlined
      item-text="text"
      item-value="value"
      :items="items"
      @change="setType"
      :value="selectedVal"
    >
      <template v-slot:append-item>
        <v-divider class="mb-2"></v-divider>
        <v-list-item>
          <v-list-item-content>
            <v-btn @click="showDialog = true">Add</v-btn>
          </v-list-item-content>
        </v-list-item>
      </template>
    </v-select>
    <v-text-field
      outlined
      dense
      class="ml-2"
      label="Regex"
      v-if="isRegex"
      :value="customRegex"
      @input="setRegex"
    ></v-text-field>
    <v-dialog
      v-model="showDialog"
      persistent
      max-width="400"
      style="padding: 30px"
    >
      <v-card>
        <v-card-title class="text-h5 mb-2">
          Create a new regex token</v-card-title
        >
        <v-card-text>
          <v-text-field
            v-model="tokenGroup"
            :rules="nameRules"
            label="Token group"
            class="my-4"
            required
            outlined
            dense
          ></v-text-field>
          <v-text-field
            v-model="tokenName"
            :rules="nameRules"
            label="Token name"
            class="my-4"
            required
            outlined
            dense
          ></v-text-field>
          <v-text-field
            v-model="label"
            label="Label"
            class="my-4"
            required
            outlined
            dense
          ></v-text-field>
          <v-text-field
            v-model="regex"
            label="Regex"
            class="my-4"
            required
            outlined
            dense
          ></v-text-field>
          <v-checkbox
            v-model="skipForScan"
            label="Skip for scanning"
          ></v-checkbox>
          <v-text-field
            v-model="varName"
            label="Variable prefix"
            class="my-4"
            required
            outlined
            dense
          ></v-text-field>
          <map-editor label="Properties to set" :obj="tokenProps"></map-editor>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" small @click="saveRegexToken"> Save </v-btn>
          <v-btn color="red darken-1" small text @click="showDialog = false">
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { car } from "@/car.js";
import MapEditor from "@/components/utils/MapEditor.vue";
let LBL = "REGEX:::";
export default {
  name: "CustomftypeEditor",
  props: ["field", "type"],
  components: { MapEditor },
  data() {
    return {
      items: [],
      regex: ".*",
      tokenName: "",
      tokenGroup: "",
      showDialog: false,
      regexes: {},
      regexTokenCreator: () => {},
      tokenProps: {},
      varName: "tok",
      skipForScan: true,
      label: "",
      selectedVal: undefined,
    };
  },
  computed: {
    isRegex() {
      let toks = this.field.regexTokenNames;
      return toks.length == 1 && toks[0].startsWith(LBL);
    },
    customRegex() {
      let toks = this.field.regexTokenNames;
      return toks.length == 1 ? toks[0].substring(LBL.length) : "";
    },
  },
  created() {
    car.getRegexTokens().then((regexes) => {
      let items = [];
      this.regexes = regexes;
      Object.entries(regexes).forEach((rg) => {
        if (
          Object.values(rg[1])
            .map((x) => x.type)
            .some((x) => x == this.type)
        )
          items.push({ header: rg[0] });
        Object.entries(rg[1]).forEach((r) => {
          if (r[1].type == this.type)
            items.push({ text: r[1].label, value: `${rg[0]}/${r[0]}` });
        });
      });
      items.push({ header: "Power user" });
      items.push({ text: "Regex", value: "regex" });
      this.items = items;
      if (this.isRegex) {
        this.selectedVal = items[items.length - 1].value;
      }
    });
  },
  methods: {
    setType(evt) {
      if (evt[evt.length - 1] == "regex") {
        this.setRegex("");
        this.selectedVal = "regex";
      } else {
        this.selectedVal = this.field.regexTokenNames = evt.filter(
          (e) => !e.startsWith(LBL)
        );
      }
      console.log(evt);
    },
    setRegex(evt) {
      this.$set(this.field, "regexTokenNames", [LBL + evt]);
    },
    saveRegexToken() {
      let tok = {
        grpKey: this.tokenGroup,
        tokKey: this.tokenName,
        regex: this.regex,
        label: this.label,
        type: this.type,
        varName: this.varName,
        skipForScan: this.skipForScan,
        props: Object.entries(this.tokenProps).map((x) => ({
          key: x[0],
          value: x[1],
        })),
      };
      console.log(tok);
      car
        .saveRegexToken(tok)
        .then(() => {
          this.regexes[tok.grpKey][tok.tokKey] = tok;
        })
        .finally(() => {
          this.showDialog = false;
        });
    },
  },
};
</script>
