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