<template>
  <div :style="{ margin: '20px 0px' }">
    <small><b>Failed profile mapping files</b></small>
    <table>
      <tr v-for="(file, idx) in files" :key="idx">
        <td style="padding: 0px 5px">{{ file.obj }}</td>
        <td style="padding: 0px 5px">{{ file.agent }}</td>
        <td style="padding: 0px 5px">
          <router-link
            :to="`/ui/datasets/${dset.id}/browse?_file=is,${file.paths.import}`"
            >import</router-link
          >
          &nbsp;
          <router-link
            :to="`/ui/datasets/${dset.id}/browse?_file=is,${file.paths.session}`"
            >session</router-link
          >
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: "CarProfileMappingLogAnaComponent",
  props: ["objs", "dset"],
  data() {
    return {
      files: {},
    };
  },
  created() {
    this.files = this.objs.reduce((m, obj) => {
      let i = obj.info;
      let p = i.path
        .replace("import.log", ".log")
        .replace("session.log", ".log");
      if (!m[p]) {
        m[p] = { obj: i.obj, agent: i.agent, paths: {} };
      }
      m[p].paths[i.path.endsWith("session.log") ? "session" : "import"] =
        i.path;
      return m;
    }, {});
  },
};
</script>

<style>
</style>