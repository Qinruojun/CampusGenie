import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000
})

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')

        if (token) {
            config.headers.authentication = token
        }

        return config
    },
    error => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        return response.data//返回的是data
        //Axios原始响应长这样：{data: {}, status: 200, statusText: "OK", headers: {}, config: {}, request: XMLHttpRequest}，
        // 只有data字段是后端的响应
    },
    error => {
        console.error('请求失败：', error)
        return Promise.reject(error)
    }
)

export default request