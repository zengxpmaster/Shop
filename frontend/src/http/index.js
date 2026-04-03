import Axios from 'axios'
import axiosConfig, {
    requestInterceptor,
    responseInterceptor,
    requestErrorHandler,
    responseErrorHandler
  } from './config'

export const httpClient = Axios.create(axiosConfig)

httpClient.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest'

httpClient.interceptors.request.use(requestInterceptor, requestErrorHandler)
httpClient.interceptors.response.use(responseInterceptor, responseErrorHandler)

export const httpGet = (api, params) => {
    return httpClient.get(api, {params:params})
}

export const httpPost = (api, params) => {
    return httpClient.post(api, params)
}

export default httpClient