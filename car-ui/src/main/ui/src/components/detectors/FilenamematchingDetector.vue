<template>
  <div class="form form-row">
    <label>file name pattern: </label>
    <input v-model="detector.pattern" />
  </div>
</template>
<script>
export default {
  name: "FilenamenatchingDetector",
  props: ["detector"],
};
</script>
