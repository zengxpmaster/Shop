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
        return {..._defaultFields}
    },
    actions:{
        load(){
            if(this.expired < Date.now() - 60*1000){
                return
            }
            // httpGet('option/config').then( res => {
            //     if(!res.code){
            //         console.log(res.data)
            //         this.$state = {...this.$state, ...res.data}
            //         this.$state.expired = Date.now()
            //     }
            // })
        },
        update(){
            
        },
        toggleMenuCollapsed(){
            this.isMenuCollapsed = !this.isMenuCollapsed
        },
        setMenuCollapsed(val){
            this.isMenuCollapsed = val
        },
    },
    persist:{
        key: 'ahead_se_config'
    }
})

export default configStore