import request from '@/utils/request'

/**
 * 获取钱包信息
 */
export function getWallet() {
    return request({
        url: '/wallet',
        method: 'get'
    })
}

/**
 * 获取钱包概览（余额+最近交易）
 */
export function getWalletOverview() {
    return request({
        url: '/wallet/overview',
        method: 'get'
    })
}

/**
 * 获取余额明细
 * @param {Object} params - { type, page, size }
 */
export function getBalanceLogs(params) {
    return request({
        url: '/wallet/logs',
        method: 'get',
        params
    })
}

/**
 * 获取会员中心完整信息（余额+会员等级+进度）
 */
export function getMemberCenter() {
    return request({
        url: '/wallet/member-center',
        method: 'get'
    })
}

