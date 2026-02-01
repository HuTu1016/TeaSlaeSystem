<template>
  <div class="article-list-page">
    <!-- Hero Banner -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">茶韵 · 专栏</h1>
        <p class="hero-subtitle">品茶韵，悟人生。探索中国传统茶文化的深邃与魅力。</p>
      </div>
    </div>

    <div class="main-container">
      <!-- Tabs Navigation -->
      <div class="tabs-wrapper">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="custom-tabs">
          <el-tab-pane label="茶文化文章" name="CULTURE"></el-tab-pane>
          <el-tab-pane label="冲泡教程" name="BREW_TUTORIAL"></el-tab-pane>
        </el-tabs>
      </div>

      <!-- Article Grid -->
      <div class="article-grid" v-loading="loading">
        <div 
          v-for="article in articles" 
          :key="article.id" 
          class="article-card"
          @click="$router.push(`/articles/${article.id}`)"
        >
          <div class="card-image-wrapper">
            <img :src="article.coverUrl" class="card-image" loading="lazy" />
            <div class="card-overlay">
              <span class="read-more">阅读全文</span>
            </div>
          </div>
          <div class="card-content">
            <div class="card-meta">
              <span class="publish-date">{{ formatDate(article.createdAt) }}</span>
            </div>
            <h3 class="card-title">{{ article.title }}</h3>
            <p class="card-summary">{{ article.summary }}</p>
            <div class="card-footer">
              <span class="view-count">
                <i class="el-icon-view"></i> {{ article.viewCount || 0 }} 阅读
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <el-empty v-if="!loading && articles.length === 0" description="暂无相关文章" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '@/api/content'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('CULTURE')
const loading = ref(false)
const articles = ref([])
const latestRequestId = ref(0)

const loadArticles = async (typeOverride = null) => {
  // If a specific type is requested (e.g. from tab click), use it. 
  // Otherwise verify activeTab.
  const targetType = typeOverride || activeTab.value
  
  // Increment request ID to mark this as the newest attempt
  const requestId = ++latestRequestId.value
  
  loading.value = true
  try {
    const res = await getArticles({
        type: targetType,
        page: 1,
        pageSize: 20
    })
    
    // If this request is no longer the latest one, ignore the result
    if (requestId !== latestRequestId.value) {
        return
    }

    if (res && res.items) {
        articles.value = res.items
    } else {
        articles.value = []
    }
  } catch (e) {
    if (requestId !== latestRequestId.value) {
        return
    }
    console.error(e)
    articles.value = []
  } finally {
    // Crucial fix: Only turn off loading if this was the LATEST request.
    // If a newer request started, it will handle its own loading state.
    if (requestId === latestRequestId.value) {
        loading.value = false
    }
  }
}

const handleTabClick = (tabPane) => {
  // Manually ensure activeTab is sync'd before loading, specific to Element Plus behavior
  // tabPane.props.name contains the name of the clicked tab
  const newType = tabPane.props.name
  activeTab.value = newType
  loadArticles(newType)
}

// 格式化日期显示
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
/* 页面整体布局 */
.article-list-page {
  min-height: calc(100vh - 60px);
  background-color: #f9fbfadd; /* 极淡的米色/绿色背景 */
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%); /* 默认备选渐变 */
  background: linear-gradient(to right, #5f8266, #3a5c40); /* 茶绿色系 */
  height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  text-align: center;
  margin-bottom: 40px;
}

.hero-content {
  animation: fadeInUp 0.8s ease-out;
}

.hero-title {
  font-size: 3rem;
  font-weight: 300; /* 细体显得更精致 */
  margin-bottom: 16px;
  letter-spacing: 4px;
  font-family: "Songti SC", "SimSun", serif; /* 尝试使用宋体增强文化感 */
}

.hero-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 1px;
}

/* 容器限制 */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

/* Tabs 优化 */
.tabs-wrapper {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e4e7ed;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: #5f8266; /* 激活态颜色与 Hero 呼应 */
}

:deep(.el-tabs__active-bar) {
  background-color: #5f8266;
}

/* 文章网格 */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); /* 响应式 Grid */
  gap: 30px;
}

/* 卡片样式 */
.article-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(95, 130, 102, 0.15); /* 悬停时带点绿色的阴影 */
}

/* 卡片图片区域 */
.card-image-wrapper {
  height: 220px;
  position: relative;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .card-image {
  transform: scale(1.08);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.article-card:hover .card-overlay {
  opacity: 1;
}

.read-more {
  color: #fff;
  border: 1px solid #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  letter-spacing: 1px;
}

/* 卡片内容区域 */
.card-content {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-meta {
  margin-bottom: 12px;
}

.publish-date {
  font-size: 12px;
  color: #999;
  letter-spacing: 0.5px;
}

.card-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 12px;
  line-height: 1.4;
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 1; /* 标题单行限制 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 20px;
  flex: 1; /* 让摘要占据剩余空间，保证底部对齐 */
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 摘要3行限制 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-align: justify; /* 两端对齐更整洁 */
}

.card-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.view-count {
  font-size: 12px;
  color: #aaa;
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
