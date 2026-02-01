<script setup>
import { ref, onMounted, computed } from 'vue'
import { getAdminOverview, getSalesTrend, getOrderStats, getProductStats, getAfterSaleStats } from '@/api/admin'

const stats = ref({
    totalUsers: 0,
    totalMerchants: 0,
    totalOrders: 0,
    totalSales: 0,
    todayOrders: 0,
    todaySales: 0,
    pendingReviews: 0,
    pendingTraces: 0
})

const orderStats = ref({})
const productStats = ref({})
const afterSaleStats = ref({})
const salesTrend = ref([])

const loadOverview = async () => {
    try {
        const res = await getAdminOverview()
        if (res) stats.value = res
    } catch (e) {}
}

const loadOrderStats = async () => {
    try {
        const res = await getOrderStats()
        if (res) orderStats.value = res
    } catch (e) {}
}

const loadProductStats = async () => {
    try {
        const res = await getProductStats()
        if (res) productStats.value = res
    } catch (e) {}
}

const loadAfterSaleStats = async () => {
    try {
        const res = await getAfterSaleStats()
        if (res) afterSaleStats.value = res
    } catch (e) {}
}

const loadSalesTrend = async () => {
    try {
        const endDate = new Date().toISOString().split('T')[0]
        const startDate = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
        const res = await getSalesTrend(startDate, endDate)
        if (res) salesTrend.value = res
    } catch (e) {}
}

const formatMoney = (value) => {
    if (!value) return '0.00'
    return Number(value).toFixed(2)
}

onMounted(() => {
    loadOverview()
    loadOrderStats()
    loadProductStats()
    loadAfterSaleStats()
    loadSalesTrend()
})
</script>

<template>
  <div class="dashboard">
    <h1>茶叶销售管理系统 - 控制台</h1>
    
    <!-- 核心指标 -->
    <div class="section-title">核心指标</div>
    <div class="stat-cards main-stats">
      <el-card class="stat-card primary">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.totalSales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </el-card>
      <el-card class="stat-card success">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </el-card>
      <el-card class="stat-card warning">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">注册用户</div>
        </div>
      </el-card>
      <el-card class="stat-card info">
        <div class="stat-icon">🏪</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalMerchants || 0 }}</div>
          <div class="stat-label">入驻商家</div>
        </div>
      </el-card>
    </div>

    <!-- 今日数据 -->
    <div class="section-title">今日数据</div>
    <div class="stat-cards today-stats">
      <el-card>
        <div class="today-item">
          <span class="label">今日订单</span>
          <span class="value primary">{{ stats.todayOrders || 0 }}</span>
        </div>
      </el-card>
      <el-card>
        <div class="today-item">
          <span class="label">今日销售额</span>
          <span class="value success">¥{{ formatMoney(stats.todaySales) }}</span>
        </div>
      </el-card>
      <el-card>
        <div class="today-item">
          <span class="label">待审核评论</span>
          <span class="value warning">{{ stats.pendingReviews || 0 }}</span>
        </div>
      </el-card>
      <el-card>
        <div class="today-item">
          <span class="label">待审核溯源</span>
          <span class="value warning">{{ stats.pendingTraces || 0 }}</span>
        </div>
      </el-card>
    </div>

    <!-- 业务概览 -->
    <div class="section-title">业务概览</div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="overview-card">
          <template #header><span>订单状态分布</span></template>
          <div class="overview-list">
            <div class="overview-item">
              <span>待支付</span>
              <span class="warning">{{ orderStats.pendingOrders || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>已支付</span>
              <span class="primary">{{ orderStats.paidOrders || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>已发货</span>
              <span>{{ orderStats.shippedOrders || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>已完成</span>
              <span class="success">{{ orderStats.completedOrders || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card">
          <template #header><span>商品状态分布</span></template>
          <div class="overview-list">
            <div class="overview-item">
              <span>全部商品</span>
              <span>{{ productStats.totalProducts || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>上架中</span>
              <span class="success">{{ productStats.onShelfProducts || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>已下架</span>
              <span class="info">{{ productStats.offShelfProducts || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card">
          <template #header><span>售后状态分布</span></template>
          <div class="overview-list">
            <div class="overview-item">
              <span>今日售后</span>
              <span class="primary">{{ afterSaleStats.todayAfterSales || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>待处理</span>
              <span class="warning">{{ afterSaleStats.pendingAfterSales || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>处理中</span>
              <span>{{ afterSaleStats.processingAfterSales || 0 }}</span>
            </div>
            <div class="overview-item">
              <span>已完成</span>
              <span class="success">{{ afterSaleStats.completedAfterSales || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近7日销售趋势 -->
    <div class="section-title">近7日销售趋势</div>
    <el-card>
      <div class="trend-chart">
        <div v-for="(item, index) in salesTrend" :key="index" class="trend-bar">
          <div class="bar-wrapper">
            <div class="bar" :style="{ height: Math.max(item.orderCount * 10, 5) + 'px' }"></div>
          </div>
          <div class="bar-label">{{ item.date?.substring(5) }}</div>
          <div class="bar-value">{{ item.orderCount }}单</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard { padding: 20px; }
.dashboard h1 { margin-bottom: 24px; font-size: 24px; color: #303133; }

.section-title { 
  font-size: 16px; 
  font-weight: 600; 
  color: #303133; 
  margin: 24px 0 16px; 
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}

.stat-cards { 
  display: grid; 
  gap: 16px; 
}

.main-stats { 
  grid-template-columns: repeat(4, 1fr); 
}

.today-stats { 
  grid-template-columns: repeat(4, 1fr); 
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card.primary { border-left: 4px solid #409EFF; }
.stat-card.success { border-left: 4px solid #67C23A; }
.stat-card.warning { border-left: 4px solid #E6A23C; }
.stat-card.info { border-left: 4px solid #909399; }

.stat-icon { font-size: 36px; margin-right: 16px; }
.stat-info { flex: 1; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.today-item { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  padding: 10px 0;
}
.today-item .label { color: #606266; }
.today-item .value { font-size: 20px; font-weight: bold; }
.today-item .value.primary { color: #409EFF; }
.today-item .value.success { color: #67C23A; }
.today-item .value.warning { color: #E6A23C; }

.overview-card { height: 200px; }
.overview-list { display: flex; flex-direction: column; gap: 12px; }
.overview-item { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #EBEEF5; }
.overview-item:last-child { border-bottom: none; }
.overview-item .primary { color: #409EFF; font-weight: bold; }
.overview-item .success { color: #67C23A; font-weight: bold; }
.overview-item .warning { color: #E6A23C; font-weight: bold; }
.overview-item .info { color: #909399; }

.trend-chart { 
  display: flex; 
  justify-content: space-around; 
  align-items: flex-end; 
  height: 150px; 
  padding: 20px 0;
}
.trend-bar { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  width: 60px;
}
.bar-wrapper { 
  height: 80px; 
  display: flex; 
  align-items: flex-end; 
}
.bar { 
  width: 30px; 
  background: linear-gradient(180deg, #409EFF, #66b1ff); 
  border-radius: 4px 4px 0 0;
  min-height: 5px;
}
.bar-label { font-size: 12px; color: #909399; margin-top: 8px; }
.bar-value { font-size: 12px; color: #409EFF; font-weight: bold; }
</style>
