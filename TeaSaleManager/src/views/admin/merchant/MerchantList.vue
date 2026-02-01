<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantList, auditMerchant, enableMerchant, disableMerchant } from '@/api/admin'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    size: 20,
    keyword: '',
    status: null
})

const auditDialogVisible = ref(false)
const currentMerchant = ref(null)
const auditReason = ref('')

const statusMap = {
    0: { label: '禁用', type: 'danger' },
    1: { label: '正常', type: 'success' },
    2: { label: '待审核', type: 'warning' },
    3: { label: '审核拒绝', type: 'info' }
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getMerchantList(queryParams.value)
        if (res.list) {
            list.value = res.list
            total.value = res.total
        } else {
            list.value = []
            total.value = 0
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    queryParams.value.page = 1
    loadData()
}

const handleAudit = (row) => {
    currentMerchant.value = row
    auditReason.value = ''
    auditDialogVisible.value = true
}

const submitAudit = async (passed) => {
    try {
        const status = passed ? 1 : 3 // 1: Pass, 3: Reject
        await auditMerchant(currentMerchant.value.id, status, auditReason.value)
        ElMessage.success(passed ? '审核通过' : '审核拒绝')
        auditDialogVisible.value = false
        loadData()
    } catch (e) {
        console.error(e)
    }
}

const toggleStatus = async (row) => {
    const action = row.status === 1 ? '禁用' : '启用'
    try {
        await ElMessageBox.confirm(`确定要${action}该商家吗?`, '提示', { type: 'warning' })
        if (row.status === 1) {
            await disableMerchant(row.id)
        } else {
            await enableMerchant(row.id)
        }
        ElMessage.success(`${action}成功`)
        loadData()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

// 跳转到商家详情
const goToDetail = (row) => {
    router.push(`/admin/merchants/${row.id}`)
}

onMounted(() => {
    loadData()
})
</script>

<template>
  <div class="merchant-list">
    <div class="toolbar">
        <el-input 
            v-model="queryParams.keyword" 
            placeholder="搜索店铺名称/商家姓名" 
            style="width: 200px" 
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
            <el-option label="正常" :value="1" />
            <el-option label="待审核" :value="2" />
            <el-option label="已禁用" :value="0" />
            <el-option label="审核拒绝" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="shopName" label="店铺名称" min-width="150" />
        <el-table-column prop="contactName" label="商家姓名" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="statusMap[scope.row.status]?.type">
                    {{ statusMap[scope.row.status]?.label }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
            <template #default="scope">
                <el-button 
                    type="primary" 
                    link 
                    @click="goToDetail(scope.row)"
                >
                    查看详情
                </el-button>
                <el-button 
                    v-if="scope.row.status === 2" 
                    type="success" 
                    link 
                    @click="handleAudit(scope.row)"
                >
                    审核
                </el-button>
                <el-button 
                    v-if="scope.row.status === 1 || scope.row.status === 0"
                    :type="scope.row.status === 1 ? 'danger' : 'success'"
                    link 
                    @click="toggleStatus(scope.row)"
                >
                    {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
            </template>
        </el-table-column>
    </el-table>

    <div class="pagination">
        <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadData"
        />
    </div>

    <el-dialog v-model="auditDialogVisible" title="商家入驻审核" width="500px">
        <p>当前商家：{{ currentMerchant?.shopName }}</p>
        <el-input
            v-model="auditReason"
            type="textarea"
            placeholder="请输入拒绝原因（通过可不填）"
            rows="3"
        />
        <template #footer>
            <el-button type="danger" @click="submitAudit(false)">拒绝</el-button>
            <el-button type="success" @click="submitAudit(true)">通过</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchant-list { padding: 20px; background: white; border-radius: 4px; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
