import request from '../request'
import { USE_MORK } from "@/constants/test.js";
import { SUCCESS } from "@/constants/code.js";

export function login(data) {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "登录成功",
            "data": {
                "token": "user-token-123456",
                "username": data.username,
                "role": "0"
            }
        })
    }
    return request({
        url: '/user/user/login',
        method: 'post',
        data
    })
}

export function register(data) {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "注册成功",
            "data": null
        })
    }
    return request({
        url: '/user/user/register',
        method: 'post',
        data
    })
}

export function getUserInfo() {
    return request({
        url: '/user/user/info',
        method: 'get'
    })
}

export function updateUserInfo(data) {
    return request({
        url: '/user/user/info',
        method: 'put',
        data
    })
}

export function changePassword(data) {
    return request({
        url: '/user/user/password',
        method: 'put',
        data
    })
}