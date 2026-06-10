import request from './request'
import {mockCategoryList} from "@/mock/Response/category.js";
import  { USE_MORK } from "@/constants/test.js"
import { SUCCESS } from "@/constants/code.js"
//export是把这个函数暴露给外部，让别的文件import导入可以用
// export function getCategoryList() {
//     return request({
//         url: '/category/list',
//         method: 'get'
//     })
// }
//先进行mork测试

export function getCategoryList() {
    if(USE_MORK===true) {
        return Promise.resolve({
            code: SUCCESS,
            msg: 'success',
            data: mockCategoryList
        })
    }
        return request({
        url: '/category/list',
        method: 'get'
    })
}