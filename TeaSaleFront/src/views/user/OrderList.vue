<template>
  <div class="user-orders">
    <h2>我的订单</h2>
    <el-tabs v-model="activeStatus" @tab-click="handleTabClick">
      <el-tab-pane label="全部" name="ALL"></el-tab-pane>
      <el-tab-pane label="待付款" name="CREATED"></el-tab-pane>
      <el-tab-pane label="待发货" name="PAID"></el-tab-pane>
      <el-tab-pane label="已发货" name="SHIPPED"></el-tab-pane>
      <el-tab-pane label="已完成" name="COMPLETED"></el-tab-pane>
      <el-tab-pane label="售后中" name="AFTER_SALE"></el-tab-pane>
      <el-tab-pane label="已取消" name="CANCELLED"></el-tab-pane>
    </el-tabs>

    <div class="order-list" v-loading="loading">
      <el-card v-for="order in orders" :key="order.id" class="order-card" shadow="hover">
        <template #header>
          <div class="order-header">
            <span>{{ formatDate(order.createdAt) }}</span>
            <span>订单号：{{ order.orderNo }}</span>
            <span class="status" :class="{'after-sale-status': order.hasAfterSale}">
              {{ order.hasAfterSale ? '售后中' : formatStatus(order.status) }}
            </span>
          </div>
        </template>
        <div class="order-body">
           <div class="goods-list">
             <div class="goods-item" v-for="item in order.items" :key="item.id">
               <img :src="item.image" class="thumb" />
               <div class="info">
                 <p>{{ item.title }}</p>
                 <p class="sku">{{ item.skuName }} x {{ item.quantity }}</p>
               </div>
             </div>
           </div>
           <div class="order-actions">
             <div class="amount">实付: ¥{{ order.payAmount }}</div>
             <div class="btns">
               <!-- 待付款状态：显示去支付和取消订单 -->
               <el-button size="small" type="primary" v-if="order.status === 'CREATED'" @click="handlePayOrder(order.id)">去支付</el-button>
               <el-button size="small" v-if="order.status === 'CREATED'" @click="handleCancelOrder(order.id)">取消订单</el-button>
               <!-- 已发货状态：显示确认收货 -->
               <el-button size="small" v-if="order.status === 'SHIPPED'" type="primary" @click="handleConfirmReceipt(order.id)">确认收货</el-button>
               <!-- 待发货状态：显示申请退款（未有售后时） -->
               <el-button size="small" v-if="order.status === 'PAID' && !order.hasAfterSale" @click="goToRefund(order.id)">申请退款</el-button>
               <!-- 已完成状态：显示申请售后（未有售后时） -->
               <el-button size="small" v-if="order.status === 'COMPLETED' && !order.hasAfterSale" @click="goToAfterSale(order.id)">申请售后</el-button>
               <!-- 售后中：显示查看售后详情 -->
               <el-button size="small" type="warning" v-if="order.hasAfterSale" @click="goToAfterSaleDetail(order)">查看售后详情</el-button>
               <!-- 根据是否已全部评价显示不同按钮 -->
               <el-button size="small" v-if="order.status === 'COMPLETED' && isOrderFullyReviewed(order.id)" @click="goToViewReview">查看评价</el-button>
               <el-button size="small" v-if="order.status === 'COMPLETED' && !isOrderFullyReviewed(order.id)" @click="goToReview(order.id)">去评价</el-button>
               <!-- 所有状态都显示查看详情 -->
               <el-button size="small" @click="$router.push(`/user/orders/${order.id}`)">查看详情</el-button>
             </div>
           </div>
        </div>
      </el-card>
      <el-empty v-if="orders.length === 0" description="暂无订单" />
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, confirmReceipt, cancelOrder, payOrder } from '@/api/order'
import { getOrdersReviewStatus } from '@/api/review'

const router = useRouter()
const activeStatus = ref('ALL')
const orders = ref([])
const loading = ref(false)
const reviewedOrderIds = ref([]) // 已全部评价的订单ID列表
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
let requestId = 0 // 请求计数器，用于防止竞态条件

// 订单状态排序优先级（全部条件下：待付款优先，售后中其次）
const getOrderPriority = (order) => {
  // 待付款最优先
  if (order.status === 'CREATED') return 1
  // 售后中其次
  if (order.hasAfterSale) return 2
  // 其他状态按原顺序
  const statusOrder = { PAID: 3, SHIPPED: 4, COMPLETED: 5, CANCELLED: 6 }
  return statusOrder[order.status] || 99
}

// 对订单按优先级排序
const sortOrdersByPriority = (items) => {
  return [...items].sort((a, b) => getOrderPriority(a) - getOrderPriority(b))
}

const formatStatus = (status) => {
    const map = {
        CREATED: '待付款',
        PAID: '待发货',
        SHIPPED: '已发货',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
    }
    return map[status] || status
}

