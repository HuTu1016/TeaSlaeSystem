<template>
  <div class="user-favorites">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>

    <div class="favorites-list" v-loading="loading">
      <el-empty v-if="!loading && products.length === 0" description="暂无收藏商品" />
      
      <div v-else class="product-grid">
        <div v-for="item in products" :key="item.id" class="product-item">
          <div class="image-wrapper" @click="goToDetail(item.id)">
            <img :src="item.mainImage || 'https://placeholder.com/200'" :alt="item.title" />
          </div>
          <div class="info">
            <h3 class="title" @click="goToDetail(item.id)">{{ item.title }}</h3>
            <p class="subtitle">{{ item.subtitle }}</p>
            <div class="price-row">
              <span class="price">¥{{ item.minPrice }}</span>
              <el-button type="danger" link size="small" @click="handleRemove(item.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavorites, removeFavorite } from '@/api/favorite'

const router = useRouter()
const loading = ref(false)
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
    loading.value = true
    try {
        const res = await getFavorites({
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

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    loadData()
}

const handleRemove = (id) => {
    ElMessageBox.confirm('确定要取消收藏该商品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await removeFavorite(id)
            ElMessage.success('已取消收藏')
            loadData() // Reload list
        } catch (e) {
            console.error(e)
        }
    }).catch(() => {})
}

const goToDetail = (id) => {
    router.push(`/products/${id}`)
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.user-favorites {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  font-size: 20px;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
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
  transform: translateY(-2px);
}

.image-wrapper {
  height: 200px;
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
  padding: 15px;
}

.title {
  font-size: 16px;
  color: #333;
  margin: 0 0 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.title:hover {
  color: var(--vt-c-tea-green-dark);
}

.subtitle {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
