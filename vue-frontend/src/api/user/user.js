import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
export function login(data){
    return request({
        url:'/user/user/login',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}
export function register(data){
    return request({
        url:'/user/user/register',
        method: 'post',//在Axios中定义的，Axios 支持get,post, put, delete
        data
    })
}
//GET:问后端拿数据，POST:给后端提交数据