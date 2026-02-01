<template>
  <el-container class="layout-container">
    <el-header height="70px" class="header" :class="{ 'header-scrolled': isScrolled }">
      <div class="header-content">
        <!-- Left Section: Logo + Nav -->
        <div class="header-left">
          <h1 class="logo" @click="$router.push('/')">中国茶文化</h1>
          

          <el-menu
            mode="horizontal"
            :router="true"
            :default-active="$route.path"
            class="nav-menu"
            :ellipsis="false" 
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/products">茶叶商城</el-menu-item>
            <el-menu-item index="/articles">茶文化</el-menu-item>
          </el-menu>

        </div>

        <!-- Right Section: Search + Actions -->
        <div class="header-right">
           <!-- 领券中心入口 -->
           <el-tooltip content="领券中心" placement="bottom" :show-after="300">
             <div class="coupon-entry" @click="$router.push('/coupon/center')">
               <el-badge is-dot class="coupon-badge">
                 <el-icon class="coupon-icon"><Present /></el-icon>
               </el-badge>
             </div>
           </el-tooltip>

           <div class="search-wrapper">
              <SearchInput />
           </div>

          <div class="action-items">
            <!-- 购物车图标 -->
            <el-tooltip content="购物车" placement="bottom" :show-after="300">
              <el-badge :value="cartStore.totalCount" :max="99" class="cart-badge" :hidden="cartStore.totalCount === 0">
                <el-button link class="icon-btn" icon="ShoppingCart" @click="handleCartClick" />
              </el-badge>
            </el-tooltip>

            <!-- 分隔线 -->
            <div class="action-divider"></div>

            <!-- 用户信息/登录按钮 -->
            <template v-if="userStore.token">
              <el-dropdown trigger="click" @command="handleCommand">
                <div class="user-avatar-container">
                  <div class="avatar-wrapper">
                    <el-avatar :size="36" :src="userStore.userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                    <el-icon class="avatar-arrow"><ArrowDown /></el-icon>
                  </div>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="user">个人中心</el-dropdown-item>
                    <el-dropdown-item command="wallet">我的钱包</el-dropdown-item>
                    <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
               <el-button type="primary" round size="small" class="login-btn" @click="showAuthModal = true">登录/注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <el-footer height="auto" class="footer" v-if="!hideFooter">
      <div class="footer-content">
        <div class="footer-funcs">
          <div class="footer-col">
            <h4>关于茗韵茶业</h4>
            <p>茗韵茶业成立于2008年，专注于高品质中国茶叶的种植、加工与销售。</p>
            <p>我们秉承"源头直采、匠心制茶"的理念，与全国多个核心产茶区建立长期合作。</p>
            <p>致力于将正宗中国好茶带给每一位爱茶人士。</p>
          </div>
          <div class="footer-col">
             <h4>客服支持</h4>
             <p>服务热线：400-888-9527</p>
             <p>客服邮箱：service@mingyuntea.com</p>
             <p>工作时间：周一至周日 9:00 - 21:00</p>
             <p>公司地址：浙江省杭州市西湖区龙井路168号</p>
          </div>

          <div class="footer-col">
             <h4>服务中心</h4>
             <p class="footer-link" @click="$router.push('/trace')">防伪溯源</p>
             <p class="footer-link" @click="$router.push('/products')">茶叶商城</p>
             <p class="footer-link" @click="$router.push('/articles')">茶文化</p>
          </div>

        </div>
        <div class="copyright">
          &copy; 2026 茗韵茶业有限公司 版权所有 | 浙ICP备2026888888号-1 | 营业执照
        </div>
      </div>
    </el-footer>

    <AuthModal v-model="showAuthModal" />
    <AiChatWidget />
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search, User, Lock, Iphone, Present, ArrowDown } from '@element-plus/icons-vue'
import AuthModal from '@/components/AuthModal.vue'
import SearchInput from '@/components/SearchInput.vue'
import AiChatWidget from '@/components/AiChatWidget.vue'

const cartStore = { totalCount: 0 } 
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isScrolled = ref(false)
const showAuthModal = ref(false)

// Hide footer on cart page
const hideFooter = computed(() => route.path === '/cart')

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

