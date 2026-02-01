<template>
  <div class="user-footprints">
    <div class="page-header">
      <h2>我的足迹</h2>
      <el-button type="info" link @click="handleClearAll" :disabled="products.length === 0">
        清空足迹
      </el-button>
    </div>

    <div class="footprints-list" v-loading="loading">
      <el-empty v-if="!loading && products.length === 0" description="暂无浏览记录" />
      
      <div v-else class="product-grid">
        <div v-for="item in products" :key="item.id" class="product-item">
          <div class="image-wrapper" @click="goToDetail(item.id)">
            <img :src="item.mainImage || 'https://placeholder.com/200'" :alt="item.title" />
          </div>
          <div class="info">
            <h3 class="title" @click="goToDetail(item.id)">{{ item.title }}</h3>
            <div class="price-row">
              <span class="price">¥{{ item.minPrice }}</span>
              <el-button type="danger" link size="small" @click="handleRemove(item.id)">删除</el-button>
            </div>
            <div class="view-time" v-if="item.viewedAt">
              浏览时间: {{ formatTime(item.viewedAt) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFootprints, removeFootprint, clearFootprints } from '@/api/favorite'

const router = useRouter()
const loading = ref(false)
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadData = async () => {
    loading.value = true
    try {
        const res = await getFootprints({
            page: currentPage.value,
            pageSize: pageSize.value
        })
        products.value = res.items || []
        total.value = res.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleRemove = (id) => {
    ElMessageBox.confirm('确定要删除这条足迹吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await removeFootprint(id)
            ElMessage.success('已删除')
            loadData() // Reload list
        } catch (e) {
            console.error(e)
        }
    }).catch(() => {})
}

const handleClearAll = () => {
    ElMessageBox.confirm('确定要清空所有浏览记录吗?', '警示', {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await clearFootprints()
            ElMessage.success('已清空足迹')
            loadData() // Reload list
        } catch (e) {
            console.error(e)
        }
    }).catch(() => {})
}

const goToDetail = (id) => {
    router.push(`/products/${id}`)
}

const formatTime = (timeStr) => {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.user-footprints {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-item {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  transition: all 0.3s;
  background: white;
}

.product-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.image-wrapper {
  height: 180px;
  overflow: hidden;
  cursor: pointer;
  background: #f9f9f9;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info {
  padding: 12px;
}

.title {
  font-size: 14px;
  color: #333;
  margin: 0 0 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.title:hover {
  color: var(--vt-c-tea-green-dark);
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.view-time {
  font-size: 12px;
  color: #ccc;
  text-align: right;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
