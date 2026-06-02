//这个类专门负责创建Axios实例1
import axios from 'axios'

const request = axios.create({
    //baseURL: 'http://8.148.158.43:8080',//后端地址
    baseURL:'http://localhost:8080',
    timeout:10000
})

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')

        if (token){
            config.headers.token = token
        }

        return config
    },
    error => {
    return Promise.reject(error)
  }
)
// 响应拦截器：统一处理后端返回结果
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('请求出错：', error)
    return Promise.reject(error)
  }
)

export default request