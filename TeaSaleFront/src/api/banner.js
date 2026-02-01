import request from '@/utils/request'

/**
 * 获取首页启用的轮播图列表
 */
export function getBanners() {
    return request({
        url: '/banners',
        method: 'get'
    })
}
