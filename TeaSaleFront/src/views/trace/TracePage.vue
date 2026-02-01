<template>
    <div class="trace-page">
        <!-- 顶部搜索区域 -->
        <div class="trace-banner">
            <div class="banner-content">
                <h1 class="title">茶叶溯源查询</h1>
                <p class="subtitle">每一片茶叶都有它的故事，扫码见证品质</p>
                
                <div class="search-box">
                    <el-input 
                        v-model="searchCode" 
                        placeholder="请输入溯源码 (例如: TEA20231001001)" 
                        class="search-input"
                        clearable
                        @keyup.enter="handleSearch"
                    >
                        <template #prefix>
                            <el-icon><Search /></el-icon>
                        </template>
                        <template #append>
                            <el-button @click="handleSearch" :loading="loading">查询</el-button>
                        </template>
                    </el-input>
                </div>
            </div>
        </div>

        <!-- 结果展示区域 -->
        <div class="trace-result" v-if="traceData || loading">
            <el-skeleton :loading="loading" animated>
                <template #template>
                    <div class="skeleton-wrapper">
                        <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
                        <el-skeleton-item variant="p" style="width: 50%" />
                    </div>
                </template>
                <template #default>
                    <div class="result-container" v-if="traceData">
                        <!-- 商品基本信息 -->
                        <el-card class="product-card" shadow="hover">
                            <div class="product-info">
                                <el-image 
                                    v-if="productInfo"
                                    :src="productInfo.mainImage" 
                                    class="product-image"
                                    fit="cover"
                                >
                                    <template #error>
                                        <div class="image-slot">
                                            <el-icon><Picture /></el-icon>
                                        </div>
                                    </template>
                                </el-image>
                                <div class="info-content">
                                    <h2 class="product-name">{{ productInfo?.title || '未知商品' }}</h2>
                                    <div class="tags">
                                        <el-tag type="success" effect="dark">正品保证</el-tag>
                                        <el-tag type="warning" effect="plain">{{ traceData.batchNo }}</el-tag>
                                    </div>
                                    <p class="desc">{{ productInfo?.subtitle }}</p>
                                    
                                    <div class="meta-grid">
                                        <div class="meta-item">
                                            <span class="label">溯源码</span>
                                            <span class="value code">{{ traceData.traceCode }}</span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="label">产地</span>
                                            <span class="value">{{ traceData.origin || '未知产地' }}</span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="label">生产商</span>
                                            <span class="value">{{ traceData.producer || '-' }}</span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="label">工艺</span>
                                            <span class="value">{{ traceData.process || '-' }}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </el-card>

                        <!-- 溯源时间轴 -->
                        <div class="section-title">
                            <h3><el-icon><Timer /></el-icon> 溯源时间轴</h3>
                            <span class="line"></span>
                        </div>
                        
                        <div class="timeline-box">
                            <el-timeline>
                                <el-timeline-item
                                    :timestamp="formatDate(traceData.pickDate)"
                                    placement="top"
                                    type="primary"
                                    hollow
                                    size="large"
                                >
                                    <el-card class="timeline-card">
                                        <h4>原料采摘</h4>
                                        <p>采摘于 {{ traceData.origin }}</p>
                                    </el-card>
                                </el-timeline-item>
                                
                                <el-timeline-item
                                    :timestamp="formatDate(traceData.createdAt)"
                                    placement="top"
                                    type="warning"
                                    size="large"
                                >
                                    <el-card class="timeline-card">
                                        <h4>生产加工</h4>
                                        <p>工艺：{{ traceData.process }}</p>
                                        <p>批次号：{{ traceData.batchNo }}</p>
                                    </el-card>
                                </el-timeline-item>

                                <el-timeline-item
                                    placement="top"
                                    type="success"
                                    size="large"
                                >
                                    <el-card class="timeline-card">
                                        <h4>质检合格</h4>
                                        <p>已通过各项质量检测，准予出厂</p>
                                        <div class="certs" v-if="traceData.inspectionReportUrl || traceData.certificateUrl">
                                            <el-image 
                                                v-if="traceData.inspectionReportUrl"
                                                :src="traceData.inspectionReportUrl" 
                                                :preview-src-list="[traceData.inspectionReportUrl]"
                                                class="cert-img"
                                            >
                                                <template #error><div class="cert-placeholder">检测报告</div></template>
                                            </el-image>
                                            <el-image 
                                                v-if="traceData.certificateUrl"
                                                :src="traceData.certificateUrl" 
                                                :preview-src-list="[traceData.certificateUrl]"
                                                class="cert-img"
                                            >
                                                <template #error><div class="cert-placeholder">证书</div></template>
                                            </el-image>
                                        </div>
                                    </el-card>
                                </el-timeline-item>
                            </el-timeline>
                        </div>

                        <!-- 溯源摘要 -->
                        <div class="summary-box" v-if="traceData.summary">
                             <div class="section-title">
                                <h3><el-icon><Document /></el-icon> 溯源档案摘要</h3>
                                <span class="line"></span>
                            </div>
                            <p class="summary-text">{{ traceData.summary }}</p>
                        </div>

                    </div>
                    <div class="empty-result" v-else-if="searched">
                        <el-empty description="未查询到相关溯源信息，请检查溯源码是否正确" />
                    </div>
                </template>
            </el-skeleton>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Picture, Timer, Document } from '@element-plus/icons-vue'
