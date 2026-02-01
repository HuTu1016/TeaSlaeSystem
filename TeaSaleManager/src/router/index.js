import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/',
            name: 'layout',
            component: () => import('../layout/MainLayout.vue'),
            redirect: '/dashboard',
            children: [
                {
                    path: 'dashboard',
                    name: 'dashboard',
                    component: () => import('../views/dashboard/Dashboard.vue'),
                    meta: { title: '控制台' }
                },
                // Merchant Routes
                {
                    path: 'merchant/products/list',
                    name: 'merchant-product-list',
                    component: () => import('../views/merchant/product/ProductList.vue'),
                    meta: { title: '商品列表' }
                },
                {
                    path: 'merchant/products/publish',
                    name: 'merchant-product-publish',
                    component: () => import('../views/merchant/product/ProductPublish.vue'),
                    meta: { title: '发布商品' }
                },
                {
                    path: 'merchant/orders',
                    name: 'merchant-order-list',
                    component: () => import('../views/merchant/order/OrderList.vue'),
                    meta: { title: '订单管理' }
                },
                // Admin Routes
                {
                    path: 'admin/merchants',
                    name: 'admin-merchant-list',
                    component: () => import('../views/admin/merchant/MerchantList.vue'),
                    meta: { title: '商家管理' }
                },
                {
                    path: 'admin/merchants/:id',
                    name: 'admin-merchant-detail',
                    component: () => import('../views/admin/merchant/MerchantDetail.vue'),
                    meta: { title: '商家详情' }
                },
                {
                    path: 'admin/users',
                    name: 'admin-user-list',
                    component: () => import('../views/admin/user/UserList.vue'),
                    meta: { title: '用户管理' }
                },
                {
                    path: 'admin/categories',
                    name: 'admin-category-list',
                    component: () => import('../views/admin/category/CategoryList.vue'),
                    meta: { title: '分类管理' }
                },
                {
                    path: 'admin/banners',
                    name: 'admin-banner-list',
                    component: () => import('../views/admin/banner/BannerList.vue'),
                    meta: { title: '轮播图管理' }
                },
                {
                    path: 'admin/coupons',
                    name: 'admin-coupon-list',
                    component: () => import('../views/admin/coupon/CouponList.vue'),
                    meta: { title: '优惠券管理' }
                },
                {
                    path: 'admin/reviews',
                    name: 'admin-review-list',
                    component: () => import('../views/admin/review/ReviewList.vue'),
                    meta: { title: '评价审核' }
                },
                {
                    path: 'admin/trace',
                    name: 'admin-trace-list',
                    component: () => import('../views/admin/trace/TraceList.vue'),
                    meta: { title: '溯源审核' }
                },
                {
                    path: 'admin/search',
                    name: 'admin-search-index',
                    component: () => import('../views/admin/search/SearchIndex.vue'),
                    meta: { title: '搜索管理' }
                },
                {
                    path: 'admin/contents',
                    name: 'admin-content-list',
                    component: () => import('../views/admin/content/ContentList.vue'),
                    meta: { title: '内容管理' }
                },
                {
                    path: 'admin/contents/create',
                    name: 'admin-content-create',
                    component: () => import('../views/admin/content/ContentEdit.vue'),
                    meta: { title: '新建文章' }
                },
                {
                    path: 'admin/contents/edit',
                    name: 'admin-content-edit',
                    component: () => import('../views/admin/content/ContentEdit.vue'),
                    meta: { title: '编辑文章' }
                },
                // 订单管理
                {
                    path: 'admin/orders',
                    name: 'admin-order-list',
                    component: () => import('../views/admin/order/OrderList.vue'),
                    meta: { title: '订单管理' }
                },
                // 商品管理
                {
                    path: 'admin/products',
                    name: 'admin-product-list',
                    component: () => import('../views/admin/product/ProductList.vue'),
                    meta: { title: '商品管理' }
                },
                {
                    path: 'admin/products/audit',
                    name: 'admin-product-audit',
                    component: () => import('../views/admin/product/ProductAudit.vue'),
                    meta: { title: '商品审核' }
                },
                // 售后管理
                {
                    path: 'admin/after-sales',
                    name: 'admin-aftersale-list',
                    component: () => import('../views/admin/aftersale/AfterSaleList.vue'),
                    meta: { title: '售后管理' }
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const isAuthenticated = !!userStore.token

    if (to.name !== 'login' && !isAuthenticated) {
        next({ name: 'login' })
    } else {
        next()
    }
})

export default router
