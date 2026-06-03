import { request } from './request.js'

//export是把这个函数暴露给外部，让别的文件import导入可以用
export function getCategoryList() {
    return request({
        url: '/category/list',
        method: 'get'
    })
}