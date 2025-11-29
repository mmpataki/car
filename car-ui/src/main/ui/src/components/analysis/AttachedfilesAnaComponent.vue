<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Files</b></small>
    <table>
      <tr v-for="(f, idx) in objs" :key="idx">
        <td :style="{ minWidth: '350px' }">{{f.info.name}}</td>
        <td>{{f.info.attachtime}}</td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: "AttachedfilesAnaComponent",
  props: ["objs"]
};
</script>

<style>
</style>