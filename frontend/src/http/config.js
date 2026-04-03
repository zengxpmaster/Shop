import qs from 'qs'

import authStore from '@/store/auth'
import { notice } from '@/utils'

const axiosConfig = {
    baseURL: (import.meta.env.VITE_APP_API || '') + '/',
    timeout: 30000
}

/**
 * 请求拦截器
 * @param config 
 * @returns 
 */
export const requestInterceptor = (config) =>{
    const auth = authStore()
    if(auth.isAuth()){
        config.headers.Authorization = 'Bearer ' + auth.getToken()
    }
    if ('post' === config.method.toLocaleLowerCase() || 'put' === config.method.toLocaleLowerCase()) {
        // 参数统一处理，请求都使用data传参
        if(Object.prototype.toString.call(config.data) === '[object FormData]'){
            config.headers['Content-Type'] = 'multipart/form-data'
        }else{
            // 使用json格式，
            config.headers['Content-Type'] = 'application/json'
            // 使用表单
            //config.data = qs.stringify(config.data)
        }
    } else if ('get' === config.method.toLocaleLowerCase() || 'delete' === config.method.toLocaleLowerCase()) {
        // 参数统一处理
        // config.params = config.data
        //console.log(config)
    } else {
        notice.error('不允许的请求方法：' + config.method)
    }
    return config
}

/**
 * 响应拦截器
 * @param response 
 * @returns 
 */
export const responseInterceptor = (response) =>{
    const { status, data } = response
    if(undefined != data.code && "" != data.code && null != data.code){
        data.code = parseInt(data.code)
    }
    console.log("http request", data)
    if (200 === status) {
        if (0 === data.code) {
            return data
        } else if(401 === data.code){
            //router.push('/login')
            return Promise.reject(data)
        } else {
            return Promise.reject(data)
        }
    }else {
        return Promise.reject(response)
    }
}
/**
 * 请求错误处理
 * @param error 
 * @returns 
 */
export const requestErrorHandler = (error) => {
    return Promise.reject(error)
}

/**
 * 响应错误处理
 * @param error 
 * @returns 
 */
export const responseErrorHandler = (error) =>{
    const { response } = error
    if(JSON.stringify(error).includes('Network Error')){
        notice.error('网络超时')
    }
    // 对响应错误做点什么
    if (response && response.status) {
        const { status, data } = response
        switch (status) {
            case 401: notice.error('未登录!'); break;
            case 403: notice.error('登录过期，请重新登录!'); break;
            case 404: notice.error('请求地址错误!'); break;
            default:
                notice.error('请求错误')
        }
        return Promise.reject(response)
    }
    return Promise.reject(error)
}

export default axiosConfig