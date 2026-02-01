<template>
  <div class="dashboard-container">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>数据概览</span>
          <el-tag type="success">实时</el-tag>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-title">今日销售额</div>
            <div class="stat-value">¥ {{ stats.todaySales || '0.00' }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-title">今日订单数</div>
            <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-title">待发货订单</div>
            <div class="stat-value text-warning">{{ stats.pendingShipment || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-title">待处理售后</div>
            <div class="stat-value text-danger">{{ stats.pendingAfterSale || 0 }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>近7天销售趋势</span>
        </div>
      </template>
      <div ref="chartRef" style="height: 350px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const stats = reactive({
  todaySales: 0,
  todayOrders: 0,
  pendingShipment: 0,
  pendingAfterSale: 0
})

const chartRef = ref(null)
let chartInstance = null

const initChart = (data) => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  
  const dates = data.map(item => item.date)
  const sales = data.map(item => item.salesAmount)
  const orders = data.map(item => item.orderCount)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['销售额', '订单数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额',
        axisLabel: {
            formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right',
        alignTicks: true,
        splitLine: {
            show: false
        }
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: sales,
        itemStyle: { color: '#10B981' },
        areaStyle: {
           color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(16, 185, 129, 0.5)' },
              { offset: 1, color: 'rgba(16, 185, 129, 0.0)' }
            ])
        }
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: orders,
        itemStyle: { color: '#409EFF' }
      }
    ]
  }
  chartInstance.setOption(option)
}

const loadData = async () => {
    try {
        // 获取概览数据
        const overviewRes = await request.get('/merchant/stats/overview')
        if (overviewRes) {
            Object.assign(stats, overviewRes)
        }
        
        // 获取趋势数据 (默认近7天)
        const end = new Date()
        const start = new Date()
        start.setDate(start.getDate() - 6)
        
        const formatDate = (d) => d.toISOString().split('T')[0]
        
        const trendRes = await request.get('/merchant/stats/trend', {
            params: {
                startDate: formatDate(start),
                endDate: formatDate(end)
            }
        })
        
        if (trendRes) {
            initChart(trendRes)
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
    loadData()
    window.addEventListener('resize', () => chartInstance?.resize())
})
</script>

<style scoped>
.mb-4 {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 8px;
}
.stat-title {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.text-warning {
  color: #E6A23C;
}
.text-danger {
  color: #F56C6C;
}
</style>
