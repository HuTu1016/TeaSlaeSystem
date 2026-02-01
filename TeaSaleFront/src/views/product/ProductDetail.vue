<template>
  <div class="product-detail" v-if="product">
    <!-- Breadcrumb -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">茶叶商城</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- Main Layout -->
    <div class="product-container">
      <!-- Left Column: Details Text -->
      <div class="column-details">
         <div class="details-text-list">
            <div class="dt-section-header">PRODUCT DETAILS</div>
            <div class="detail-block">
                <h3>茶品介绍</h3>
                <p>{{ product.subtitle }}</p>
            </div>
            <div class="detail-block">
                <h3>冲泡方法</h3>
                <p>{{ product.brewMethod }}</p>
            </div>
            <div class="detail-block">
                <h3>口感描述</h3>
                <p>{{ product.taste }}</p>
            </div>
            <div class="detail-block">
                <h3>产地环境</h3>
                <p>{{ product.environment }}</p>
            </div>
            
            <div class="detail-block" v-if="traceInfo">
                <h3>品质溯源</h3>
                <div class="trace-box-simple" @click="viewTrace(traceInfo.traceCode)">
                    <div class="trace-content">
                        <span class="label">溯源码</span>
                        <span class="code">{{ traceInfo.traceCode }}</span>
                    </div>
                    <div class="trace-action">
                       查看溯源档案 <el-icon><ArrowRight /></el-icon>
                    </div>
                </div>
            </div>
         </div>
      </div>

      <!-- Center Column: Info + Gallery -->
      <div class="column-center-main">
         <div class="info-main-split">
            <!-- Info Side -->
            <div class="info-side">
                <h1 class="title">{{ product.title }}</h1>
                <div class="price-box">
                    <span class="currency">¥</span>
                    <span class="price">{{ currentSku ? currentSku.price : product.minPrice }}</span>
                    <span class="sales">月销 {{ product.salesCount }}+</span>
                </div>

                <div class="sku-selector">
                    <div class="sku-row">
                        <span class="label">规格</span>
                        <div class="options">
                        <div 
                            v-for="sku in product.skus" 
                            :key="sku.id"
                            class="sku-option"
                            :class="{ active: currentSku?.id === sku.id }"
                            @click="selectSku(sku)"
                        >
                            {{ sku.skuName }}
                        </div>
                        </div>
                    </div>
                    <div class="sku-row">
                        <span class="label">数量</span>
                        <el-input-number v-model="quantity" :min="1" :max="currentSku?.stock || 99" size="small" />
                        <span class="stock" v-if="currentSku"> (库存: {{ currentSku.stock }})</span>
                    </div>
                </div>

                <div class="actions">
                    <button class="btn-primary" @click="handleAddToCart">
                        <el-icon><ShoppingCart /></el-icon> 加入购物车
                    </button>
                    <button class="btn-checkout" @click="handleCheckout">
                        去结算
                    </button>
                    <button class="btn-icon" :class="{ active: isFavorite }" @click="toggleFavorite" title="收藏">
                        <el-icon v-if="isFavorite"><StarFilled /></el-icon>
                        <el-icon v-else><Star /></el-icon>
                    </button>
                </div>
            </div>

            <!-- Gallery Side (Right Red Box Area) -->
            <div class="gallery-side">
                <div class="gallery">
                    <el-image 
                    :src="currentImage" 
                    fit="cover" 
                    class="main-image"
                    :preview-src-list="[currentImage]"
                    >
                    <template #error>
                        <div class="image-slot">
                        <el-icon><Picture /></el-icon>
                        <span>暂无图片</span>
                        </div>
                    </template>
                    </el-image>
                    <div class="thumbnails" v-if="product.images && product.images.length > 1">
                    <img 
                        v-for="(img, idx) in product.images" 
                        :key="idx" 
                        :src="img.url" 
                        :class="{ active: currentImage === img.url }"
                        @click="currentImage = img.url"
                    />
                    </div>
                </div>
            </div>
         </div>
      </div>

      <!-- Right Column: Sidebar -->
      <div class="column-right">
          <div class="sidebar-sticky">
             <div class="sidebar-header">
                 <h3>猜你喜欢</h3>
                 <span class="eng">Recommend</span>
             </div>
             
             <div class="sidebar-list">
                 <div v-for="item in similarProducts" :key="item.id" class="side-card" @click="goToProduct(item.id)">
                     <div class="side-img">
                         <img :src="item.mainImage" :alt="item.title">
                     </div>
                     <div class="side-info">
                         <div class="side-title">{{ item.title }}</div>
                         <div class="side-price">¥{{ item.minPrice }}</div>
                     </div>
                 </div>
             </div>
          </div>
      </div>
    </div>

    <!-- 用户评论区域 -->
    <div class="reviews-section">
      <div class="reviews-header">
        <h2>用户评价</h2>
        <span class="review-count" v-if="reviewData.total > 0">共 {{ reviewData.total }} 条评价</span>
        <span class="review-count" v-else>暂无评价</span>
      </div>

      <div class="reviews-list" v-if="reviewData.list && reviewData.list.length">
        <div class="review-item" v-for="review in reviewData.list" :key="review.id">
          <div class="review-user">
            <el-avatar :src="review.userAvatar" :size="40" />
            <div class="user-info">
              <span class="username">{{ review.anonymous ? '匿名用户' : review.userName }}</span>
              <span class="review-time">{{ formatReviewTime(review.createdAt) }}</span>
            </div>
          </div>
          <div class="review-content">
            <el-rate v-model="review.rating" disabled :colors="['#ffd21e', '#ffd21e', '#ffd21e']" />
            <p class="review-text">{{ review.content }}</p>
            <div class="review-images" v-if="review.images && review.images.length">
              <el-image 
                v-for="(img, idx) in review.images" 
                :key="idx"
                :src="img" 
                :preview-src-list="review.images"
                fit="cover"
                class="review-img"
              />
            </div>
            <!-- 商家回复（支持多级回复）-->
            <div class="merchant-replies" v-if="review.replies && review.replies.length">
              <div class="merchant-reply" v-for="reply in review.replies" :key="reply.id">
                <span class="reply-label">商家回复：</span>
                <span class="reply-content">{{ reply.content }}</span>
                <span class="reply-time">{{ formatReviewTime(reply.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="reviewData.total > reviewData.list?.length">
        <el-button @click="loadMoreReviews" :loading="reviewLoading" text type="primary">
          查看更多评价
        </el-button>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!reviewLoading && (!reviewData.list || reviewData.list.length === 0)" description="该商品暂无评价" :image-size="100" />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, ShoppingCart, Star, StarFilled, ArrowRight } from '@element-plus/icons-vue'
import { getProductDetail, getSimilarProducts } from '@/api/product'
import { getCart, addToCart, updateCartItem } from '@/api/cart'
import { checkFavorite, addFavorite, removeFavorite } from '@/api/favorite'
import { getTraceByProductId } from '@/api/trace'
import { getProductReviews } from '@/api/review'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const currentImage = ref('')
const currentSku = ref(null)
const quantity = ref(1)
const isFavorite = ref(false)
const similarProducts = ref([])
const cartItems = ref([])
const cartItemId = ref(null)
const traceInfo = ref(null)
const reviewData = ref({ list: [], total: 0 })
const reviewLoading = ref(false)
const reviewPage = ref(1)

const loadProduct = async () => {
    const id = route.params.id
    try {
        const res = await getProductDetail(id)
        
        if (res.images && res.images.length) {
            res.images = res.images.map(img => typeof img === 'string' ? { url: img } : img)
            currentImage.value = res.images[0].url
        } else if (res.mainImage) {
             currentImage.value = res.mainImage
             res.images = [{ url: res.mainImage }]
        }
        
        product.value = res
        if (res.skus && res.skus.length) selectSku(res.skus[0])
        
        checkIsFavorite(id)
        loadSimilarProducts(id)
        loadTraceInfo(id) // 加载溯源信息
        loadReviews(id) // 加载评论
        loadCart() // Sync cart
    } catch (e) {
        console.error(e)
    }
}

const loadCart = async () => {
    try {
        const res = await getCart()
        cartItems.value = res.items || []
        updateQuantityFromCart()
    } catch (e) {
        console.error(e)
    }
}

const updateQuantityFromCart = () => {
    if (!currentSku.value || !cartItems.value.length) {
        cartItemId.value = null
        quantity.value = 1
        return
    }
    
    const item = cartItems.value.find(i => i.skuId === currentSku.value.id)
    if (item) {
        quantity.value = item.quantity
        cartItemId.value = item.id
    } else {
        quantity.value = 1
        cartItemId.value = null
    }
}

const loadSimilarProducts = async (id) => {
    try {
        const res = await getSimilarProducts(id, 6) 
        similarProducts.value = res
    } catch (e) {
        console.error(e)
    }
}

const loadTraceInfo = async (productId) => {
    try {
        const res = await getTraceByProductId(productId)
        if (res) {
            traceInfo.value = res
        }
    } catch (e) {
        // 商品可能没有溯源信息，忽略错误
        console.log('No trace info for this product')
    }
}

const checkIsFavorite = async (id) => {
    try {
        const res = await checkFavorite(id)
        isFavorite.value = res
    } catch (e) {
    }
}

const toggleFavorite = async () => {
    if (!product.value) return
    const id = product.value.id
    try {
        if (isFavorite.value) {
            await removeFavorite(id)
            isFavorite.value = false
            ElMessage.success('已取消收藏')
        } else {
            await addFavorite(id)
            isFavorite.value = true
            ElMessage.success('已添加收藏')
        }
    } catch (e) {
        if (e.response && e.response.status === 401) {
            ElMessage.warning('请先登录')
        }
    }
}

const selectSku = (sku) => {
    currentSku.value = sku
    updateQuantityFromCart()
}

const handleAddToCart = async () => {
    if (!currentSku.value) return ElMessage.warning('请选择规格')
    try {
        if (cartItemId.value) {
            // Update existing interactive item
            await updateCartItem(cartItemId.value, {
                quantity: quantity.value
            })
            ElMessage.success('购物车已更新')
        } else {
            // Add new item
            await addToCart({
                skuId: currentSku.value.id,
                quantity: quantity.value
            })
            ElMessage.success('已加入购物车')
        }
        loadCart() // Refresh status
    } catch(e) {
        console.error(e)
    }
}

const handleCheckout = () => {
    // 如果没有登录，先提示登录？（或者 router 的守卫会处理）
    // 检查购物车
    if (!cartItems.value || cartItems.value.length === 0) {
        ElMessage.warning('购物车还没有加入商品')
        return
    }
    router.push('/cart')
}

const buyNow = () => {
    handleAddToCart()
}

const goToProduct = (id) => {
    router.push(`/products/${id}`)
}

const viewTrace = (code) => {
    router.push({ path: '/trace', query: { code } })
}

// 加载商品评论
const loadReviews = async (productId) => {
    reviewLoading.value = true
    try {
        const res = await getProductReviews(productId, { page: 1, size: 5 })
        reviewData.value = {
            list: res.list || [],
            total: res.total || 0
        }
        reviewPage.value = 1
    } catch (e) {
        console.error('加载评论失败:', e)
    } finally {
        reviewLoading.value = false
    }
}

// 加载更多评论
const loadMoreReviews = async () => {
    if (!product.value) return
    reviewLoading.value = true
    try {
        reviewPage.value++
        const res = await getProductReviews(product.value.id, { page: reviewPage.value, size: 5 })
        if (res.list && res.list.length) {
            reviewData.value.list.push(...res.list)
        }
    } catch (e) {
        console.error('加载更多评论失败:', e)
    } finally {
        reviewLoading.value = false
    }
}

// 格式化评论时间
const formatReviewTime = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

const watchRoute = watch(() => route.params.id, (newId) => {
    if (newId) {
        loadProduct()
        window.scrollTo(0, 0)
    }
})

onMounted(() => {
    loadProduct()
})
</script>

<style scoped>
.product-detail {
    padding-bottom: 80px;
    background-color: #fcfcfc;
    min-height: 100vh;
}

.breadcrumb {
  padding: 20px 0;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color: #999;
  max-width: 1400px;
  margin: 0 auto;
  padding-left: 20px;
  padding-right: 20px;
}

/* 3-Column Layout */
.product-container {
    display: flex;
    justify-content: space-between;
    max-width: 1500px; /* Wider container */
    margin: 0 auto;
    padding: 0 40px;
    align-items: flex-start;
    gap: 60px; /* Large gaps */
}

.column-details {
    width: 200px; /* Left column width */
    flex-shrink: 0;
    padding-top: 10px;
}

.column-center-main {
    flex: 1; /* Center column takes majority space */
    min-width: 0;
}

.column-right {
    width: 240px; /* Sidebar width */
    flex-shrink: 0;
}

/* Details Text Styling */
.dt-section-header {
    color: #42b983;
    font-size: 11px;
    font-weight: bold;
    letter-spacing: 1px;
    margin-bottom: 20px;
    text-transform: uppercase;
    border-bottom: 2px solid #42b983;
    padding-bottom: 5px;
    display: inline-block;
}

.detail-block {
    margin-bottom: 30px;
}

.detail-block h3 {
    font-size: 15px;
    color: #333;
    margin-bottom: 8px;
    font-family: 'Georgia', serif;
    font-weight: 600;
}

.detail-block p {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    margin: 0;
}

.trace-box-simple {
    font-size: 12px;
    color: #666;
    background: #f9f9f9;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #eee;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.trace-box-simple:hover {
    background: #f0fdf4;
    border-color: #10b981;
}

.trace-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.trace-content .label {
    font-size: 10px;
    color: #999;
}

.trace-content .code {
    font-family: monospace;
    font-weight: bold;
    color: #333;
}

.trace-action {
    display: flex;
    align-items: center;
    gap: 2px;
    color: #10b981;
    font-size: 11px;
}


/* Center Info Split */
.info-main-split {
    display: flex;
    gap: 40px; /* Use gap instead of space-between to keep content together */
    align-items: flex-start;
    background: white;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.info-side {
    flex: 1;
    padding-right: 40px;
    min-width: 300px; /* Ensure enough space for SKU options */
}

.gallery-side {
    width: 280px; /* Fixed small width for image */
    flex-shrink: 0;
}

/* Gallery Styling */
.main-image {
  width: 100%;
  height: 280px;
  border-radius: 4px;
  margin-bottom: 15px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08); /* Stronger shadow */
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f8f8f8;
  color: #c0c4cc;
}

.thumbnails {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}
.thumbnails img {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
    cursor: pointer;
    opacity: 0.6;
    transition: all 0.2s;
}
.thumbnails img.active,
.thumbnails img:hover {
    opacity: 1;
    border: 1px solid #42b983;
}

/* Info Styling */
.title {
  margin: 0 0 20px;
  font-family: 'Georgia', serif;
  font-size: 32px;
  color: #2c3e50;
  font-weight: 600;
}

.price-box {
  margin-bottom: 30px;
  display: flex;
  align-items: baseline;
}

.currency {
  color: #cf4444;
  font-size: 24px;
  margin-right: 4px;
}

.price {
  color: #cf4444;
  font-size: 48px; /* Large price */
  font-weight: bold;
  font-family: 'Lato', sans-serif;
  margin-right: 20px;
}

.sales {
    color: #999;
    font-size: 14px;
}

.sku-selector {
  margin-bottom: 30px;
}

.sku-row {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.label {
  width: 60px;
  color: #999;
  font-size: 14px;
}

.options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sku-option {
  border: 1px solid #ddd;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
  border-radius: 20px; /* Pill */
  font-size: 14px;
}

.sku-option:hover {
  border-color: #42b983;
  color: #42b983;
}

.sku-option.active {
  background-color: #42b983; /* Tea green - hardcoded fallback */
  color: white;
  border-color: #42b983;
}

.style-stock {
    font-size: 12px;
    color: #ccc;
    margin-left: 10px;
}

.actions {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.btn-primary {
  flex: 1;
  max-width: 200px;
  height: 50px;
  border: none;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 4px; /* Square button */
  background: #333; /* Dark Premium */
  color: white;
}

.btn-primary:hover {
  background: black;
}

.btn-checkout {
  flex: 1;
  max-width: 200px;
  height: 50px;
  border: none;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  background: #2c4c3b;
  color: white;
}

.btn-checkout:hover {
  background: #1e3529;
}

.btn-icon {
  width: 50px;
  height: 50px;
  border: 1px solid #eee;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #999;
  border-radius: 4px;
}

.btn-icon.active {
    color: #e6a23c;
    border-color: #e6a23c;
}

/* Sidebar - Right Column */
.sidebar-sticky {
    position: sticky;
    top: 20px;
    background: transparent; /* Clean */
}

.sidebar-header {
    margin-bottom: 20px;
}

.sidebar-header h3 {
    font-size: 14px;
    color: #999;
    font-weight: bold;
    margin: 0;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.sidebar-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.side-card {
    display: flex;
    gap: 12px;
    cursor: pointer;
    transition: all 0.3s;
}

.side-card:hover .side-title {
    color: var(--vt-c-tea-green-dark);
}

.side-img {
    width: 60px;
    height: 60px;
    flex-shrink: 0;
    border-radius: 2px;
    overflow: hidden;
    background: #f5f5f5;
}

.side-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.side-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.side-title {
    font-size: 13px;
    color: #333;
    line-height: 1.4;
    margin-bottom: 5px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    transition: color 0.2s;
}

.side-price {
    font-size: 14px;
    color: #333;
    font-weight: bold;
    font-family: 'Lato', sans-serif;
}

/* 评论区域样式 */
.reviews-section {
    max-width: 1500px;
    margin: 60px auto 0;
    padding: 0 40px 60px;
}

.reviews-header {
    display: flex;
    align-items: baseline;
    gap: 15px;
    margin-bottom: 30px;
    border-bottom: 2px solid #42b983;
    padding-bottom: 15px;
}

.reviews-header h2 {
    font-size: 20px;
    font-family: 'Georgia', serif;
    color: #2c3e50;
    margin: 0;
}

.review-count {
    font-size: 14px;
    color: #999;
}

.reviews-list {
    display: flex;
    flex-direction: column;
    gap: 25px;
}

.review-item {
    background: white;
    border-radius: 8px;
    padding: 25px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.review-user {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 15px;
}

.user-info {
    display: flex;
    flex-direction: column;
    gap: 3px;
}

.username {
    font-size: 14px;
    font-weight: 600;
    color: #333;
}

.review-time {
    font-size: 12px;
    color: #999;
}

.review-content {
    padding-left: 52px;
}

.review-text {
    margin: 12px 0;
    font-size: 14px;
    color: #555;
    line-height: 1.6;
}

.review-images {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 12px;
}

.review-img {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    cursor: pointer;
}

.merchant-replies {
    margin-top: 15px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.merchant-reply {
    padding: 12px 15px;
    background: #f9f9f9;
    border-radius: 6px;
    font-size: 13px;
    border-left: 3px solid #42b983;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
}

.reply-label {
    color: #42b983;
    font-weight: 600;
}

.reply-content {
    color: #666;
    flex: 1;
}

.reply-time {
    color: #999;
    font-size: 12px;
}

.load-more {
    text-align: center;
    margin-top: 25px;
}
</style>