// 格式化日期为 "xxxx年xx月xx日" 格式
const formatDate = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}年${month}月${day}日`
}

const loadOrders = async () => {
    loading.value = true
    orders.value = [] // 立即清空旧数据，避免显示不匹配的过时数据
    const currentRequestId = ++requestId // 记录当前请求的ID
    const statusToQuery = activeStatus.value // 在发送请求前捕获当前状态
    try {
        const params = {
            page: currentPage.value,
            pageSize: pageSize.value
        }
        // 售后中筛选需要查询全部订单然后过滤
        if (statusToQuery !== 'ALL' && statusToQuery !== 'AFTER_SALE') {
            params.status = statusToQuery
        }
        // 并行加载订单列表和评价状态
        const [orderRes, reviewStatusRes] = await Promise.all([
            getOrderList(params),
            getOrdersReviewStatus()
        ])
        
        // 只有当这是最新的请求时才更新数据
        if (currentRequestId === requestId) {
            let orderList = orderRes.items || []
            // 如果是售后中筛选，只显示有售后的订单
            if (statusToQuery === 'AFTER_SALE') {
                orderList = orderList.filter(order => order.hasAfterSale)
            }
            // 全部条件下按优先级排序
            if (statusToQuery === 'ALL') {
                orderList = sortOrdersByPriority(orderList)
            }
            orders.value = orderList
            total.value = orderRes.total || 0
            reviewedOrderIds.value = reviewStatusRes || []
            loading.value = false
        }
    } catch (error) {
        console.error('加载订单列表失败:', error)
        // 发生错误时，只有最新请求才关闭loading
        if (currentRequestId === requestId) {
            loading.value = false
        }
    }
}

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    loadOrders()
}

const handlePageChange = (val) => {
    currentPage.value = val
    loadOrders()
}

// 确认收货
const handleConfirmReceipt = async (orderId) => {
    try {
        await ElMessageBox.confirm('确认已收到货物？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        
        await confirmReceipt(orderId)
        ElMessage.success('确认收货成功')
        loadOrders()
    } catch (error) {
        if (error === 'cancel') return
        console.error('确认收货失败:', error)
        ElMessage.error('确认收货失败')
    }
}

// 取消订单
const handleCancelOrder = async (orderId) => {
    try {
        await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        
        await cancelOrder(orderId)
        ElMessage.success('订单取消成功')
        loadOrders()
    } catch (error) {
        if (error === 'cancel') return
        console.error('取消订单失败:', error)
        ElMessage.error('取消订单失败')
    }
}

// 支付订单
const handlePayOrder = async (orderId) => {
    try {
        await ElMessageBox.confirm('确认支付此订单？', '支付确认', {
            confirmButtonText: '确认支付',
            cancelButtonText: '取消',
            type: 'info'
        })
        
        await payOrder(orderId)
        ElMessage.success('支付成功')
        loadOrders()
    } catch (error) {
        if (error === 'cancel') return
        console.error('支付失败:', error)
        ElMessage.error('支付失败')
    }
}

// 跳转到售后申请页面
const goToAfterSale = (orderId) => {
    router.push(`/user/after-sales/apply?orderId=${orderId}`)
}

// 跳转到退款申请页面（待发货订单申请仅退款）
const goToRefund = (orderId) => {
    router.push(`/user/after-sales/apply?orderId=${orderId}&type=REFUND_ONLY`)
}

// 跳转到评价页面
const goToReview = (orderId) => {
    router.push(`/user/reviews/create?orderId=${orderId}`)
}

// 跳转到查看评价页面（已评价列表）
const goToViewReview = () => {
    router.push('/user/reviews?tab=published')
}

// 判断订单是否已全部评价
const isOrderFullyReviewed = (orderId) => {
    return reviewedOrderIds.value.includes(orderId)
}

// 跳转到售后详情页面
const goToAfterSaleDetail = (order) => {
    // 找到有售后的订单项
    const itemWithAfterSale = order.items.find(item => item.afterSaleId)
    if (itemWithAfterSale && itemWithAfterSale.afterSaleId) {
        router.push(`/user/after-sales/${itemWithAfterSale.afterSaleId}`)
    } else {
        router.push('/user/after-sales')
    }
}

// 使用 tab-click 事件的参数来获取选中的标签
const handleTabClick = (pane) => {
    // pane.props.name 是新选中的标签名称
    // 但此时 v-model 已经更新了，所以可以使用 nextTick 确保状态同步后再请求
    currentPage.value = 1 // 切换tab时重置页码
    nextTick(() => {
        loadOrders()
    })
}

onMounted(() => {
    loadOrders()
})
</script>

<style scoped>
.order-card {
  margin-bottom: 20px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}
.status {
    color: #f56c6c;
    font-weight: bold;
}
.status.after-sale-status {
    color: #e6a23c;
}
.order-body {
    display: flex;
    justify-content: space-between;
}
.goods-item {
    display: flex;
    margin-bottom: 10px;
}
.thumb {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    object-fit: cover;
}
.sku {
    color: #999;
    font-size: 12px;
}
.amount {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
    text-align: right;
}
.btns {
    display: flex;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>
