<template>
  <label>
    <slot></slot>
    <popper trigger="clickToToggle" :options="{ placement: 'top' }">
      <div class="popper" v-html="help"  style="text-align: left; max-width: 450px; padding: 5px">
      </div>
      <i
        slot="reference"
        v-if="help"
        :style="{
          'font-size': '0.7em',
          'margin-left': '5px',
          color: color || 'black',
        }"
        class="fa fa-info-circle"
      ></i>
    </popper>
  </label>
</template>

<script>
import Popper from "vue-popperjs";
import "vue-popperjs/dist/vue-popper.css";

export default {
  name: "HelpfulLabel",
  props: ["help", "color"],
  components: {
    popper: Popper,
  },
};
</script>
