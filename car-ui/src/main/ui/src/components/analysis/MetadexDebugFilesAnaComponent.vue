<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Metadex debug files</b></small>
    <ul>
      <li v-for="(obj, idx) in objs" :key="idx">
        {{ obj.info }}
        <router-link :to="`/ui/datasets/${dset.id}/browse?_file=is,${obj.info}`">view</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "MetadexDebugFilesAnaComponent",
  props: ["objs", "dset"]
};
</script>

<style>
</style>