import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css";

export var HOST = "http://localhost:8080/movie-archive/";
export var AUTH_HOST = "http://localhost:8180/";
export var Owner = "Familie Hainz";

createApp(App).use(router).mount('#app')