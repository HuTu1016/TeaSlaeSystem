<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Menu as IconMenu, 
  Goods, 
  List, 
  User, 
  Setting, 
  SwitchButton,
  Monitor,
  Picture,
  Ticket,
  ChatDotSquare,
  Search,
  Check
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const menus = computed(() => {
    const role = userStore.role
    if (role === 'MERCHANT') {
        return [
            { index: '/dashboard', icon: IconMenu, title: '控制台' },
            { 
              index: '/merchant/products', 
              icon: Goods, 
              title: '商品管理',
              children: [
                { index: '/merchant/products/list', title: '商品列表' },
                { index: '/merchant/products/publish', title: '发布商品' }
              ]
            },
            { index: '/merchant/orders', icon: List, title: '订单管理' }
        ]
    } else if (role === 'ADMIN') {
        return [
            { index: '/dashboard', icon: IconMenu, title: '控制台' },
            { index: '/admin/orders', icon: List, title: '订单管理' },
            { 
              index: '/admin/products-menu', 
              icon: Goods, 
              title: '商品管理',
              children: [
                { index: '/admin/products', title: '商品列表' },
                { index: '/admin/products/audit', title: '商品审核' }
              ]
            },
            { index: '/admin/after-sales', icon: Setting, title: '售后管理' },
            { index: '/admin/users', icon: User, title: '用户管理' },
            { index: '/admin/merchants', icon: Goods, title: '商家管理' },
            { index: '/admin/contents', icon: Monitor, title: '内容管理' },
            { 
              index: '/admin/marketing', 
              icon: Ticket, 
              title: '营销与其他',
              children: [
                { index: '/admin/categories', title: '分类管理' },
                { index: '/admin/banners', title: '轮播图' },
                { index: '/admin/coupons', title: '优惠券' }
              ]
            },
            {
               index: '/admin/audit',
               icon: Check,
               title: '审核中心',
               children: [
                   { index: '/admin/reviews', title: '评价审核' },
                   { index: '/admin/trace', title: '溯源审核' }
               ]
            },
            { index: '/admin/search', icon: Search, title: '搜索管理' }
        ]
    }
    return []
})

const handleLogout = () => {
    userStore.logout()
    router.push('/login')
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <img src="https://cdn.pixabay.com/photo/2015/07/02/10/22/training-828726_1280.jpg" alt="Logo">
        <span>茶销后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        router
        unique-opened
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="item in menus" :key="item.index">
           <el-sub-menu v-if="item.children" :index="item.index">
             <template #title>
               <el-icon><component :is="item.icon" /></el-icon>
               <span>{{ item.title }}</span>
             </template>
             <el-menu-item v-for="sub in item.children" :key="sub.index" :index="sub.index">
               {{ sub.title }}
             </el-menu-item>
           </el-sub-menu>
           <el-menu-item v-else :index="item.index">
             <el-icon><component :is="item.icon" /></el-icon>
             <span>{{ item.title }}</span>
           </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
            <!-- Breadcrumb placeholder -->
             <span>当前位置: {{ route.meta.title || '首页' }}</span>
        </div>
        <div class="user-actions">
           <span>{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
           <el-button link :icon="SwitchButton" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      
      <el-main class="main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  
  .aside {
    background-color: #304156;
    color: #bfcbd9;
    display: flex;
    flex-direction: column;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      padding: 0 20px;
      font-size: 18px;
      font-weight: bold;
      color: white;
      background-color: #2b2f3a;
      
      img {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 10px;
      }
    }
    
    .el-menu-vertical {
      border-right: none;
      flex: 1;
    }
  }
  
  .header {
    background-color: #fff;
    border-bottom: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    
    .user-actions {
        display: flex;
        align-items: center;
        gap: 15px;
    }
  }
  
  .main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style>
