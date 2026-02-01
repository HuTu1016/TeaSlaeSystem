<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAfterSaleList, forceCompleteAfterSale, forceCancelAfterSale, getAfterSaleStats } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const stats = ref({})
const queryParams = ref({
    page: 1,
    pageSize: 20,
    status: null,
    type: null,
    afterSaleNo: ''
})

const statusOptions = [
    { label: '待处理', value: 'PENDING' },
    { label: '处理中', value: 'PROCESSING' },
    { label: '已完成', value: 'COMPLETED' },
    { label: '已取消', value: 'CANCELLED' },
    { label: '已拒绝', value: 'REJECTED' }
]

const typeOptions = [
    { label: '退款', value: 'REFUND' },
    { label: '退货退款', value: 'RETURN_REFUND' },
    { label: '换货', value: 'EXCHANGE' }
]

const statusMap = {
    PENDING: { label: '待处理', type: 'warning' },
    PROCESSING: { label: '处理中', type: 'primary' },
    COMPLETED: { label: '已完成', type: 'success' },
    CANCELLED: { label: '已取消', type: 'info' },
    REJECTED: { label: '已拒绝', type: 'danger' }
}

const typeMap = {
    REFUND: '退款',
    RETURN_REFUND: '退货退款',
    EXCHANGE: '换货'
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getAfterSaleList(queryParams.value)
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
        const res = await getAfterSaleStats()
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
        type: null,
        afterSaleNo: ''
    }
    loadData()
}

const handleComplete = async (row) => {
    try {
        const { value } = await ElMessageBox.prompt('请输入处理备注（可选）', '强制完成售后', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '管理员备注'
        })
        await forceCompleteAfterSale(row.id, value)
        ElMessage.success('操作成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleCancel = async (row) => {
    try {
        const { value } = await ElMessageBox.prompt('请输入取消原因', '强制取消售后', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '取消原因'
        })
        await forceCancelAfterSale(row.id, value)
        ElMessage.success('操作成功')
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
  <div class="aftersale-list">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-title">全部售后</div>
        <div class="stat-value">{{ stats.totalAfterSales || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">今日售后</div>
        <div class="stat-value primary">{{ stats.todayAfterSales || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">待处理</div>
        <div class="stat-value warning">{{ stats.pendingAfterSales || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">处理中</div>
        <div class="stat-value">{{ stats.processingAfterSales || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已完成</div>
        <div class="stat-value success">{{ stats.completedAfterSales || 0 }}</div>
      </el-card>
    </div>

    <!-- 搜索栏 -->
    <div class="toolbar">
      <el-input v-model="queryParams.afterSaleNo" placeholder="售后单号" style="width: 180px" clearable />
      <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="queryParams.type" placeholder="类型" clearable style="width: 120px">
        <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 售后列表 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="afterSaleNo" label="售后单号" width="180" />
      <el-table-column prop="userName" label="用户" width="100" />
      <el-table-column prop="merchantName" label="商家" width="120" />
      <el-table-column label="类型" width="100">
        <template #default="scope">{{ typeMap[scope.row.type] || scope.row.type }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusMap[scope.row.status]?.type" size="small">
            {{ statusMap[scope.row.status]?.label || scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyAmount" label="申请金额" width="100">
        <template #default="scope">¥{{ scope.row.applyAmount }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="原因" min-width="150" show-overflow-tooltip />
      <el-table-column label="申请时间" width="160">
        <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small">详情</el-button>
          <template v-if="scope.row.status === 'PENDING' || scope.row.status === 'PROCESSING'">
            <el-button link type="success" size="small" @click="handleComplete(scope.row)">完成</el-button>
            <el-button link type="danger" size="small" @click="handleCancel(scope.row)">取消</el-button>
          </template>
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
.aftersale-list { padding: 20px; background: white; border-radius: 4px; }
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
