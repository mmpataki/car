<template>
  <v-app style="position: relative">
    <router-view v-if="loggedIn != undefined && loggedIn"/>
    <splitpanes
      class="default-theme"
      v-if="false && loggedIn != undefined && loggedIn"
    >
      <pane
        :size="tab == 0 ? 100 : 25"
        :max-size="tab == 0 ? 100 : 60"
        class="leftpane"
      >
        <div style="height: 100%; width: 100%">
          <v-tabs v-model="tab" dense>
            <v-tab style="padding: 0px 0px; margin: 0px 3px">
              <router-link to="/ui" class="tab-link"> Home </router-link>
            </v-tab>

            <v-tab style="padding: 0px 0px; margin: 0px 3px">
              <router-link to="/ui/datasets" class="tab-link">
                Dataset
              </router-link>
            </v-tab>

            <v-tab style="padding: 0px 0px; margin: 0px 3px">
              <router-link to="/ui/rules" class="tab-link"> Rules </router-link>
            </v-tab>

            <v-tab style="padding: 0px 0px; margin: 0px 3px">
              <router-link to="/ui/help" class="tab-link"> Help </router-link>
            </v-tab>
          </v-tabs>
          <v-tabs-items v-model="tab" style="height: 100%; overflow: auto">
            <v-tab-item>
              <Home></Home>
            </v-tab-item>
            <v-tab-item>
              <DatasetTree style="padding: 20px 0px"></DatasetTree>
            </v-tab-item>
            <v-tab-item>
              <RulesTree style="padding: 20px 0px"></RulesTree>
            </v-tab-item>
            <v-tab-item>
              <HelpTree style="padding: 20px 0px"></HelpTree>
            </v-tab-item>
          </v-tabs-items>
        </div>
      </pane>
      <pane v-if="tab != 0" size="75" style="height: 100%; overflow: auto">
        <router-view/>
      </pane>
    </splitpanes>
    <div
      style="
        position: absolute;
        bottom: 0px;
        right: 15px;
        display: flex;
        flex-direction: column;
      "
    >
      <v-alert
        v-for="(alert, idx) in alerts"
        :key="idx"
        dismissible
        elevation="1"
        :type="alert.type"
        width="400px"
      >
        {{ alert.msg }}
      </v-alert>
    </div>
    <div
      v-if="loggedIn != undefined && !loggedIn"
      style="
        background: ghostwhite;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        width: 100%;
      "
    >
      <div
        style="border: solid 1px lightgray; padding: 45px 60px"
        class="loginform"
      >
        <div v-if="login">
          <h3 style="margin: 20px 0px">Login</h3>
          <v-text-field
            label="User name"
            outlined
            v-model="lusername"
            class="mb-2"
            dense
          ></v-text-field>
          <v-text-field
            label="Password"
            outlined
            password
            v-model="lpassword"
            type="password"
            class="mb-2"
            dense
            style="width: 250px"
          ></v-text-field>
          <div style="display: flex; margin: 10px 0px">
            <v-btn small color="primary" @click="doLogin">Login</v-btn>
            <v-btn small text @click="login = false"> register</v-btn>
          </div>
        </div>
        <div v-if="!login">
          <h3 style="margin: 20px 0px">Register</h3>

          <v-text-field
            label="User name"
            outlined
            v-model="rusername"
            class="mb-2"
            dense
          ></v-text-field>
          <v-text-field
            label="Email"
            outlined
            v-model="remail"
            class="mb-2"
            dense
          ></v-text-field>
          <v-text-field
            label="Password"
            outlined
            password
            v-model="rpassword"
            type="password"
            class="mb-2"
            dense
            style="width: 250px"
          ></v-text-field>
          <div style="display: flex; margin: 10px 0px">
            <v-btn small color="primary" @click="doRegister"> register</v-btn>
            <v-btn small color="primary" text @click="login = true"
              >Login</v-btn
            >
          </div>
        </div>
      </div>
    </div>
  </v-app>
</template>

<script>
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import RulesTree from "@/components/RulesTree";
import HelpTree from "@/components/HelpTree";
import Home from "@/views/Home.vue";
import { car } from "@/car.js";
export default {
  name: "App",
  components: { Splitpanes, Pane, RulesTree, HelpTree, Home },
  computed: {
    tab() {
      let path = this.$route.path.split("/")[2];
      console.log(path);
      switch (path) {
        case "datasets":
          return 1;
        case "rules":
        case "logtypegroup":
          return 2;
        case "help":
          return 3;
        case "test":
          return 4;
        default:
          return 0;
      }
    },
  },
  data() {
    return {
      alerts: [],
      loggedIn: undefined,
      login: true,

      /* for login */
      lusername: "",
      lpassword: "",

      /* for registration */
      rusername: "",
      rpassword: "",
      remail: "",
    };
  },
  created() {
    car
      .testLogin()
      .then(() => {
        this.loggedIn = true;
      })
      .catch(() => (this.loggedIn = false))
      .finally(() => {
        car.setUpAlertHandler((e) => {
          let alert = {
            msg: e.message,
            type: e.type,
          };
          this.alerts.push(alert);
          setTimeout(() => {
            this.alerts.splice(
              this.alerts.findIndex((x) => x == alert),
              1
            );
          }, 3000);
        });
      });
  },
  methods: {
    doLogin() {
      car
        .login(this.lusername, this.lpassword)
        .then(() => {
          window.loggedIn = this.loggedIn = true;
          window.userName = this.lusername;
          car.success(`Welcome ${this.lusername}!`);
        })
        .catch(() =>
          car.error("Couldn't login, please check your credentials")
        );
    },
    doRegister() {
      car.register(this.remail, this.rusername, this.rpassword).then(() => {
        this.login = true;
        car.success(
          "Registration successful, please check your email inbox to confirm the registration"
        );
      });
    },
  },
};
</script>

<style scoped>
.splitpanes__pane {
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Inter, BlinkMacSystemFont, Helvetica, Arial, sans-serif;
}
.splitpanes.default-theme .splitpanes__pane {
  background-color: transparent;
}
.v-application {
  height: 100%;
}
.tab-link {
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: seagreen;
}
</style>

<style>
.leftpane .v-window__container,
.leftpane .v-window-item,
.leftpane .v-tabs-bar {
  background: ghostwhite !important;
}
</style>