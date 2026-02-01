<template>
  <div class="reviews">
    <h2>我的评价</h2>
    <el-tabs v-model="activeName" @tab-click="handleTabChange">
       <!-- 待评价标签页 -->
       <el-tab-pane label="待评价" name="pending">
          <div v-loading="pendingLoading">
            <div class="pending-item" v-for="item in pendingItems" :key="`${item.orderId}-${item.productId}`">
              <div class="product-info">
                <el-image :src="item.image" class="product-img" fit="cover" />
                <div class="product-detail">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="sku-name">{{ item.skuName }}</div>
                  <div class="order-info">订单号: {{ item.orderNo }}</div>
                </div>
              </div>
              <el-button type="primary" size="small" @click="goToReview(item)">去评价</el-button>
            </div>
            <el-empty v-if="pendingItems.length === 0 && !pendingLoading" description="暂无待评价商品" />
          </div>
          <!-- 待评价分页 -->
          <div class="pagination-container" v-if="pendingTotal > 0">
            <el-pagination
              background
              v-model:current-page="pendingPage"
              v-model:page-size="pendingPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pendingTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePendingSizeChange"
              @current-change="loadPendingItems"
            />
          </div>
       </el-tab-pane>
       <!-- 已评价标签页 -->
       <el-tab-pane label="已评价" name="published">
          <div v-loading="loading">
            <div class="review-item" v-for="item in reviews" :key="item.id">
              <div class="review-header">
                <el-image :src="item.productImage" class="review-product-img" fit="cover" />
                <div class="review-main">
                  <div class="header">
                    <span class="product-name">{{ item.productName }}</span>
                    <span class="time">{{ formatDate(item.createdAt) }}</span>
                  </div>
                  <el-rate v-model="item.rating" disabled />
                  <p class="content">{{ item.content }}</p>
                  <div class="images" v-if="item.images && item.images.length">
                     <el-image v-for="img in item.images" :key="img" :src="img" class="rv-img" :preview-src-list="item.images" />
                  </div>
                  <div class="reply" v-if="item.replies && item.replies.length">
                    <span class="reply-label">商家回复：</span>{{ item.replies[0].content }}
                  </div>
                </div>
                <!-- 查看详情按钮 -->
                <div class="review-actions">
                  <el-button type="primary" link @click="goToProductDetail(item.productId)">查看详情</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="reviews.length === 0 && !loading" description="暂无评价记录" />
          </div>
          <!-- 已评价分页 -->
          <div class="pagination-container" v-if="reviewTotal > 0">
            <el-pagination
              background
              v-model:current-page="reviewPage"
              v-model:page-size="reviewPageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="reviewTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleReviewSizeChange"
              @current-change="loadReviews"
            />
          </div>
       </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getMyReviews, getPendingReviewItems } from '@/api/review'

const router = useRouter()
const route = useRoute()
const activeName = ref('pending')
const reviews = ref([])
const loading = ref(false)
const pendingItems = ref([])
const pendingLoading = ref(false)

// 待评价分页
const pendingPage = ref(1)
const pendingPageSize = ref(10)
const pendingTotal = ref(0)

// 已评价分页
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)

// 加载待评价列表
const loadPendingItems = async () => {
  pendingLoading.value = true
  try {
    const res = await getPendingReviewItems({ page: pendingPage.value, pageSize: pendingPageSize.value })
    pendingItems.value = res.list || res.items || []
    pendingTotal.value = res.total || pendingItems.value.length
  } catch (error) {
    console.error('加载待评价列表失败:', error)
    pendingItems.value = []
  } finally {
    pendingLoading.value = false
  }
}

// 加载已评价列表
const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getMyReviews({ page: reviewPage.value, pageSize: reviewPageSize.value })
    reviews.value = res.list || res.items || []
    reviewTotal.value = res.total || reviews.value.length
  } catch (error) {
    console.error('加载评价列表失败:', error)
    reviews.value = []
  } finally {
    loading.value = false
  }
}

const handlePendingSizeChange = (val) => {
  pendingPageSize.value = val
  pendingPage.value = 1
  loadPendingItems()
}

const handleReviewSizeChange = (val) => {
  reviewPageSize.value = val
  reviewPage.value = 1
  loadReviews()
}

// 切换标签时加载对应数据，重置页码
const handleTabChange = (tab) => {
  // tab.paneName 是点击的标签名
  if (tab.paneName === 'pending') {
    pendingPage.value = 1
    loadPendingItems()
  } else {
    reviewPage.value = 1
    loadReviews()
  }
}

// 跳转到评价页面
const goToReview = (item) => {
  router.push({
    path: '/user/reviews/create',
    query: {
      orderId: item.orderId,
      productId: item.productId
    }
  })
}

// 跳转到商品详情页
const goToProductDetail = (productId) => {
  router.push(`/products/${productId}`)
}

// 格式化日期为年月日
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

onMounted(() => {
  // 检查URL参数，如果tab=published则切换到已评价标签
  const tab = route.query.tab
  if (tab === 'published') {
    activeName.value = 'published'
    loadReviews()
  } else {
    // 默认加载待评价列表
    loadPendingItems()
  }
})
</script>

<style scoped>
.pending-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #eee;
}
.product-info {
    display: flex;
    align-items: center;
}
.product-img {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    margin-right: 15px;
}
.product-detail {
    display: flex;
    flex-direction: column;
    gap: 5px;
}
.product-name {
    font-weight: bold;
    font-size: 14px;
}
.sku-name {
    color: #666;
    font-size: 12px;
}
.order-info {
    color: #999;
    font-size: 12px;
}
.review-item {
    border-bottom: 1px solid #eee;
    padding: 20px 0;
}
.review-header {
    display: flex;
    align-items: flex-start;
}
.review-product-img {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    margin-right: 15px;
    flex-shrink: 0;
}
.review-main {
    flex: 1;
}
.header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
}
.time {
    color: #999;
    font-size: 13px;
}
.content {
    margin: 10px 0;
    color: #333;
}
.rv-img {
    width: 80px;
    height: 80px;
    margin-right: 10px;
}
.reply {
    margin-top: 10px;
    padding: 10px;
    background: #f5f5f5;
    border-radius: 4px;
    font-size: 13px;
}
.reply-label {
    color: #e6a23c;
    font-weight: bold;
}

/* 查看详情按钮区域 */
.review-actions {
    flex-shrink: 0;
    margin-left: 15px;
    display: flex;
    align-items: center;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>
