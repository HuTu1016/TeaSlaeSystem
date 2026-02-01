<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingReviews, getAllReviews, auditReview, deleteReview } from '@/api/admin'

const activeTab = ref('pending')
const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10
})

const auditDialogVisible = ref(false)
const currentReview = ref(null)
const auditReason = ref('')

const loadData = async () => {
    loading.value = true
    try {
        let res
        if (activeTab.value === 'pending') {
            res = await getPendingReviews(queryParams.value)
        } else {
            res = await getAllReviews(queryParams.value)
        }
        if (res.list) {
            list.value = res.list
            total.value = res.total
        }
    } catch (e) { console.error(e) }
    finally { loading.value = false }
}

const handleTabChange = () => {
    queryParams.value.page = 1
    loadData()
}

const openAudit = (row) => {
    currentReview.value = row
    auditReason.value = ''
    auditDialogVisible.value = true
}

const submitAudit = async (passed) => {
    try {
        const status = passed ? 1 : 2 // 1: Pass, 2: Reject
        await auditReview(currentReview.value.id, { status, reason: auditReason.value })
        ElMessage.success('审核完成')
        auditDialogVisible.value = false
        loadData()
    } catch (e) {}
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定删除评论?', '警告')
        await deleteReview(row.id)
        ElMessage.success('删除成功')
        loadData()
    } catch (e) {}
}

onMounted(() => loadData())
</script>

<template>
  <div class="review-list">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核评论" name="pending"></el-tab-pane>
        <el-tab-pane label="全部评论" name="all"></el-tab-pane>
    </el-tabs>

    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productTitle" label="商品" show-overflow-tooltip />
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column label="评分" width="80">
            <template #default="scope">{{ scope.row.star }}星</template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag>{{ scope.row.status === 0 ? '待审核' : (scope.row.status === 1 ? '通过' : '拒绝') }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
                <el-button v-if="scope.row.status === 0" link type="primary" @click="openAudit(scope.row)">审核</el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog v-model="auditDialogVisible" title="审核评论" width="400px">
        <p>评论内容：{{ currentReview?.content }}</p>
        <el-input v-model="auditReason" type="textarea" placeholder="请输入拒绝原因 (通过可不填)" />
        <template #footer>
            <el-button type="danger" @click="submitAudit(false)">拒绝</el-button>
            <el-button type="success" @click="submitAudit(true)">通过</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.review-list { padding: 20px; background: white; }
.pagination { margin-top: 20px; text-align: right; }
</style>
