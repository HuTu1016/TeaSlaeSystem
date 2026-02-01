import request from '@/utils/request'

// ==================== 优惠券 ====================

// 获取可领取的优惠券列表
export function getAvailableCoupons() {
    return request({
        url: '/api/coupons/available',
        method: 'get'
    })
}

// 领取优惠券
export function receiveCoupon(couponId) {
    return request({
        url: `/api/coupons/${couponId}/receive`,
        method: 'post'
    })
}

// 获取我的优惠券
export function getMyCoupons(status) {
    return request({
        url: '/api/my/coupons',
        method: 'get',
        params: { status }
    })
}

// 获取可用于指定金额的优惠券
export function getAvailableCouponsForAmount(amount) {
    return request({
        url: '/api/my/coupons/available',
        method: 'get',
        params: { amount }
    })
}

// ==================== 会员 ====================

// 获取我的会员信息
export function getMyMembership() {
    return request({
        url: '/api/my/membership',
        method: 'get'
    })
}

// 计算会员折扣
export function calculateMemberDiscount(amount) {
    return request({
        url: '/api/my/member-discount',
        method: 'get',
        params: { amount }
    })
}
