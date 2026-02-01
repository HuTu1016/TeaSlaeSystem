import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('tea-merchant-token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('tea-merchant-info') || '{}'))

    const isLogin = computed(() => !!token.value)

    function setToken(t) {
        token.value = t
        localStorage.setItem('tea-merchant-token', t)
    }

    function setUserInfo(info) {
        userInfo.value = info
        localStorage.setItem('tea-merchant-info', JSON.stringify(info))
    }

    function logout() {
        token.value = ''
        userInfo.value = {}
        localStorage.removeItem('tea-merchant-token')
        localStorage.removeItem('tea-merchant-info')
    }

    return {
        token,
        userInfo,
        isLogin,
        setToken,
        setUserInfo,
        logout
    }
})
