import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const service = axios.create({
    baseURL: '/api', // 配合 vite proxy
    timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers['Authorization'] = `Bearer ${userStore.token}`
        }
        // 添加 requestId
        // config.headers['X-Request-Id'] = crypto.randomUUID()
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response) => {
        const res = response.data
        // 约定: code === 0 为成功
        if (res.code !== 0) {
            ElMessage.error(res.message || 'Error')

            // 40101: 未登录或Token过期
            if (res.code === 40101) {
                const userStore = useUserStore()
                userStore.logout()
                router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res.data
        }
    },
    (error) => {
        console.error('err' + error)
        let msg = error.message || 'Request Error'
        if (error.response) {
            msg = error.response.data.message || msg
            if (error.response.status === 401) {
                const userStore = useUserStore()
                userStore.logout()
                router.push(`/login`)
            }
        }
        ElMessage.error(msg)
        return Promise.reject(error)
    }
)

export default service
