<template>
  <div class="aftersale-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="待处理" value="PENDING" />
              <el-option label="待退货" value="APPROVED" />
              <el-option label="退货中" value="RETURNING" />
              <el-option label="已收货" value="RECEIVED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="afterSaleNo" label="售后单号" width="180" />
        <el-table-column prop="type" label="类型" width="100">
             <template #default="scope">
                 {{ scope.row.type === 'REFUND_ONLY' ? '仅退款' : '退货退款' }}
             </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="applyAmount" label="申请金额" width="120">
             <template #default="scope">¥{{ scope.row.applyAmount }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
             <template v-if="scope.row.status === 'PENDING'">
                <el-button size="small" type="success" @click="handleApprove(scope.row)">通过</el-button>
                <el-button size="small" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
             </template>
             <template v-if="scope.row.status === 'RETURNING'">
                <el-button size="small" type="primary" @click="handleConfirmReceived(scope.row)">确认收货</el-button>
             </template>
             <!-- 假设 APPROVED(仅退款) 或 RECEIVED(已收货) 状态下可以执行退款 -->
             <template v-if="(scope.row.type === 'REFUND_ONLY' && scope.row.status === 'APPROVED') || scope.row.status === 'RECEIVED'">
                <el-button size="small" type="warning" @click="handleRefund(scope.row)">退款</el-button>
             </template>
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

    <!-- 拒绝弹窗 -->
    <el-dialog title="拒绝售后申请" v-model="rejectDialogVisible" width="500px">
        <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef">
            <el-form-item label="拒绝原因" prop="comment">
                <el-input v-model="rejectForm.comment" type="textarea" placeholder="请输入拒绝原因" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="rejectDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmReject" :loading="rejectLoading">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  status: ''
})

const rejectDialogVisible = ref(false)
const rejectLoading = ref(false)
const rejectFormRef = ref(null)
const currentId = ref(null)
const rejectForm = reactive({
    comment: ''
})

const rejectRules = {
    comment: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/after-sales', {
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

const handleApprove = (row) => {
    ElMessageBox.confirm('确定同意该售后申请吗？', '提示', {
        type: 'success'
    }).then(async () => {
        await request.post(`/merchant/after-sales/${row.id}/approve`)
        ElMessage.success('操作成功')
        loadData()
    }).catch(() => {})
}

const handleReject = (row) => {
    currentId.value = row.id
    rejectForm.comment = ''
    rejectDialogVisible.value = true
}

const confirmReject = async () => {
    if (!rejectFormRef.value) return
    await rejectFormRef.value.validate(async (valid) => {
        if (valid) {
            rejectLoading.value = true
            try {
                await request.post(`/merchant/after-sales/${currentId.value}/reject`, null, {
                    params: { comment: rejectForm.comment }
                })
                ElMessage.success('已拒绝')
                rejectDialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            } finally {
                rejectLoading.value = false
            }
        }
    })
}

const handleConfirmReceived = (row) => {
    ElMessageBox.confirm('确定已收到退货商品吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        await request.post(`/merchant/after-sales/${row.id}/confirm-received`)
        ElMessage.success('操作成功')
        loadData()
    }).catch(() => {})
}

const handleRefund = (row) => {
    ElMessageBox.confirm('确定执行退款吗？金额将原路退回', '警告', {
        type: 'warning'
    }).then(async () => {
        await request.post(`/merchant/after-sales/${row.id}/refund`)
        ElMessage.success('退款成功')
        loadData()
    }).catch(() => {})
}

const getStatusText = (status) => {
    const map = {
        'PENDING': '待处理',
        'APPROVED': '待退货', // 或待退款(仅退款)
        'RETURNING': '退货中',
        'RECEIVED': '已收货',
        'REFUNDING': '退款中',
        'COMPLETED': '已完成',
        'REJECTED': '已驳回',
        'CANCELLED': '已取消'
    }
    return map[status] || status
}

const getStatusType = (status) => {
    const map = {
        'PENDING': 'warning',
        'APPROVED': 'primary',
        'RETURNING': 'info',
        'RECEIVED': 'primary',
        'COMPLETED': 'success',
        'REJECTED': 'danger'
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
