<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { reindexAll } from '@/api/admin'

const loading = ref(false)

const handleReindex = async () => {
    loading.value = true
    try {
        await reindexAll()
        ElMessage.success('全量索引重建已提交')
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}
</script>

<template>
  <div class="search-manage">
    <el-card header="搜索索引管理">
        <p>当商品数据与搜索结果不一致时，可手动触发全量索引重建。</p>
        <el-button type="primary" :loading="loading" @click="handleReindex">重建全量索引</el-button>
    </el-card>
  </div>
</template>

<style scoped>
.search-manage { padding: 20px; }
</style>
