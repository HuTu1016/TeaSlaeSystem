<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
    account: '',
    password: ''
})

const rules = {
    account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formRef = ref(null)

const handleLogin = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                const res = await request.post('/auth/login', { ...form, role: 'ADMIN' })
                userStore.setToken(res.accessToken)
                userStore.setUserInfo(res.userInfo)
                userStore.setRole(res.role)
                
                ElMessage.success('登录成功')
                router.push('/')
            } catch (e) {
                console.error(e)
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>茶叶销售管理系统</h2>
        <p>商家与管理员统一入口</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="account">
          <el-input 
            v-model="form.account" 
            placeholder="账号/手机号" 
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  background-image: url('https://cdn.pixabay.com/photo/2015/07/02/10/22/training-828726_1280.jpg');
  background-size: cover;
  background-position: center;
  
  .login-card {
    width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    
    .login-header {
      text-align: center;
      margin-bottom: 30px;
      
      h2 {
        color: #333;
        margin: 0 0 10px 0;
      }
      p {
        color: #999;
        margin: 0;
      }
    }
    
    .login-btn {
      width: 100%;
    }
  }
}
</style>
