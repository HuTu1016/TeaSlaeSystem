<template>
  <div class="home-container">
    <!-- Banner Carousel -->
    <div class="banner-section">
      <el-carousel height="100vh" trigger="click" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-wrapper">
            <img :src="item.imageUrl" class="banner-image" alt="banner" />
            <div class="banner-content">
              <h1 class="banner-brand fade-in-up">TEA CULTURE</h1>
              <h2 class="banner-title fade-in-up delay-200">中国茶道 · 传承千年</h2>
              <p class="banner-subtitle fade-in-up delay-200">品味自然馈赠，静享禅意人生</p>
              <el-button type="primary" size="large" class="banner-btn fade-in-up delay-400" @click="$router.push('/products')">立即选购</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- Hot Products -->
    <div class="section-spacer">
      <div class="centered-content">
        <div class="section-header" @click="$router.push('/products?sort=sales_desc')">
          <h2 class="section-title interactive-title">热销珍选</h2>
          <p class="section-subtitle">Customers' Favorite Selections</p>
          <div class="section-divider"></div>
        </div>
      </div>
      
      <!-- Seamless Marquee -->
      <div class="marquee-container" v-loading="loading">
        <div 
          class="marquee-content"
          :style="{ animationPlayState: isPaused ? 'paused' : 'running' }"
        >
          <!-- First Set -->
          <div 
            v-for="product in hotProducts" 
            :key="'set1-' + product.id" 
            class="product-item"
          >
            <ProductCard 
              :product="product" 
              @image-enter="isPaused = true"
              @image-leave="isPaused = false"
            />
          </div>
          <!-- Second Set (Duplicate for Loop) -->
          <div 
            v-for="product in hotProducts" 
            :key="'set2-' + product.id" 
            class="product-item"
          >
            <ProductCard 
              :product="product" 
              @image-enter="isPaused = true"
              @image-leave="isPaused = false"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- New Products -->
    <div class="section-spacer bg-alt">
      <div class="centered-content">
        <div class="section-header" @click="$router.push('/products?sort=newest')">
          <h2 class="section-title interactive-title">当季新品</h2>
          <p class="section-subtitle">Fresh from the Origin</p>
          <div class="section-divider"></div>
        </div>
      </div>
      
      <!-- Seamless Marquee -->
      <div class="marquee-container" v-loading="loading">
        <div 
          class="marquee-content"
          :style="{ animationPlayState: isPaused ? 'paused' : 'running' }"
        >
          <!-- First Set -->
          <div 
            v-for="product in newProducts" 
            :key="'set1-' + product.id" 
            class="product-item"
          >
            <ProductCard 
              :product="product" 
              @image-enter="isPaused = true"
              @image-leave="isPaused = false"
            />
          </div>
          <!-- Second Set (Duplicate for Loop) -->
          <div 
            v-for="product in newProducts" 
            :key="'set2-' + product.id" 
            class="product-item"
          >
            <ProductCard 
              :product="product" 
              @image-enter="isPaused = true"
              @image-leave="isPaused = false"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ProductCard from '@/components/ProductCard.vue'
import { getHotProducts, getNewProducts } from '@/api/product'
import { getBanners } from '@/api/banner'

const isPaused = ref(false)

// 轮播图数据（从后端获取）
const banners = ref([])
// 默认轮播图（后端无数据时使用）
const defaultBanners = [
  { id: 1, imageUrl: 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?q=80&w=2574&auto=format&fit=crop' },
  { id: 2, imageUrl: 'https://images.unsplash.com/photo-1563911302283-d2bc129e7c1f?q=80&w=2670&auto=format&fit=crop' }
]

const hotProducts = ref([])
const newProducts = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    // 加载轮播图
    const bannerRes = await getBanners()
    if (bannerRes && bannerRes.length > 0) {
      banners.value = bannerRes
    } else {
      banners.value = defaultBanners
    }

    const [hotRes, newRes] = await Promise.all([
      getHotProducts(),
      getNewProducts()
    ])
    
    if (hotRes) {
      // Get more products for the marquee (e.g., top 10)
      hotProducts.value = hotRes.map(p => ({
        ...p,
        image: p.mainImage || p.image 
      })).slice(0, 10) 
    }
    
    if (newRes) {
      newProducts.value = newRes.map(p => ({
        ...p,
        image: p.mainImage || p.image
      })).slice(0, 10)
    }

  } catch (e) {
    console.error('API Load Failed', e)
    // 加载失败时使用默认轮播图
    banners.value = defaultBanners
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-container {
  padding-bottom: 60px;
}

.banner-section {
  margin-bottom: 0;
}

.banner-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.85);
}

