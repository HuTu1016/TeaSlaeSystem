<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableCoupons, receiveCoupon, getMyCoupons } from '@/api/coupon'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const myCoupons = ref([]) // 用户已领取的优惠券列表
const claimedMap = ref({}) // couponId -> UserCoupon map

const loadData = async () => {
    loading.value = true
    try {
        const [availableRes, myRes] = await Promise.all([
            getAvailableCoupons(),
            getMyCoupons('') // 获取所有状态的优惠券，以便检查是否领取过
        ])
        list.value = availableRes
        myCoupons.value = myRes || []
        
        // 构建已领取映射：只要领取过就标记为已领取（不管状态）
        const map = {}
        if (myRes) {
            myRes.forEach(uc => {
                // 只要用户领取过该优惠券，无论状态如何，都标记为已领取
                // 这样可以正确禁用"立即领取"按钮
                map[uc.couponId] = uc
            })
        }
        claimedMap.value = map
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleReceive = async (coupon) => {
    try {
        await receiveCoupon(coupon.id)
        ElMessage.success('领取成功')
        // 刷新列表以更新进度条和状态
        loadData()
    } catch (e) {
        // 错误已经在request拦截器处理了
    }
}

// 饥饿营销：生成虚假的已抢进度，让优惠券看起来很抢手
// 实际数据不变，只在前端展示时造假
const getFakeProgress = (item) => {
    if (item.totalCount <= 0) return 0
    
    const realPercent = Math.floor((item.receivedCount / item.totalCount) * 100)
    
    // 如果真的抢完了，或者快抢完了，就显示真实的
    if (realPercent >= 100) return 100
    if (realPercent >= 90) return realPercent
    
    // 否则生成一个 60% ~ 95% 之间的虚假进度
    // 使用 id 作为随机种子，保证同一张券每次刷新显示的假进度一致
    const fakeBase = 60 + (item.id % 30) // 基础 60% ~ 89%
    
    let fakePercent = fakeBase + (item.id % 5)
    
    // 确保不超过 98%（留一点给用户抢），且不小于真实进度
    return Math.min(Math.max(fakePercent, realPercent), 98)
}

// 判断是否已领取且未使用
const isClaimed = (couponId) => {
    return !!claimedMap.value[couponId]
}

// 获取已领取优惠券的有效期信息
const getClaimedInfo = (couponId) => {
    const uc = claimedMap.value[couponId]
    if (!uc) return null
    return {
        validFrom: formatDate(uc.validFrom, 'YYYY-MM-DD'),
        validTo: formatDate(uc.validTo, 'YYYY-MM-DD')
    }
}

onMounted(() => loadData())
</script>

<template>
  <div class="coupon-center-page">
    <div class="page-header">
        <h2>领券中心</h2>
        <p class="subtitle">领取优惠券，享受超值优惠</p>
    </div>

    <div class="coupon-container" v-loading="loading">
        <el-empty v-if="!loading && list.length === 0" description="暂无优惠券可领取" />
        
        <div class="coupon-grid" v-else>
            <div v-for="item in list" :key="item.id" class="coupon-card">
                <div class="card-left" :class="{ 'discount-type': item.type === 'PERCENT' }">
                    <div class="amount" v-if="item.type === 'AMOUNT'">
                        <span class="symbol">¥</span>{{ item.amount }}
                    </div>
                    <div class="amount" v-else>
                        {{ item.discountPercent / 10 }}<span class="symbol">折</span>
                    </div>
                    <div class="condition">满{{ item.minAmount }}元可用</div>
                </div>
                <div class="card-right">
                    <div class="info">
                        <div class="name">{{ item.name }}</div>
                        
                        <!-- 有效期展示逻辑 -->
                        <div class="validity" v-if="isClaimed(item.id)">
                           <span class="highlight-date">
                             有效期至：{{ getClaimedInfo(item.id)?.validTo }}
                           </span>
                        </div>
                        <div class="validity" v-else>
                            <span v-if="item.validDays">领取后{{ item.validDays }}天内有效</span>
                            <span v-else>{{ formatDate(item.validFrom, 'YYYY-MM-DD') }} 至 {{ formatDate(item.validTo, 'YYYY-MM-DD') }}</span>
                        </div>

                        <div class="progress-box">
                            <el-progress 
                                :percentage="getFakeProgress(item)" 
                                :stroke-width="6"
                                :show-text="false"
                                :color="getFakeProgress(item) >= 100 ? '#909399' : '#ff4757'"
                            />
                            <span class="text">已抢{{ getFakeProgress(item) }}%</span>
                        </div>
                    </div>
                    <div class="action">
                        <el-button 
                            v-if="isClaimed(item.id)"
                            type="info" 
                            size="small" 
                            round 
                            disabled
                        >
                            已领取
                        </el-button>
                        <el-button 
                            v-else
                            type="primary" 
                            size="small" 
                            round 
                            :disabled="item.receivedCount >= item.totalCount"
                            @click="handleReceive(item)"
                        >
                            {{ item.receivedCount >= item.totalCount ? '已抢光' : '立即领取' }}
                        </el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.coupon-center-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.page-header {
    text-align: center;
    margin-bottom: 40px;
}

.page-header h2 {
    font-size: 28px;
    color: #333;
    margin-bottom: 10px;
}

.subtitle {
    color: #666;
    font-size: 14px;
}

.coupon-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    gap: 20px;
}

.coupon-card {
    display: flex;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
    height: 120px;
    transition: transform 0.3s;
}

.coupon-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.card-left {
    width: 120px;
    background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 10px;
}

.card-left.discount-type {
    background: linear-gradient(135deg, #ffa502 0%, #ff7f50 100%);
}

.amount {
    font-size: 32px;
    font-weight: bold;
    line-height: 1.2;
}

.amount .symbol {
    font-size: 16px;
    margin-right: 2px;
}

.condition {
    font-size: 12px;
    opacity: 0.9;
    margin-top: 5px;
    text-align: center;
}

.card-right {
    flex: 1;
    padding: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    background: white;
}

/* 锯齿效果模拟 */
.card-right::before {
    content: '';
    position: absolute;
    left: -5px;
    top: 50%;
    transform: translateY(-50%);
    width: 10px;
    height: calc(100% - 20px);
    background-image: radial-gradient(circle, #f5f7fa 4px, transparent 5px);
    background-size: 10px 10px;
    background-repeat: repeat-y;
}

.info {
    flex: 1;
    margin-right: 15px;
}

.name {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.validity {
    font-size: 12px;
    color: #999;
    margin-bottom: 12px;
}

.highlight-date {
    color: #e6a23c;
    font-weight: bold;
}

.progress-box {
    display: flex;
    align-items: center;
    gap: 8px;
}

.progress-box .el-progress {
    width: 100px;
}

.progress-box .text {
    font-size: 12px;
    color: #999;
}

.action {
    display: flex;
    flex-direction: column;
    justify-content: center;
}
</style>

