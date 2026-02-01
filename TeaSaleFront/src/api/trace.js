import request from '@/utils/request'

/**
 * 通过溯源码查询
 * @param {string} traceCode - 溯源码
 */
export function getTraceByCode(traceCode) {
    return request.get(`/trace/code/${traceCode}`)
}

/**
 * 通过商品ID查询溯源
 * @param {number|string} productId - 商品ID
 */
export function getTraceByProductId(productId) {
    return request.get(`/trace/product/${productId}`)
}
