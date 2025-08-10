import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css";
import "@/assets/css/font.css";
import { getCookie } from './tools/Cookies';
import { openLogin, runRefreshTokenFlow } from './tools/Auth';
import { getConfig } from './tools/api-wrapper/PubMovie';

createApp(App).use(router).mount('#app');

const hasToken = getCookie("accessToken");
const hasRefreshToken = getCookie("refreshToken");
const hasUserData = localStorage.getItem("name");

if (!hasToken) {
    if (hasRefreshToken) {
        runRefreshTokenFlow();
    }
    else if (hasUserData && !window.location.pathname.startsWith("/auth")) {
        openLogin();
    }
}

export const HOST = window.location.hostname == "localhost"
    ? "http://localhost:8080/api/v1/movie-archive/"
    : window.location.protocol + "//" + window.location.host + "/api/v1/movie-archive/";
console.log("API Host: " + HOST);

if (!localStorage.getItem("authServerUrl")) {
    getConfig().then(config => {
        localStorage.setItem("authServerUrl", config.authServerUrl);
        localStorage.setItem("authClientId", config.authClientId);
        localStorage.setItem("adminRole", config.adminRole);
        localStorage.setItem("platformOwner", config.platformOwner);
    });
}