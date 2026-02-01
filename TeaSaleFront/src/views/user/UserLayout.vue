<template>
  <div class="user-layout">
    <div class="sidebar">
      <el-menu
        :default-active="$route.path"
        router
        class="user-menu"
      >
        <el-menu-item index="/user/profile">
          <el-icon><User /></el-icon>
          <span>个人资料</span>
        </el-menu-item>
        <el-menu-item index="/user/wallet">
          <el-icon><Medal /></el-icon>
          <span>会员中心</span>
        </el-menu-item>
        <el-menu-item index="/user/address">
          <el-icon><Location /></el-icon>
          <span>收货地址</span>
        </el-menu-item>
        <el-menu-item index="/user/orders">
          <el-icon><List /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        <el-menu-item index="/user/after-sales">
          <el-icon><Service /></el-icon>
          <span>售后服务</span>
        </el-menu-item>
        <el-menu-item index="/user/reviews">
          <el-icon><ChatLineSquare /></el-icon>
          <span>我的评价</span>
        </el-menu-item>
        <el-menu-item index="/user/favorites">
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
        </el-menu-item>
        <el-menu-item index="/user/footprints">
          <el-icon><Timer /></el-icon>
          <span>我的足迹</span>
        </el-menu-item>
        <el-menu-item index="/user/coupons">
          <el-icon><Ticket /></el-icon>
          <span>我的优惠券</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="user-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { User, Location, List, Service, ChatLineSquare, Star, Timer, Ticket, Medal } from '@element-plus/icons-vue'
</script>

<style scoped>
.user-layout {
  display: flex;
  margin-top: 20px;
  gap: 20px;
}

.sidebar {
  width: 200px;
  background: white;
  min-height: 500px;
  border-radius: 4px;
}

.user-menu {
  border-right: none;
}

.user-content {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 4px;
  min-height: 500px;
}
</style>
