import request from '@/utils/request'

/**
 * 获取购物车列表
 */
export function getCart() {
    return request({
        url: '/cart',
        method: 'get'
    })
}

/**
 * 添加商品到购物车
 * @param {Object} data - { skuId, quantity }
 */
export function addToCart(data) {
    return request({
        url: '/cart/items',
        method: 'post',
        data
    })
}

/**
 * 更新购物车商品数量
 * @param {number} itemId - 购物车项ID
 * @param {Object} data - { quantity }
 */
/**
 * 更新购物车商品数量
 * @param {number} itemId - 购物车项ID
 * @param {Object} data - { quantity }
 */
export function updateCartItem(itemId, data) {
    return request({
        url: `/cart/items/${itemId}`,
        method: 'put',
        params: data
    })
}

/**
 * 删除购物车商品
 * @param {number} itemId - 购物车项ID
 */
export function deleteCartItem(itemId) {
    return request({
        url: `/cart/items/${itemId}`,
        method: 'delete'
    })
}

/**
 * 清空购物车
 */
export function clearCart() {
    return request({
        url: '/cart/clear',
        method: 'post'
    })
}
