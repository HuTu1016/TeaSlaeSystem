<template>
  <div class="product-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="商品状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="上架" value="ON_SHELF" />
              <el-option label="下架" value="OFF_SHELF" />
              <el-option label="草稿" value="DRAFT" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="queryForm.keyword" placeholder="商品名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" icon="Plus" @click="$router.push('/products/edit')">发布商品</el-button>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image 
              style="width: 60px; height: 60px" 
              :src="scope.row.mainImage" 
              :preview-src-list="[scope.row.mainImage]"
              fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="minPrice" label="价格" width="120">
             <template #default="scope">
                 ¥{{scope.row.minPrice}} - ¥{{scope.row.maxPrice}}
             </template>
        </el-table-column>
        <el-table-column prop="salesCount" label="销量" width="100" sortable />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="$router.push(`/products/${scope.row.id}/reviews`)">评论</el-button>
            <el-button size="small" @click="$router.push(`/products/edit/${scope.row.id}`)">编辑</el-button>
            <el-button 
              size="small" 
              type="success" 
              v-if="scope.row.status !== 'ON_SHELF'"
              @click="handleStatus(scope.row.id, 'on-shelf')">上架</el-button>
            <el-button 
              size="small" 
              type="warning" 
              v-if="scope.row.status === 'ON_SHELF'"
              @click="handleStatus(scope.row.id, 'off-shelf')">下架</el-button>
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
  status: '',
  keyword: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/products', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        status: queryForm.status || undefined,
        keyword: queryForm.keyword || undefined
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

const resetQuery = () => {
    queryForm.status = ''
    queryForm.keyword = ''
    handleSearch()
}

const handleStatus = async (id, action) => {
    try {
        await request.post(`/merchant/products/${id}/${action}`)
        ElMessage.success('操作成功')
        loadData()
    } catch(e) {}
}

const getStatusText = (status) => {
    const map = { 'ON_SHELF': '已上架', 'OFF_SHELF': '已下架', 'DRAFT': '草稿' }
    return map[status] || status
}

const getStatusType = (status) => {
    const map = { 'ON_SHELF': 'success', 'OFF_SHELF': 'info', 'DRAFT': 'warning' }
    return map[status] || 'info'
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.toolbar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
