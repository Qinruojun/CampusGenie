//分页查询用户贡献
import { request } from "../request.js"
export function getContribution(data){
    return request({
        url:'/user/contributions/page',//后端接口地址
        method: 'get',
        data//前端传给后端的内容
    })
}