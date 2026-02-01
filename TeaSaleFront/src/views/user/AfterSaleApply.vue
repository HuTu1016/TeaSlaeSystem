<template>
  <div class="after-sale-apply">
    <h2>{{ isRefundOnly ? '申请退款' : '申请售后' }}</h2>
    
    <el-card v-if="orderDetail" class="order-info-card">
      <template #header>订单信息</template>
      <el-descriptions :column="2">
        <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(orderDetail.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ formatOrderStatus(orderDetail.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="apply-form-card" v-loading="loading">
      <template #header>选择退款商品（可多选）</template>
      
      <!-- 订单商品列表 - 多选 -->
      <div class="items-list" v-if="orderDetail">
        <el-checkbox-group v-model="selectedItemIds" class="item-checkbox-group">
          <div v-for="item in orderDetail.items" :key="item.id" class="item-row">
            <el-checkbox :value="item.id" class="item-checkbox">
              <div class="item-info">
                <img :src="item.image" class="item-thumb" />
                <div class="item-detail">
                  <p class="item-title">{{ item.title }}</p>
                  <p class="item-sku">{{ item.skuName }} x {{ item.quantity }}</p>
                  <p class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</p>
                </div>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>

      <!-- 售后表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="apply-form">
        <!-- 售后类型：待发货只显示仅退款 -->
        <el-form-item label="售后类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="REFUND_ONLY">仅退款</el-radio>
            <el-radio value="RETURN_REFUND" v-if="!isRefundOnly">退货退款</el-radio>
          </el-radio-group>
          <div class="type-tip" v-if="isRefundOnly">
            <el-text type="info" size="small">待发货订单仅支持退款，无需退货</el-text>
          </div>
        </el-form-item>

        <el-form-item label="退款原因" prop="reason">
          <el-select v-model="form.reason" placeholder="请选择退款原因" style="width: 100%;">
            <el-option v-if="isRefundOnly" label="不想要了" value="不想要了" />
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="商品与描述不符" value="商品与描述不符" />
            <el-option label="商品破损/包装破损" value="商品破损/包装破损" />
            <el-option label="商家发错货" value="商家发错货" />
            <el-option label="个人原因" value="个人原因" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>

        <el-form-item label="退款金额" prop="applyAmount">
          <el-input-number v-model="form.applyAmount" :min="minRefundAmount" :max="maxRefundAmount" :precision="2" :step="0.01" style="width: 200px;" />
          <span class="max-amount">最多可退 ¥{{ maxRefundAmount }}</span>
        </el-form-item>

        <el-form-item label="详细描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请详细描述您的售后原因（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail } from '@/api/order'
import { applyAfterSale } from '@/api/aftersale'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const orderDetail = ref(null)
const selectedItemIds = ref([]) // 多选商品ID列表

const form = ref({
  type: 'REFUND_ONLY',
  reason: '',
  description: '',
  applyAmount: 0
})

const rules = {
  type: [{ required: true, message: '请选择售后类型', trigger: 'change' }],
  reason: [{ required: true, message: '请选择退款原因', trigger: 'change' }],
  applyAmount: [{ required: true, message: '请输入退款金额', trigger: 'blur' }]
}

// 是否是仅退款模式（待发货状态）
const isRefundOnly = computed(() => {
  return route.query.type === 'REFUND_ONLY' || orderDetail.value?.status === 'PAID'
})

// 计算选中商品的最大可退款金额（多选）
const maxRefundAmount = computed(() => {
  if (!orderDetail.value || selectedItemIds.value.length === 0) return 0
  let total = 0
  for (const itemId of selectedItemIds.value) {
    const item = orderDetail.value.items.find(i => i.id === itemId)
    if (item) {
      total += item.price * item.quantity
    }
  }
  return Number(total.toFixed(2))
})

// 计算最小退款金额（确保min不大于max）
const minRefundAmount = computed(() => {
  return maxRefundAmount.value > 0 ? 0.01 : 0
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

// 格式化订单状态
const formatOrderStatus = (status) => {
  const map = {
    CREATED: '待付款',
    PAID: '待发货',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = route.query.orderId
  if (!orderId) {
    ElMessage.error('缺少订单ID参数')
    router.back()
    return
  }

  loading.value = true
  try {
    orderDetail.value = await getOrderDetail(orderId)
    // 默认全选所有商品
    if (orderDetail.value.items && orderDetail.value.items.length > 0) {
      selectedItemIds.value = orderDetail.value.items.map(item => item.id)
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 监听选中商品变化，自动设置退款金额
watch(selectedItemIds, () => {
  form.value.applyAmount = maxRefundAmount.value
}, { deep: true })

// 提交售后申请（多选商品，每个商品单独创建售后申请）
const handleSubmit = async () => {
  if (selectedItemIds.value.length === 0) {
    ElMessage.warning('请选择要退款的商品')
    return
  }

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    // 按选中的商品分别创建售后申请
    for (const itemId of selectedItemIds.value) {
      const item = orderDetail.value.items.find(i => i.id === itemId)
      const itemAmount = item ? Number((item.price * item.quantity).toFixed(2)) : 0
      
      const data = {
        orderItemId: itemId,
        type: form.value.type,
        reason: form.value.reason,
        description: form.value.description || '',
        applyAmount: itemAmount // 每个商品按实际金额退款
      }
      
      await applyAfterSale(data)
    }
    
    ElMessage.success('售后申请提交成功')
    router.push('/user/after-sales')
  } catch (error) {
    console.error('提交售后申请失败:', error)
    ElMessage.error(error.message || '提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 从URL读取预设的售后类型
  const typeParam = route.query.type
  if (typeParam && ['REFUND_ONLY', 'RETURN_REFUND'].includes(typeParam)) {
    form.value.type = typeParam
  }
  loadOrderDetail()
})
</script>

<style scoped>
.after-sale-apply {
  max-width: 800px;
  margin: 0 auto;
}

.order-info-card {
  margin-bottom: 20px;
}

.apply-form-card {
  margin-bottom: 20px;
}

.items-list {
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.item-checkbox-group {
  display: block;
}

.item-row {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: border-color 0.3s;
}

.item-row:hover {
  border-color: #409eff;
}

.item-checkbox {
  width: 100%;
  height: auto;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.item-thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.item-detail {
  flex: 1;
}

.item-title {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.item-sku {
  margin: 0 0 5px 0;
  font-size: 12px;
  color: #999;
}

.item-price {
  margin: 0;
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.apply-form {
  margin-top: 20px;
}

.max-amount {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.type-tip {
  margin-top: 5px;
}
</style>
