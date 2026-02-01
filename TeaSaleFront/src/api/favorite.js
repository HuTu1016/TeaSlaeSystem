import request from '@/utils/request'

/**
 * 获取我的收藏列表
 * @param {Object} params - 分页参数 { page, pageSize }
 */
export function getFavorites(params) {
    return request({
        url: '/my/favorites',
        method: 'get',
        params
    })
}

/**
 * 检查商品是否已收藏
 * @param {number} productId - 商品ID
 */
export function checkFavorite(productId) {
    return request({
        url: `/my/favorites/${productId}/check`,
        method: 'get'
    })
}

/**
 * 添加收藏
 * @param {number} productId - 商品ID
 */
export function addFavorite(productId) {
    return request({
        url: `/my/favorites/${productId}`,
        method: 'post'
    })
}

/**
 * 取消收藏
 * @param {number} productId - 商品ID
 */
export function removeFavorite(productId) {
    return request({
        url: `/my/favorites/${productId}`,
        method: 'delete'
    })
}

/**
 * 获取我的足迹列表
 * @param {Object} params - 分页参数 { page, pageSize }
 */
export function getFootprints(params) {
    return request({
        url: '/my/footprints',
        method: 'get',
        params
    })
}

/**
 * 删除足迹
 * @param {number} productId - 商品ID
 */
export function removeFootprint(productId) {
    return request({
        url: `/my/footprints/${productId}`,
        method: 'delete'
    })
}

/**
 * 清空足迹
 */
export function clearFootprints() {
    return request({
        url: '/my/footprints',
        method: 'delete'
    })
}
