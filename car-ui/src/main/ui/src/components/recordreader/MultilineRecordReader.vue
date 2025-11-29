<template>
  <div class="my-5">
    <v-textarea
      v-model="recordreader.msgStartRegex"
      :counter="10"
      rows="2"
      :rules="nameRules"
      label="First line regex"
      required
      outlined
      dense
    ></v-textarea>
  </div>
</template>

<script>
export default {
  name: "MultilineRecordReader",
  props: ["recordreader"],
};
</script>
