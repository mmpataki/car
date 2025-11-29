<template>
  <div
    class="filter"
    :style="{
      background: valid ? (filter.readonly ? '#e0e0e0' : '#e0f2f1') : '#FBE9E7',
      border: valid ? (filter.readonly ? '#e0e0e0' : '#b2dfdb') : '#FFCCBC',
      position: 'relative',
    }"
  >
    <div
      @mouseover="mousein = true"
      @mouseleave="mousein = false"
      style="
        display: inline-flex;
        align-items: center;
        padding: 0px;
        position: relative;
        padding: 0px;
      "
    >
      <strong class="filtertext" @dblclick="enableEdit()">
        <small>{{ filter.key }}</small>
      </strong>
      <strong style="display: inline-block" @dblclick="enableEdit()"
        ><small>{{ filter.op }}</small></strong
      >
      <strong class="filtertext" @dblclick="enableEdit()">
        <small>{{
          Array.isArray(filter.val) ? filter.val.join(" / ") : filter.val
        }}</small>
      </strong>
      <div
        v-if="!filter.readonly"
        style="
          position: absolute;
          right: -5px;
          background: white;
          border-radius: 5px;
          padding: 0px 5px;
          margin: 1px;
        "
      >
        <v-btn
          @click="enableEdit()"
          v-if="mousein"
          x-small
          rounded
          icon
          color="success"
        >
          <v-icon x-small>mdi-pencil</v-icon>
        </v-btn>
        <v-btn
          @click="$emit('deleted', filter)"
          v-if="mousein"
          x-small
          rounded
          icon
          color="error"
        >
          <v-icon x-small>mdi-close</v-icon>
        </v-btn>
      </div>
    </div>

    <v-dialog
      persistent
      v-model="filter.editing"
      width="500"
      style="height: 500px"
    >
      <v-card>
        <v-card-title class="grey lighten-2"> Edit filter </v-card-title>

        <v-card-text>
          <v-select
            v-model="filter.key"
            :items="Object.keys(filtervalues)"
            label="Field"
            outlined
            dense
            class="my-5"
            @change="keyChanged"
          ></v-select>
          <v-select
            label="operator"
            v-model="filter.op"
            :items="Object.keys(operators)"
            outlined
            dense
            class="my-5"
          ></v-select>

          <v-select
            outlined
            dense
            multiple
            append-outer-icon="mdi-pencil"
            v-if="
              !edit && isInputType(filter, 'select') && filtervalues[filter.key]
            "
            v-model="filter.val"
            :items="vals"
            @click:append-outer="edit = true"
          >
          </v-select>
          <v-text-field
            dense
            v-else
            v-model="filter.val"
            outlined
            append-icon="mdi-close-circle-outline"
            @click:append="edit = false"
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            small
            color="primary"
            @click="(filter.editing = false), $emit('filterchange')"
            >Done</v-btn
          >
          <v-btn color="primary" text small @click="cancelEdit()">cancel</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  props: ["filter", "filtervalues", "field"],
  data() {
    return {
      operators: {
        is: {},
        contains: {},
        "isnot": {},
      },
      edit: false,
      mousein: false,
    };
  },
  computed: {
    valid() {
      let f = this.filter;
      return f.key != undefined && f.val != undefined && f.op != undefined;
    },
    vals() {
      return Object.keys(this.filtervalues[this.filter.key]);
    },
  },
  methods: {
    disableEdit() {
      this.filter.editing = false;
    },
    enableEdit() {
      if (this.filter.readonly) return;
      this.filter.editing = true;
      this.filterBackup = { ...this.filter };
      this.$emit("editing");
      this.recomputeEdit();
    },
    recomputeEdit() {
      let filt = this.filtervalues[this.filter.key];
      if (!filt) return;
      this.edit =
        Object.keys(filt).filter((v) => v == this.filter.val).length == 0;
    },
    isInputType({ key, op }, type) {
      return (
        (key == "dataset" || key == "_file" || op == "is" || op == "isnot") &&
        type == "select"
      );
    },
    cancelEdit() {
      if (!this.filterBackup) {
        return this.$emit("deleted", this.filter);
      }
      Object.entries(this.filterBackup).forEach(([k, v]) => {
        this.filter[k] = v;
      });
      this.filter.editing = false;
    },
    keyChanged() {
      this.filter.val = [Object.keys(this.filtervalues[this.filter.key])[0]];
      this.recomputeEdit();
    },
  },
};
</script>
