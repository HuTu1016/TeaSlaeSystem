<template>
  <div class="register-container">
    <div class="register-card">
      <h2>商家入驻</h2>
      <p class="subtitle">填写信息完成注册</p>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="large">
        <el-divider content-position="left">账户信息</el-divider>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="4-20位字母数字下划线" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="8-20位，含字母和数字" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        
        <el-divider content-position="left">店铺信息</el-divider>
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="您的店铺名称" />
        </el-form-item>
        <el-form-item label="店铺描述" prop="shopDesc">
          <el-input v-model="form.shopDesc" type="textarea" :rows="2" placeholder="简单描述您的店铺" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="客服联系电话" />
        </el-form-item>
        <el-form-item label="营业执照" prop="businessLicense">
          <el-input v-model="form.businessLicense" placeholder="营业执照图片URL（可选）" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleSubmit" :loading="loading">立即注册</el-button>
        </el-form-item>
        
        <div class="login-link">
          已有账号？<el-link type="primary" @click="$router.push('/login')">去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  username: '',
  password: '',
  confirmPassword: '',
  shopName: '',
  shopDesc: '',
  contactPhone: '',
  businessLicense: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  username: [
    { min: 4, max: 20, message: '用户名长度4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字、下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度8-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  shopName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { max: 50, message: '店铺名称最长50字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/auth/merchant-register', {
          phone: form.phone,
          username: form.username,
          password: form.password,
          shopName: form.shopName,
          shopDesc: form.shopDesc,
          contactPhone: form.contactPhone,
          businessLicense: form.businessLicense
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}
.register-card {
  background: white;
  padding: 40px;
  border-radius: 12px;
  width: 500px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
}
.register-card h2 {
  text-align: center;
  margin-bottom: 5px;
  color: #303133;
}
.subtitle {
  text-align: center;
  color: #909399;
  margin-bottom: 20px;
}
.login-link {
  text-align: center;
  margin-top: 15px;
  color: #606266;
}
</style>
