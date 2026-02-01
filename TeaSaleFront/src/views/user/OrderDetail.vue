<template>
  <div class="order-detail-page" v-if="order">
    <h2>订单详情</h2>
    <div class="status-bar">
      <h3>当前状态：<span style="color: #f56c6c">{{ formatStatus(order.status) }}</span></h3>
      <div class="actions">
         <el-button v-if="order.status === 'CREATED'" type="primary" @click="handlePayOrder">去支付</el-button>
         <el-button v-if="order.status === 'CREATED'" @click="handleCancelOrder">取消订单</el-button>
         <el-button v-if="order.status === 'SHIPPED'" type="primary" @click="handleConfirmReceipt">确认收货</el-button>
         <el-button v-if="order.status === 'COMPLETED'" @click="goToAfterSale">申请售后</el-button>
         <el-button v-if="order.status === 'PAID'" disabled>等待发货</el-button>
         <el-button v-if="order.status === 'COMPLETED'" @click="goToReview">去评价</el-button>
      </div>
    </div>

    <el-card class="info-card">
      <template #header>订单信息</template>
      <el-descriptions :column="2">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(order.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ order.addressDetail }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="items-card">
       <template #header>商品清单</template>
       <el-table :data="order.items">
         <el-table-column label="商品信息">
            <template #default="scope">
              <div style="display: flex; align-items: center;">
                 <img :src="scope.row.image" style="width: 50px; height: 50px; margin-right: 10px;" />
                 <span>{{ scope.row.title }}</span>
              </div>
            </template>
         </el-table-column>
         <el-table-column prop="price" label="单价" />
         <el-table-column prop="quantity" label="数量" />
         <el-table-column label="小计">
            <template #default="scope">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
         </el-table-column>
       </el-table>
       <div class="total-bar">
         <div v-if="order.couponDiscountAmount && order.couponDiscountAmount > 0" class="discount-info">
           <span>商品总价：¥{{ order.totalAmount }}</span>
           <span style="color: #67c23a; margin-left: 20px;">优惠券抵扣：-¥{{ order.couponDiscountAmount }}</span>
         </div>
         实付金额：<span class="price">¥{{ order.payAmount }}</span>
       </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, confirmReceipt, cancelOrder, payOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(false)

const formatStatus = (status) => {
    const map = { CREATED: '待付款', PAID: '待发货', SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消' }
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

const loadOrderDetail = async () => {
    loading.value = true
    try {
        order.value = await getOrderDetail(route.params.id)
    } catch (error) {
        console.error('加载订单详情失败:', error)
    } finally {
        loading.value = false
    }
}

const handleConfirmReceipt = async () => {
    try {
        await ElMessageBox.confirm('确认已收到货物？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        await confirmReceipt(order.value.id)
        ElMessage.success('确认收货成功')
        loadOrderDetail()
    } catch (error) {
        if (error === 'cancel') return
        console.error('确认收货失败:', error)
    }
}

const handleCancelOrder = async () => {
    try {
        await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        await cancelOrder(order.value.id)
        ElMessage.success('订单取消成功')
        loadOrderDetail()
    } catch (error) {
        if (error === 'cancel') return
        console.error('取消订单失败:', error)
        ElMessage.error('取消订单失败')
    }
}

// 支付订单
const handlePayOrder = async () => {
    try {
        await ElMessageBox.confirm('确认支付此订单？', '支付确认', {
            confirmButtonText: '确认支付',
            cancelButtonText: '取消',
            type: 'info'
        })
        await payOrder(order.value.id)
        ElMessage.success('支付成功')
        loadOrderDetail()
    } catch (error) {
        if (error === 'cancel') return
        console.error('支付失败:', error)
        ElMessage.error('支付失败')
    }
}

// 跳转到售后申请页面
const goToAfterSale = () => {
    router.push(`/user/after-sales/apply?orderId=${order.value.id}`)
}

const goToReview = () => {
    router.push(`/user/reviews/create?orderId=${order.value.id}`)
}

onMounted(() => {
    loadOrderDetail()
})
</script>

<style scoped>
.status-bar {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.info-card, .items-card {
  margin-bottom: 20px;
}
.total-bar {
  text-align: right;
  margin-top: 20px;
  font-size: 16px;
}
.price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
.discount-info {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}
</style>
