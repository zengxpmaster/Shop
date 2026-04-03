import { defineStore } from 'pinia'
import { httpPost, httpGet } from '@/http'

const _DEFAULT = {
    id: 0,
    username: '',
    nickname: '管理员',
    avatar: '',
    token: '',
    expired: 0
}

const authStore = defineStore('auth', {
    state(){
        return {..._DEFAULT}
    },
    actions:{
        isAuth(){
            return this.$state.id
        },
        async login(data){
            const res = await httpPost('/auth/login', data)
            if (!res.code) {
                console.log(res.data)
                this.$state.token = res.data.token
                this.$state.expired = parseInt(Date.now()/1000) + res.data.expired
                this.$state = { ...this.$state, ...res.data.user}
                
                return Promise.resolve(res.data.user)
            } else {
                return Promise.reject(res)
            }
        },
        logout(){
            this.$state = {..._DEFAULT}
        },
        getToken(){
            return this.$state.token //"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzaXNvbWUiLCJpc3MiOiJzaXNvbWUiLCJpYXQiOjE3NjA5NDI4MzMsImV4cCI6MTc2MTU0NzYzM30.nSy2ulOJ5-E29T0LLpykvx-H_FyT70ejyGXkC1eJHtc"
        }
    },
    persist:{
        key: 'ahead_se_auth',
    }
})

export default authStore