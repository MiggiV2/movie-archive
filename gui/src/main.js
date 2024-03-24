import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css";

export var HOST = "https://apis.mymiggi.de/movie-archive/";
export var AUTH_HOST = "https://auth.familyhainz.de/";
export var Owner = "Familie Hainz";
export var OMDB_KEY = "a90598ab"

createApp(App).use(router).mount('#app')