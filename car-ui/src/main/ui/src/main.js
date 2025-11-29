import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "./plugins/vuetify";
import upperFirst from "lodash/upperFirst";
import camelCase from "lodash/camelCase";
import HighchartsVue from 'highcharts-vue'
import vClickOutside from 'v-click-outside'

Vue.config.productionTip = false;

function registerComponents(requireComponent) {

  requireComponent.keys().forEach(fileName => {
    // Get component config
    const componentConfig = requireComponent(fileName)

    // Get PascalCase name of component
    const componentName = upperFirst(
      camelCase(
        // Gets the file name regardless of folder depth
        fileName
          .split('/')
          .pop()
          .replace(/\.\w+$/, '')
      )
    )

    // Register component globally
    Vue.component(
      componentName,
      // Look for the component options on `.default`, which will
      // exist if the component was exported with `export default`,
      // otherwise fall back to module's root.
      componentConfig.default || componentConfig
    )
  })
}

Vue.use(HighchartsVue);
Vue.use(vClickOutside)

registerComponents(require.context('./components/visualizations', true, /[A-Z]\w+Visualization\w*\.(vue|js)$/));
registerComponents(require.context('./components/datatypes', true, /[A-Z]\w+Editor\.(vue|js)$/));
registerComponents(require.context('./components/stores', true, /[A-Z]\w+Editor\.(vue|js)$/));
registerComponents(require.context('./components/eventhandlers', true, /[A-Z]\w+Editor\.(vue|js)$/));
registerComponents(require.context('./components/detectors', true, /[A-Z]\w+Detector\.(vue|js)$/));
registerComponents(require.context('./components/recordreader', true, /[A-Z]\w+RecordReader\.(vue|js)$/));
registerComponents(require.context('./components/utils', true, /HelpfulLabel\.(vue|js)$/));
registerComponents(require.context('./components/rulebuilder', true, /[A-Z]\w+Editor\.(vue|js)$/));
registerComponents(require.context('./components/readconfigs', true, /[A-Za-z]+(Config|Mapper)\.(vue|js)$/));
registerComponents(require.context('./components/analysis', true, /[A-Za-z]+(AnaComponent)\.(vue|js)$/));

new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount("#app");
