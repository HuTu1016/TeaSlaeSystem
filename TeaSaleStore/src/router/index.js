import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/Login.vue'),
        meta: { hidden: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/Register.vue'),
        meta: { hidden: true }
    },
    {
        path: '/',
        component: () => import('@/layout/MainLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/Dashboard.vue'),
                meta: { title: '数据看板', icon: 'Odometer' }
            },
            {
                path: 'products',
                name: 'ProductList',
                component: () => import('@/views/product/ProductList.vue'),
                meta: { title: '商品管理', icon: 'Goods' }
            },
            {
                path: 'products/edit/:id?', // id可选，无id为新增
                name: 'ProductEdit',
                component: () => import('@/views/product/ProductEdit.vue'),
                meta: { title: '编辑商品', hidden: true }
            },
            {
                path: 'products/:id/reviews',
                name: 'ProductReviews',
                component: () => import('@/views/product/ProductReviews.vue'),
                meta: { title: '商品评论', hidden: true }
            },
            {
                path: 'orders',
                name: 'OrderList',
                component: () => import('@/views/order/OrderList.vue'),
                meta: { title: '订单管理', icon: 'List' }
            },
            {
                path: 'traces',
                name: 'TraceList',
                component: () => import('@/views/trace/TraceList.vue'),
                meta: { title: '溯源管理', icon: 'Aim' }
            },
            {
                path: 'after-sales',
                name: 'AfterSaleList',
                component: () => import('@/views/aftersale/AfterSaleList.vue'),
                meta: { title: '售后管理', icon: 'Service' }
            },
            {
                path: 'reviews',
                name: 'ReviewList',
                component: () => import('@/views/review/ReviewList.vue'),
                meta: { title: '评论管理', icon: 'ChatDotSquare' }
            },
            {
                path: 'shop-settings',
                name: 'ShopSettings',
                component: () => import('@/views/shop/ShopSettings.vue'),
                meta: { title: '店铺设置', icon: 'Setting' }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/dashboard'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    // 直接从 localStorage 获取 token，避免 Pinia 初始化时序问题
    const token = localStorage.getItem('tea-merchant-token')
    // 无需登录的白名单页面
    const whiteList = ['/login', '/register']
    if (whiteList.includes(to.path)) {
        next()
    } else {
        if (token) {
            next()
        } else {
            next(`/login?redirect=${to.path}`)
        }
    }
})

export default router

