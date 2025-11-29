<template>
  <div
    v-click-outside="closePopup"
    @click="openPopup"
    style="display: flex; flex-direction: column; position: relative"
  >
    <button
      :style="{
        background: open ? 'white' : 'transparent',
        border: open ? 'solid 2px lightgray' : 'none',
        padding: '0px 7px',
        'z-index': 101,
        'border-bottom': open ? 'none' : 'none',
      }"
    >
      <v-icon style="font-size: 16px">mdi-filter</v-icon>
    </button>
    <div
      :style="{
        position: 'absolute',
        top: '18px',
        right: '0px',
        background: 'white',
        border: 'solid 2px lightgray',
        'z-index': 100,
        'margin-top': '5px',
        display: open ? 'block' : 'none',
      }"
    >
      <ul ref="mainmenu" class="list">
        <li
          :style="{ position: 'relative' }"
          :class="{ key: true, 'key-selected': hoveredKey == key }"
          v-for="key in keys"
          :key="key"
          @click="hoveredKey = key"
        >
          <div style="display: flex">
            <span>
              <v-icon class="key-icon" style="font-size: 18px"
                >mdi-chevron-left
              </v-icon>
              <div
                :style="{
                  right: subMenuPos - 5 + 'px',
                  top: '0px',
                  position: 'absolute',
                  background: 'white',
                  border: 'solid 2px lightgray',
                  'max-height': '150px',
                  'max-width': '200px',
                  display: hoveredKey == key ? 'block' : 'none',
                }"
              >
                <div
                  style="position: relative: height: auto; overflow: auto; padding: 5px"
                >
                  <div style="width: 100%; display: flex; height: 30px">
                    <select
                      size="1"
                      @change="filterChanged(key, $event)"
                      style="margin: 5px; flex-grow: 1"
                    >
                      <optgroup
                        :label="filterGroupName"
                        v-for="(filterGroup, filterGroupName) in filterTypes"
                        :key="filterGroupName"
                      >
                        <option
                          :value="type"
                          :key="type"
                          v-for="type in filterGroup.types"
                        >
                          {{ type }}
                        </option>
                      </optgroup>
                    </select>
                  </div>
                  <div style="max-height: 105px">
                    <component
                      :is="filterComponent(key)"
                      :data="data"
                      :datakey="key"
                      :filter="filters[key]"
                      :key="filters[key].viewkey"
                      @change="$emit('change')"
                    ></component>
                  </div>
                </div>
              </div>
            </span>

            <span class="key-text" style="white-space: nowrap">{{
              key + (filters[key].enabled ? "*" : "")
            }}</span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import ValueFilter from "./ValueFilter.vue";
import TextFilter from "./TextFilter.vue";
import NumericFilter from "./NumericFilter.vue";
import ClickOutside from "vue-click-outside";

function Filter(type) {
  this.enabled = false;
  this.viewkey = +new Date();
  this.type = type || "in";
  this.accepts = (row) => {
    if (!this.enabled) return true;
    return this._accepts(row);
  };
}

export default {
  name: "FilterMenu",
  props: ["data", "filters"],
  components: { ValueFilter, TextFilter, NumericFilter },
  directives: {
    ClickOutside,
  },
  data() {
    return {
      hoveredKey: null,
      open: false,
      keys: Object.keys(this.data[0]),
      subMenuPos: 0,
      filterTypes: {
        "Value filter": {
          component: "ValueFilter",
          types: ["in", "not in"],
        },
        "Text filters": {
          component: "TextFilter",
          types: [
            "contains",
            "doesn't contain",
            "equal to",
            "not equal to",
            "starts with",
            "doesn't start with",
            "ends with",
            "doesn't end with",
          ],
        },
        "Numeric filters": {
          component: "NumericFilter",
          types: [
            "greater than",
            "less than",
            "greater than or equal to",
            "less than or equal to",
          ],
        },
      },
    };
  },
  created() {
    Object.keys(this.data[0]).forEach((key) => {
      this.$set(this.filters, key, new Filter());
    });

    /* filter type => Components */
    this.filterComponents = {};
    Object.values(this.filterTypes).forEach((group) => {
      group.types.forEach((type) => {
        this.filterComponents[type] = group.component;
      });
    });
  },
  methods: {
    filterChanged(key, event) {
      this.$set(this.filters, key, new Filter(event.target.value));
    },
    filterComponent(key) {
      return this.filterComponents[this.filters[key].type];
    },

    /* this was freaking awesome hack */
    openPopup() {
      this.open = true;
      setTimeout(
        () => (this.subMenuPos = this.$refs.mainmenu.offsetWidth + 7),
        500
      );
    },

    closePopup() {
      this.open = false;
    },
  },
};
</script>

<style>
.valuepicker {
  margin: 0px 2px;
}
.key {
  cursor: pointer;
  padding: 0px 3px;
}
.key-text {
  padding: 0px 5px;
}
.key:hover {
  background: #eee;
}
.key-icon {
  display: none;
}
.list {
  list-style-type: none;
  padding-left: 0px !important;
}
.key-selected {
  background: #eee;
  border-top: solid 1px lightgray;
  border-bottom: solid 1px lightgray;
}
</style>