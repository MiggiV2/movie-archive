import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/views/HomePageView';

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
            import ( /* webpackChunkName: "auth" */ '@/views/AuthView.vue')
    }, {
        path: '/search',
        name: 'search',
        component: () =>
            import ( /* webpackChunkName: "search" */ '@/views/SearchView.vue')
    }, {
        path: '/add',
        name: 'add',
        component: () =>
            import ( /* webpackChunkName: "add" */ '@/views/AddView.vue')
    }, {
        path: '/audit-log',
        name: 'audit-log',
        component: () =>
            import ( /* webpackChunkName: "search" */ '@/views/AuditLogView.vue')
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router