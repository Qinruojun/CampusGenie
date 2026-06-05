//管理知识条目
import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
//新增知识条目
export function addKnowledge(data){
    return request({
        url:'/admin/knowledge/new',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
//编辑知识条目
export function editKnowledge(data){
    return request({
        url:'/admin/knowledge/edit',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
//删除知识条目
export function deleteKnowledge(data){
    return request({
        url:'/admin/knowledge/delete',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}

export function getKnowledgeById(id){
    return request({
        url: `/admin/knowledge/${id}`, // 注意这里使用的是反引号 ` 
        method: 'get'
    })
}
