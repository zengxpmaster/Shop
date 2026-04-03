import { createApp } from 'vue'
import pinia from '@/pinia'
import naive from '@/naive'
import App from '@/App.vue'
import router from '@/routes'

export default function install() {
    const app = createApp(App)
    app.use(router).use(pinia).use(naive)
    return { app }
}

