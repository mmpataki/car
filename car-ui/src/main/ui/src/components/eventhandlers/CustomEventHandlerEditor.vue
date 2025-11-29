<template>
  <div>
    <div>
      <v-textarea
        label="JS Code"
        hint="Custom JS code to handle event"
        v-model="ehandler.parameters.funcCode"
      ></v-textarea>
    </div>
  </div>
</template>

<script>
export default {
  name: "CustomEventHandlerEditor",
  props: ["ehandler", "visualization", "dashboard"],
};
</script>
