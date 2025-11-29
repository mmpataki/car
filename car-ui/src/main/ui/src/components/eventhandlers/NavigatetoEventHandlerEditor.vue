<template>
  <div>
    <div>
      <v-text-field
        label="URL"
        hint="Where to navigate to"
        v-model="ehandler.parameters.url"
      ></v-text-field>
    </div>
  </div>
</template>

<script>
export default {
  name: "NavigatetoEventHandlerEditor",
  props: ["ehandler", "visualization", "dashboard"],
};
</script>
