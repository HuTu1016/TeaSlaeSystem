<template>
  <div class="after-sales">
    <h2>售后服务</h2>
    <el-tabs v-model="activeName" @tab-click="handleTabChange">
      <el-tab-pane label="全部" name="all"></el-tab-pane>
      <el-tab-pane label="待处理" name="APPLIED"></el-tab-pane>
      <el-tab-pane label="商家已同意" name="MERCHANT_APPROVED"></el-tab-pane>
      <el-tab-pane label="待商家收货" name="BUYER_SHIPPED_BACK"></el-tab-pane>
      <el-tab-pane label="已完成" name="COMPLETED"></el-tab-pane>
      <el-tab-pane label="已拒绝" name="MERCHANT_REJECTED"></el-tab-pane>
      <el-tab-pane label="已取消" name="CANCELLED"></el-tab-pane>
    </el-tabs>

    <el-table :data="records" style="width: 100%" v-loading="loading">
      <el-table-column prop="afterSaleNo" label="服务单号" width="180" />
      <el-table-column label="商品">
        <template #default="scope">{{ scope.row.productName }}</template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">{{ formatType(scope.row.type) }}</template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template #default="scope">
          <span class="amount">¥{{ scope.row.applyAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="180">
        <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="viewDetail(scope.row.id)">查看详情</el-button>
          <el-button link type="danger" v-if="scope.row.status === 'APPLIED'" @click="handleCancel(scope.row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="records.length === 0 && !loading" description="暂无售后记录" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getAfterSales, cancelAfterSale } from '@/api/aftersale'

const router = useRouter()
const activeName = ref('all')
const records = ref([])
const loading = ref(false)

const formatType = (type) => {
  const map = { REFUND_ONLY: '仅退款', RETURN_REFUND: '退货退款', EXCHANGE: '换货' }
  return map[type] || type
}

const formatStatus = (status) => {
  const map = { 
    APPLIED: '待商家处理', 
    MERCHANT_APPROVED: '商家已同意', 
    MERCHANT_REJECTED: '商家已拒绝', 
    BUYER_SHIPPED_BACK: '已寄回待收货',
    MERCHANT_RECEIVED: '商家已收货',
    REFUNDING: '退款中',
    COMPLETED: '售后完成', 
    CANCELLED: '已取消' 
  }
  return map[status] || status
}

const getStatusTagType = (status) => {
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

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const loadAfterSales = async () => {
  loading.value = true
  try {
    const params = { page: 1, pageSize: 20 }
    if (activeName.value !== 'all') {
      params.status = activeName.value
    }
    const res = await getAfterSales(params)
    records.value = res.items || []
  } catch (error) {
    console.error('加载售后列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  // 使用nextTick确保activeName已更新后再加载数据
  nextTick(() => {
    loadAfterSales()
  })
}

const viewDetail = (id) => {
  router.push(`/user/after-sales/${id}`)
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个售后申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelAfterSale(id)
    ElMessage.success('取消成功')
    loadAfterSales()
  } catch (error) {
    if (error === 'cancel') return
    console.error('取消售后失败:', error)
  }
}

onMounted(() => {
  loadAfterSales()
})
</script>

<style scoped>
.after-sales {
  padding: 20px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.el-table {
  margin-top: 20px;
}
</style>
