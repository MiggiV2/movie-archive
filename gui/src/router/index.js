import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/views/HomePage';

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
            import ( /* webpackChunkName: "auth" */ '@/views/Auth.vue')
    }, {
        path: '/search',
        name: 'search',
        component: () =>
            import ( /* webpackChunkName: "search" */ '@/views/Search.vue')
    }, {
        path: '/add',
        name: 'add',
        component: () =>
            import ( /* webpackChunkName: "add" */ '@/views/Add.vue')
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router