import request from '@/utils/request'

// Product Management
export function getMerchantProducts(params) {
    return request({
        url: '/merchant/products', // Need to check if there is a list endpoint for merchant
        method: 'get',
        params
    })
}

export function createProduct(data) {
    return request({
        url: '/merchant/products',
        method: 'post',
        data
    })
}

export function updateProduct(id, data) {
    return request({
        url: `/merchant/products/${id}`,
        method: 'put',
        data
    })
}

export function onShelfProduct(id) {
    return request({
        url: `/merchant/products/${id}/on-shelf`,
        method: 'post'
    })
}

export function offShelfProduct(id) {
    return request({
        url: `/merchant/products/${id}/off-shelf`,
        method: 'post'
    })
}

export function getMerchantOrders(params) {
    return request({
        url: '/merchant/orders',
        method: 'get',
        params
    })
}

export function shipOrder(orderId, data) {
    return request({
        url: `/merchant/orders/${orderId}/ship`,
        method: 'post',
        data
    })
}

// Category
export function getCategories() {
    return request({
        url: '/categories',
        method: 'get'
    })
}
