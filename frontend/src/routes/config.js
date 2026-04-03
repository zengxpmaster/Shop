import { createDiscreteApi } from 'naive-ui'
import authStore from '@/store/auth'

const { loadingBar } = createDiscreteApi(['loadingBar'])

const routes = [
    {
        path: '/',
        component: () => import('@/views/home/index.vue'),
        children: [
            { path: '', redirect: '/dashboard' },
            { name: 'dashboard', path: '/dashboard', component: () => import('@/views/dashboard.vue') },
            { name: 'users', path: '/users', component: () => import('@/views/users/index.vue') },
            { name: 'products', path: '/products', redirect: '/products/list' },
            { name: 'productList', path: '/products/list', component: () => import('@/views/products/index.vue') },
            { name: 'productCategories', path: '/products/categories', component: () => import('@/views/products/categories.vue') },
            { name: 'orders', path: '/orders', component: () => import('@/views/orders/index.vue') },
            { name: 'distribution', path: '/distribution', redirect: '/distribution/list' },
            { name: 'distributionList', path: '/distribution/list', component: () => import('@/views/distribution/index.vue') },
            { name: 'commissions', path: '/distribution/commissions', component: () => import('@/views/distribution/commissions.vue') },
            { name: 'wallets', path: '/finance/wallets', component: () => import('@/views/finance/wallets.vue') },
            { name: 'withdrawals', path: '/finance/withdrawals', component: () => import('@/views/finance/withdrawals.vue') },
            { name: 'settings', path: '/settings', component: () => import('@/views/settings/index.vue') },
        ]
    },
    { name: 'login', path: '/login', component: () => import('@/views/login.vue') },
    { name: 'error', path: '/:pathMatch(.*)*', component: () => import('@/views/error.vue') }
]

const whiteList = ['/login', '/error']

const includes = (matched) => matched.some(m => whiteList.includes(m.path))

export const beforeEachHandler = (to, from, next) => {
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
    next()
}

export const afterEachHandler = () => {
    loadingBar.finish()
}

export default routes