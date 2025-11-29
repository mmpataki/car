<template>
  <div>

<v-dialog
      v-model="dialog"
      width="500"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="red lighten-2"
          dark
          v-bind="attrs"
          v-on="on"
        >
          Click Me
        </v-btn>
      </template>

      <v-card>
        <v-card-title class="text-h5 grey lighten-2">
          Privacy Policy
        </v-card-title>

        <v-card-text>
          Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            text
            @click="dialog = false"
          >
            I accept
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


    <!-- <div
      :style="{
        'align-items': 'center',
        display: !valid || filter.editing ? 'inline-flex' : 'none',
      }"
    >
      <input
        ref="hiddenInput"
        class="hiddenfocus"
        style="display: none"
        @blur="disableEdit()"
      />
      <select
        style="margin: 0px 5px; background: transparent"
        placeholder="field"
        v-model="filter.key"
        @blur="disableEdit()"
        @focus="enableEdit()"
      >
        <option v-for="(f, idx) in Object.keys(filtervalues)" :key="idx">
          {{ f }}
        </option>
      </select>
      <select
        style="margin: 0px 5px; background: transparent"
        placeholder="operator"
        v-model="filter.op"
        @blur="disableEdit()"
        @focus="enableEdit()"
      >
        <option v-for="(op, idx) in Object.keys(operators)" :key="idx">
          {{ op }}
        </option>
      </select>
      <div style="display: inline">
        <select
          v-if="isInputType(filter.key, 'select')"
          @blur="disableEdit()"
          @focus="enableEdit()"
          v-model="filter.val"
          style="background: transparent"
        >
          <option v-for="(val, idx) in filtervalues[filter.key]" :key="idx">
            {{ val }}
          </option>
        </select>
        <input
          v-else
          v-model="filter.val"
          style="border: solid 1px gray; background: transparent"
          @blur="disableEdit(filter)"
          @focus="enableEdit(filter)"
        />
      </div>
    </div>
    <v-icon
      style="margin-left: 5px"
      v-if="filter.editing"
      small
      @click="disableEdit(), newsearch()"
      >mdi-checkbox-marked-circle</v-icon
    >
    <v-icon
      style="margin-left: 5px"
      v-if="(!filter.readonly && !filter.editing) || !valid"
      small
      @click="removeFilter()"
      >mdi-close</v-icon
    > -->
  </div>
</template>

<script>
export default {
  props: ["filter", "filtervalues"],
  data() {
    return {
      operators: {
        is: {},
        contains: {},
        "isnot": {},
      },
    };
  },
  computed: {
    valid() {
      let f = this.filter;
      return f.key != undefined && f.val != undefined && f.op != undefined;
    },
  },
  methods: {
    disableEdit() {
      this.filter.editing = false;
    },
    enableEdit() {
      //if (this.filter.readonly) return;
      this.filter.editing = true;
      this.$refs.hiddenInput.focus();
    },
    isInputType(key, type) {
      return (key == "dataset" || key == "_file") && type == "select";
    },
  },
};
</script>

<style>
</style>