import { getTraceByCode } from '@/api/trace'
import { getProductDetail } from '@/api/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const searchCode = ref('')
const loading = ref(false)
const traceData = ref(null)
const productInfo = ref(null)
const searched = ref(false)

const handleSearch = async () => {
    if (!searchCode.value) {
        ElMessage.warning('请输入溯源码')
        return
    }

    loading.value = true
    traceData.value = null
    productInfo.value = null
    searched.value = false

    try {
        // 1. 获取溯源信息 (request.js已经unwrap了data)
        const res = await getTraceByCode(searchCode.value)
        console.log('Trace response:', res) // Debug log
        if (res) {
            traceData.value = res
            
            // 2. 获取商品信息
            if (traceData.value.productId) {
                const productRes = await getProductDetail(traceData.value.productId)
                console.log('Product response:', productRes) // Debug log
                if (productRes) {
                    productInfo.value = productRes
                }
            }
            
            // 更新路由参数而不刷新页面，方便分享
            router.replace({ query: { code: searchCode.value } })
        } else {
             searched.value = true
        }
    } catch (error) {
        console.error('Trace query error:', error)
        searched.value = true
        // 如果是404或其他特定错误，也可以在这处理，但通常request.js会处理全局错误
    } finally {
        loading.value = false
    }
}

const formatDate = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    return date.toLocaleDateString()
}

onMounted(() => {
    // 如果URL中有code参数，自动查询
    if (route.query.code) {
        searchCode.value = route.query.code
        handleSearch()
    }
})
</script>

<style scoped>
.trace-page {
    min-height: 80vh;
    background-color: #f8f9fa;
    padding-bottom: 40px;
}

.trace-banner {
    height: 300px;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    color: white;
    position: relative;
    overflow: hidden;
}

.trace-banner::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?q=80&w=1920&auto=format&fit=crop');
    background-size: cover;
    background-position: center;
    opacity: 0.2;
}

.banner-content {
    position: relative;
    z-index: 1;
    max-width: 800px;
    width: 90%;
}

.title {
    font-size: 2.5rem;
    font-weight: 600;
    margin-bottom: 10px;
    letter-spacing: 2px;
}

.subtitle {
    font-size: 1.1rem;
    margin-bottom: 30px;
    opacity: 0.9;
}

.search-box {
    max-width: 600px;
    margin: 0 auto;
}

.search-input :deep(.el-input-group__append) {
    background-color: #34d399;
    border-color: #34d399;
    color: white;
    font-weight: bold;
}
.search-input :deep(.el-input-group__append:hover) {
    background-color: #10b981;
}

.trace-result {
    max-width: 1000px;
    margin: -50px auto 0;
    padding: 0 20px;
    position: relative;
    z-index: 2;
}

.result-container {
    animation: fadeInUp 0.5s ease;
}

.product-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 10px 30px rgba(0,0,0,0.05);
    margin-bottom: 30px;
    overflow: hidden;
}

.product-info {
    display: flex;
    gap: 30px;
}

.product-image {
    width: 280px;
    height: 280px;
    border-radius: 8px;
    flex-shrink: 0;
    background-color: #f1f1f1;
    display: flex;
    align-items: center;
    justify-content: center;
}

.info-content {
    flex: 1;
    padding: 10px 0;
}

.product-name {
    font-size: 1.8rem;
    color: #333;
    margin: 0 0 15px 0;
}

.tags {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.desc {
    color: #666;
    line-height: 1.6;
    margin-bottom: 25px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-clamp: 2;
    overflow: hidden;
}

.meta-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    background-color: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
}

.meta-item {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.meta-item .label {
    font-size: 0.9rem;
    color: #888;
}

.meta-item .value {
    font-size: 1.1rem;
    color: #333;
    font-weight: 500;
}

.meta-item .value.code {
    font-family: monospace;
    color: #10b981;
    font-weight: bold;
}

.section-title {
    display: flex;
    align-items: center;
    margin: 40px 0 25px;
    color: #333;
}

.section-title h3 {
    margin: 0;
    font-size: 1.4rem;
    display: flex;
    align-items: center;
    gap: 10px;
}

.section-title .line {
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, #ddd, transparent);
    margin-left: 20px;
}

.timeline-box {
    padding: 20px 40px;
}

.timeline-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.timeline-card h4 {
    margin-top: 0;
    color: #333;
}

.timeline-card p {
    color: #666;
    margin-bottom: 5px;
    font-size: 0.95rem;
}

.certs {
    display: flex;
    gap: 15px;
    margin-top: 15px;
}

.cert-img {
    width: 100px;
    height: 140px;
    border-radius: 4px;
    border: 1px solid #eee;
    cursor: pointer;
    transition: transform 0.2s;
}

.cert-img:hover {
    transform: scale(1.05);
}

.cert-placeholder {
    width: 100%;
    height: 100%;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 12px;
}

.summary-text {
    line-height: 1.8;
    color: #555;
    background-color: white;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.03);
    font-size: 1.05rem;
    border-left: 4px solid #10b981;
}

.skeleton-wrapper {
    background: white;
    padding: 30px;
    border-radius: 12px;
}

.empty-result {
    background: white;
    padding: 60px 0;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.03);
    text-align: center;
}

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

@media (max-width: 768px) {
    .product-info {
        flex-direction: column;
    }
    
    .product-image {
        width: 100%;
        height: 250px;
    }
    
    .meta-grid {
        grid-template-columns: 1fr;
    }
    
    .timeline-box {
        padding: 20px 0;
    }
}
</style>
