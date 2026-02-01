<template>
  <div class="article-detail" v-if="article">
    <h1 class="title">{{ article.title }}</h1>
    <div class="meta">
      <span>发布时间: {{ article.publishTime }}</span>
      <span>阅读: {{ article.viewCount }}</span>
    </div>
    
    <div class="content markdown-body" v-html="article.content"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/content'

const route = useRoute()
const article = ref(null)

onMounted(async () => {
    const id = route.params.id
    try {
        const res = await getArticleDetail(id)
        article.value = res
    } catch (e) {
        console.error(e)
    }
})
</script>

<style scoped>
.article-detail {
    max-width: 800px;
    margin: 0 auto;
    background: white;
    padding: 40px;
    border-radius: 8px;
}
.title {
    text-align: center;
    margin-bottom: 20px;
}
.meta {
    text-align: center;
    color: #999;
    margin-bottom: 40px;
    border-bottom: 1px solid #eee;
    padding-bottom: 20px;
}
.content {
    line-height: 1.8;
    font-size: 16px;
    color: #333;
}
</style>
