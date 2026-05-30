import { request } from '../request'

//export是把这个函数暴露给外部，让别的文件import导入可以用
export function getCategory(data){
    return request({
        url:'/admin/catogery',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}