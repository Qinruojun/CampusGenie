import request from '../request'
import { USE_MORK } from "@/constants/test.js";
import { SUCCESS } from "@/constants/code.js";
import { USER_HOT_QUESTION } from "@/mock/Response/userHotQuestion.js";

export function adminGetHotlist() {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "获取热点问题成功",
            "data": USER_HOT_QUESTION
        })
    }
    return request({
        url: '/admin/hotquestions',
        method: 'get'
    })
}

export function adminRefreshHotlist() {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "热点问题刷新成功",
            "data": null
        })
    }
    return request({
        url: '/admin/hotquestions/refresh',
        method: 'post'
    })
}