<template>
  <div class="product-list-container">
    <div class="filter-sidebar">
      <div class="filter-group">
        <h3 class="filter-title">价格区间</h3>
        <div class="price-filter">
          <el-slider v-model="priceRange" range :max="2000" @change="handleFilter" :show-tooltip="false" class="custom-slider" />
          <div class="price-labels">
            <span>¥{{ priceRange[0] }}</span>
            <span>¥{{ priceRange[1] }}</span>
          </div>
        </div>
      </div>

    <div class="filter-group">
      <div class="filter-header">
        <h3 class="filter-title">茶类精选</h3>
        <el-button link type="info" size="small" @click="resetFilters" class="reset-btn" :icon="Refresh">重置筛选</el-button>
      </div>
      <el-tree 
        ref="categoryTreeRef"
        :data="categories" 
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        @node-click="handleCategoryClick"
        highlight-current
        class="category-tree"
      />
    </div>
  </div>

  <div class="product-main">
    <!-- Toolbox -->
    <div class="toolbox">
      <div class="sort-group">
        <el-radio-group v-model="sort" size="default" @change="handleFilter" class="custom-radio">
          <el-radio-button label="relevance">综合</el-radio-button>
          <el-radio-button label="sales_desc">销量</el-radio-button>
          <el-radio-button label="price_asc">价格↑</el-radio-button>
          <el-radio-button label="price_desc">价格↓</el-radio-button>
          <el-radio-button label="newest">新品</el-radio-button>
        </el-radio-group>
      </div>
      <!-- Right side toolbox items if needed -->
    </div>

    <!-- Grid -->
    <div class="product-grid" v-if="products.length > 0" v-loading="loading">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
    <el-empty v-else description="暂无符合条件的商品" />

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import ProductCard from '@/components/ProductCard.vue'
import { getProductList, getCategories } from '@/api/product'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const products = ref([])
const total = ref(0)
const pageSize = ref(12)
const currentPage = ref(1)
const categoryTreeRef = ref(null)

// Filters
const keyword = ref(route.query.keyword || '')
const categoryId = ref(route.query.categoryId || null)
const priceRange = ref([0, 2000])
// 从 URL 参数读取排序方式，支持 sales_desc 和 newest
const sort = ref(route.query.sort || 'relevance')

const categories = ref([])

const loadCategories = async () => {
    try {
        const res = await getCategories()
        if (res) categories.value = res
    } catch (e) {
        console.error(e)
    }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      categoryId: categoryId.value,
      minPrice: priceRange.value[0],
      maxPrice: priceRange.value[1],
      sort: sort.value
    }

    const res = await getProductList(params)
    if (res && res.items) {
      products.value = res.items.map(p => ({
        ...p,
        image: p.mainImage || p.image
      }))
      total.value = res.total
    }
  } catch (e) {
    console.error(e)
    products.value = []
  } finally {
    loading.value = false
  }
}

const handleCategoryClick = (data) => {
  categoryId.value = data.id
  handleFilter()
}

const handleFilter = () => {
  currentPage.value = 1
  window.scrollTo({ top: 0, behavior: 'smooth' })
  loadProducts()
}

const resetFilters = () => {
  // 重置所有状态
  categoryId.value = null
  priceRange.value = [0, 2000]
  keyword.value = ''
  sort.value = 'relevance'
  currentPage.value = 1
  
  // 清除URL参数
  router.push({ query: {} })
  
  // 清除树选中状态
  if (categoryTreeRef.value) {
    categoryTreeRef.value.setCurrentKey(null)
  }
  
  // 重新加载
  loadProducts()
}

const handlePageChange = (val) => {
  loadProducts()
}

onMounted(() => {
  loadCategories()
  loadProducts()
})

watch(() => route.query.keyword, (newVal) => {
    keyword.value = newVal || ''
    handleFilter()
})
</script>

<style scoped>
.product-list-container {
  display: flex;
  gap: 50px;
  max-width: 1400px; /* Match header width */
  margin: 0 auto;
  padding: 40px;
  background-color: #fcfbf7; /* Consistent global background */
}

.filter-sidebar {
  width: 240px;
  flex-shrink: 0;
}

.filter-group {
  margin-bottom: 50px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.filter-title {
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 0;
  letter-spacing: 1px;
}

.reset-btn {
  padding: 0;
  font-size: 13px;
  color: #999;
}

.reset-btn:hover {
  color: var(--vt-c-tea-green-dark);
}

.category-tree {
  background: transparent;
  font-family: var(--font-body);
  color: #666;
}

:deep(.el-tree-node__content) {
  height: 40px;
  transition: all 0.3s;
}

:deep(.el-tree-node__content:hover) {
  background-color: transparent;
  color: var(--vt-c-tea-green-dark);
  padding-left: 5px !important; /* Subtle shift */
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: transparent;
  color: var(--vt-c-tea-green-dark);
  font-weight: bold;
}

.price-filter {
  padding: 0 5px;
}

.price-labels {
  display: flex; 
  justify-content: space-between; 
  margin-top: 15px;
  color: #999;
  font-size: 12px;
}

/* Custom Slider */
.custom-slider :deep(.el-slider__bar) {
  background-color: #333;
}

.custom-slider :deep(.el-slider__button) {
  border-color: #333;
  width: 14px;
  height: 14px;
}

.custom-slider :deep(.el-slider__runway) {
  background-color: #e0e0e0;
}

.product-main {
  flex: 1;
}

.toolbox {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* Custom Radio Group (Text Only) */
.custom-radio :deep(.el-radio-button__inner) {
  border: none;
  background: transparent;
  box-shadow: none !important;
  color: #999;
  padding: 5px 15px;
  font-size: 14px;
  transition: color 0.3s;
}

.custom-radio :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  color: #fff;
  background-color: #2c4c3b; /* Selected pill style */
  border-radius: 4px; 
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.pagination {
  margin-top: 60px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: var(--vt-c-tea-green-dark);
}
</style>
