<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminArticles, publishArticle, unpublishArticle, deleteArticle } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10,
    type: '',
    status: ''
})

const typeMap = {
    'CULTURE': '茶文化',
    'BREW_TUTORIAL': '冲泡教程',
    'NEWS': '新闻资讯'
}

const statusMap = {
    'DRAFT': { label: '草稿', type: 'info' },
    'PUBLISHED': { label: '已发布', type: 'success' },
    'OFFLINE': { label: '已下架', type: 'warning' }
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getAdminArticles(queryParams.value)
        if (res.items) {
            list.value = res.items
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

const handleEdit = (id) => {
    router.push(`/admin/contents/edit?id=${id}`)
}

const handleCreate = () => {
    router.push('/admin/contents/create')
}

const handleTogglePublish = async (row) => {
    const action = row.status === 'PUBLISHED' ? '下架' : '发布'
    try {
        await ElMessageBox.confirm(`确定要${action}文章 "${row.title}" 吗?`, '提示')
        if (row.status === 'PUBLISHED') {
            await unpublishArticle(row.id)
        } else {
            await publishArticle(row.id)
        }
        ElMessage.success(`${action}成功`)
        loadData()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(`确定要删除文章 "${row.title}" 吗?`, '警告', { type: 'warning' })
        await deleteArticle(row.id)
        ElMessage.success('删除成功')
        loadData()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

onMounted(() => {
    loadData()
})
</script>

<template>
  <div class="content-list">
    <div class="toolbar">
        <el-select v-model="queryParams.type" placeholder="文章类型" clearable style="width: 150px" @change="handleSearch">
            <el-option v-for="(label, val) in typeMap" :key="val" :label="label" :value="val" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已下架" value="OFFLINE" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleCreate">新建文章</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="120">
            <template #default="scope">
                {{ typeMap[scope.row.type] || scope.row.type }}
            </template>
        </el-table-column>
        <el-table-column label="封面" width="100">
            <template #default="scope">
                <el-image v-if="scope.row.coverImage" :src="scope.row.coverImage" style="width: 60px; height: 40px" fit="cover" />
            </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="statusMap[scope.row.status]?.type">
                    {{ statusMap[scope.row.status]?.label || scope.row.status }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row.id)">编辑</el-button>
                <el-button 
                    link 
                    :type="scope.row.status === 'PUBLISHED' ? 'warning' : 'success'"
                    @click="handleTogglePublish(scope.row)"
                >
                    {{ scope.row.status === 'PUBLISHED' ? '下架' : '发布' }}
                </el-button>
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
  </div>
</template>

<style scoped>
.content-list { padding: 20px; background: white; border-radius: 4px; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
