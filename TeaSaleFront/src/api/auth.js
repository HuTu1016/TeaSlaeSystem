import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { account, password }
 */
export function login(data) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}

/**
 * 用户注册
 * @param {Object} data - { username, phone, password }
 */
export function register(data) {
    return request({
        url: '/auth/register',
        method: 'post',
        data
    })
}

/**
 * 退出登录
 */
export function logout() {
    return request({
        url: '/auth/logout',
        method: 'post'
    })
}

/**
 * 发送短信验证码
 * @param {Object} data - { phone, type } type: LOGIN/REGISTER/RESET
 */
export function sendSmsCode(data) {
    return request({
        url: '/auth/sms/send',
        method: 'post',
        data
    })
}

/**
 * 短信验证码登录
 * @param {Object} data - { phone, code }
 */
export function smsLogin(data) {
    return request({
        url: '/auth/sms/login',
        method: 'post',
        data
    })
}

/**
 * 重置密码
 * @param {Object} data - { phone, code, newPassword }
 */
export function resetPassword(data) {
    return request({
        url: '/auth/password/reset',
        method: 'post',
        data
    })
}

/**
 * 检查用户名是否存在
 * @param {string} username - 用户名
 * @returns {Promise<{exists: boolean}>}
 */
export function checkUsernameExists(username) {
    return request({
        url: '/auth/check-username',
        method: 'get',
        params: { username }
    })
}

/**
 * 检查手机号是否存在
 * @param {string} phone - 手机号
 * @returns {Promise<{exists: boolean}>}
 */
export function checkPhoneExists(phone) {
    return request({
        url: '/auth/check-phone',
        method: 'get',
        params: { phone }
    })
}


/**
 * 刷新令牌
 * @param {string} refreshToken 
 */
export function refreshToken(token) {
    return request({
        url: '/auth/refresh-token',
        method: 'post',
        data: { refreshToken: token }
    })
}
