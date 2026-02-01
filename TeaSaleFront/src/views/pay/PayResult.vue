<template>
  <div class="pay-result">
    <el-result
      icon="success"
      title="支付成功"
      sub-title="您的订单已成功支付，我们将尽快为您发货"
      v-if="success"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/user/orders')">查看订单</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
    
    <el-result
      icon="error"
      title="支付失败"
      sub-title="支付遇到问题，请重试"
      v-else
    >
      <template #extra>
        <el-button type="primary" @click="$router.back()">重新支付</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const success = computed(() => route.query.success === 'true')
</script>

<style scoped>
.pay-result {
    padding: 50px 0;
    background: white;
}
</style>
