import request from '@/utils/request'

export function getArticles(params) {
    return request({
        url: '/content/list',
        method: 'get',
        params
    })
}

export function getArticleDetail(id) {
    return request({
        url: `/content/${id}`,
        method: 'get'
    })
}
