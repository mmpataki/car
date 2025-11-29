<template>
  <div
    style="
      height: 100%;
      display: flex;
      align-items: start;
      justify-content: center;
      margin: 10px 0px 0px 0px
    "
  >
    <v-text-field
      outlined
      dense
      hide-details="true"
      class="mx-2"
      v-model="inputText"
      :label="visualization.label"
    ></v-text-field>
    <v-btn
      v-if="visualization.showSubmitBtn"
      color="primary"
      @click="handleEvent"
    >
      {{ visualization.btnText || "Submit" }}
    </v-btn>
  </div>
</template>

<script>
export default {
  props: ["visualization"],
  data() {
    return { inputText: this.visualization.defaultValue };
  },
  methods: {
    handleEvent() {
      this.visualization.eventOccured("change", this.inputText);
    },
  },
};
</script>
