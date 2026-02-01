<template>
  <div class="order-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="订单状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="待付款" value="PENDING_PAYMENT" />
              <el-option label="待发货" value="PENDING_SHIPMENT" />
              <el-option label="待收货" value="SHIPPED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
             <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="itemCount" label="商品数量" width="100" />
        <el-table-column prop="createdAt" label="下单时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button 
                size="small" 
                type="primary" 
                v-if="scope.row.status === 'PENDING_SHIPMENT'"
                @click="openShipDialog(scope.row)">发货</el-button>
             <!-- 详情按钮预留 -->
             <el-button size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog title="订单发货" v-model="shipDialogVisible" width="500px">
        <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
            <el-form-item label="物流公司" prop="logisticsCompany">
                <el-select v-model="shipForm.logisticsCompany" placeholder="请选择物流公司" style="width: 100%">
                    <el-option label="顺丰速运" value="SF" />
                    <el-option label="圆通速递" value="YTO" />
                    <el-option label="中通快递" value="ZTO" />
                    <el-option label="韵达快递" value="YD" />
                    <el-option label="邮政EMS" value="EMS" />
                </el-select>
            </el-form-item>
            <el-form-item label="物流单号" prop="logisticsNo">
                <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="shipDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmShip" :loading="shipLoading">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  status: ''
})

const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipFormRef = ref(null)
const currentOrderId = ref(null)
const shipForm = reactive({
    logisticsCompany: '',
    logisticsNo: ''
})

const shipRules = {
    logisticsCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
    logisticsNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/orders', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        status: queryForm.status || undefined
      }
    })
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
    currentPage.value = 1
    loadData()
}

const openShipDialog = (row) => {
    currentOrderId.value = row.id
    shipForm.logisticsCompany = ''
    shipForm.logisticsNo = ''
    shipDialogVisible.value = true
}

const confirmShip = async () => {
    if (!shipFormRef.value) return
    await shipFormRef.value.validate(async (valid) => {
        if (valid) {
            shipLoading.value = true
            try {
                await request.post(`/merchant/orders/${currentOrderId.value}/ship`, shipForm)
                ElMessage.success('发货成功')
                shipDialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            } finally {
                shipLoading.value = false
            }
        }
    })
}

const getStatusText = (status) => {
    const map = {
        'PENDING_PAYMENT': '待付款',
        'PENDING_SHIPMENT': '待发货',
        'SHIPPED': '待收货',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
    }
    return map[status] || status
}

const getStatusType = (status) => {
    const map = {
        'PENDING_PAYMENT': 'warning',
        'PENDING_SHIPMENT': 'danger',
        'SHIPPED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
    }
    return map[status] || ''
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.toolbar {
    margin-bottom: 20px;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
