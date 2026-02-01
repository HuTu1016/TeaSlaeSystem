import request from '@/utils/request'

/**
 * 获取当前用户信息
 */
export function getUserProfile() {
    return request({
        url: '/users/me',
        method: 'get'
    })
}

/**
 * 更新当前用户信息
 * @param {Object} data - { nickname, avatar }
 */
export function updateUserProfile(data) {
    return request({
        url: '/users/me',
        method: 'put',
        data
    })
}

/**
 * 上传图片
 * @param {File} file - 图片文件
 */
export function uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/upload/image',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 获取收货地址列表
 */
export function getAddresses() {
    return request({
        url: '/addresses',
        method: 'get'
    })
}

/**
 * 创建收货地址
 */
export function createAddress(data) {
    return request({
        url: '/addresses',
        method: 'post',
        data
    })
}

/**
 * 更新收货地址
 */
export function updateAddress(id, data) {
    return request({
        url: `/addresses/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除收货地址
 */
export function deleteAddress(id) {
    return request({
        url: `/addresses/${id}`,
        method: 'delete'
    })
}
