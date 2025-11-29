<template>
  <div class="collapsible-pane" style="position: relative; min-height: 20px">
    <div
      v-if="type == 'left'"
      style="
        position: absolute;
        height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        right: 0;
      "
    >
      <div
        :class="{ 'collapse-btn': !collapsed }"
        @click="collapsed = !collapsed"
        style="
          padding: 10px 2px 10px 3px;
          border: solid 1px lightgray;
          border-right: none;
          border-top-left-radius: 3px;
          border-bottom-left-radius: 3px;
          cursor: pointer;
        "
      >
        <i
          v-if="!collapsed"
          class="fa fa-chevron-left"
          style="font-size: 0.7em"
        ></i>
        <i
          v-if="collapsed"
          class="fa fa-chevron-right"
          style="font-size: 0.7em"
        ></i>
      </div>
    </div>

    <div
      v-if="type == 'top'"
      style="
        position: absolute;
        width: 100%;
        display: flex;
        justify-content: center;
        bottom: 0;
      "
    >
      <div
        :class="{ 'collapse-btn': !collapsed }"
        @click="collapsed = !collapsed"
        style="
          padding: 0px 10px 0px 10px;
          border: solid 1px lightgray;
          border-bottom: none;
          border-top-right-radius: 3px;
          border-top-left-radius: 3px;
          cursor: pointer;
        "
      >
        <i
          v-if="!collapsed"
          class="fa fa-chevron-up"
          style="font-size: 0.7em"
        ></i>
        <i
          v-if="collapsed"
          class="fa fa-chevron-down"
          style="font-size: 0.7em"
        ></i>
        <span v-if="collapsed">{{ text }}</span>
      </div>
    </div>

    <div
      v-if="type == 'right'"
      style="
        position: absolute;
        height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        left: 0;
      "
    >
      <div
        :class="{ 'collapse-btn': !collapsed }"
        @click="collapsed = !collapsed"
        style="
          padding: 10px 2px 10px 3px;
          border: solid 1px lightgray;
          border-top-right-radius: 3px;
          border-bottom-right-radius: 3px;
          cursor: pointer;
          border-left: none;
        "
      >
        <i
          v-if="!collapsed"
          class="fa fa-chevron-right"
          style="font-size: 0.7em"
        ></i>
        <i
          v-if="collapsed"
          class="fa fa-chevron-left"
          style="font-size: 0.7em"
        ></i>
      </div>
    </div>

    <div
      v-if="type == 'bottom'"
      style="
        position: absolute;
        width: 100%;
        display: flex;
        justify-content: center;
        bottom: 0;
      "
    >
      <div
        :class="{ 'collapse-btn': !collapsed }"
        @click="collapsed = !collapsed"
        style="
          padding: 10px 2px 10px 3px;
          border: solid 1px lightgray;
          border-top-left-radius: 3px;
          border-bottom-left-radius: 3px;
          cursor: pointer;
        "
      >
        <i
          v-if="!collapsed"
          class="fa fa-chevron-left"
          style="font-size: 0.7em"
        ></i>
        <i
          v-if="collapsed"
          class="fa fa-chevron-right"
          style="font-size: 0.7em"
        ></i>
      </div>
    </div>

    <slot v-if="!collapsed"></slot>
  </div>
</template>

<script>
export default {
  name: "CollapsiblePane",
  props: ["type", "text"],
  data() {
    return {
      collapsed: false,
    };
  },
};
</script>
