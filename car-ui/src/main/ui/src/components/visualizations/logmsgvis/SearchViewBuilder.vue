<template>
  <v-dialog value="show" width="500" style="height: 500px" persistent>
    <v-card style="position: relative">
      <v-btn
        @click="$emit('closed', undefined)"
        rounded
        icon
        style="position: absolute; right: 10px; top: 10px"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
      <v-card-title class="grey lighten-2">
        Choose/create a search view
      </v-card-title>

      <v-card-text>
        <div style="margin: 20px">
          <strong>Pick a view</strong>
          <div style="display: flex; align-items: center">
            <v-select :items="filteredViews" v-model="currentView"></v-select>
          </div>
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" small @click="emitAndClose"> Done </v-btn>
        <v-btn text small @click="showManageViews = true">
          Manage views
          <search-view-manager
            v-if="showManageViews"
            :views="filteredViews"
            :fields="queriedFields"
            @closed="showManageViews = false"
          ></search-view-manager>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { Field } from "@/car.js";
import SearchViewManager from "./SearchViewManager.vue";
export default {
  name: "SearchViewBuilder",
  props: ["visualization", "queriedFields", "searchView", "views"],
  components: { SearchViewManager },
  data() {
    return {
      showManageViews: false,
      currentView: this.searchView,
      filteredViews: [],
    };
  },
  watch: {
    queriedFields: {
      handler: function () {
        this.setupViews();
      },
    },
  },

  created() {
    this.setupViews();
  },

  methods: {
    setupViews() {
      // remove the views which are not compatible here
      let filteredViews = this.views.filter(({ value }) => {
        return (
          value.fields.filter(
            (f) => f.visible && !this.queriedFields.includes(f.name)
          ).length == 0
        );
      });

      // make sure all the fields in the queried fields are present in all views
      filteredViews.forEach(({ value }) => {
        this.queriedFields.forEach((qf) => {
          if (value.fields.filter((f) => f.name == qf).length == 0) {
            value.fields.push(new Field(qf));
          }
        });
      });

      this.filteredViews = filteredViews;
    },

    emitAndClose() {
      this.$emit("closed", this.currentView);
    },
  },
};
</script>
