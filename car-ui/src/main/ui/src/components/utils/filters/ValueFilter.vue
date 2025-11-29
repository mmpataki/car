<template>
  <div style="display: flex; flex-direction: column">
    <div style="display: flex; align-items: center; margin: 2px 5px">
      <input
        v-model="selectall"
        @change="pickAllCurrentValues"
        type="checkbox"
      />
      <input
        @input="selectall = false"
        type="text"
        v-model="search"
        style="
          flex-grow: 1;
          border: solid 1px black;
          margin-left: 2px;
          padding: 0px 2px;
          border-radius: 3px;
          background: white;
          font-size: 12px;
          outline: none;
        "
      />
    </div>
    <ul class="list valuelist" ref="valueList">
      <li
        v-for="(val, idx) in vals"
        :key="idx"
        class="key"
        :style="{display: (search == '' || val.toLowerCase().includes(search.toLowerCase())) ? 'block' : 'none'}"
      >
        <label style="white-space: nowrap; flex-grow: 1; cursor: pointer">
          <input
            @change="emitChange"
            :class="{
              valuepicker: true,
              'valuepicker-visible':
                search == '' ||
                val.toLowerCase().includes(search.toLowerCase()),
            }"
            type="checkbox"
            :dataKey="val"
          />
          <span class="key-text">{{ val }}</span>
        </label>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "ValueFilter",
  props: ["data", "datakey", "filter"],
  data() {
    return {
      selectall: false,
      search: "",
      vals: [...new Set(this.data.map((item) => item[this.datakey]))],
    };
  },
  created() {
    this.filter._accepts = (row) => {
      let selectedOnes = this.selectedOnes;
      if (selectedOnes.length == 0) {
        return true;
      }
      for (let i = 0; i < selectedOnes.length; i++) {
        if (selectedOnes[i].getAttribute("datakey") == row[this.datakey]) {
          return this.filter.type == "in";
        }
      }
      return this.filter.type != "in";
    };
  },
  methods: {
    emitChange() {
      /* query selector many times can be costly, so buffer these for incoming data */
      this.selectedOnes = this.$refs.valueList.querySelectorAll(
        "input.valuepicker:checked"
      );
      this.filter.enabled = this.selectedOnes && this.selectedOnes.length > 0;
      this.$emit("change");
    },
    pickAllCurrentValues() {
      let toBePicked = this.$refs.valueList.querySelectorAll(
        "input.valuepicker-visible"
      );
      if (toBePicked) {
        for (let i = 0; i < toBePicked.length; i++) {
          toBePicked[i].checked = this.selectall;
        }
        this.emitChange();
      }
    },
  },
};
</script>

<style>
</style>