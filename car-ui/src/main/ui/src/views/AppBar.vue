<template>
  <div
    v-if="true"
    class="title-bar"
    :style="{
      display: 'flex',
      flexDirection: orientation,
      alignItems: 'center',
      justifyContent: 'center',
      padding: orientation == 'row' ? '5px 20px' : '10px 5px',
    }"
  >
    <!-- <img
      style="width: 30px; margin: 0px"
      src="https://companieslogo.com/img/orig/test-c4767c1c.png?t=1636076858"
    /> -->

    <v-btn
      icon
      :class="{
        'mr-2': orientation == 'row',
        'mb-2': orientation == 'column',
      }"
      @click="$router.push({ path: '/' })"
    >
      <v-icon color="white">mdi-car</v-icon>
    </v-btn>
    <!-- <router-link to="/" class="mr-2"> </router-link> -->
    <div class="carlane">
      <!-- <marquee direction="right" style="flex-grow: 1; height: 30px; position: absolute; bottom: -3px">
        <img
          class="car"
          src="https://img.icons8.com/color/36/000000/car--v1.png"
        />
      </marquee> -->
    </div>
    <v-menu
      :rounded="true"
      offset-y
      :left="orientation == 'row'"
      :bottom="orientation == 'row'"
      :top="orientation == 'column'"
      :right="orientation == 'column'"
    >
      <template v-slot:activator="{ attrs, on }">
        <v-btn
          icon
          :class="{
            'ml-3': orientation == 'row',
            'mt-3': orientation == 'column',
          }"
          v-bind="attrs"
          v-on="on"
        >
          <v-icon color="white">mdi-cog</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-list-item @click="(_) => $router.push(`/ui/logtypegroup`)">
          Rules
        </v-list-item>
        <v-list-item @click="(_) => $router.push(`/ui/help`)">
          Help
        </v-list-item>
      </v-list>
    </v-menu>

    <v-menu
      :rounded="true"
      offset-y
      :left="orientation == 'row'"
      :bottom="orientation == 'row'"
      :top="orientation == 'column'"
      :right="orientation == 'column'"
    >
      <template v-slot:activator="{ attrs, on }">
        <v-btn
          icon
          :class="{
            'ml-3': orientation == 'row',
            'mt-3': orientation == 'column',
          }"
          v-bind="attrs"
          v-on="on"
        >
          <v-icon color="white">mdi-panda</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-list-item @click="logout"> Logout </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import { car } from "@/car.js";
export default {
  name: "AppBar",
  props: ["orientation"],
  methods: {
    logout() {
      car.logout().then(() => {
        window.loggedIn = this.loggedIn = false;
        window.userName = this.userName = "anonymous";
        this.$router.go(this.$router.currentRoute);
      });
    },
  },
};
</script>

<style scoped>
.title-bar {
  padding: 0px 10px 0px 10px;
  font-weight: bolder;
  background-color: #42a5f5;
  /* box-shadow: rgba(0, 0, 0, 0.05) 0px 1px 2px 0px, rgba(0, 0, 0, 0.05) 0px 1px 4px 0px, rgba(0, 0, 0, 0.05) 0px 2px 8px 0px; */
}
.car {
  position: relative;
  width: 30px;
  height: 30px;
  margin: 0px;
}

.carlane {
  flex-grow: 1;
  display: flex;
  align-items: end;
  position: relative;
  height: 30px;
}
</style>
