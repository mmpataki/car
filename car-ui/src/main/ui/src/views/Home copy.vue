<template>
  <div
    v-if="true"
    style="
      height: 100%;
      width: 100%;
      display: flex;
      flex-direction: column;
      color: black;
    "
  >
    <div style="background-color: #42a5f5">
      <div
        style="
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 10px 20px;
        "
      >
        <div style="flex-grow: 1; display: flex; align-items: end;">
          <b style="font-family: 'Courier New', Courier, monospace; font-size: 1.3em; color: white">CAR</b>
        </div>
        <div style="width: 25%">
          <v-text-field
            placeholder="search datasets"
            outlined
            dense
            append-icon="mdi-magnify"
            v-model="filter"
            @input="filterChanged"
            dark
          ></v-text-field>
        </div>
        <div style="flex-grow: 1;">
        </div>
      </div>
    </div>
  </div>
  <div
    v-else
    style="
      height: calc(100vh - 48px);
      width: 100%;
      display: flex;
      background: ghostwhite;
      flex-grow: 1;
      color: black;
    "
  >
    <div style="margin: 100px; flex-grow: 1; overflow: auto">
      <h1 style="font-size: 5em; margin-bottom: 20px">
        Welcome, {{ userName }}!
      </h1>

      <file-upload-zone
        v-if="loggedIn && tab == 1"
        message="drag a file here to quickly analyze"
        style="margin-right: 20px"
        :dataset="'adhoc'"
      >
        <template slot="message">
          <small style="color: seagreen"
            >drag a file here to quickly analyze</small
          >
        </template>

        <div style="position: relative">
          <div style="margin: 20px 20px" v-if="history && history.length > 0">
            <p>Here is where you left off last time..</p>
            <div style="padding: 20px 10px">
              <ul>
                <li v-for="(hitem, idx) in history" :key="idx">
                  <router-link :to="hitem.link">{{ hitem.label }}</router-link>
                </li>
              </ul>
            </div>
          </div>

          <div style="margin: 40px 20px">
            <strong>Need any help? Here are some starters</strong>
            <div style="padding: 20px 10px">
              <ul>
                <li>
                  <router-link to="/ui/help/quick-start"
                    >Quick start</router-link
                  >
                </li>
                <li>
                  <router-link to="/ui/help">Manual</router-link>
                </li>
                <li>
                  <router-link
                    to="/ui/datasets/public/samples/reports/default/sar.txt"
                    >Sample dashboards</router-link
                  >
                </li>
              </ul>
            </div>
          </div>
        </div>
      </file-upload-zone>

      <div
        v-if="!loggedIn && tab == 1"
        style="margin: 20px 0px; font-size: 1.3em"
      >
        <v-btn
          x-small
          class="ml-1 mb-1"
          color="primary"
          text
          @click="loginOrRegister = loginOrRegister != 1 ? 1 : 0"
          >Login</v-btn
        >
        /
        <v-btn
          x-small
          class="ml-1 mb-1"
          color="primary"
          text
          @click="loginOrRegister = loginOrRegister != 2 ? 2 : 0"
          >Register</v-btn
        >
        to explore all reports and rules
      </div>

      <div v-if="tab == 3">
        <p style="font-size: 1.3em">
          Here is what we have in for you.
          <span v-if="!loggedIn">You may want to login to see all stats</span>
        </p>
        <div style="display: flex; margin: 40px 0px">
          <router-link
            :to="metric.link"
            v-for="(metric, idx) in metrics"
            :key="idx"
            style="text-decoration: none; margin-right: 20px"
          >
            <div
              :style="{
                border: `solid 1px ${metric.color}`,
                padding: '10px 40px',
                borderRadius: '15px',
                background: metric.background,
                textAlign: 'center',
              }"
            >
              <strong
                :style="{
                  'font-size': '8em',
                  'line-height': '1.3',
                  color: metric.pcolor,
                  display: 'block',
                }"
                >{{ metric.value }}</strong
              >
              <strong :style="{ 'font-size': '1em', color: metric.pcolor }">{{
                metric.name
              }}</strong>
            </div>
          </router-link>
        </div>
      </div>
    </div>

    <div
      style="
        width: 300px;
        display: flex;
        flex-direction: column;
        align-items: start;
        justify-content: center;
      "
    >
      <div style="width: 100%">
        <div
          class="side-link"
          :style="{
            borderLeft:
              tab != 1 ? 'solid 5px transparent' : 'solid 5px seagreen',
          }"
          @click="tab = 1"
        >
          Welcome page
        </div>
        <div
          class="side-link"
          v-if="loggedIn"
          :style="{
            borderLeft:
              tab != 2 ? 'solid 5px transparent' : 'solid 5px seagreen',
          }"
          @click="tab = 2"
        >
          Account
        </div>
        <div
          class="side-link"
          :style="{
            borderLeft:
              tab != 3 ? 'solid 5px transparent' : 'solid 5px seagreen',
          }"
          @click="tab = 3"
        >
          Stats
        </div>
      </div>
    </div>

    <div
      style="
        position: absolute;
        top: 10px;
        right: 10px;
        display: flex;
        flex-direction: column;
        align-items: right;
      "
    >
      <div style="display: flex; justify-content: right">
        <v-btn x-small class="ml-1 mb-1" color="primary" @click="logout"
          >Logout</v-btn
        >
      </div>
    </div>
  </div>
</template>

<script>
import DsetSearchView from "./DsetSearchView.vue";
import FileUploadZone from "../components/utils/FileUploadZone.vue";
import { car } from "@/car.js";
export default {
  components: { FileUploadZone, DsetSearchView },
  data() {
    return {
      loggedIn: car.isLoggedIn(),

      userName: car.currentUid() || "anonymous",

      history: undefined,
      historyLimit: 10,

      tab: 1,

      metrics: [
        {
          name: "Datasets",
          value: 34,
          pcolor: "#1E88E5",
          scolor: "#42A5F5",
          background: "#E3F2FD",
          link: "/ui/datasets",
        },
        {
          name: "Product Groups",
          value: 8,
          pcolor: "#43A047",
          scolor: "#42A5F5",
          background: "#E8F5E9",
          link: "/ui/rules",
        },
        {
          name: "Dashboards",
          value: 14,
          pcolor: "#EF6C00",
          scolor: "#42A5F5",
          background: "#FFF3E0",
          link: "/ui/datasets",
        },
      ],
    };
  },

  created() {
    if (this.loggedIn) {
      this.updateHistory();
    }
  },
  methods: {
    updateHistory() {
      this.history = car.getHistory();
    },
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
.side-link {
  cursor: pointer;
  width: 100%;
  padding: 5px 20px;
}
.side-link:hover {
  background: #fafafa;
}

.search-box::placeholder {
  color: white;
}
.search-box {
  background: transparent;
  border-radius: 5px;
  padding: 2px 10px;
  width: 100%;
  border: solid 1px white;
  color: white;
}
.search-box:focus {
  background: white;
  color: black;
}
</style>