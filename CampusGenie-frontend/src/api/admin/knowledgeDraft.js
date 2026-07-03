import request from '../request'
import { SUCCESS } from "@/constants/code.js"

export function getPage(params) {
  return request.get('/admin/knowlegedraft/page', {
    params
  })
}

export function approve(id) {
  return request({
    url: `/admin/knowlegedraft/${id}/approve`,
    method: 'put'
  })
}

export function reject(id) {
  return request({
    url: `/admin/knowlegedraft/${id}/reject`,
    method: 'delete'
  })
}

export function edit(data) {
  return request({
    url: '/admin/knowlegedraft/edit',
    method: 'put',
    data
  })
}