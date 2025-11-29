<template>
  <div>
    <div>
      <v-select
        label="Variable name"
        hint="Variable name which can be used to create other visualizations in this report"
        :items="dashboard.variables"
        item-text="name"
        item-value="name"
        v-model="ehandler.parameters.variablename"
        no-data-text="No variables are defined, create them in dashboard settings"
      ></v-select>
    </div>
    <div>
      <v-select
        :hint="'Attribute which should be used to set the value for the above variable'"
        label="Data attribute"
        v-model="ehandler.parameters.dataKey"
        :items="[...keys, 'Full record']"
      >
      </v-select>
    </div>
  </div>
</template>

<script>
export default {
  name: "SetvariableEventHandlerEditor",
  props: ["ehandler", "visualization", "dashboard"],
  computed: {
    keys() {
      if (this.visualization.view.data && this.visualization.view.data.length)
        return Object.keys(this.visualization.view.data[0]);
      else return [];
    },
  },
};
</script>
