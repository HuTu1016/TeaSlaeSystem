<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, onShelfProduct, offShelfProduct, deleteProduct, getProductStats, getAllCategories } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const stats = ref({})
const categories = ref([])
const queryParams = ref({
    page: 1,
    pageSize: 20,
    status: null,
    categoryId: null,
    keyword: ''
})

const statusOptions = [
    { label: '上架中', value: 'ON_SHELF' },
    { label: '已下架', value: 'OFF_SHELF' },
    { label: '待审核', value: 'PENDING' }
]

const statusMap = {
    ON_SHELF: { label: '上架中', type: 'success' },
    OFF_SHELF: { label: '已下架', type: 'info' },
    PENDING: { label: '待审核', type: 'warning' },
    DELETED: { label: '已删除', type: 'danger' }
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getProductList(queryParams.value)
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
        const res = await getProductStats()
        if (res) stats.value = res
    } catch (e) {}
}

const loadCategories = async () => {
    try {
        const res = await getAllCategories()
        if (res) categories.value = res
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
        categoryId: null,
        keyword: ''
    }
    loadData()
}

const handleOnShelf = async (row) => {
    try {
        await ElMessageBox.confirm('确定要上架该商品吗？', '提示', { type: 'warning' })
        await onShelfProduct(row.id)
        ElMessage.success('上架成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleOffShelf = async (row) => {
    try {
        await ElMessageBox.confirm('确定要下架该商品吗？', '提示', { type: 'warning' })
        await offShelfProduct(row.id)
        ElMessage.success('下架成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定要删除该商品吗？此操作不可恢复！', '警告', { type: 'error' })
        await deleteProduct(row.id)
        ElMessage.success('删除成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

onMounted(() => {
    loadData()
    loadStats()
    loadCategories()
})
</script>

<template>
  <div class="product-list">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-title">全部商品</div>
        <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">上架中</div>
        <div class="stat-value success">{{ stats.onShelfProducts || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已下架</div>
        <div class="stat-value warning">{{ stats.offShelfProducts || 0 }}</div>
      </el-card>
    </div>

    <!-- 搜索栏 -->
    <div class="toolbar">
      <el-input v-model="queryParams.keyword" placeholder="商品名称" style="width: 200px" clearable />
      <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 120px">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="queryParams.categoryId" placeholder="商品分类" clearable style="width: 150px">
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 商品列表 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品图片" width="80">
        <template #default="scope">
          <el-image :src="scope.row.mainImage" :preview-src-list="[scope.row.mainImage]" 
            style="width: 50px; height: 50px;" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="merchantName" label="商家" width="120" />
      <el-table-column label="价格" width="120">
        <template #default="scope">
          <span v-if="scope.row.minPrice === scope.row.maxPrice">¥{{ scope.row.minPrice }}</span>
          <span v-else>¥{{ scope.row.minPrice }} - {{ scope.row.maxPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="salesCount" label="销量" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="statusMap[scope.row.status]?.type" size="small">
            {{ statusMap[scope.row.status]?.label || scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small">详情</el-button>
          <el-button v-if="scope.row.status === 'OFF_SHELF'" link type="success" size="small" @click="handleOnShelf(scope.row)">上架</el-button>
          <el-button v-if="scope.row.status === 'ON_SHELF'" link type="warning" size="small" @click="handleOffShelf(scope.row)">下架</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
.product-list { padding: 20px; background: white; border-radius: 4px; }
.stats-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-title { font-size: 14px; color: #666; margin-bottom: 8px; }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-value.success { color: #67C23A; }
.stat-value.warning { color: #E6A23C; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; flex-wrap: wrap; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
