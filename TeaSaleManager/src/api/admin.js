import request from '@/utils/request'

// Merchant Management
export function getMerchantList(params) {
    return request({
        url: '/admin/merchants',
        method: 'get',
        params
    })
}

export function auditMerchant(merchantId, status, reason) {
    return request({
        url: `/admin/merchants/${merchantId}/audit`,
        method: 'put',
        params: { status, reason }
    })
}

export function enableMerchant(merchantId) {
    return request({
        url: `/admin/merchants/${merchantId}/enable`,
        method: 'put'
    })
}

export function disableMerchant(merchantId) {
    return request({
        url: `/admin/merchants/${merchantId}/disable`,
        method: 'put'
    })
}

// 获取商家详情
export function getMerchantDetail(merchantId) {
    return request({
        url: `/admin/merchants/${merchantId}`,
        method: 'get'
    })
}

// 获取商家统计数据
export function getMerchantStats(merchantId) {
    return request({
        url: `/admin/merchants/${merchantId}/stats`,
        method: 'get'
    })
}

// Content Management
export function getAdminArticles(params) {
    return request({
        url: '/admin/content/list',
        method: 'get',
        params
    })
}

export function getArticleDetail(id) {
    return request({
        url: `/admin/content/${id}`,
        method: 'get'
    })
}

export function createArticle(data) {
    return request({
        url: '/admin/content',
        method: 'post',
        data
    })
}

export function updateArticle(id, data) {
    return request({
        url: `/admin/content/${id}`,
        method: 'put',
        data
    })
}

export function publishArticle(id) {
    return request({
        url: `/admin/content/${id}/publish`,
        method: 'post'
    })
}

export function unpublishArticle(id) {
    return request({
        url: `/admin/content/${id}/unpublish`,
        method: 'post'
    })
}

export function deleteArticle(id) {
    return request({
        url: `/admin/content/${id}`,
        method: 'delete'
    })
}

// Category Management
export function getAllCategories() {
    return request({
        url: '/admin/categories',
        method: 'get'
    })
}

export function createCategory(data) {
    return request({
        url: '/admin/categories',
        method: 'post',
        data
    })
}

export function updateCategory(id, data) {
    return request({
        url: `/admin/categories/${id}`,
        method: 'put',
        data
    })
}

export function enableCategory(id) {
    return request({
        url: `/admin/categories/${id}/enable`,
        method: 'post'
    })
}

export function disableCategory(id) {
    return request({
        url: `/admin/categories/${id}/disable`,
        method: 'post'
    })
}

// User Management (Controller: /admin/users)
export function getUserList(params) {
    return request({
        url: '/admin/users',
        method: 'get',
        params
    })
}

export function getUserDetail(id) {
    return request({
        url: `/admin/users/${id}`,
        method: 'get'
    })
}

export function enableUser(id) {
    return request({
        url: `/admin/users/${id}/enable`,
        method: 'put'
    })
}

export function disableUser(id) {
    return request({
        url: `/admin/users/${id}/disable`,
        method: 'put'
    })
}

// Banner Management
export function getBannerList(params) {
    return request({
        url: '/admin/banners',
        method: 'get',
        params
    })
}

export function createBanner(data) {
    return request({
        url: '/admin/banners',
        method: 'post',
        data
    })
}

export function updateBanner(id, data) {
    return request({
        url: `/admin/banners/${id}`,
        method: 'put',
        data
    })
}

export function deleteBanner(id) {
    return request({
        url: `/admin/banners/${id}`,
        method: 'delete'
    })
}

export function enableBanner(id) {
    return request({
        url: `/admin/banners/${id}/enable`,
        method: 'post'
    })
}

export function disableBanner(id) {
    return request({
        url: `/admin/banners/${id}/disable`,
        method: 'post'
    })
}

// Coupon Management
export function getCouponList(params) {
    return request({
        url: '/admin/coupons',
        method: 'get',
        params
    })
}

export function createCoupon(data) {
    return request({
        url: '/admin/coupons',
        method: 'post',
        data
    })
}

export function updateCoupon(id, data) {
    return request({
        url: `/admin/coupons/${id}`,
        method: 'put',
        data
    })
}

export function enableCoupon(id) {
    return request({
        url: `/admin/coupons/${id}/enable`,
        method: 'post'
    })
}

