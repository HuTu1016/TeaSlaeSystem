import request from '@/utils/request'

/**
 * 获取首页推荐商品
 * @param {Object} params - 查询参数
 * @param {number} params.limit - 返回数量
 */
export function getRecommendedHome(params) {
    return request.get('/products/hot', { params: { limit: params?.limit || 4 } })
}

/**
 * 获取商品列表（分页）
 * @param {Object} params - 查询参数
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.categoryId - 分类ID
 * @param {number} params.minPrice - 最低价格
 * @param {number} params.maxPrice - 最高价格
 * @param {string} params.sort - 排序方式
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 */
export function getProductList(params = {}) {
    return request.get('/products', { params })
}

/**
 * 获取热销商品
 * @param {number} limit - 返回数量，默认8
 */
export function getHotProducts(limit = 8) {
    return request.get('/products/hot', { params: { limit } })
}

/**
 * 获取新品
 * @param {number} limit - 返回数量，默认8
 */
export function getNewProducts(limit = 8) {
    return request.get('/products/new', { params: { limit } })
}

/**
 * 获取商品详情
 * @param {number|string} id - 商品ID
 */
export function getProductDetail(id) {
    return request.get(`/products/${id}`)
}

/**
 * 获取商品分类列表
 */
export function getCategories() {
    return request.get('/categories')
}

/**
 * 获取相似商品
 * @param {number|string} id - 商品ID
 * @param {number} limit - 返回数量
 */
export function getSimilarProducts(id, limit = 12) {
    return request.get(`/products/${id}/similar`, { params: { limit } })
}
