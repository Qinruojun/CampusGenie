import request from '../request'
import { SUCCESS } from "@/constants/code.js"
import { mockKnowledgeDraftList } from "@/mock/Response/knowledgeDraft.js"
import { USE_MORK } from "@/constants/test.js"

export function getPage(params) {
  if (USE_MORK) {
    let filtered = [...mockKnowledgeDraftList]

    if (params.keyword) {
      filtered = filtered.filter(item =>
        item.question.includes(params.keyword) ||
        item.answer.includes(params.keyword)
      )
    }

    if (params.categoryId) {
      filtered = filtered.filter(item => String(item.categoryId) === String(params.categoryId))
    }

    if (params.status !== undefined && params.status !== '') {
      filtered = filtered.filter(item => Number(item.status) === Number(params.status))
    }

    const pageSize = params.pageSize || 10
    const page = params.page || 1
    const start = (page - 1) * pageSize
    const end = start + pageSize

    return Promise.resolve({
      "code": SUCCESS,
      "msg": "分页查询成功",
      "data": {
        "total": filtered.length,
        "records": filtered.slice(start, end)
      }
    })
  }

  return request.get('/admin/knowlegedraft/page', {
    params
  }).then(res => {
    console.log('Knowledge draft API response:', res)
    return res
  })
}

export function approve(id) {
  if (USE_MORK) {
    return Promise.resolve({
      "code": SUCCESS,
      "msg": "审核通过成功",
      "data": null
    })
  }

  return request({
    url: `/admin/knowlegedraft/${id}/approve`,
    method: 'put'
  })
}

export function remove(id) {
  if (USE_MORK) {
    return Promise.resolve({
      "code": SUCCESS,
      "msg": "删除成功",
      "data": null
    })
  }

  return request({
    url: `/admin/knowlegedraft/${id}/reject`,
    method: 'delete'
  })
}

export function edit(data) {
  if (USE_MORK) {
    return Promise.resolve({
      "code": SUCCESS,
      "msg": "编辑成功",
      "data": null
    })
  }

  return request({
    url: '/admin/knowlegedraft/edit',
    method: 'put',
    data
  })
}