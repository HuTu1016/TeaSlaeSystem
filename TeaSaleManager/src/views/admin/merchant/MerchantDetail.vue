<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantDetail, getMerchantStats, enableMerchant, disableMerchant, getProductList, getOrderList, getAfterSaleList, getAllReviews } from '@/api/admin'
import { ArrowLeft, Goods, List, ChatDotSquare, Service, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 商家ID
const merchantId = computed(() => Number(route.params.id))

// 数据状态
const loading = ref(false)
const merchant = ref(null)
const stats = ref(null)
const activeTab = ref('products')

// 各Tab的数据
const productsData = ref({ list: [], total: 0, page: 1, loading: false })
const ordersData = ref({ list: [], total: 0, page: 1, loading: false })
const afterSalesData = ref({ list: [], total: 0, page: 1, loading: false })
const reviewsData = ref({ list: [], total: 0, page: 1, loading: false })

// 状态映射
const merchantStatusMap = {
    0: { label: '禁用', type: 'danger' },
    1: { label: '正常', type: 'success' },
    2: { label: '待审核', type: 'warning' },
    3: { label: '审核拒绝', type: 'info' }
}

const productStatusMap = {
    'ON_SHELF': { label: '在售', type: 'success' },
    'OFF_SHELF': { label: '下架', type: 'info' },
    'PENDING': { label: '待审核', type: 'warning' },
    'REJECTED': { label: '已拒绝', type: 'danger' }
}

const orderStatusMap = {
    'CREATED': { label: '待支付', type: 'warning' },
    'PAID': { label: '待发货', type: 'primary' },
    'SHIPPED': { label: '已发货', type: '' },
    'COMPLETED': { label: '已完成', type: 'success' },
    'CANCELLED': { label: '已取消', type: 'info' },
    'CLOSED': { label: '已关闭', type: 'info' }
}

const afterSaleStatusMap = {
    'PENDING': { label: '待处理', type: 'warning' },
    'PROCESSING': { label: '处理中', type: 'primary' },
    'COMPLETED': { label: '已完成', type: 'success' },
    'CANCELLED': { label: '已取消', type: 'info' }
}

// 加载商家详情
const loadMerchantInfo = async () => {
    loading.value = true
    try {
        const [detailRes, statsRes] = await Promise.all([
            getMerchantDetail(merchantId.value),
            getMerchantStats(merchantId.value)
        ])
        merchant.value = detailRes
        stats.value = statsRes
    } catch (e) {
        console.error(e)
        ElMessage.error('加载商家信息失败')
    } finally {
        loading.value = false
    }
}

// 加载商品列表
const loadProducts = async () => {
    productsData.value.loading = true
    try {
        const res = await getProductList({
            merchantId: merchantId.value,
            page: productsData.value.page,
            pageSize: 10
        })
        productsData.value.list = res.list || []
        productsData.value.total = res.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        productsData.value.loading = false
    }
}

// 加载订单列表
const loadOrders = async () => {
    ordersData.value.loading = true
    try {
        const res = await getOrderList({
            merchantId: merchantId.value,
            page: ordersData.value.page,
            pageSize: 10
        })
        ordersData.value.list = res.list || []
        ordersData.value.total = res.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        ordersData.value.loading = false
    }
}

// 加载售后列表
const loadAfterSales = async () => {
    afterSalesData.value.loading = true
    try {
        const res = await getAfterSaleList({
            merchantId: merchantId.value,
            page: afterSalesData.value.page,
            pageSize: 10
        })
        afterSalesData.value.list = res.list || []
        afterSalesData.value.total = res.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        afterSalesData.value.loading = false
    }
}

// 加载评价列表
const loadReviews = async () => {
    reviewsData.value.loading = true
    try {
        const res = await getAllReviews({
            merchantId: merchantId.value,
            page: reviewsData.value.page,
            pageSize: 10
        })
        reviewsData.value.list = res.list || []
        reviewsData.value.total = res.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        reviewsData.value.loading = false
    }
}

// Tab切换处理
const handleTabChange = (tabName) => {
    switch (tabName) {
        case 'products':
            if (productsData.value.list.length === 0) loadProducts()
            break
        case 'orders':
            if (ordersData.value.list.length === 0) loadOrders()
            break
        case 'aftersales':
            if (afterSalesData.value.list.length === 0) loadAfterSales()
            break
        case 'reviews':
            if (reviewsData.value.list.length === 0) loadReviews()
            break
    }
}

// 分页处理
const handleProductsPageChange = (page) => {
    productsData.value.page = page
    loadProducts()
}

const handleOrdersPageChange = (page) => {
    ordersData.value.page = page
    loadOrders()
}

const handleAfterSalesPageChange = (page) => {
    afterSalesData.value.page = page
    loadAfterSales()
}

const handleReviewsPageChange = (page) => {
    reviewsData.value.page = page
    loadReviews()
}

// 启用/禁用商家
const toggleMerchantStatus = async () => {
    const action = merchant.value.status === 1 ? '禁用' : '启用'
    try {
        await ElMessageBox.confirm(`确定要${action}该商家吗?`, '提示', { type: 'warning' })
        if (merchant.value.status === 1) {
            await disableMerchant(merchantId.value)
        } else {
            await enableMerchant(merchantId.value)
        }
        ElMessage.success(`${action}成功`)
        loadMerchantInfo()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

// 返回商家列表
const goBack = () => {
    router.push('/admin/merchants')
}

onMounted(() => {
    loadMerchantInfo()
    loadProducts() // 默认加载商品列表
})
</script>

<template>
  <div class="merchant-detail" v-loading="loading">
    <!-- 顶部返回栏 -->
    <div class="header-bar">
        <el-button :icon="ArrowLeft" @click="goBack">返回商家列表</el-button>
        <div class="header-title" v-if="merchant">
            <img :src="merchant.logo || 'https://via.placeholder.com/40'" class="shop-logo" />
            <span class="shop-name">{{ merchant.shopName }}</span>
            <el-tag :type="merchantStatusMap[merchant.status]?.type" size="small">
                {{ merchantStatusMap[merchant.status]?.label }}
            </el-tag>
        </div>
        <el-button 
            v-if="merchant && (merchant.status === 1 || merchant.status === 0)"
            :type="merchant.status === 1 ? 'danger' : 'success'" 
            @click="toggleMerchantStatus"
        >
            {{ merchant.status === 1 ? '禁用商家' : '启用商家' }}
        </el-button>
    </div>

    <!-- 商家信息卡片 -->
    <el-card class="info-card" v-if="merchant">
        <template #header>
            <span>商家信息</span>
        </template>
        <el-descriptions :column="3" border>
            <el-descriptions-item label="店铺名称">{{ merchant.shopName }}</el-descriptions-item>
            <el-descriptions-item label="商家姓名">{{ merchant.contactName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ merchant.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="店铺描述" :span="3">{{ merchant.description || '暂无描述' }}</el-descriptions-item>
            <el-descriptions-item label="入驻时间">{{ merchant.createdAt }}</el-descriptions-item>
        </el-descriptions>
    </el-card>

    <!-- 统计数据卡片 -->
    <el-row :gutter="16" class="stats-row" v-if="stats">
        <el-col :span="6">
            <el-card class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                    <el-icon><Goods /></el-icon>
                </div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
                    <div class="stat-label">商品总数</div>
                    <div class="stat-sub">在售 {{ stats.onShelfProducts || 0 }} / 待审核 {{ stats.pendingProducts || 0 }}</div>
                </div>
            </el-card>
        </el-col>
        <el-col :span="6">
            <el-card class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                    <el-icon><List /></el-icon>
                </div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
                    <div class="stat-label">订单总数</div>
                    <div class="stat-sub">待发货 {{ stats.pendingOrders || 0 }} / 已完成 {{ stats.completedOrders || 0 }}</div>
                </div>
            </el-card>
        </el-col>
        <el-col :span="6">
            <el-card class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                    <el-icon><Service /></el-icon>
                </div>
                <div class="stat-content">
                    <div class="stat-value">{{ stats.totalAfterSales || 0 }}</div>
                    <div class="stat-label">售后总数</div>
                    <div class="stat-sub">待处理 {{ stats.pendingAfterSales || 0 }}</div>
                </div>
            </el-card>
        </el-col>
        <el-col :span="6">
            <el-card class="stat-card">
                <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                    <el-icon><ChatDotSquare /></el-icon>
                </div>
                <div class="stat-content">
                    <div class="stat-value">¥{{ stats.totalSales || '0.00' }}</div>
                    <div class="stat-label">总销售额</div>
                    <div class="stat-sub">评价 {{ stats.totalReviews || 0 }}</div>
                </div>
            </el-card>
        </el-col>
    </el-row>

    <!-- Tab切换区域 -->
    <el-card class="tab-card">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <!-- 商品Tab -->
            <el-tab-pane label="商品管理" name="products">
                <el-table :data="productsData.list" v-loading="productsData.loading" border style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column label="商品" min-width="200">
                        <template #default="{ row }">
                            <div class="product-cell">
                                <img :src="row.mainImage || 'https://via.placeholder.com/50'" class="product-img" />
                                <span>{{ row.title }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="categoryName" label="分类" width="120" />
                    <el-table-column label="价格" width="120">
                        <template #default="{ row }">
                            ¥{{ row.minPrice }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="salesCount" label="销量" width="80" />
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="productStatusMap[row.status]?.type" size="small">
                                {{ productStatusMap[row.status]?.label }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createdAt" label="创建时间" width="180" />
                </el-table>
                <div class="pagination">
                    <el-pagination
                        v-model:current-page="productsData.page"
                        :total="productsData.total"
                        :page-size="10"
                        layout="total, prev, pager, next"
                        @current-change="handleProductsPageChange"
                    />
                </div>
            </el-tab-pane>

            <!-- 订单Tab -->
            <el-tab-pane label="订单管理" name="orders">
                <el-table :data="ordersData.list" v-loading="ordersData.loading" border style="width: 100%">
                    <el-table-column prop="orderNo" label="订单号" width="180" />
                    <el-table-column prop="userName" label="买家" width="120" />
                    <el-table-column label="金额" width="100">
                        <template #default="{ row }">
                            ¥{{ row.payAmount }}
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="orderStatusMap[row.status]?.type" size="small">
                                {{ orderStatusMap[row.status]?.label }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createdAt" label="下单时间" width="180" />
                </el-table>
                <div class="pagination">
                    <el-pagination
                        v-model:current-page="ordersData.page"
                        :total="ordersData.total"
                        :page-size="10"
                        layout="total, prev, pager, next"
                        @current-change="handleOrdersPageChange"
                    />
                </div>
            </el-tab-pane>

            <!-- 售后Tab -->
            <el-tab-pane label="售后管理" name="aftersales">
                <el-table :data="afterSalesData.list" v-loading="afterSalesData.loading" border style="width: 100%">
                    <el-table-column prop="afterSaleNo" label="售后单号" width="180" />
                    <el-table-column prop="type" label="类型" width="100" />
                    <el-table-column prop="reason" label="原因" min-width="200" />
                    <el-table-column label="申请金额" width="100">
                        <template #default="{ row }">
                            ¥{{ row.applyAmount }}
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="afterSaleStatusMap[row.status]?.type" size="small">
                                {{ afterSaleStatusMap[row.status]?.label }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createdAt" label="申请时间" width="180" />
                </el-table>
                <div class="pagination">
                    <el-pagination
                        v-model:current-page="afterSalesData.page"
                        :total="afterSalesData.total"
                        :page-size="10"
                        layout="total, prev, pager, next"
                        @current-change="handleAfterSalesPageChange"
                    />
                </div>
            </el-tab-pane>

            <!-- 评价Tab -->
            <el-tab-pane label="评价管理" name="reviews">
                <el-table :data="reviewsData.list" v-loading="reviewsData.loading" border style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="productTitle" label="商品" min-width="150" />
                    <el-table-column prop="userName" label="用户" width="120" />
                    <el-table-column label="评分" width="100">
                        <template #default="{ row }">
                            <el-rate v-model="row.rating" disabled size="small" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="createdAt" label="评价时间" width="180" />
                </el-table>
                <div class="pagination">
                    <el-pagination
                        v-model:current-page="reviewsData.page"
                        :total="reviewsData.total"
                        :page-size="10"
                        layout="total, prev, pager, next"
                        @current-change="handleReviewsPageChange"
                    />
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.merchant-detail {
    padding: 20px;
}

.header-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .header-title {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .shop-logo {
            width: 40px;
            height: 40px;
            border-radius: 8px;
            object-fit: cover;
        }
        
        .shop-name {
            font-size: 18px;
            font-weight: 600;
            color: #303133;
        }
    }
}

.info-card {
    margin-bottom: 20px;
}

.stats-row {
    margin-bottom: 20px;
    
    .stat-card {
        display: flex;
        padding: 20px;
        
        :deep(.el-card__body) {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 0;
        }
        
        .stat-icon {
            width: 56px;
            height: 56px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
        }
        
        .stat-content {
            .stat-value {
                font-size: 24px;
                font-weight: 700;
                color: #303133;
            }
            
            .stat-label {
                font-size: 14px;
                color: #909399;
                margin-top: 4px;
            }
            
            .stat-sub {
                font-size: 12px;
                color: #c0c4cc;
                margin-top: 4px;
            }
        }
    }
}

.tab-card {
    .product-cell {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .product-img {
            width: 50px;
            height: 50px;
            border-radius: 4px;
            object-fit: cover;
        }
    }
    
    .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }
}
</style>
