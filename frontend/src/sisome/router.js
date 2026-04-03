import { createRouter, createWebHashHistory } from 'vue-router'
import { createDiscreteApi } from 'naive-ui'
import { notice } from '@/utils'

import LoadPlaceholder from '@/components/LoadPlaceholder.vue'

import configStore from '@/store/config'
import authStore from '@/store/auth'

import { getMenu, openMenu } from '@/sisome/menu'


const { loadingBar } = createDiscreteApi(['loadingBar'])
const modules = import.meta.glob('/src/views/**/**.vue')

// 默认路由
const routes = [
    {
        name: 'dashboard',
        path: '/',
        component: () => import('@/views/dashboard.vue')
    },
    {
        name: 'login',
        path: '/login',
        component: () => import('@/views/login.vue')
    },
    {
        name: 'error',
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/error.vue')
    }
]

const whiteList = [
    '/login',
    '/error'
]
const includes = function(matched) {
    for (const matchedElement of matched) {
        if (whiteList.includes(matchedElement.path)) {
            return true
        }
    }
    return false
}


const router = createRouter({
    history: createWebHashHistory(),
    routes
})
// 全局前置守卫
router.beforeEach((to, from, next) => {
    const auth = authStore()
    if (!auth.isAuth()) {
        if (!includes(to.matched)) {
            next('/login')
            return
        }
        next()
        return
    }
    loadingBar.start()
    if (!includes(to.matched)) {
        if (!next) {
            throw new Error('RouterInterceptor:next is undefined')
        }
        // 非菜单项直接跳转
        if(!to.meta.menuId){
            next()
            return
        }

        var menu = getMenu(to.meta.menuId)
        if (isEmpty(menu)) {
            console.warn('RouterInterceptor:Menu is not found, nav failed')
            return
        }
        // 菜单项需要
        openMenu(menu)
        next()
    } else {
        next()
    }
})

// 全局后置钩子
router.afterEach((to, from) => {
    loadingBar.finish()
})

const { RouteAlive, removeComponentCache } = useRouterAlive({
    sisome : sisome,
    router: router,
    element: '#main-content>.n-layout-scroll-container', // 滚动条内容
    placeHolderComponent: LoadPlaceholder
})

export { RouteAlive, removeComponentCache }

export default router