<template>
  <div style="display: inline">
    <label>delimiter:</label>
    <input v-model="type.delimiter" />
  </div>
</template>

<script>
export default {
  name: "ListftypeEditor",
  props: ["type"],
};
</script>
