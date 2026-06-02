//审核用户贡献
import request from '../request'
export function contribute(data){
    return request({
        url:'/admin/knowledge/new',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}