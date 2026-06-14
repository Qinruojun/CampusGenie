//管理用户注册
import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
import { USE_MORK} from "@/constants/test.js";

export function login(data){
    if(USE_MORK){
        return Promise.resolve({

        })
    }
    return request({
        url:'/admin/login',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
