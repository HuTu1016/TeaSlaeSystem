import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

    const refreshToken = ref(localStorage.getItem('refreshToken') || '')

    function setToken(newToken, newRefreshToken) {
        token.value = newToken
        localStorage.setItem('token', newToken)
        if (newRefreshToken) {
            refreshToken.value = newRefreshToken
            localStorage.setItem('refreshToken', newRefreshToken)
        }
    }

    function setUserInfo(info) {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    function logout() {
        token.value = ''
        refreshToken.value = ''
        userInfo.value = {}
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')
    }

    return { token, refreshToken, userInfo, setToken, setUserInfo, logout }
})
