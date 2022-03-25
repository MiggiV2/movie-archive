export var HOST = "http://localhost:8888/api/";

import { createApp, h } from 'vue/dist/vue.esm-bundler.js';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.min.js";
import "bootstrap-icons/font/bootstrap-icons.css"

import HomePage from './HomePage.vue';
import NoPage from './NoPage.vue';

require('@/assets/css/style.css');

const routes = {
    '/': HomePage,
}

const SimpleRouter = {
    data: () => ({
        currentRoute: window.location.pathname
    }),

    computed: {
        CurrentComponent() {
            return routes[this.currentRoute] || NoPage
        }
    },

    render() {
        return h(this.CurrentComponent)
    }
}

const app = createApp(SimpleRouter);

app.mount('#app')