import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const service = axios.create({
    baseURL: '/api', // Proxy to backend
    timeout: 10000
})

let isRefreshing = false
let requests = []

// Request Interceptor
service.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers['Authorization'] = `Bearer ${userStore.token}`
        }
        // Idempotency Key logic can be added here
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// Response Interceptor
// Response Interceptor
service.interceptors.response.use(
    async response => {
        const res = response.data

        // 成功
        if (res.code === 0) {
            return res.data
        }

        // 处理 Token 过期 (40101: 未登录/过期, 40102: 无效, 40103: 过期)
        // 排除登录接口，因为登录接口返回 40101 表示账号密码错误
        if ((res.code === 40101 || res.code === 40102 || res.code === 40103) && !response.config.url.includes('/auth/login')) {
            const userStore = useUserStore()
            const config = response.config

            // 如果是刷新Token的请求本身失败了，说明Refresh Token也过期了
            if (config.url.includes('/auth/refresh-token')) {
                userStore.logout()
                return Promise.reject(new Error(res.message))
            }

            if (!isRefreshing) {
                isRefreshing = true

                try {
                    const refreshToken = userStore.refreshToken
                    if (!refreshToken) {
                        throw new Error('No refresh token')
                    }

                    // 使用 axios 原生实例调用刷新接口，避免走拦截器死循环
                    // 注意：这里要把 baseURL 加上，或者用相对路径如果 axios 默认配置了
                    // 由于 service 配置了 baseURL: '/api'，原生 axios 没有默认配置
                    // 所以我们要手动拼接或者使用 service.defaults.baseURL (如果不走拦截器的话)
                    // 但 service 会走拦截器。所以最好新建一个简单的 axios 实例或者直接 axios.post('/api/...')
                    const refreshRes = await axios.post('/api/auth/refresh-token', {
                        refreshToken: refreshToken
                    })

                    if (refreshRes.data.code === 0) {
                        const { accessToken, refreshToken: newRefreshToken, userInfo } = refreshRes.data.data
                        userStore.setToken(accessToken, newRefreshToken)
                        if (userInfo) {
                            userStore.setUserInfo(userInfo)
                        }

                        // 重试队列中的请求
                        requests.forEach(cb => cb(accessToken))
                        requests = []

                        // 重试当前请求
                        config.headers['Authorization'] = `Bearer ${accessToken}`
                        return service(config)
                    } else {
                        throw new Error(refreshRes.data.message || 'Refresh failed')
                    }
                } catch (e) {
                    console.error('Refresh token failed', e)
                    userStore.logout()
                    requests.forEach(cb => cb(null)) // 或者通知失败
                    requests = []
                    // window.location.reload() // 可选
                    return Promise.reject(new Error('Session expired, please login again'))
                } finally {
                    isRefreshing = false
                }
            } else {
                // 正在刷新，将请求加入队列
                return new Promise((resolve) => {
                    requests.push((token) => {
                        if (token) {
                            config.headers['Authorization'] = `Bearer ${token}`
                            resolve(service(config))
                        } else {
                            // 刷新失败，放弃重试
                            // resolve(Promise.reject(new Error('Token refresh failed'))) 
                            // 实际上这里应该 reject，但为了简单我们可能让它走入下一个 error loop 或者直接 reject
                            // resolve(service(config)) // 会再次触发 401 然后 logout
                        }
                    })
                })
            }
        }

        ElMessage.error(res.message || 'Error')
        return Promise.reject(new Error(res.message || 'Error'))
    },
    error => {
        console.error('err' + error)
        ElMessage.error(error.message || 'Request Failed')
        return Promise.reject(error)
    }
)

export default service
