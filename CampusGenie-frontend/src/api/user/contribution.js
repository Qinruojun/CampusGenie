import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
import { SUCCESS } from "@/constants/code.js"

import {USE_MORK} from "@/constants/test.js";
import {UserContributionList} from "@/mock/Response/userContribution.js";

export function getPage(params){
    if(USE_MORK){
        return Promise.resolve({
            "code": 200,
            "msg": "查询成功",
            "data": {
                "total": UserContributionList.length,
                "records": UserContributionList
            }
        })
    }
    return request.get('/user/contributions/page', {
        params
    })

}
export function Delete(id){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "删除成功",
            "data": null
        })
    }
    return request.delete(`/user/contributions/${id}/delete`)
}
export function contribute(data){
   return request({
       url: '/user/contribute',//后端接口地址
       method: 'post',
       data: data
   })//前端传给后端的内容
}

export function getUserStatistics() {
    return request.get('/user/contributions/statistics')
}