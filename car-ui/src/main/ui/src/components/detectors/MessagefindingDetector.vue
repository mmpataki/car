<template>
  <div>
    <div
      :key="idx"
      v-for="(msg, idx) in d.msgs"
      class="my-4"
      style="display: flex; align-items: center"
    >
      <div style="flex-grow: 1">
        <v-select
          :items="types"
          v-model="msg.type"
          label="Use this text for"
          outlined
          dense
        ></v-select>
        <v-textarea outlined rows="2" :label="'#' + idx" v-model="msg.text" />
      </div>
      <div style="padding-left: 20px">
        <i class="fa fa-minus-circle" @click="removeMsg(idx)"></i>
      </div>
    </div>
    <v-btn color="secondary" x-small @click="addMsg">Add message</v-btn>
  </div>
</template>

<script>
export default {
  name: "MessagefindingDetector",
  props: ["detector"],
  data() {
    return {
      types: [
        { value: "Regex_matching", text: "Regex matching" },
        { value: "Text_search", text: "Text search" },
      ],
    };
  },
  created() {
    if (!this.detector.msgs) this.detector.msgs = [];
  },
  computed: {
    d() {
      return this.detector;
    },
  },
  methods: {
    addMsg() {
      this.detector.msgs.push({ text: "", type: "Text_search" });
    },
    removeMsg(idx) {
      this.detector.msgs.splice(idx, 1);
    }
  },
};
</script>
