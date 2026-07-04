import request from '../request'
import { USE_MORK } from "@/constants/test.js";
import { SUCCESS } from "@/constants/code.js";

export function login(data) {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "登录成功",
            "data": {
                "token": "admin-token-123456",
                "username": "admin",
                "role": "1"
            }
        })
    }
    return request({
        url: '/admin/login',
        method: 'post',
        data
    })
}