import request from '../request'

export function getRecentLogs(params) {
    return request.get('/admin/logs/recent', { params })
}