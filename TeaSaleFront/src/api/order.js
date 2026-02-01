import request from '@/utils/request'

/**
 * 创建订单
 * @param {Object} data - 订单数据
 */
export function createOrder(data) {
    return request({
        url: '/orders',
        method: 'post',
        data
    })
}

/**
 * 获取订单列表
 * @param {Object} params - { status, page, pageSize }
 */
export function getOrderList(params) {
    return request({
        url: '/orders',
        method: 'get',
        params
    })
}

/**
 * 获取订单详情
 * @param {string} orderId - 订单ID
 */
export function getOrderDetail(orderId) {
    return request({
        url: `/orders/${orderId}`,
        method: 'get'
    })
}

/**
 * 确认收货
 * @param {string} orderId - 订单ID
 */
export function confirmReceipt(orderId) {
    return request({
        url: `/orders/${orderId}/confirm-receipt`,
        method: 'post'
    })
}

/**
 * 取消订单
 * @param {string} orderId - 订单ID
 */
export function cancelOrder(orderId) {
    return request({
        url: `/orders/${orderId}/cancel`,
        method: 'post'
    })
}

/**
 * 支付订单
 * @param {string} orderId - 订单ID
 * @param {string} paymentMethod - 支付方式 (DIRECT-直接支付/BALANCE-余额支付)
 */
export function payOrder(orderId, paymentMethod = 'DIRECT') {
    return request({
        url: `/orders/${orderId}/pay`,
        method: 'post',
        params: { paymentMethod }
    })
}

/**
 * 申请退款
 * @param {string} orderId - 订单ID
 * @param {Object} data - { reason }
 */
export function applyRefund(orderId, data) {
    return request({
        url: `/orders/${orderId}/refund`,
        method: 'post',
        data
    })
}

/**
 * 获取订单支出明细列表（用于会员中心）
 * @param {Object} params - { filterType: ALL/PAYMENT/REFUND, page, size }
 */
export function getOrderExpenseList(params) {
    return request({
        url: '/orders/expense-list',
        method: 'get',
        params
    })
}

