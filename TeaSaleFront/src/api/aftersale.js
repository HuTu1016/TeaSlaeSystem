import request from '@/utils/request'

/**
 * 获取售后列表
 * @param {Object} params - { status, page, pageSize }
 */
export function getAfterSales(params) {
    return request({
        url: '/after-sales',
        method: 'get',
        params
    })
}

/**
 * 获取售后详情
 */
export function getAfterSaleDetail(id) {
    return request({
        url: `/after-sales/${id}`,
        method: 'get'
    })
}

/**
 * 申请售后
 */
export function applyAfterSale(data) {
    return request({
        url: '/after-sales',
        method: 'post',
        data
    })
}

/**
 * 取消售后
 */
export function cancelAfterSale(id) {
    return request({
        url: `/after-sales/${id}/cancel`,
        method: 'post'
    })
}

/**
 * 填写退货物流（退货退款场景）
 */
export function shipAfterSale(id, data) {
    return request({
        url: `/after-sales/${id}/ship`,
        method: 'post',
        data
    })
}
