import request from '../request'

export function login(data) {
    return request.post('/admin/login', data)
}

export function getAdminInfo() {
    return request.get('/admin/info')
}

export function updateAdminInfo(data) {
    return request.put('/admin/info', data)
}

export function changePassword(data) {
    return request.put('/admin/password', data)
}