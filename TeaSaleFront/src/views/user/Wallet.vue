<template>
  <div class="member-center">
    <!-- 会员等级卡片 -->
    <div class="member-card" :style="memberCardStyle">
      <div class="card-content">
        <div class="member-header">
          <div class="avatar-area">
            <el-avatar :size="64" :src="userStore.userInfo?.avatar || defaultAvatar" />
            <div class="level-info">
              <h3>{{ memberInfo.currentLevel?.name || '加载中...' }}</h3>
              <p>累计消费：¥{{ formatAmount(memberInfo.totalExpense) }}</p>
            </div>
          </div>
          <div class="discount-badge" v-if="memberInfo.currentLevel?.discountRate < 100">
            <span class="discount-text">{{ (memberInfo.currentLevel?.discountRate / 10).toFixed(1) }}折</span>
            <span class="discount-label">专享</span>
          </div>
        </div>
        
        <!-- 等级进度条 -->
        <div class="level-progress" v-if="memberInfo.nextLevel">
          <div class="progress-header">
            <span class="current-level">{{ memberInfo.currentLevel?.name }}</span>
            <span class="next-level">{{ memberInfo.nextLevel?.name }}</span>
          </div>
          <el-progress 
            :percentage="memberInfo.progressPercent || 0" 
            :stroke-width="12"
            :show-text="false"
            color="#ffd700"
          />
          <div class="progress-tip">
            还需消费 <span class="highlight">¥{{ formatAmount(memberInfo.nextLevelRequirement) }}</span> 升级至{{ memberInfo.nextLevel?.name }}
          </div>
        </div>
        <div class="max-level-tip" v-else>
          <el-icon><Trophy /></el-icon>
          <span>恭喜！您已达到最高等级</span>
        </div>
      </div>
    </div>

    <!-- 订单支出明细 -->
    <div class="expense-section">
      <div class="section-header">
        <h3>订单明细</h3>
        <el-radio-group v-model="filterType" size="small" @change="handleFilterChange">
          <el-radio-button label="ALL">全部</el-radio-button>
          <el-radio-button label="PAYMENT">支出</el-radio-button>
          <el-radio-button label="REFUND">收入</el-radio-button>
        </el-radio-group>
      </div>

      <div class="expense-list" v-loading="loading">
        <template v-if="expenseList.length > 0">
          <div 
            class="expense-item" 
            v-for="item in expenseList" 
            :key="`${item.orderId}-${item.expenseType}-${item.createdAt}`"
            @click="goToOrderDetail(item.orderId)"
          >
            <div class="item-left">
              <img :src="item.productImage || defaultProductImage" class="product-img" />
              <div class="item-info">
                <div class="item-title">{{ item.productTitle || '订单商品' }}</div>
                <div class="item-remark">
                  <el-tag 
                    v-if="item.expenseType === 'REFUND'" 
                    size="small" 
                    :type="item.refundType === 'RETURN_REFUND' ? 'warning' : 'info'"
                  >
                    {{ item.refundType === 'RETURN_REFUND' ? '退货退款' : '仅退款' }}
                  </el-tag>
                  <span v-else>{{ item.remark }}</span>
                </div>
                <div class="item-time">{{ formatTime(item.createdAt) }}</div>
              </div>
            </div>
            <div class="item-right">
              <div class="item-amount" :class="{ 'income': parseFloat(item.amount) > 0 }">
                {{ parseFloat(item.amount) > 0 ? '+' : '' }}¥{{ formatAmount(Math.abs(parseFloat(item.amount))) }}
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无交易记录" />
      </div>

      <!-- 分页 -->
      <div class="expense-pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="fetchExpenseList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMemberCenter } from '@/api/wallet'
import { getOrderExpenseList } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { Trophy, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const defaultProductImage = 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=100'

const memberInfo = reactive({
  currentLevel: null,
  nextLevel: null,
  totalExpense: 0,
  nextLevelRequirement: 0,
  progressPercent: 0,
  points: 0
})

const expenseList = ref([])
const loading = ref(false)
const filterType = ref('ALL')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 会员等级颜色映射
const levelColors = {
  'BRONZE': ['#cd7f32', '#8b4513'],
  'SILVER': ['#c0c0c0', '#808080'],
  'GOLD': ['#ffd700', '#b8860b'],
  'PLATINUM': ['#e5e4e2', '#a9a9a9'],
  'DIAMOND': ['#b9f2ff', '#4169e1'],
  'BLACK_DIAMOND': ['#1a1a2e', '#16213e']
}

const memberCardStyle = computed(() => {
  const code = memberInfo.currentLevel?.code || 'BRONZE'
  const colors = levelColors[code] || levelColors['BRONZE']
  return {
    background: `linear-gradient(135deg, ${colors[0]} 0%, ${colors[1]} 100%)`
  }
})

const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '0.00'
  return parseFloat(amount).toFixed(2)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const fetchMemberCenter = async () => {
  try {
    const res = await getMemberCenter()
    if (res) {
      Object.assign(memberInfo, res)
    }
  } catch (e) {
    console.error('获取会员信息失败', e)
  }
}

const fetchExpenseList = async () => {
  loading.value = true
  try {
    const res = await getOrderExpenseList({
      filterType: filterType.value,
      page: page.value,
      size: pageSize.value
    })
    expenseList.value = res.items || []
    total.value = res.total || 0
  } catch (e) {
    console.error('获取订单明细失败', e)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  page.value = 1
  fetchExpenseList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  page.value = 1
  fetchExpenseList()
}

const goToOrderDetail = (orderId) => {
  router.push(`/user/orders/${orderId}`)
}

onMounted(() => {
  fetchMemberCenter()
  fetchExpenseList()
})
</script>

<style scoped>
.member-center {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 会员卡片样式 */
.member-card {
  border-radius: 16px;
  padding: 24px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.card-content {
  position: relative;
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.avatar-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.level-info h3 {
  margin: 0 0 4px 0;
  font-size: 22px;
  font-weight: 600;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.2);
}

.level-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.discount-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255,255,255,0.25);
  padding: 8px 16px;
  border-radius: 8px;
  backdrop-filter: blur(4px);
}

.discount-text {
  font-size: 24px;
  font-weight: bold;
}

.discount-label {
  font-size: 12px;
  opacity: 0.9;
}

.level-progress {
  background: rgba(255,255,255,0.15);
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(4px);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.current-level {
  font-weight: 600;
}

.next-level {
  opacity: 0.8;
}

.progress-tip {
  margin-top: 10px;
  font-size: 13px;
  text-align: center;
}

.progress-tip .highlight {
  color: #ffd700;
  font-weight: bold;
  font-size: 15px;
}

.max-level-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: rgba(255,255,255,0.15);
  border-radius: 12px;
  font-size: 16px;
}

/* 订单明细样式 */
.expense-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.expense-list {
  min-height: 200px;
}

.expense-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.expense-item:hover {
  background: #fafafa;
}

.expense-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.product-img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-remark {
  font-size: 13px;
  color: #999;
  margin-bottom: 2px;
}

.item-time {
  font-size: 12px;
  color: #bbb;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-amount {
  font-size: 16px;
  font-weight: 600;
  color: #666;
}

.item-amount.income {
  color: #67c23a;
}

.arrow-icon {
  color: #ccc;
  font-size: 14px;
}

.expense-pagination {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}
</style>
