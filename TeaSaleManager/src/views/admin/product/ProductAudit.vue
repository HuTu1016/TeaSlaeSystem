<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingProductList, approveProduct, rejectProduct, getProductStats } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const stats = ref({})
const queryParams = ref({
    page: 1,
    pageSize: 20,
    keyword: ''
})

const loadData = async () => {
    loading.value = true
    try {
        const res = await getPendingProductList(queryParams.value)
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

const handleSearch = () => {
    queryParams.value.page = 1
    loadData()
}

const handleReset = () => {
    queryParams.value = { page: 1, pageSize: 20, keyword: '' }
    loadData()
}

const handleApprove = async (row) => {
    try {
        const { value } = await ElMessageBox.prompt('请输入审核备注（可选）', '审核通过', {
            confirmButtonText: '确认通过',
            cancelButtonText: '取消',
            inputPlaceholder: '审核备注'
        })
        await approveProduct(row.id, value)
        ElMessage.success('审核通过成功')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleReject = async (row) => {
    try {
        const { value } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
            confirmButtonText: '确认拒绝',
            cancelButtonText: '取消',
            inputPlaceholder: '拒绝原因（必填）',
            inputValidator: (val) => val ? true : '请输入拒绝原因'
        })
        await rejectProduct(row.id, value)
        ElMessage.success('已拒绝该商品')
        loadData()
        loadStats()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const detailVisible = ref(false)
const currentProduct = ref(null)

const showDetail = (row) => {
    currentProduct.value = row
    detailVisible.value = true
}

onMounted(() => {
    loadData()
    loadStats()
})
</script>

<template>
  <div class="product-audit">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card warning">
        <div class="stat-title">待审核商品</div>
        <div class="stat-value">{{ stats.pendingProducts || 0 }}</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">全部商品</div>
        <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
      </el-card>
      <el-card class="stat-card success">
        <div class="stat-title">上架中</div>
        <div class="stat-value">{{ stats.onShelfProducts || 0 }}</div>
      </el-card>
    </div>

    <!-- 搜索栏 -->
    <div class="toolbar">
      <el-input v-model="queryParams.keyword" placeholder="商品名称" style="width: 200px" clearable />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 待审核列表 -->
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
      <el-table-column label="提交时间" width="160">
        <template #default="scope">
          {{ scope.row.createdAt?.replace('T', ' ').substring(0, 19) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="showDetail(scope.row)">详情</el-button>
          <el-button link type="success" size="small" @click="handleApprove(scope.row)">通过</el-button>
          <el-button link type="danger" size="small" @click="handleReject(scope.row)">拒绝</el-button>
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

    <!-- 商品详情弹窗 -->
    <el-dialog v-model="detailVisible" title="商品详情" width="700px">
      <div v-if="currentProduct" class="product-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
          <el-descriptions-item label="商家">{{ currentProduct.merchantName }}</el-descriptions-item>
          <el-descriptions-item label="商品名称" :span="2">{{ currentProduct.title }}</el-descriptions-item>
          <el-descriptions-item label="副标题" :span="2">{{ currentProduct.subtitle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentProduct.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="价格">
            <span v-if="currentProduct.minPrice === currentProduct.maxPrice">¥{{ currentProduct.minPrice }}</span>
            <span v-else>¥{{ currentProduct.minPrice }} - {{ currentProduct.maxPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="产地">{{ currentProduct.origin || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工艺">{{ currentProduct.process || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="product-image" v-if="currentProduct.mainImage">
          <h4>商品主图</h4>
          <el-image :src="currentProduct.mainImage" :preview-src-list="[currentProduct.mainImage]" 
            style="width: 200px; height: 200px;" fit="cover" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="success" @click="handleApprove(currentProduct); detailVisible = false">审核通过</el-button>
        <el-button type="danger" @click="handleReject(currentProduct); detailVisible = false">审核拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.product-audit { padding: 20px; background: white; border-radius: 4px; }
.stats-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { text-align: center; padding: 15px; }
.stat-card.warning { border-left: 4px solid #E6A23C; }
.stat-card.success { border-left: 4px solid #67C23A; }
.stat-title { font-size: 14px; color: #666; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: bold; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.product-detail { padding: 10px 0; }
.product-image { margin-top: 20px; }
.product-image h4 { margin-bottom: 10px; color: #606266; }
</style>
