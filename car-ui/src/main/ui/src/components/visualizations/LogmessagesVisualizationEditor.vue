<template>
  <div v-if="visualization.view.data && visualization.view.data.length" style="height: 100%; display: flex; flex-direction: column" class="mt-5">
    <div v-if="visualization.datastore.mode != 'facet'">
      <b>Log view</b>
      <span style="text-decoration: underline" @click="openSearchViewBuilder = true">Custom</span>
      <SearchViewBuilder 
        v-if="openSearchViewBuilder" 
        :queriedFields="visualization.datastore.queriedFields"
        :searchView="visualization.customView"
        @closed="searchViewBuilderClosed($event)">
      </SearchViewBuilder>
    </div>
    <v-select v-model="visualization.colorField" label="Color field" outlined dense :items="Object.keys(visualization.view.data[0])"></v-select>
  </div>
</template>

<script>
import SearchViewBuilder from "./logmsgvis/SearchViewBuilder.vue";
export default {
  name: "LogmessagesVisualizationEditor",
  props: ["visualization"],
  components: { SearchViewBuilder },
  data() {
    return {
      openSearchViewBuilder: false
    }
  },
  computed: {
    fields() {
      return this.visualization.datastore.queriedFields ? this.visualization.datastore.queriedFields : []
    }
  },

  methods: {
    searchViewBuilderClosed(evt) {
      console.log(evt)
      if(evt) {
        this.visualization.customView = evt
        this.visualization.viewName = evt.name
        this.fields = evt.fields
      }
      this.openSearchViewBuilder = false;
    }
  }
};
</script>
