<template>
  <v-data-table
    dense
    v-if="tableData.length > 0 && !visualization.keyValueDisplay"
    :headers="colheads"
    :items="tableData"
    :calculate-widths="true"
    :disable-pagination="true"
    :hide-default-footer="true"
    style="background: transparent"
  >
    <template v-slot:item="item">
      <table-row :item="item"></table-row>
    </template>
  </v-data-table>
  <div v-else>
    <table v-if="tableData.length > 0 && visualization.keyValueDisplay">
      <tr v-for="(h, i) in colheads" :key="i">
        <td style="padding-right: 20px">
          <small>{{ h.text }}</small>
        </td>
        <td>
          {{ tableData[0][h.value] }}
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import TableRow from "../utils/TableRow.vue";
export default {
  name: "TableVisualization",
  components: { TableRow },
  props: ["view", "visualization", "dataWatch", "metaWatch"],
  data() {
    return {
      tableData: [],
      colheads: [],
    };
  },
  watch: {
    dataWatch: {
      handler: function () {
        console.log("dataWatch", this.visualization.title);
        this.update();
      },
    },
    metaWatch: {
      handler: function () {
        console.log("metaWatch", this.visualization.title);
        this.update();
      },
    },
  },
  mounted() {
    console.log("mounted", this.visualization.title);
    this.update();
  },
  methods: {
    update() {
      console.log("update", this.visualization.title);
      if (this.view.data && this.view.data.length > 0) {
        this.tableData.splice(0, this.tableData.length);
        this.view.data.forEach((r) => {
          this.tableData.push(r);
        });
        console.log(this.tableData);
        this.colheads = Object.keys(this.tableData[0]).map((x) => ({
          text: x,
          align: "start",
          sortable: true,
          value: x,
        }));
      }
    },
  },
};
</script>
