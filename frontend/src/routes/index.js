import { createRouter, createWebHashHistory } from 'vue-router'
import routes, { beforeEachHandler, afterEachHandler } from './config'

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach(beforeEachHandler)
router.afterEach(afterEachHandler)

export default router