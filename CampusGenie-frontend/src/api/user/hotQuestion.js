import request from '../request'
import { USE_MORK} from "@/constants/test.js";
import {USER_HOT_QUESTION} from "@/mock/Response/userHotQuestion.js";
import {SUCCESS} from "@/constants/code.js"
//export是把这个函数暴露给外部，让别的文件import导入可以用
export function getHotlist(){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "获取成功",
            "data":USER_HOT_QUESTION,
        } )
    }
    return request({
        url:'/user/hotquestions',//后端接口地址
        method: 'get',

    })
}