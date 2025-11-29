<template>
  <div
    style="
      background: ghostwhite;
      display: flex;
      align-items: center;
      justify-content: center;
    "
  >
    <div>
      <v-text-field v-model="username"></v-text-field>
      <v-text-field password v-model="password"></v-text-field>
      <div style="display: flex">
        <v-btn>Login</v-btn>
        <v-btn text> register</v-btn>
      </div>
    </div>
  </div>
</template>

<script>
export default {};
</script>

<style>
</style>