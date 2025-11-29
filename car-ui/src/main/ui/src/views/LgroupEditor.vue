<template>
  <div style="width: 100%; height: 100%; position: relative">
    <div v-if="group">
      <v-form class="py-5 px-5">
        <v-text-field
          v-model="group.name"
          :counter="10"
          :rules="nameRules"
          label="Name"
          required
          outlined
          dense
        ></v-text-field>
        <v-textarea
          class="my-4"
          outlined
          label="Description"
          rows="3"
          v-model="group.description"
        ></v-textarea>
        <v-btn color="primary" @click="save">Save</v-btn>
      </v-form>
    </div>
    <v-overlay :value="group == null" :absolute="false">
      <v-progress-circular indeterminate size="32"></v-progress-circular>
    </v-overlay>
  </div>
</template>

<script>
import { car } from "@/car.js";
export default {
  name: "lgroup-editor",
  data() {
    return {
      group: null,
    };
  },
  created() {
    if (!this.$attrs.group) {
      car
        .getLogGroupTypes()
        .then(
          (lgts) =>
            (this.group = lgts.filter(
              (lgt) => lgt.name == this.$route.params.logtypegroup
            )[0])
        );
    } else {
      this.group = this.$attrs.group;
    }
  },
  methods: {
    save() {
      car.saveLogGroup(this.group).then(() => {
        this.$router.push({
          name: this.group.href.name,
          params: this.group.getHrefParams(),
        });
      });
    },
  },
};
</script>