export function disableCoupon(id) {
    return request({
        url: `/admin/coupons/${id}/disable`,
        method: 'post'
    })
}

// Review Management (Controller: /admin/reviews)
export function getPendingReviews(params) {
    return request({
        url: '/admin/reviews/pending',
        method: 'get',
        params
    })
}

export function getAllReviews(params) {
    return request({
        url: '/admin/reviews',
        method: 'get',
        params
    })
}

export function auditReview(id, data) {
    // data: { status: 1 or 2, reason: '' }
    return request({
        url: `/admin/reviews/${id}/audit`,
        method: 'put',
        data
    })
}

export function deleteReview(id) {
    return request({
        url: `/admin/reviews/${id}`,
        method: 'delete'
    })
}

// Trace Management
export function getTraceList(params) {
    return request({
        url: '/admin/trace/list',
        method: 'get',
        params
    })
}

export function approveTrace(id) {
    return request({
        url: `/admin/trace/${id}/approve`,
        method: 'post'
    })
}

export function rejectTrace(id, reason) {
    return request({
        url: `/admin/trace/${id}/reject`,
        method: 'post',
        data: { reason }
    })
}

// Search Management
export function reindexAll() {
    return request({
        url: '/admin/search/reindex',
        method: 'post'
    })
}

// Stats
export function getAdminOverview() {
    return request({
        url: '/admin/stats/overview',
        method: 'get'
    })
}

export function getSalesTrend(startDate, endDate) {
    return request({
        url: '/admin/stats/trend',
        method: 'get',
        params: { startDate, endDate }
    })
}

// ==================== 订单管理 ====================

export function getOrderList(params) {
    return request({
        url: '/admin/orders',
        method: 'get',
        params
    })
}

export function getOrderDetail(id) {
    return request({
        url: `/admin/orders/${id}`,
        method: 'get'
    })
}

export function cancelOrder(id) {
    return request({
        url: `/admin/orders/${id}/cancel`,
        method: 'post'
    })
}

export function closeOrder(id, reason) {
    return request({
        url: `/admin/orders/${id}/close`,
        method: 'post',
        params: { reason }
    })
}

export function getOrderStats() {
    return request({
        url: '/admin/orders/stats',
        method: 'get'
    })
}

// ==================== 商品管理 ====================

export function getProductList(params) {
    return request({
        url: '/admin/products',
        method: 'get',
        params
    })
}

export function getProductDetail(id) {
    return request({
        url: `/admin/products/${id}`,
        method: 'get'
    })
}

export function onShelfProduct(id) {
    return request({
        url: `/admin/products/${id}/on-shelf`,
        method: 'post'
    })
}

export function offShelfProduct(id, reason) {
    return request({
        url: `/admin/products/${id}/off-shelf`,
        method: 'post',
        params: { reason }
    })
}

export function deleteProduct(id) {
    return request({
        url: `/admin/products/${id}`,
        method: 'delete'
    })
}

export function getProductStats() {
    return request({
        url: '/admin/products/stats',
        method: 'get'
    })
}

export function getPendingProductList(params) {
    return request({
        url: '/admin/products/pending',
        method: 'get',
        params
    })
}

export function approveProduct(id, comment) {
    return request({
        url: `/admin/products/${id}/approve`,
        method: 'post',
        params: { comment }
    })
}

export function rejectProduct(id, reason) {
    return request({
        url: `/admin/products/${id}/reject`,
        method: 'post',
        params: { reason }
    })
}

// ==================== 售后管理 ====================

export function getAfterSaleList(params) {
    return request({
        url: '/admin/after-sales',
        method: 'get',
        params
    })
}

export function getAfterSaleDetail(id) {
    return request({
        url: `/admin/after-sales/${id}`,
        method: 'get'
    })
}

export function interveneAfterSale(id, data) {
    return request({
        url: `/admin/after-sales/${id}/intervene`,
        method: 'post',
        data
    })
}

export function forceCompleteAfterSale(id, comment) {
    return request({
        url: `/admin/after-sales/${id}/complete`,
        method: 'post',
        params: { comment }
    })
}

export function forceCancelAfterSale(id, reason) {
    return request({
        url: `/admin/after-sales/${id}/cancel`,
        method: 'post',
        params: { reason }
    })
}

export function getAfterSaleStats() {
    return request({
        url: '/admin/after-sales/stats',
        method: 'get'
    })
}
