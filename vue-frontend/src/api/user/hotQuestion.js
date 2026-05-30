import request from '../request'
//export是把这个函数暴露给外部，让别的文件import导入可以用
export function getHotlist(data){
    return request({
        url:'/user/user/hotquestions',//后端接口地址
        method: 'post',
        data//前端传给后端的内容
    })
}