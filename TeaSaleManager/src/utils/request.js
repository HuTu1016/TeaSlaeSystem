import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const service = axios.create({
    baseURL: '/api',
    timeout: 10000
})

service.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers['Authorization'] = `Bearer ${userStore.token}`
        }
        return config
    },
    error => Promise.reject(error)
)

service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code === 0) {
            return res.data
        } else {
            ElMessage.error(res.message || 'Error')
            // 401: Unauthorized
            if (res.code === 40101) {
                const userStore = useUserStore()
                userStore.logout()
                router.push('/login')
            }
            return Promise.reject(new Error(res.message || 'Error'))
        }
    },
    error => {
        ElMessage.error(error.message)
        return Promise.reject(error)
    }
)

export default service
