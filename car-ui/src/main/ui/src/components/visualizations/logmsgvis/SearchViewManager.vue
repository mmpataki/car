<template>
  <v-dialog
    :value="true"
    width="500"
    style="height: 500px; width: 500px"
    persistent
  >
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
        Search views relevant to these results
      </v-card-title>

      <v-card-text>
        <div style="padding: 10px 20px">
          <div style="display: flex; align-items: center; padding: 10px 0px">
            <span>Manage search views</span>
            <v-spacer></v-spacer>
            <v-btn text x-small color="primary" @click="openNewEditor()"
              >New</v-btn
            >
          </div>
          <table style="width: 100%; text-align: left">
            <tr>
              <th>Name</th>
              <th>Columns</th>
              <th style="width: 60px">Actions</th>
            </tr>
            <tr
              v-for="(view, idx) in myViews"
              :key="idx"
              @click="currentView = view.value"
              :class="{ 'active-view': currentView == view.value }"
            >
              <td>{{ view.value.name }}</td>
              <td>
                {{
                  view.value.fields
                    .filter((f) => f.visible)
                    .map((f) => f.name)
                    .join(", ")
                }}
              </td>
              <td>
                <v-btn text x-small @click="showEditor = true">
                  edit
                  <search-view-editor
                    v-if="showEditor"
                    :view="currentView"
                    @closed="editorClosed($event)"
                  ></search-view-editor>
                </v-btn>
              </td>
            </tr>
          </table>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import { car, Field } from "@/car.js";
import SearchViewEditor from "./SearchViewEditor.vue";
export default {
  components: { SearchViewEditor },
  name: "SearchViewManager",
  props: ["views", "fields"],
  data() {
    return {
      showEditor: false,
      currentView: undefined,
      myViews: {},
    };
  },

  created() {
    this.myViews = this.views.reduce((m, v) => {
      m[v.text] = v;
      return m;
    }, {});
  },

  methods: {
    openNewEditor() {
      this.currentView = {
        name: "New view",
        fields: this.fields.map((f) => new Field(f)),
      };
      this.showEditor = true;
    },

    editorClosed(nsv) {
      this.showEditor = false;
      if (!nsv) return;
      this.$set(this.myViews, nsv.text, { text: nsv.name, value: nsv });
      car.saveSearchView(nsv).then(() => {
        car.success(`${nsv.name} was saved`);
      });
    },

    emitAndClose() {
      this.$emit("closed", Object.values(this.myViews));
    },
  },
};
</script>

<style>
.active-view {
  background: skyblue;
  border: solid 3px skyblue;
}
</style>
