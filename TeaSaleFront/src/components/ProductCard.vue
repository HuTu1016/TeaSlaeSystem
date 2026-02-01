<template>
  <div class="product-card" @click="goToDetail">
    <div 
      class="image-wrapper"
      @mouseenter="$emit('image-enter')"
      @mouseleave="$emit('image-leave')"
    >
      <img :src="product.image || 'https://placeholder.com/tea.jpg'" class="image" />
      <div class="taste-badge" v-if="product.taste">{{ product.taste }}</div>
    </div>
    <div class="card-content">
      <h3 class="title" :title="product.title">{{ product.title }}</h3>
      
      <!-- 产地信息，带绿点 -->
      <div class="origin-row" v-if="product.origin">
        <span class="dot"></span>
        <span class="origin-text">{{ product.origin }}</span>
      </div>

      <!-- 价格和销量行 -->
      <div class="price-row">
        <div class="price-wrapper">
          <span class="currency">¥</span>
          <span class="price-val">{{ product.minPrice }}</span>
        </div>
        <span class="sales" v-if="product.salesCount">已售{{ product.salesCount }}+</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['image-enter', 'image-leave'])

const router = useRouter()

const goToDetail = () => {
  router.push(`/products/${props.product.id}`)
}
</script>

<style scoped>
.product-card {
  cursor: pointer;
  transition: all 0.4s ease;
  height: 100%;
  background: transparent;
  position: relative;
}

.product-card:hover .image {
  transform: scale(1.05);
}

.image-wrapper {
  width: 100%;
  padding-bottom: 100%; /* 1:1 Aspect Ratio */
  position: relative;
  background: #f5f7fa;
  overflow: hidden;
  border-radius: 12px;
  margin-bottom: 12px;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.product-card:hover .image-wrapper {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
}

.image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.taste-badge {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #5a4a42;
  font-size: 13px;
  padding: 10px 15px;
  text-align: center;
  font-family: 'Noto Serif SC', serif;
  transform: translateY(0); /* Always show */
  z-index: 2;
  border-top: 1px solid rgba(0,0,0,0.03);
}

.card-content {
  text-align: center;
  padding: 0 5px;
}

.title {
  font-size: 16px;
  font-family: var(--font-heading);
  color: var(--color-heading);
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}

/* 新增：产地行样式 */
.origin-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 8px;
}

.dot {
  width: 6px;
  height: 6px;
  background-color: var(--vt-c-tea-green); /* 使用绿茶色 */
  border-radius: 50%;
  display: inline-block;
}

.origin-text {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.price-row {
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 12px;
}

.price-wrapper {
  color: var(--vt-c-tea-green-dark);
  line-height: 1;
}

.currency {
  font-size: 14px;
  font-weight: bold;
  margin-right: 2px;
}

.price-val {
  font-size: 20px;
  font-weight: bold;
  font-family: 'Lato', sans-serif;
}

.sales {
  font-size: 12px;
  color: #999;
}
</style>
