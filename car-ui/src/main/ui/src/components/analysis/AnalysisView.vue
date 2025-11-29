<template>
  <div>
    <div
      style="
        display: flex;
        background: aliceblue;
        align-items: center;
        padding: 5px;
      "
    >
      <v-btn @click="(filter = !filter), doFilter()" small text>{{
        filter ? "hide-clutter" : "show-all"
      }}</v-btn>
      <v-btn @click="showCommentBox = true" small text>post comment</v-btn>
      <v-btn @click="update(true)" small text>re-analyze</v-btn>
    </div>
    <div v-if="showCommentBox" style="margin: 20px 5px; padding: 0px 20px">
      <v-textarea v-model="comment" outlined></v-textarea>
      <div style="display: flex">
        <v-spacer></v-spacer>
        <v-btn
          small
          color="primary"
          @click="postComment"
          style="margin-right: 5px"
          >Post</v-btn
        >
        <v-btn small color="error" text @click="showCommentBox = false"
          >Cancel</v-btn
        >
      </div>
    </div>
    <div style="padding: 0px 15px">
      <div v-for="(histent, idx) in history" :key="idx" class="case-event">
        <component
          v-for="[key, values] in histent.knowledge"
          :is="getComponentType(key)"
          :key="key"
          :objs="values"
          :dset="dset"
          @component-not-found="handleComponentNotFound"
        />
      </div>
    </div>
    <v-overlay :value="loading" color="white" :absolute="true">
      <v-progress-circular
        indeterminate
        size="32"
        color="primary"
      ></v-progress-circular>
    </v-overlay>
  </div>
</template>

<script>
import { get, post, car } from "@/car.js";
export default {
  name: "AnalysisView",
  props: ["analyticskey", "dset"],
  data() {
    return {
      history: [],
      loading: true,
      filter: false,
      data: undefined,
      showCommentBox: false,
      comment: "",
    };
  },
  created() {
    this.update(false);
  },
  methods: {
    doFilter() {
      function orderedKvp(data) {
        let list = [
          "case_comment",
          "casedetail",
          "car_comment",
          "case_type",
          "missing_info",
        ];
        let ret = [],
          used = {};
        list.forEach((k) => {
          if (data[k]) {
            ret.push([k, data[k]]);
            used[k] = 1;
          }
        });
        Object.keys(data).forEach((k) => {
          if (!used[k]) ret.push([k, data[k]]);
        });
        return ret;
      }
      let data = JSON.parse(JSON.stringify(this.data.json));
      this.history = data.history
        .slice()
        .reverse()
        .map((histent) => {
          ["analysis_done", "unverified_org_id", "unverified_trace_id"].forEach(
            (key) => delete histent.knowledge[key]
          );
          return histent;
        })
        .filter((histent) =>
          !this.filter ? Object.keys(histent.knowledge).length > 1 : true
        )
        .map((histent) => {
          histent.knowledge = orderedKvp(histent.knowledge);
          return histent;
        });
    },
    update(reanalyze) {
      this.loading = true;
      get(
        reanalyze
          ? `http://localhost:7006/api/re-process/events/of/${this.analyticskey}`
          : `http://localhost:7006/api/analysis/for/${this.analyticskey}`
      ).then((resp) => {
        this.data = resp;
        this.doFilter();
        this.loading = false;
      });
    },
    getComponentType(type) {
      function snakeToCamel(str) {
        return str
          .replace(/([-_]\w)/g, (match) => match.charAt(1).toUpperCase())
          .replace(/^\w/, (match) => match.toUpperCase());
      }
      return snakeToCamel(type) + "AnaComponent";
    },
    handleComponentNotFound(e) {
      console.log(e);
    },
    postComment() {
      if (this.comment.trim() == "") {
        car.error("No text to comment");
        return;
      }
      post(
        `http://localhost:7006/api/events/for/${this.analyticskey}?eventname=car_comment`,
        [this.comment]
      ).then((resp) => {
        console.log(resp);
        car.success("Posted successfully");
        this.showCommentBox = false;
      });
    },
  },
};
</script>

<style scoped>
.history-container {
  margin-bottom: 20px;
}

.info-container {
  margin-bottom: 5px;
}

strong {
  font-weight: bold;
}

h3 {
  margin-bottom: 10px;
}

.case-event {
  padding: 20px 0px;
  border-bottom: solid 1px gray;
}
</style>