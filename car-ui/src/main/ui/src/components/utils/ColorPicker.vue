<template>
  <div
    style="display: inline-block; cursor: pointer"
    @click="openPicker = true"
  >
    <div style="display: flex; align-items: center">
      <strong class="mr-4">{{label}}</strong>
      <div
        :style="{
          background: color,
          width: `${width || 55}px`,
          height: `${height || 30}px`,
          border: 'solid 1px lightgray',
          'border-radius': '3px',
        }"
      ></div>
    </div>
    <v-dialog v-if="openPicker" v-model="openPicker" style="width: 300px">
      <v-card style="width: 300px">
        <v-card-title>Pick a color</v-card-title>
        <v-card-text>
          <v-color-picker
            class="ma-2"
            show-swatches
            v-model="pickedColor"
          ></v-color-picker>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              x-small
              color="primary"
              @click="$emit('picked', pickedColor), (openPicker = false)"
              >Done</v-btn
            >
            <v-btn
              x-small
              text
              color="error"
              @click="$emit('picked', color), (openPicker = false)"
              >cancel</v-btn
            >
          </v-card-actions>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  props: ["color", "width", "height", "label"],
  data() {
    return {
      pickedColor: "",
      openPicker: false,
    };
  },
};
</script>

<style>
.v-dialog {
  width: auto;
}
</style>