.banner-content {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  width: 80%;
}

.banner-brand {
  font-size: 80px;
  font-family: 'Cinzel', serif;
  margin-bottom: 30px;
  background: linear-gradient(to bottom, #ffffff 30%, #dff0d8 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  letter-spacing: 24px;
  font-weight: 400;
  text-transform: uppercase;
  filter: drop-shadow(0 4px 10px rgba(0,0,0,0.3));
  line-height: 1.2;
}

.banner-title {
  font-size: 56px;
  font-family: 'ZCOOL XiaoWei', 'Noto Serif SC', serif;
  margin-bottom: 25px;
  filter: drop-shadow(0 4px 20px rgba(0,0,0,0.4));
  letter-spacing: 14px;
  font-weight: 400;
  background: linear-gradient(to bottom, #ffffff 30%, #dff0d8 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  position: relative;
  display: inline-block;
}

.banner-title::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #c9a96e, transparent);
}

.banner-subtitle {
  font-size: 18px;
  margin-top: 40px;
  margin-bottom: 60px;
  font-family: 'Noto Serif SC', 'Playfair Display', serif;
  font-weight: 300;
  letter-spacing: 8px;
  color: rgba(255,255,255,0.85);
  font-style: italic;
}

.banner-btn {
  background: transparent;
  border: 2px solid;
  border-image: linear-gradient(to right, #ffffff, #dff0d8) 1;
  font-family: 'ZCOOL XiaoWei', serif;
  padding: 16px 48px;
  font-size: 15px;
  letter-spacing: 3px;
  color: #e0f0e0;
  transition: all 0.4s ease;
}

.banner-btn:hover {
  background: rgba(255,255,255,0.15);
  border-image: linear-gradient(to right, #ffffff, #dff0d8) 1;
  color: #ffffff;
}

.section-spacer {
  padding: 30px 0;
  width: 100%;
}

.section-spacer.bg-alt {
  background-color: var(--vt-c-white-soft);
}

.centered-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 36px;
  font-family: 'Noto Serif SC', 'ZCOOL XiaoWei', serif; /* Artistic serif font */
  color: #2c5e2e; /* Dark tea green/Ink green */
  margin-bottom: 12px;
  font-weight: 700;
  letter-spacing: 2px;
}

.interactive-title {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
}

.interactive-title:hover {
  color: #1a3c1b; /* Darker green on hover */
  transform: translateY(-2px);
  text-shadow: 0 4px 12px rgba(44, 94, 46, 0.2);
}

.interactive-title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #c9a96e; /* Gold underline */
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.interactive-title:hover::after {
  width: 100%;
}

.section-subtitle {
  font-family: 'Playfair Display', serif;
  font-style: italic;
  color: #888;
  font-size: 16px;
  margin-bottom: 25px;
}

.section-divider {
  width: 60px;
  height: 3px;
  background-color: var(--vt-c-tea-gold);
  margin: 0 auto;
}

@keyframes scroll-right {
  0% {
    transform: translateX(-50%);
  }
  100% {
    transform: translateX(0);
  }
}

.marquee-container {
  overflow: hidden;
  position: relative;
  width: 100%;
  padding: 10px 0;
  mask-image: linear-gradient(to right, transparent, black 5%, black 95%, transparent);
}

.marquee-content {
  display: flex;
  width: max-content;
  animation: scroll-right 40s linear infinite;
  /* Start from left (-50%) and move right (0) */
  transform: translateX(-50%); 
}

.product-item {
  width: 280px;
  flex-shrink: 0;
  margin: 0 15px; /* Gap between items */
}

/* Animations */
.fade-in-up {
  animation: fadeInUp 1s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

.delay-200 { animation-delay: 0.2s; }
.delay-400 { animation-delay: 0.4s; }

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .product-item {
    width: 220px;
    margin: 0 10px;
  }
}
</style>