import { logout } from '@/api/auth'
import { getCart } from '@/api/cart'
import { ElMessage } from 'element-plus'

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) {
      console.error(e)
    }
    userStore.logout()
    router.push('/')
    showAuthModal.value = true
  } else if (command === 'user') {
    router.push('/user/profile')
  } else if (command === 'wallet') {
    router.push('/user/wallet')
  } else if (command === 'orders') {
    router.push('/user/orders')
  }
}

const handleCartClick = async () => {
  if (!userStore.token) {
    showAuthModal.value = true
    return
  }
  try {
    const res = await getCart()
    const items = res.items || res || []
    if (items.length > 0) {
      router.push('/cart')
    } else {
      ElMessage.warning('购物车还没有加入商品')
    }
  } catch (e) {
    console.error(e)
    router.push('/cart') // 出错时兜底跳转
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #fcfbf7; /* Global soft background matching styles */
}

/* Header */
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  padding: 0;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
}

/* Left Section */
.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
  cursor: pointer;
  letter-spacing: 1px;
}

.nav-menu {
  background: transparent;
  border: none;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  color: #666;
  font-family: var(--font-body);
  font-size: 15px;
  background: transparent !important;
  border: none !important;
  padding: 0 15px;
  height: 70px;
  line-height: 70px;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: #333 !important;
  font-weight: 600;
  border-bottom: none !important;
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  color: #333 !important;
}

/* Coupon Entry */
.coupon-entry {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px; /* Spacing from search bar */
  transition: transform 0.3s;
}

.coupon-entry:hover {
  transform: scale(1.1) rotate(5deg);
}

.coupon-icon {
  font-size: 24px;
  color: #ff4757; /* Red color */
}

/* Right Section */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* Pill Search Bar */
.search-wrapper {
  width: 240px;
  margin-right: 20px;
}

.header-search :deep(.el-input__wrapper) {
  width: 100%;
  border-radius: 99px; /* Pill shape */
  background-color: #f2f4f7;
  box-shadow: none;
  border: none;
  padding: 4px 15px;
  transition: all 0.3s;
}

.header-search :deep(.el-input__wrapper:hover),
.header-search :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #e0e0e0;
}

.action-items {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-divider {
  width: 1px;
  height: 20px;
  background: #e0e0e0;
}

.icon-btn {
  font-size: 22px;
  color: #333;
}

.icon-btn:hover {
  color: var(--vt-c-tea-green-dark);
}

.user-avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px;
  transition: all 0.3s;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.user-avatar-container:hover {
  opacity: 0.8;
}

/* 头像下拉箭头 */
.avatar-arrow {
  font-size: 10px;
  color: #999;
  transition: transform 0.3s;
}

.user-avatar-container:hover .avatar-arrow {
  color: #666;
  transform: translateY(2px);
}

.text-btn {
  color: #666;
  font-size: 14px;
}

.text-btn:hover {
  color: #333;
}

.login-btn {
  background: linear-gradient(135deg, #8a9a5b 0%, #7a8a4b 100%);
  border: none;
  font-size: 13px;
  padding: 8px 20px;
  font-weight: 500;
}

.login-btn:hover {
  background: linear-gradient(135deg, #7a8a4b 0%, #6a7a3b 100%);
}

/* Main Content */
.main-content {
  flex: 1;
  width: 100%;
  padding: 0;
  padding-top: 70px; /* Offset for fixed header */
}

/* Footer */
.footer {
  background: #f9f9f9;
  color: #555;
  padding: 60px 0 30px;
}

.footer-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 80px;
}

.footer-funcs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}

.footer-col {
  text-align: left;
}

.footer-col:first-child {
  flex: 1.2;
}

.footer-col:nth-child(2) {
  flex: 1;
}

.footer-col:last-child {
  flex: 0.6;
}

.footer-col h4 {
  font-family: var(--font-heading);
  color: #1a1a1a;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.copyright {
  text-align: center;
  border-top: 1px solid rgba(0,0,0,0.06);
  padding-top: 30px;
  font-size: 12px;
  letter-spacing: 1px;
  color: #888;
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.footer-link {
  cursor: pointer;
  transition: color 0.2s;
}
.footer-link:hover {
  color: #10b981;
}

</style>
