import {createRouter, createWebHistory} from 'vue-router'
import {unauthorized} from "@/net/index.js";


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                },
                {
                    path: 'reset',
                    name: 'welcome-reset',
                    component: () => import('@/views/welcome/ResetPage.vue')
                },
            ]
        }, {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})

// 配置路由守卫 防止用户没有登录就能访问我们的页面
router.beforeEach((to, form, next) => {
    // 判断是否完成登录
    const isUnauthorized = unauthorized()
    // 用户已经登录并且访问登录页面
    if (to.name.startsWith('welcome-') && !isUnauthorized) {
        next('/index')
        // 没有登录访问登录后的页面
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router
