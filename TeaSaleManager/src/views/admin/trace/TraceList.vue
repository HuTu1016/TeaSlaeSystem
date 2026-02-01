<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTraceList, approveTrace, rejectTrace } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10,
    status: ''
})

const rejectDialogVisible = ref(false)
const currentTrace = ref(null)
const rejectReason = ref('')

const loadData = async () => {
    loading.value = true
    try {
        const res = await getTraceList(queryParams.value)
        if (res.items) {
            list.value = res.items
            total.value = res.total
        }
    } catch (e) { console.error(e) }
    finally { loading.value = false }
}

const handleApprove = async (row) => {
    try {
        await approveTrace(row.id)
        ElMessage.success('审核通过')
        loadData()
    } catch (e) {}
}

const openReject = (row) => {
    currentTrace.value = row
    rejectReason.value = ''
    rejectDialogVisible.value = true
}

const submitReject = async () => {
    try {
        await rejectTrace(currentTrace.value.id, rejectReason.value)
        ElMessage.success('已拒绝')
        rejectDialogVisible.value = false
        loadData()
    } catch (e) {}
}

onMounted(() => loadData())
</script>

<template>
  <div class="trace-list">
    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="batchNo" label="批次号" />
        <el-table-column prop="origin" label="产地" />
        <el-table-column prop="pickDate" label="采摘日期" />
        <el-table-column label="状态" width="100">
            <template #default="scope">{{ scope.row.status }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
                <div v-if="scope.row.status === 'PENDING'">
                    <el-button link type="success" @click="handleApprove(scope.row)">通过</el-button>
                    <el-button link type="danger" @click="openReject(scope.row)">拒绝</el-button>
                </div>
            </template>
        </el-table-column>
    </el-table>
     <div class="pagination">
        <el-pagination 
            v-model:current-page="queryParams.page" 
            v-model:page-size="queryParams.pageSize" 
            :total="total" 
            layout="total, prev, pager, next" 
            @current-change="loadData"
        />
    </div>
    <el-dialog v-model="rejectDialogVisible" title="拒绝申请" width="400px">
        <el-input v-model="rejectReason" type="textarea" placeholder="拒绝原因" />
        <template #footer>
            <el-button @click="rejectDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitReject">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.trace-list { padding: 20px; background: white; }
.pagination { margin-top: 20px; text-align: right; }
</style>
