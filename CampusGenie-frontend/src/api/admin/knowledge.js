//管理知识条目
import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用

import {SUCCESS } from "@/constants/code.js"
import { mockKnowledgeList } from "@/mock/Response/knowledge.js"
import {mockCategoryList} from "@/mock/Response/category.js";
import { morkImportKnowledge} from"@/mock/Response/importKnowledge.js"
import {USE_MORK} from "@/constants/test.js"
//新增知识条目
export function getKnowledgeById(id){
    if(USE_MORK){
        const item = mockKnowledgeList.find(item => String(item.id) === String(id))

        return Promise.resolve({
            "code": SUCCESS,
            "msg": "查询知识条目成功",
            "data": item || null
        })
    }
    return request({
        url:`/admin/knowledge/${id}`,
        method: 'get'
    })
}
export function add(data){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "新增知识条目成功",
            "data": null
        })
    }
    return request({
        url:'/admin/knowledge/new',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
//编辑知识条目
export function edit(data){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "编辑知识条目成功",
            "data": null
        })
    }
    return request({
        url:'/admin/knowledge/edit',//后端接口地址
        method: 'put',
        data//前端传给后端的内容
    })
}

export function changeStatus(id,status){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "切换知识条目状态成功",
            "data": null
        })
    }
    return request({
        url:`/admin/knowledge/${id}/status`,//后端接口地址
        method: 'put',
        data: { status }
    })
}
//删除知识条目
export function Delete(id){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "删除知识条目成功",
            "data": null
        })
    }
    return request({
        url:`/admin/knowledge/${id}/delete`,//后端接口地址
        method: 'delete',

    })
}
//分页查询
export function getPage(params){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "分页查询成功",
            "data": {
                "total":mockCategoryList.length,
                "records":mockKnowledgeList
            }
        })
    }
    return request.get('/admin/knowledge/page', {
        params
    })
}
//批量导入功能
export function Import(FormData){
    if(USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "message": "导入已完成",
            "data": morkImportKnowledge,
        })
    }
    return request({
        url:'/admin/knowledge/import',
        method: 'post',
        data:FormData
    })
}
//----已实现版本

export function getStatistics() {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "获取统计数据成功",
            "data": {
                "publishedCount": 1102,
                "stoppedCount": 154,
                "weeklyUpdateCount": 32,
                "lastWeekUpdateCount": 27
            }
        })
    }

    return request.get('/admin/knowledge/statistics')
}

export function exportKnowledge(params) {
    const query = new URLSearchParams()
    if (params.keyword) query.set('keyword', params.keyword)
    if (params.categoryId) query.set('categoryId', params.categoryId)
    if (params.status !== undefined && params.status !== '') query.set('status', params.status)

    const xhr = new XMLHttpRequest()
    const url = `http://localhost:8080/admin/knowledge/export?${query.toString()}`
    console.log('导出请求:', url)
    xhr.open('GET', url)
    xhr.setRequestHeader('token', localStorage.getItem('token') || '')
    xhr.responseType = 'blob'

    xhr.onload = () => {
        console.log('导出响应状态:', xhr.status)
        if (xhr.status === 200) {
            console.log('导出成功, 大小:', xhr.response?.size)
            const blobUrl = window.URL.createObjectURL(xhr.response)
            const a = document.createElement('a')
            a.href = blobUrl
            a.download = '知识库导出.xlsx'
            a.style.display = 'none'
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
            window.URL.revokeObjectURL(blobUrl)
        } else {
            alert('导出失败，状态码: ' + xhr.status)
        }
    }

    xhr.onerror = () => {
        console.error('导出请求网络错误')
        alert('导出失败，请检查网络')
    }

    xhr.send()
}
