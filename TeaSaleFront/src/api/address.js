import request from '@/utils/request'

/**
 * 获取用户地址列表
 */
export function getAddresses() {
    return request({
        url: '/user/addresses',
        method: 'get'
    })
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
    return request({
        url: '/user/addresses/default',
        method: 'get'
    })
}

/**
 * 新增地址
 */
export function addAddress(data) {
    return request({
        url: '/user/addresses',
        method: 'post',
        data
    })
}

/**
 * 修改地址
 */
export function updateAddress(id, data) {
    return request({
        url: `/user/addresses/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除地址
 */
export function deleteAddress(id) {
    return request({
        url: `/user/addresses/${id}`,
        method: 'delete'
    })
}

/**
 * 设为默认地址
 */
export function setDefaultAddress(id) {
    return request({
        url: `/user/addresses/${id}/set-default`,
        method: 'put'
    })
}
