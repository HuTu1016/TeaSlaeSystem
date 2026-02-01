import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('admin_token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('admin_user_info') || '{}'))
    const role = ref(localStorage.getItem('admin_role') || '')

    const setToken = (newToken) => {
        token.value = newToken
        localStorage.setItem('admin_token', newToken)
    }

    const setUserInfo = (info) => {
        userInfo.value = info
        localStorage.setItem('admin_user_info', JSON.stringify(info))
    }

    const setRole = (newRole) => {
        role.value = newRole
        localStorage.setItem('admin_role', newRole)
    }

    const logout = () => {
        token.value = ''
        userInfo.value = {}
        role.value = ''
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user_info')
        localStorage.removeItem('admin_role')
    }

    return { token, userInfo, role, setToken, setUserInfo, setRole, logout }
})
