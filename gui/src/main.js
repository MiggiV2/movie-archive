import { createApp } from 'vue'
import App from '@/App.vue'
import router from '@/router'

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css";
import "@/assets/css/font.css";
import { getCookie } from './tools/Cookies';
import { openLogin, runRefreshTokenFlow } from './tools/Auth';

createApp(App).use(router).mount('#app');

const hasToken = getCookie("accessToken");
const hasRefreshToken = getCookie("refreshToken");
const hasUserData = localStorage.getItem("name");

if (!hasToken) {
    if (hasRefreshToken) {
        runRefreshTokenFlow();
    }
    else if(hasUserData && !window.location.pathname.startsWith("/auth")) {
        openLogin();
    }
}