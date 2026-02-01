<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Shop /></el-icon>
        <span>商家管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="resolvePath(route)">
            <el-icon v-if="route.meta && route.meta.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <span>{{ route.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
          <!-- 这里可以加面包屑 -->
          <h3>{{ currentTitle }}</h3>
        </div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userStore.userInfo.username || userStore.userInfo.phone || '商家' }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Shop, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  return route.path
})

const currentTitle = computed(() => {
  return route.meta.title || ''
})

const menuRoutes = computed(() => {
  // 简单的过滤，只显示 layout 下 children 中有 meta.title 且不 hidden 的
  const layoutRoute = router.options.routes.find(r => r.path === '/')
  if (layoutRoute && layoutRoute.children) {
    return layoutRoute.children.filter(child => child.meta && child.meta.title && !child.meta.hidden)
  }
  return []
})

function resolvePath(routeItem) {
  return '/' + routeItem.path
}

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3649;
  color: #fff;
  gap: 10px;
}
.logo-icon {
  font-size: 24px;
  color: #409EFF;
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.user-info {
  cursor: pointer;
}
.el-dropdown-link {
  display: flex;
  align-items: center;
  outline: none;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
