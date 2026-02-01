<template>
  <div class="auth-container">
    <div class="glass-card">
      <div class="auth-header">
        <h2>欢迎回来</h2>
        <p class="subtitle">登录您的账户</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="0"
        size="large"
        class="minimal-form"
      >
        <el-form-item prop="account">
          <el-input 
            v-model="loginForm.account" 
            placeholder="用户名 / 手机号" 
            class="minimal-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            show-password
            class="minimal-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <button class="login-btn" @click.prevent="handleLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </el-form-item>
        <div class="auth-footer">
          <span class="text">还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        
        // Mock token handling if store differs from mock
        const token = res.accessToken || res.token
        const refreshToken = res.refreshToken
        const user = res.userInfo || res.user
        
        userStore.setToken(token, refreshToken)
        userStore.setUserInfo(user)
        
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error(error)
        ElMessage.error('登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  /* Background is now global in base.css */
}

.glass-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 4px; /* Slightly sharper corners for premium feel */
  box-shadow: 0 15px 35px rgba(0,0,0,0.1);
  text-align: center;
}

.auth-header h2 {
  font-family: var(--font-heading);
  font-size: 28px;
  color: var(--vt-c-tea-green-dark);
  margin-bottom: 5px;
  font-weight: 400;
}

.auth-header .subtitle {
  font-family: var(--font-body);
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
  font-style: italic;
}

/* Minimal Input Styling Override */
.minimal-form :deep(.el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
  border-radius: 0;
  border-bottom: 1px solid #ccc;
  padding-left: 0;
}

.minimal-form :deep(.el-input__wrapper.is-focus) {
  border-bottom: 1px solid var(--vt-c-tea-green-dark);
}

.minimal-form :deep(.el-input__inner) {
  text-align: center; /* Center input text for elegance */
  font-size: 16px;
  color: #333;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: var(--vt-c-tea-green-dark);
  color: white;
  border: none;
  font-size: 14px;
  letter-spacing: 2px;
  cursor: pointer;
  margin-top: 10px;
  transition: all 0.3s;
}

.login-btn:hover {
  background-color: #1e3529;
}

.login-btn:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 20px;
  font-size: 14px;
}

.auth-footer .text {
  color: #666;
  margin-right: 5px;
}

.auth-footer .link {
  color: var(--vt-c-tea-green-dark);
  font-weight: bold;
  border-bottom: 1px solid transparent;
}

.auth-footer .link:hover {
  border-bottom-color: var(--vt-c-tea-green-dark);
}
</style>
