<script setup>
import { ref, onMounted, watch } from 'vue'
import { getMyCoupons } from '@/api/coupon'
import { formatDate } from '@/utils/date' // Assuming standard utility exists, otherwise will implement local format function

const activeTab = ref('UNUSED')
const loading = ref(false)
const list = ref([])

const loadData = async () => {
    loading.value = true
    try {
        const status = activeTab.value === 'ALL' ? '' : activeTab.value
        const res = await getMyCoupons(status)
        
        // 如果是"未使用"标签，过滤掉已过期的优惠券
        if (activeTab.value === 'UNUSED') {
            const now = new Date()
            list.value = res.filter(item => {
                const validTo = new Date(item.validTo)
                return validTo > now
            })
        } else {
            list.value = res
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

watch(activeTab, () => {
    loadData()
})

const formatTime = (time) => {
    if (!time) return ''
    return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => loadData())
</script>

<template>
  <div class="my-coupons-page">
    <div class="page-header">
        <h2>我的优惠券</h2>
    </div>

    <el-tabs v-model="activeTab" class="coupon-tabs">
        <el-tab-pane label="未使用" name="UNUSED"></el-tab-pane>
        <el-tab-pane label="已使用" name="USED"></el-tab-pane>
        <el-tab-pane label="已过期" name="EXPIRED"></el-tab-pane>
    </el-tabs>

    <div class="coupon-list" v-loading="loading">
        <el-empty v-if="!loading && list.length === 0" description="暂无相关优惠券" />
        
        <div v-else class="coupon-grid">
            <div 
                v-for="item in list" 
                :key="item.id" 
                class="coupon-card"
                :class="{ 'is-disabled': item.status !== 'UNUSED' }"
            >
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
                        <div class="name">
                            <el-tag size="small" type="danger" v-if="item.type === 'AMOUNT'">满减</el-tag>
                            <el-tag size="small" type="warning" v-else>折扣</el-tag>
                            {{ item.name }}
                        </div>
                        <div class="time-range">
                            有效期：{{ formatTime(item.validFrom) }} ~ {{ formatTime(item.validTo) }}
                        </div>
                        <div class="status-tag" v-if="item.status === 'USED'">
                            <span class="iconfont icon-used">已使用</span>
                        </div>
                        <div class="status-tag" v-else-if="item.status === 'EXPIRED'">
                            <span class="iconfont icon-expired">已过期</span>
                        </div>
                    </div>
                    <div class="action" v-if="item.status === 'UNUSED'">
                        <el-button type="primary" size="small" round @click="$router.push('/products')">去使用</el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.my-coupons-page {
    padding: 20px;
    background: #fff;
    min-height: 500px;
    border-radius: 8px;
}

.page-header h2 {
    font-size: 20px;
    margin-bottom: 20px;
    color: #333;
}

.coupon-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.coupon-card {
    display: flex;
    border: 1px solid #eee;
    border-radius: 8px;
    overflow: hidden;
    height: 110px;
    transition: all 0.3s;
}

.coupon-card:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.card-left {
    width: 100px;
    background: #ff6b6b;
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.card-left.discount-type {
    background: #ffa502;
}

.amount {
    font-size: 24px;
    font-weight: bold;
}

.symbol {
    font-size: 14px;
}

.condition {
    font-size: 12px;
    margin-top: 5px;
}

.card-right {
    flex: 1;
    padding: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    background: #fff;
}

.info {
    flex: 1;
}

.name {
    font-size: 15px;
    color: #333;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
}

.time-range {
    font-size: 12px;
    color: #999;
}

/* 禁用状态样式（已使用/已过期） */
.coupon-card.is-disabled .card-left {
    background: #ccc;
}

.coupon-card.is-disabled .name {
    color: #999;
}
</style>
