<template>
  <div class="form form-col">
    <label>JSON</label>
    <textarea rows="10" v-model="store.json"></textarea>
  </div>
</template>

<script>
export default {
  name: "JsonStoreEditor",
  props: ["store"],
};
</script>
