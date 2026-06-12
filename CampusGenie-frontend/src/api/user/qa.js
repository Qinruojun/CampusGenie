import request from '../request'
import {USE_MORK} from "@/constants/test.js";
import {SUCCESS} from "@/constants/code.js";

//export是把这个函数暴露给外部，让别的文件import导入可以用
export function askQuestion(question){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "获取答案成功",
            "data": {
            "knowledgeId": 128,
                "question": "图书馆几点开门？",
                "answer": "图书馆开放时间为7:00-23:00",
                "categoryName": "图书馆服务",
                "source": "官网",
                "updatedTime": "2026-06-04 15:30:00"
        }}
        )
    }
    return request({
        url:'/user/qa',//后端接口地址
        method: 'post',
        data:{
            question:question
        }//前端传给后端的内容
    })
}

export function getConversations() {
    return request({
        url: '/user/qa/conversations',
        method: 'get'
    })
}

export function createConversation() {
    return request({
        url: '/user/qa/conversations',
        method: 'post'
    })
}

export function getConversationMessages(id) {
    return request({
        url: `/user/qa/conversations/${id}/messages`,
        method: 'get'
    })
}

export function sendConversationMessage(id, question) {
    return request({
        url: `/user/qa/conversations/${id}/messages`,
        method: 'post',
        data: {
            question
        }
    })
}