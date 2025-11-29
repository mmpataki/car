<template>
  <div :style="{ margin: '20px 0px' }">
    <small>
      <b>Trace ids detected (and verified)</b>
    </small>
    <table>
      <tr v-for="(obj, idx) in objs" :key="idx">
        <td :style="{ minwidth: '350px' }">{{obj.info}}</td>
        <td>
          <small :style="{margin: '0px 5px'}">
            <router-link :to="`/ui/datasets/${dset.id}/browse?_file=is,${obj.info}.zip/${obj.info}.log`">view</router-link>
          </small>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: "TraceIdAnaComponent",
  props: ["objs", "dset"],
};
</script>

<style>
</style>