<template>
  <div class="after-sale-detail">
    <h2>售后详情</h2>
    
    <el-card v-loading="loading">
      <template v-if="detail">
        <!-- 状态信息 -->
        <div class="status-section">
          <el-tag :type="getStatusType(detail.status)" size="large">{{ formatStatus(detail.status) }}</el-tag>
          <span class="after-sale-no">服务单号：{{ detail.afterSaleNo }}</span>
        </div>
        
        <!-- 状态提示信息 -->
        <el-alert 
          v-if="statusTip" 
          :title="statusTip" 
          :type="getStatusAlertType(detail.status)"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <!-- 商品信息 -->
        <div class="product-section">
          <h3>商品信息</h3>
          <div class="product-info">
            <img :src="detail.productImage" class="product-image" />
            <div class="product-detail">
              <p class="product-name">{{ detail.productName }}</p>
              <p class="product-sku" v-if="detail.skuName">规格：{{ detail.skuName }}</p>
              <p class="product-price">¥{{ detail.price }} x {{ detail.quantity }}</p>
            </div>
          </div>
        </div>
        
        <!-- 售后信息 -->
        <div class="info-section">
          <h3>售后信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="售后类型">{{ formatType(detail.type) }}</el-descriptions-item>
            <el-descriptions-item label="申请金额">¥{{ detail.applyAmount }}</el-descriptions-item>
            <el-descriptions-item label="申请原因">{{ detail.reason }}</el-descriptions-item>
            <el-descriptions-item label="详细描述" v-if="detail.description">{{ detail.description }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDate(detail.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间" v-if="detail.completedAt">{{ formatDate(detail.completedAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 凭证图片 -->
        <div class="images-section" v-if="detail.images && detail.images.length > 0">
          <h3>凭证图片</h3>
          <div class="image-list">
            <el-image 
              v-for="(img, index) in detail.images" 
              :key="index" 
              :src="img" 
              :preview-src-list="detail.images"
              fit="cover"
              class="evidence-image"
            />
          </div>
        </div>
        
        <!-- 商家处理说明 -->
        <div class="merchant-section" v-if="detail.merchantComment">
          <h3>商家处理说明</h3>
          <p class="merchant-comment">{{ detail.merchantComment }}</p>
        </div>
        
        <!-- 填写物流表单（退货退款且商家已同意） -->
        <div class="ship-section" v-if="showShipForm">
          <h3>填写退货物流</h3>
          <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
            <el-form-item label="物流公司" prop="expressCompany">
              <el-select v-model="shipForm.expressCompany" placeholder="请选择物流公司" style="width: 100%;">
                <el-option label="顺丰速运" value="顺丰速运" />
                <el-option label="圆通快递" value="圆通快递" />
                <el-option label="中通快递" value="中通快递" />
                <el-option label="韵达快递" value="韵达快递" />
                <el-option label="申通快递" value="申通快递" />
                <el-option label="EMS" value="EMS" />
                <el-option label="京东物流" value="京东物流" />
                <el-option label="菜鸟裹裹" value="菜鸟裹裹" />
              </el-select>
            </el-form-item>
            <el-form-item label="物流单号" prop="expressNo">
              <el-input v-model="shipForm.expressNo" placeholder="请输入物流单号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleShip" :loading="shipping">提交物流信息</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-section">
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="danger" v-if="detail.status === 'APPLIED'" @click="handleCancel" :loading="canceling">取消申请</el-button>
        </div>
      </template>
      
      <el-empty v-else-if="!loading" description="售后申请不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAfterSaleDetail, cancelAfterSale, shipAfterSale } from '@/api/aftersale'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const canceling = ref(false)
const shipping = ref(false)
const detail = ref(null)
const shipFormRef = ref(null)

const shipForm = ref({
  expressCompany: '',
  expressNo: ''
})

const shipRules = {
  expressCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const formatType = (type) => {
  const map = { REFUND_ONLY: '仅退款', RETURN_REFUND: '退货退款', EXCHANGE: '换货' }
  return map[type] || type
}

const formatStatus = (status) => {
  const map = { 
    APPLIED: '待商家处理', 
    MERCHANT_APPROVED: '商家已同意', 
    MERCHANT_REJECTED: '商家已拒绝', 
    BUYER_SHIPPED_BACK: '已寄回待商家收货',
    MERCHANT_RECEIVED: '商家已收货',
    REFUNDING: '退款中',
    COMPLETED: '售后完成', 
    CANCELLED: '已取消' 
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 
    APPLIED: 'warning', 
    MERCHANT_APPROVED: 'success', 
    MERCHANT_REJECTED: 'danger', 
    BUYER_SHIPPED_BACK: '',
    MERCHANT_RECEIVED: '',
    REFUNDING: 'warning',
    COMPLETED: 'success', 
    CANCELLED: 'info' 
  }
  return map[status] || ''
}

const getStatusAlertType = (status) => {
  const map = { 
    APPLIED: 'warning', 
    MERCHANT_APPROVED: 'success', 
    MERCHANT_REJECTED: 'error', 
    BUYER_SHIPPED_BACK: 'info',
    MERCHANT_RECEIVED: 'info',
    REFUNDING: 'warning',
    COMPLETED: 'success', 
    CANCELLED: 'info' 
  }
  return map[status] || 'info'
}

// 状态提示信息
const statusTip = computed(() => {
  if (!detail.value) return ''
  const status = detail.value.status
  const type = detail.value.type
  
  const tips = {
    APPLIED: '您的售后申请已提交，请等待商家处理',
    MERCHANT_APPROVED: type === 'RETURN_REFUND' ? '商家已同意，请填写退货物流信息并寄回商品' : '商家已同意，等待退款处理',
    MERCHANT_REJECTED: '商家拒绝了您的售后申请，如有疑问请联系客服',
    BUYER_SHIPPED_BACK: '您已寄出商品，等待商家确认收货',
    MERCHANT_RECEIVED: '商家已收到退回商品，等待退款处理',
    REFUNDING: '正在处理退款，请耐心等待',
    COMPLETED: '售后已完成，退款将原路返回',
    CANCELLED: '您已取消此售后申请'
  }
  return tips[status] || ''
})

// 是否显示填写物流表单
const showShipForm = computed(() => {
  return detail.value && 
         detail.value.type === 'RETURN_REFUND' && 
         detail.value.status === 'MERCHANT_APPROVED'
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('缺少售后ID')
    router.back()
    return
  }
  
  loading.value = true
  try {
    detail.value = await getAfterSaleDetail(id)
  } catch (error) {
    console.error('加载售后详情失败:', error)
    ElMessage.error('加载售后详情失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个售后申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    canceling.value = true
    await cancelAfterSale(detail.value.id)
    ElMessage.success('取消成功')
    router.push('/user/after-sales')
  } catch (error) {
    if (error === 'cancel') return
    console.error('取消售后失败:', error)
    ElMessage.error('取消失败，请稍后重试')
  } finally {
    canceling.value = false
  }
}

// 提交退货物流
const handleShip = async () => {
  try {
    await shipFormRef.value.validate()
  } catch (error) {
    return
  }
  
  shipping.value = true
  try {
    await shipAfterSale(detail.value.id, shipForm.value)
    ElMessage.success('物流信息提交成功')
    loadDetail() // 刷新详情
  } catch (error) {
    console.error('提交物流失败:', error)
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    shipping.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.after-sale-detail {
  max-width: 800px;
  margin: 0 auto;
}

.status-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.after-sale-no {
  color: #666;
  font-size: 14px;
}

.product-section,
.info-section,
.images-section,
.merchant-section,
.ship-section {
  margin-bottom: 20px;
}

.product-section h3,
.info-section h3,
.images-section h3,
.merchant-section h3,
.ship-section h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
}

.product-info {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.product-detail {
  flex: 1;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.product-sku {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #999;
}

.product-price {
  margin: 0;
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.image-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.evidence-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
}

.merchant-comment {
  padding: 15px;
  background: #f0f9eb;
  border-radius: 8px;
  color: #67c23a;
  margin: 0;
}

.ship-section {
  background: #fff8e6;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ffd666;
}

.action-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}
</style>
