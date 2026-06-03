//管理知识条目
import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
//新增知识条目
export function add(data){
    return request({
        url:'/admin/knowledge/new',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
//编辑知识条目
export function edit(data){
    return request({
        url:'/admin/knowledge/edit',//后端接口地址
        method: 'put',
        data//前端传给后端的内容
    })
}

export function changStatus(id,data){
    return request({
        url:'/admin/knowledge/${id}/status',//后端接口地址
        method: 'put',
        data//前端传给后端的内容
    })
}
//删除知识条目
export function Delete(id,data){
    return request({
        url:`/admin/knowledge/${id}/delete`,//后端接口地址
        method: 'delete',
        data//前端传给后端的内容
    })
}
