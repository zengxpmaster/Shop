import { createRouter, createWebHashHistory } from 'vue-router'

import routes, { beforeEachHandler, afterEachHandler } from './config'
import { useRouterAlive } from '../sisome/router'
import sisome from '../sisome'
import LoadPlaceholder from '@/components/LoadPlaceholder.vue'

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach(beforeEachHandler)
router.afterEach(afterEachHandler)

const { RouteAlive, removeComponentCache } = useRouterAlive({
    sisome : sisome,
    router: router,
    element: '#main-content>.n-layout-scroll-container', // 滚动条内容
    placeHolderComponent: LoadPlaceholder
})

export { RouteAlive, removeComponentCache }

export default router