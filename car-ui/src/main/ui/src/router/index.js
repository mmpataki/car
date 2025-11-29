import Vue from "vue";
import VueRouter from "vue-router";
import Home from "../views/Home.vue";
import DatasetView from "@/views/DatasetView.vue";
import SideBarApp from "@/views/SideBarApp.vue";
import DatasetWithFilesList from "@/views/DatasetWithFilesList.vue";
import FileView from "@/views/FileView.vue";
import LgroupEditor from "@/views/LgroupEditor.vue";
import LogTypeEditor from "@/views/LogTypeEditor.vue";
import RuleEditor from "@/components/rulebuilder/RuleEditor.vue";
import Help from "@/components/help/Help.vue";
import Test from "@/components/utils/Test.vue";
import LogView from "@/views/LogView.vue";
import UpgradeToRegexV2 from "@/components/utils/UpgradeToRegexV2.vue";
import DatasetEditView from "@/views/DatasetEditView.vue";
import RuleConfigWithList from "@/views/RuleConfigWithList.vue";
import HelpWithList from "@/views/HelpWithList.vue";
Vue.use(VueRouter);

const routes = [
  {
    path: "/ui/datasets",
    name: 'dataset',
    component: SideBarApp,
    props: true,
    children: [
      {
        path: "/ui/datasets/:dsetid",
        name: 'datasetdetail',
        component: DatasetView,
        props: true
      },
      {
        path: "/ui/datasets/:dsetid/edit",
        name: 'editdataset',
        component: DatasetEditView,
        props: true
      },
      {
        path: "/ui/datasets/:dsetid/new",
        name: 'newdataset',
        component: DatasetEditView,
        props: true
      },
      {
        path: "/ui/datasets/:dsetid/reports",
        component: DatasetWithFilesList,
        name: "fileview1",
        props: true,
        children: [
          {
            path: "/ui/datasets/:dsetid/reports/:report/:filename",
            component: FileView,
            name: "fileview",
            props: true
          }
        ]
      },
      {
        path: "/ui/datasets/:dsetid/browse",
        component: DatasetWithFilesList,
        name: "fileview1",
        props: true,
        children: [
          {
            path: "/ui/datasets/:dsetid/browse",
            component: LogView,
            name: "logview",
            props: true
          },
        ]
      },
    ]
  },
  {
    path: "/ui/logtypegroup",
    name: 'rulespage',
    component: SideBarApp,
    props: true,
    children: [
      {
        path: "/ui/logtypegroup",
        name: "ruleslist",
        component: RuleConfigWithList,
        props: true,
        children: [
          {
            path: "/ui/logtypegroup/:logtypegroup",
            name: "lgroupeditor",
            component: LgroupEditor,
            props: true
          },
          {
            path: "/ui/logtypegroup/:logtypegroup/logtype/:logtype",
            name: "logtypeeditor",
            component: LogTypeEditor,
            props: true
          },
          {
            path: "/ui/logtypegroup/:logtypegroup/logtype/:logtype/rule/:rulename",
            name: "ruleeditor",
            component: RuleEditor,
            props: true
          }
        ]
      }
    ]
  },
  {
    path: "/ui/help",
    name: 'helppage',
    component: SideBarApp,
    props: true,
    children: [
      {
        path: "/ui/help",
        name: "helplist",
        component: HelpWithList,
        props: true,
        children: [
          {
            path: "/ui/help/:topic(.*)",
            name: "helptopic",
            component: Help,
            props: true
          },
        ]
      }
    ]
  },
  {
    path: "/ui/home",
    name: "Home",
    component: Home,
  },
  {
    path: "/ui/test",
    name: "test",
    component: Test,
  },
  {
    path: "/ui/upgrade",
    name: "upgrade",
    component: UpgradeToRegexV2,
  },
  {
    path: "*",
    redirect: "/ui/home"
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
