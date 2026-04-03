import { createDiscreteApi } from 'naive-ui'

export const { message, notification } = createDiscreteApi(['message', 'notification'],{
    notificationProviderProps:{
        max: 6,
        placement: 'top-right',
    }
})

/**
 * 消息提醒
 */
export const notice = {
    alert(params, type = 'success', duration = 2e3){
        params['duration'] = duration
        notification[type](params)
    },
    success(content){
        this.alert({content:content}, 'success', 2e3)
    },
    warning(content){
        this.alert({content:content}, 'warning', 3e3)
    },
    error(content){
        this.alert({content:content}, 'error', 4e3)
    }
}