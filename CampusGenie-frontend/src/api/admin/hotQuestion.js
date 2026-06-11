//管理知识条目
import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用

import {SUCCESS } from "@/constants/code.js"
export function adminGetHotlist(){
    return request({
        url:'/admin/hotquestions',//后端接口地址
        method: 'get',

    })
}