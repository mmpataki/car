<template>
  <div>
    <div
      style="border: solid 1px black; margin: 10px; height: 300px; width: 600px"
    >
      <div
        style="
          width: 100%;
          background: wheat;
          padding: 10px 10px;
          display: flex;
          align-items: center;
        "
      >
        <div style="flex-grow: 1">
          <strong>Header</strong>
        </div>
        <div>
          <FilterMenu
            :data="fullData"
            :filters="filters"
            @change="doFilter"
          ></FilterMenu>
        </div>
      </div>
      <div style="width: 100%; padding: 20px 10px">
        <table style="width: 100%; border-collapse: collapse" border="1">
          <tr>
            <th v-for="(key, idx) in headers" :key="idx">{{ key }}</th>
          </tr>
          <tr v-for="(row, idx) in rows" :key="idx">
            <td
              style="padding: 0px 10px"
              v-for="(key, idx) in headers"
              :key="idx"
            >
              {{ row[key] }}
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import FilterMenu from "./filters/FilterMenu.vue";
export default {
  name: "Test",
  components: { FilterMenu },
  data() {
    return {
      fullData: [
        { name: "Madhusooodan", age: 24, edu: "B.E" },
        { name: "Achyuta", age: 15, edu: "9th" },
        { name: "Banashri", age: 21, edu: "B.Sc" },
      ],
      filters: {},
      rows: null,
    };
  },
  created() {
    this.doFilter();
  },
  computed: {
    headers() {
      return Object.keys(this.fullData[0]);
    },
  },
  methods: {
    doFilter() {
      console.log("doFilter")
      let apply = (row) => {
        for (let key in this.filters) {
          let filter = this.filters[key];
          if (!filter.accepts(row)) return false;
        }
        return true;
      };
      this.rows = this.fullData.filter((row) => apply(row));
    },
  },
};
</script>

<style>
</style>