import { createApp } from 'vue'

import pinia from '@/pinia'
import naive from '@/naive'

import App from '@/App.vue'

import router from './router'

const sisome = {
    app: null,
    inited: false,
    menu: null,
    nav: null,
}

export default function install(){
    const app = createApp(App)
    app.use(router)
    .use(pinia)
    .use(naive)
    sisome.app = app
    return { app }
}

