import { defineStore } from 'pinia'
import { httpGet } from "@/http"

const _DEFAULT = {
    siteName: '分销系统',
    siteUrl: '',
    isMenuCollapsed: false,
    intro: '',
    isDark: false,
    isDarkTheme: false,
    optionTabs: [],
    optionTypes: {},
    menuTypes: {},
    expired: 0
}

const configStore = defineStore('config', {
    state(){
        return {..._DEFAULT}
    },
    actions:{
        load(){
            if(this.expired < Date.now() - 60*1000){
                return
            }
        },
        update(){
        },
        toggleMenuCollapsed(){
            this.isMenuCollapsed = !this.isMenuCollapsed
        },
        setMenuCollapsed(val){
            this.isMenuCollapsed = val
        },
        async getAuthMenus(){
            try {
                const res = await httpGet('/api/menus')
                return res.data || []
            } catch(e) {
                return []
            }
        }
    },
    persist:{
        key: 'ahead_se_config'
    }
})

export default configStore