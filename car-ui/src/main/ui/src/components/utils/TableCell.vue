<template>
  <div :style="item.style" class="cell">
    <template v-if="item.text">{{
      item.text
    }}</template>
    <div v-if="item.html" class=".text-truncate" v-html="item.html"></div>
    <v-icon v-if="item.icon" :size="item.size" :color="item.color">{{
      item.icon
    }}</v-icon>
    <img v-if="item.img" :src="item.img" :style="item.style" />
    <router-link v-if="item.link != undefined && item.link != ''" :to="item.link" class=".text-truncate">{{
      item.linktext
    }}</router-link>
    <span v-if="item.link != undefined && item.link == ''">{{item.linktext}}</span>
    <v-btn
      v-if="item.btntext"
      @click="item.func()"
      :color="item.color"
      text
      style="text-transform: unset"
      small
      :icon="item.btnicon != undefined"
      :title="item.btntext"
    >
      <v-icon small v-if="item.btnicon">{{ item.btnicon }}</v-icon>
      <template v-else>{{ item.btntext }}</template>
    </v-btn>
    <span v-if="item.statustext" style="white-space: nowrap; overflow-wrap: nowrap;">
      <v-progress-circular
        v-if="item.value"
        :size="item.size || 12"
        :width="item.width || 1"
        :color="item.color || 'primary'"
        indeterminate
        style="margin-right: 5px"
      ></v-progress-circular>
      <span v-html="item.statustext"></span>
    </span>
  </div>
</template>
<script>
export default {
  name: "TableCell",
  props: ["item"],
};
</script>

<style scoped>
.cell {
}
</style>