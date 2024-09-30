import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/views/HomePageView';

import { openLogin, refreshToken } from '@/tools/Auth';
import { getCookie } from '@/tools/Cookies';

const routes = [{
    path: '/',
    name: 'home',
    component: HomePage
},
{
    path: '/auth',
    name: 'auth',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
        import( /* webpackChunkName: "auth" */ '@/views/AuthView.vue')
}, {
    path: '/search',
    name: 'search',
    component: () =>
        import( /* webpackChunkName: "search" */ '@/views/SearchView.vue')
}, {
    path: '/add',
    name: 'add',
    component: () =>
        import( /* webpackChunkName: "add" */ '@/views/AddView.vue')
}, {
    path: '/audit-log',
    name: 'audit-log',
    component: () =>
        import( /* webpackChunkName: "audit-log" */ '@/views/AuditLogView.vue')
}, {
    path: '/logout',
    name: 'logout',
    component: () =>
        import( /* webpackChunkName: "logout" */ '@/views/LogoutView.vue')
}
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach(async (guard) => {    
    const isCallback = guard.path.endsWith('auth') || guard.path.endsWith('logout') || guard.path.endsWith('');
    if (isCallback) return;

    if (!getCookie("accessToken") && !getCookie("refreshToken")) {
        openLogin();
    } else if (!getCookie("accessToken")) {
        await refreshToken();
    }
})

export default router