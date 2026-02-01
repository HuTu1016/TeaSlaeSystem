import request from '@/utils/request'

/**
 * 获取我的评价列表
 */
export function getMyReviews(params) {
    return request({
        url: '/reviews',
        method: 'get',
        params
    })
}

/**
 * 提交评价
 */
export function createReview(data) {
    return request({
        url: '/reviews',
        method: 'post',
        data
    })
}

/**
 * 获取商品评价列表
 */
export function getProductReviews(productId, params) {
    return request({
        url: `/products/${productId}/reviews`,
        method: 'get',
        params
    })
}

/**
 * 获取用户待评价商品列表
 */
export function getPendingReviewItems() {
    return request({
        url: '/reviews/pending-items',
        method: 'get'
    })
}

/**
 * 获取订单评价状态（返回已全部评价的订单ID列表）
 */
export function getOrdersReviewStatus() {
    return request({
        url: '/reviews/orders-review-status',
        method: 'get'
    })
}
