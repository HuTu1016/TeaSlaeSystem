import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { top: 0 }
        }
    },
    routes: [
        {
            path: '/',
            component: MainLayout,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: () => import('@/views/home/Home.vue')
                },
                {
                    path: 'products',
                    name: 'products',
                    component: () => import('@/views/product/ProductList.vue')
                },
                {
                    path: 'products/:id',
                    name: 'product-detail',
                    component: () => import('@/views/product/ProductDetail.vue')
                },
                {
                    path: 'cart',
                    name: 'cart',
                    component: () => import('@/views/cart/Cart.vue')
                },
                {
                    path: 'articles',
                    name: 'articles',
                    component: () => import('@/views/article/ArticleList.vue')
                },
                {
                    path: 'articles/:id',
                    name: 'article-detail',
                    component: () => import('@/views/article/ArticleDetail.vue')
                },
                {
                    path: 'trace',
                    name: 'trace',
                    component: () => import('@/views/trace/TracePage.vue')
                },

                {
                    path: 'coupon/center',
                    name: 'coupon-center',
                    component: () => import('@/views/coupon/CouponCenter.vue')
                },
                {
                    path: 'order/confirm',
                    name: 'order-confirm',
                    component: () => import('@/views/order/OrderConfirm.vue')
                },
                {
                    path: 'user',
                    component: () => import('@/views/user/UserLayout.vue'),
                    children: [
                        { path: 'profile', component: () => import('@/views/user/UserProfile.vue') },
                        { path: 'orders', component: () => import('@/views/user/OrderList.vue') },
                        { path: 'orders/:id', component: () => import('@/views/user/OrderDetail.vue') },
                        { path: 'address', component: () => import('@/views/user/AddressList.vue') },
                        { path: 'after-sales', component: () => import('@/views/user/AfterSalesList.vue') },
                        { path: 'after-sales/apply', component: () => import('@/views/user/AfterSaleApply.vue') },
                        { path: 'after-sales/:id', component: () => import('@/views/user/AfterSaleDetail.vue') },
                        { path: 'reviews/create', component: () => import('@/views/user/ReviewCreate.vue') },
                        { path: 'reviews', component: () => import('@/views/user/ReviewList.vue') },
                        { path: 'favorites', component: () => import('@/views/user/FavoriteList.vue') },
                        { path: 'footprints', component: () => import('@/views/user/FootprintList.vue') },
                        { path: 'coupons', component: () => import('@/views/user/MyCoupons.vue') },
                        { path: 'wallet', component: () => import('@/views/user/Wallet.vue') }
                    ]
                }
            ]
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/auth/Login.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('@/views/auth/Register.vue')
        },
        {
            path: '/pay/result',
            name: 'pay-result',
            component: () => import('@/views/pay/PayResult.vue')
        },

    ]
})

export default router
