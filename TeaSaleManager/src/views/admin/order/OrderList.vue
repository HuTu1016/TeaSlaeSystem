<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, getOrderStats } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const stats = ref({})
const queryParams = ref({
    page: 1,
    pageSize: 20,
    status: null,
    payStatus: null,
    orderNo: '',
    merchantId: null,
    startDate: null,
    endDate: null
})

const statusOptions = [
    { label: '待支付', value: 'CREATED' },
    { label: '已支付', value: 'PAID' },
    { label: '已发货', value: 'SHIPPED' },
    { label: '已完成', value: 'COMPLETED' },
    { label: '已取消', value: 'CANCELLED' },
    { label: '已关闭', value: 'CLOSED' }
]

const payStatusOptions = [
    { label: '未支付', value: 'UNPAID' },
    { label: '已支付', value: 'PAID' },
    { label: '已退款', value: 'REFUNDED' }
]

const statusMap = {
    CREATED: { label: '待支付', type: 'warning' },
    PAID: { label: '已支付', type: 'primary' },
    SHIPPED: { label: '已发货', type: '' },
    COMPLETED: { label: '已完成', type: 'success' },
    CANCELLED: { label: '已取消', type: 'info' },
    CLOSED: { label: '已关闭', type: 'danger' }
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getOrderList(queryParams.value)
        if (res.list) {
            list.value = res.list
            total.value = res.total
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const loadStats = async () => {
    try {
        const res = await getOrderStats()
        if (res) stats.value = res
    } catch (e) {}
}

const handleSearch = () => {
    queryParams.value.page = 1
    loadData()
}

const handleReset = () => {
    queryParams.value = {
        page: 1,
        pageSize: 20,
        status: null,
        payStatus: null,
        orderNo: '',
        merchantId: null,
        startDate: null,
        endDate: null
    }
    loadData()
}

const handleCancel = async (row) => {
    try {
        await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
        await cancelOrder(row.id)
        ElMessage.success('取消成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    return dateStr.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
    loadData()
    loadStats()
})
</script>

<template>
  <div class="order-list">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-title">总订单</div>
        <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">今日订单</div>
        <div class="stat-value primary">{{ stats.todayOrders || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">待支付</div>
        <div class="stat-value warning">{{ stats.pendingOrders || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已发货</div>
        <div class="stat-value">{{ stats.shippedOrders || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已完成</div>
        <div class="stat-value success">{{ stats.completedOrders || 0 }}</div>
      </el-card>
    </div>

    <!-- 搜索栏 -->
    <div class="toolbar">
      <el-input v-model="queryParams.orderNo" placeholder="订单号" style="width: 180px" clearable />
      <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 120px">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="queryParams.payStatus" placeholder="支付状态" clearable style="width: 120px">
        <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-date-picker v-model="queryParams.startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" style="width: 140px" />
      <el-date-picker v-model="queryParams.endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 140px" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 订单列表 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userName" label="用户" width="100" />
      <el-table-column prop="merchantName" label="商家" width="120" />
      <el-table-column label="订单状态" width="100">
        <template #default="scope">
          <el-tag :type="statusMap[scope.row.status]?.type" size="small">
            {{ statusMap[scope.row.status]?.label || scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payAmount" label="实付金额" width="100">
        <template #default="scope">¥{{ scope.row.payAmount }}</template>
      </el-table-column>
      <el-table-column prop="payStatus" label="支付状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.payStatus === 'PAID' ? 'success' : 'info'" size="small">
            {{ scope.row.payStatus === 'PAID' ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="scope">{{ formatDate(scope.row.createTime || scope.row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small">详情</el-button>
          <el-button 
            v-if="scope.row.status === 'CREATED' || scope.row.status === 'PAID'" 
            link type="danger" size="small" @click="handleCancel(scope.row)">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="loadData"
      />
    </div>
  </div>
</template>

<style scoped>
.order-list { padding: 20px; background: white; border-radius: 4px; }
.stats-cards { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-title { font-size: 14px; color: #666; margin-bottom: 8px; }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-value.primary { color: #409EFF; }
.stat-value.warning { color: #E6A23C; }
.stat-value.success { color: #67C23A; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; flex-wrap: wrap; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
