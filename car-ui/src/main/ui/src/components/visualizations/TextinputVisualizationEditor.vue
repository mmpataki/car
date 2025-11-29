<template>
  <div class="mt-5">
    <v-text-field
      outlined
      dense
      class="my-2"
      label="Label"
      v-model="visualization.label"
    ></v-text-field>

    <v-text-field
      outlined
      dense
      class="my-2"
      label="Default value"
      v-model="visualization.defaultValue"
    ></v-text-field>

    <v-checkbox
      dense
      class="my-2"
      label="Show submit button"
      v-model="visualization.showSubmitBtn"
    ></v-checkbox>

    <v-text-field
      dense
      outlined
      class="my-2"
      v-if="visualization.showSubmitBtn"
      v-model="visualization.btnText"
      label="Submit button text"
    ></v-text-field>
  </div>
</template>

<script>
export default {
  props: ["visualization"],
};
</script>
