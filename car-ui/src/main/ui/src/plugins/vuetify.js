import "@mdi/font/css/materialdesignicons.min.css";
import Vue from "vue";
import Vuetify from "vuetify/lib/framework";

Vue.use(Vuetify);

export default new Vuetify({
    icons: {
        iconfont: 'mdi', // default - only for display purposes
    },
    theme: {
        themes: {
            light: {
                primary: '#2e8b57',
                anchor: '#1E88E5',
                accent: '#2e8b57',
            },
        },
    },
});
