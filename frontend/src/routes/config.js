import { createDiscreteApi } from 'naive-ui'
import { notice } from '@/utils'

import { useRouterInterceptor } from "../sisome/router"
import sisome from '../sisome'
import useConfig from '../config'
import useAuth from '../auth'



const { loadingBar } = createDiscreteApi(['loadingBar'])
const modules = import.meta.glob('/src/views/**/**.vue')

// 默认路由
const routes = [
    {
        name: 'home',
        path: '/',
        component: () => import('@/views/home/index.vue')
    },
    {
        name: 'login',
        path: '/login',
        component: () => import('@/views/login.vue')
    },
    {
        name: 'error',
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/error/index.vue')
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

// 创建默认的拦截器
const beforeInterceptor = useRouterInterceptor({
    sisome: sisome,
    async dataLoader(to, from, next) {
        const result = {
            data: null,
            msg: ''
        }
        const config = useConfig()
        try {
            const data = await config.getAuthMenus()
            result.data = data
            result.msg = ''
        } catch (e) {
            result.menus = null
            result.msg = 'routes load error'
        }
        return result
    },
    failureHandler(result, to, from, next) {
        notice.error(result.msg || '菜单数据加载失败')
        next && next('/error')
    },
    injectOption: {
        parentRoute: {
            name: 'home',
            path: '/',
            component: () => import('@/views/home/index.vue')
        },
        routes: [],
        viewLoader(view) {
            console.log(`/src/views${view}`)
            return modules[`/src/views${view}`]
        }
    }
})

// 前置守卫
export const beforeEachHandler = (to, from, next) => {
    const auth = useAuth()
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
        beforeInterceptor.filter(to, from, next)
    } else {
        next()
    }
}

// 全局后置钩子
export const afterEachHandler = (to, from) => {
    loadingBar.finish()
}

export default routes