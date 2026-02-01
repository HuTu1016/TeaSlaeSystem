import request from '@/utils/request'

/**
 * 搜索商品
 * @param {Object} params - 搜索参数
 * @param {string} params.keyword - 关键词
 * @param {number} params.categoryId - 分类ID
 * @param {string} params.origin - 产地
 * @param {string} params.process - 工艺
 * @param {number} params.minPrice - 最低价
 * @param {number} params.maxPrice - 最高价
 * @param {string} params.sort - 排序方式
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 */
export function searchProducts(params = {}) {
    return request.get('/search/products', { params })
}

/**
 * 获取搜索建议
 * @param {string} keyword - 关键词
 * @param {number} limit - 返回数量，默认10
 */
export function getSuggestions(keyword, limit = 10) {
    return request.get('/search/suggest', { 
        params: { keyword, limit } 
    })
}

/**
 * 获取热搜词
 * @param {number} limit - 返回数量，默认10
 */
export function getHotWords(limit = 10) {
    return request.get('/search/hot-words', { 
        params: { limit } 
    })
}
