<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-header">
        <el-icon class="logo-icon"><Shop /></el-icon>
        <span class="title">商家管理后台</span>
      </div>
      <el-card class="login-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>账号登录</span>
          </div>
        </template>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          size="large"
        >
          <el-form-item prop="account" label="账号">
            <el-input 
              v-model="loginForm.account" 
              placeholder="请输入手机号/用户名" 
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input 
              v-model="loginForm.password" 
              placeholder="请输入密码" 
              show-password 
              prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              class="w-full" 
              :loading="loading" 
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="footer">
        <div class="register-link">
          还没有账号？<el-link type="primary" @click="$router.push('/register')">立即入驻</el-link>
        </div>
        <div class="copyright">TeaSale Store Management System &copy; 2026</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/api/request'
import { User, Lock, Shop } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  account: '',
  password: ''
})

const loginRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用后端登录接口
        // 假设接口为 POST /auth/login
        const res = await request.post('/auth/login', {
          account: loginForm.account, // 这里后端可能接收的是 username/phone，根据 API 文档调整
          password: loginForm.password,
          role: 'MERCHANT'
        })

        // 假设返回结构:
        // { accessToken: '...', role: 'MERCHANT', userInfo: { ... } }
        // 注意：后端可能返回字段名不同，这里做适配
        if (res) {
          if (res.role && res.role !== 'MERCHANT' && res.role !== 'ADMIN') {
             ElMessage.error('非商家账号，禁止登录')
             loading.value = false
             return
          }

          userStore.setToken(res.accessToken)
          userStore.setUserInfo(res.userInfo || {})
          
          ElMessage.success('登录成功')
          const redirect = route.query.redirect || '/'
          router.push(redirect)
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #10B981 0%, #047857 100%);
  position: relative;
  overflow: hidden;
}

/* 装饰背景圆 */
.login-container::before {
  content: '';
  position: absolute;
  top: -10%;
  left: -10%;
  width: 50%;
  height: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%);
  border-radius: 50%;
}

.login-content {
  width: 100%;
  max-width: 420px;
  padding: 20px;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 36px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-card {
  border-radius: 12px;
  backdrop-filter: blur(10px);
  background-color: rgba(255, 255, 255, 0.95);
  border: none;
}

.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.w-full {
  width: 100%;
}

.footer {
  margin-top: 20px;
  text-align: center;
}
.register-link {
  color: #fff;
  margin-bottom: 8px;
}
.copyright {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}
</style>
