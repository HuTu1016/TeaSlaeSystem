<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, enableUser, disableUser } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    size: 20,
    keyword: '',
    status: null
})

const loadData = async () => {
    loading.value = true
    try {
        const res = await getUserList(queryParams.value)
        if (res.list) {
            list.value = res.list
            total.value = res.total
        }
    } catch (e) { console.error(e) }
    finally { loading.value = false }
}

const handleSearch = () => {
    queryParams.value.page = 1
    loadData()
}

const handleToggleStatus = async (row) => {
    const action = row.status === 1 ? '禁用' : '启用'
    try {
        await ElMessageBox.confirm(`确定要${action}用户 "${row.nickname||row.username}" 吗?`, '提示', { type: 'warning' })
        if (row.status === 1) await disableUser(row.id)
        else await enableUser(row.id)
        ElMessage.success('操作成功')
        loadData()
    } catch (e) {}
}

onMounted(() => loadData())
</script>

<template>
  <div class="user-list">
    <div class="toolbar">
        <el-input v-model="queryParams.keyword" placeholder="用户名/手机号" style="width: 200px" clearable @change="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="70">
            <template #default="scope">
                <el-avatar :src="scope.row.avatar" :size="40" />
            </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
                <el-button link :type="scope.row.status === 1 ? 'danger' : 'success'" @click="handleToggleStatus(scope.row)">
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
  </div>
</template>

<style scoped>
.user-list { padding: 20px; background: white; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; }
.pagination { margin-top: 20px; text-align: right; }
</style>
