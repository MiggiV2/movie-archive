import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css";
import "@/assets/css/font.css";
import { getConfig } from './tools/api-wrapper/PubMovie';
import { login } from './tools/AuthManager';

createApp(App).use(router).mount('#app');

export const HOST = window.location.hostname == "localhost"
    ? "http://localhost:8080/api/v2/"
    : window.location.protocol + "//" + window.location.host + "/api/v2/";
console.log("API Host: " + HOST);

if (!localStorage.getItem("authServerUrl")) {
    getConfig().then(config => {
        localStorage.setItem("authServerUrl", config.authServerUrl);
        localStorage.setItem("authClientId", config.authClientId);
        localStorage.setItem("adminRole", config.adminRole);
        localStorage.setItem("platformOwner", config.platformOwner);
        openLogin(config.authServerUrl, config.authClientId);
    });
}
else {
    openLogin();
}

function openLogin() {
    if(window.location.pathname == "/auth" || window.location.pathname == "/") {
        return; // Avoid re-initializing the auth manager on the auth page
    }
    login();
}