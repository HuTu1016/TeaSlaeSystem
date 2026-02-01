<template>
  <el-dialog
    v-model="dialogVisible"
    :width="420"
    :show-close="false"
    class="auth-dialog"
    destroy-on-close
    align-center
  >
    <div class="auth-content">
      <!-- Header -->
      <div class="auth-header">
        <h2>{{ headerTitle }}</h2>
        <p class="subtitle">{{ headerSubtitle }}</p>
      </div>

      <!-- Password Login Form -->
      <el-form
        v-if="mode === 'login'"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        size="large"
        class="minimal-form"
      >
        <el-form-item prop="account">
          <el-input 
            v-model="loginForm.account" 
            placeholder="用户名 / 手机号" 
            class="minimal-input"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            show-password
            class="minimal-input"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <button class="auth-btn" @click.prevent="handleLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </el-form-item>
        <div class="login-options">
          <a class="link" @click="mode = 'smsLogin'">短信验证码登录</a>
          <span class="divider">|</span>
          <a class="link" @click="mode = 'forgot'">忘记密码</a>
        </div>
      </el-form>

      <!-- SMS Login Form -->
      <el-form
        v-else-if="mode === 'smsLogin'"
        ref="smsLoginFormRef"
        :model="smsLoginForm"
        :rules="smsLoginRules"
        label-width="0"
        size="large"
        class="minimal-form"
      >
        <el-form-item prop="phone">
          <el-input 
            v-model="smsLoginForm.phone" 
            placeholder="手机号" 
            class="minimal-input"
            :prefix-icon="Iphone"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item prop="code">
          <div class="code-input-wrapper">
            <el-input 
              v-model="smsLoginForm.code" 
              placeholder="验证码" 
              class="minimal-input code-input"
              :prefix-icon="Message"
              @keyup.enter="handleSmsLogin"
            />
            <el-button 
              class="send-code-btn" 
              :disabled="countdown > 0 || sendingCode"
              @click="sendCode('LOGIN')"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <button class="auth-btn" @click.prevent="handleSmsLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </el-form-item>
        <div class="login-options">
          <a class="link" @click="mode = 'login'">密码登录</a>
        </div>
      </el-form>

      <!-- Register Form -->
      <el-form
        v-else-if="mode === 'register'"
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="0"
        size="large"
        class="minimal-form"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="用户名 (4-20位字母/号码)" 
            class="minimal-input"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="手机号" 
            class="minimal-input"
            :prefix-icon="Iphone"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item prop="code">
          <div class="code-input-wrapper">
            <el-input 
              v-model="registerForm.code" 
              placeholder="验证码" 
              class="minimal-input code-input"
              :prefix-icon="Message"
            />
            <el-button 
              class="send-code-btn" 
              :disabled="registerCountdown > 0 || sendingCode"
              @click="sendRegisterCode"
            >
              {{ registerCountdown > 0 ? `${registerCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="设置密码 (8-20位)" 
            class="minimal-input"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="确认密码" 
            class="minimal-input"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <button class="auth-btn" @click.prevent="handleRegister" :disabled="loading">
            {{ loading ? '注册中...' : '立即注册' }}
          </button>
        </el-form-item>
      </el-form>

      <!-- Forgot Password Form -->
      <el-form
        v-else-if="mode === 'forgot'"
        ref="forgotFormRef"
        :model="forgotForm"
        :rules="forgotRules"
        label-width="0"
        size="large"
        class="minimal-form"
      >
        <el-form-item prop="phone">
          <el-input 
            v-model="forgotForm.phone" 
            placeholder="手机号" 
            class="minimal-input"
            :prefix-icon="Iphone"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item prop="code">
          <div class="code-input-wrapper">
            <el-input 
              v-model="forgotForm.code" 
              placeholder="验证码" 
              class="minimal-input code-input"
              :prefix-icon="Message"
            />
            <el-button 
              class="send-code-btn"
              :disabled="countdown > 0 || sendingCode"
              @click="sendCode('RESET')"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input 
            v-model="forgotForm.newPassword" 
            type="password" 
            placeholder="新密码 (8-20位)" 
            class="minimal-input"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="forgotForm.confirmPassword" 
            type="password" 
            placeholder="确认新密码" 
            class="minimal-input"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <button class="auth-btn" @click.prevent="handleResetPassword" :disabled="loading">
            {{ loading ? '重置中...' : '重置密码' }}
          </button>
        </el-form-item>
        <div class="login-options">
          <a class="link" @click="mode = 'login'">返回登录</a>
        </div>
      </el-form>

      <!-- Footer Toggle -->
      <div class="auth-footer" v-if="mode === 'login' || mode === 'smsLogin'">
        <span class="text">还没有账号？</span>
        <a class="link" @click="mode = 'register'">立即注册</a>
      </div>
      <div class="auth-footer" v-else-if="mode === 'register'">
        <span class="text">已有账号？</span>
        <a class="link" @click="mode = 'login'">去登录</a>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { User, Lock, Iphone, Message } from '@element-plus/icons-vue'
import { login, register, sendSmsCode, smsLogin, resetPassword, checkUsernameExists, checkPhoneExists } from '@/api/auth'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'login-success'])

const router = useRouter()
const userStore = useUserStore()
const mode = ref('login') // 'login', 'smsLogin', 'register', 'forgot'
const loading = ref(false)
const countdown = ref(0)
const registerCountdown = ref(0)  // 注册验证码倒计时
const usernameChecking = ref(false)  // 用户名校验中状态
const phoneChecking = ref(false)  // 手机号校验中状态
const sendingCode = ref(false)

// Refs
const loginFormRef = ref(null)
const smsLoginFormRef = ref(null)
const registerFormRef = ref(null)
const forgotFormRef = ref(null)

// Dialog visibility wrapper
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// Header text
const headerTitle = computed(() => {
  switch (mode.value) {
    case 'login': 
    case 'smsLogin': return '欢迎回来'
    case 'register': return '注册账号'
    case 'forgot': return '找回密码'
    default: return '欢迎'
  }
})

const headerSubtitle = computed(() => {
  switch (mode.value) {
    case 'login': return '使用密码登录您的账户'
    case 'smsLogin': return '使用手机验证码登录'
    case 'register': return '加入茗茶轩，开启茶道之旅'
    case 'forgot': return '通过手机验证重置密码'
    default: return ''
  }
})

// Reset state when closed
watch(dialogVisible, (val) => {
  if (!val) {
    // Clear sensitive data when modal closes
    loginForm.password = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    registerForm.code = ''
    forgotForm.code = ''
    forgotForm.newPassword = ''
    forgotForm.confirmPassword = ''
    smsLoginForm.code = ''
    
    setTimeout(() => {
      mode.value = 'login'
      countdown.value = 0
    }, 300)
  }
})

// Forms
const loginForm = reactive({ account: '', password: '' })
const smsLoginForm = reactive({ phone: '', code: '' })
const registerForm = reactive({ username: '', phone: '', code: '', password: '', confirmPassword: '' })
const forgotForm = reactive({ phone: '', code: '', newPassword: '', confirmPassword: '' })

// Rules
const loginRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const smsLoginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validateForgotPass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== forgotForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8 || value.length > 20) {
    callback(new Error('密码必须包含字母和数字，长度8-20位'))
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含字母和数字，长度8-20位'))
  } else {
    callback()
  }
}

// 异步校验用户名是否已存在
const validateUsernameAsync = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  if (value.length < 4 || value.length > 20) {
    callback(new Error('长度在 4 到 20 个字符'))
    return
  }
  
  try {
    usernameChecking.value = true
    const res = await checkUsernameExists(value)
    if (res.exists) {
      callback(new Error('用户名已存在，请换一个'))
    } else {
      callback()
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
    // 接口异常时不阻止注册，交给后端统一处理
    callback()
  } finally {
    usernameChecking.value = false
  }
}

// 异步校验手机号是否已存在
const validatePhoneAsync = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
    return
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
    return
  }
  
  try {
    phoneChecking.value = true
    const res = await checkPhoneExists(value)
    if (res.exists) {
      callback(new Error('手机号已注册，请直接登录'))
    } else {
      callback()
    }
  } catch (error) {
    console.error('检查手机号失败:', error)
    callback()
  } finally {
    phoneChecking.value = false
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'change' },
    { validator: validateUsernameAsync, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'change' },
    { validator: validatePhoneAsync, trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
}

const forgotRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateForgotPass2, trigger: 'blur' }]
}

// 发送验证码
const sendCode = async (type) => {
  const phone = type === 'RESET' ? forgotForm.phone : smsLoginForm.phone
  
  if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  sendingCode.value = true
  try {
    await sendSmsCode({ phone, type })
    ElMessage.success('验证码已发送')
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error(error)
    // 错误由全局拦截器处理，或在此处仅处理倒计时逻辑
  } finally {
    sendingCode.value = false
  }
}

// 发送注册验证码
const sendRegisterCode = async () => {
  const phone = registerForm.phone
  
  if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  sendingCode.value = true
  try {
    await sendSmsCode({ phone, type: 'REGISTER' })
    ElMessage.success('验证码已发送')
    // 开始倒计时
    registerCountdown.value = 60
    const timer = setInterval(() => {
      registerCountdown.value--
      if (registerCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error(error)
  } finally {
    sendingCode.value = false
  }
}

// 处理登录成功
const handleLoginSuccess = (res) => {
  const token = res.accessToken || res.token
  const user = res.userInfo || res.user
  
  userStore.setToken(token)
  userStore.setUserInfo(user)
  
  ElMessage.success('登录成功')
  dialogVisible.value = false
  emit('login-success')
  
  if (router.currentRoute.value.path.includes('/login') || router.currentRoute.value.path.includes('/register')) {
    router.push('/')
  }
}

// 密码登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  if (loading.value) return // Prevent multiple submissions
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    try {
      const res = await login(loginForm)
      handleLoginSuccess(res)
    } catch (error) {
      console.error('Login API Error:', error)
      // 全局拦截器已提示错误，此处不再提示
    } finally {
      loading.value = false
    }
  } catch (validationError) {
    console.log('Validation failed', validationError)
    return
  }
}

// 短信验证码登录
const handleSmsLogin = async () => {
  if (!smsLoginFormRef.value) return
  if (loading.value) return // Prevent multiple submissions
  
  try {
    await smsLoginFormRef.value.validate()
    loading.value = true
    try {
      const res = await smsLogin(smsLoginForm)
      handleLoginSuccess(res)
    } catch (error) {
      console.error('SMS Login API Error:', error)
    } finally {
      loading.value = false
    }
  } catch (validationError) {
    return
  }
}

// 注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  if (loading.value) return // Prevent multiple submissions
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    try {
      await register({
        username: registerForm.username,
        phone: registerForm.phone,
        code: registerForm.code,
        password: registerForm.password
      })
      
      ElMessage.success('注册成功，已自动为您登录')
      const res = await login({ account: registerForm.username, password: registerForm.password })
      handleLoginSuccess(res)
    } catch (error) {
      console.error('Register API Error:', error)
    } finally {
      loading.value = false
    }
  } catch (validationError) {
    return
  }
}

// 重置密码
const handleResetPassword = async () => {
  if (!forgotFormRef.value) return
  if (loading.value) return // Prevent multiple submissions
  
  try {
    await forgotFormRef.value.validate()
    loading.value = true
    try {
      await resetPassword({
        phone: forgotForm.phone,
        code: forgotForm.code,
        newPassword: forgotForm.newPassword
      })
      
      ElMessage.success('密码重置成功，请使用新密码登录')
      mode.value = 'login'
      loginForm.account = forgotForm.phone
      loginForm.password = ''
    } catch (error) {
      console.error('Reset Password API Error:', error)
    } finally {
      loading.value = false
    }
  } catch (validationError) {
    return
  }
}
</script>

<style scoped>
.auth-content {
  padding: 10px 20px 30px;
  text-align: center;
}

.auth-header h2 {
  font-family: var(--font-heading, "Noto Serif SC");
  font-size: 26px;
  color: #2C4C3B;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 1px;
}

.auth-header .subtitle {
  font-family: var(--font-body);
  color: #888;
  margin-bottom: 30px;
  font-size: 14px;
}

/* Minimal Input Styling */
.minimal-form :deep(.el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
  border-radius: 0;
  border-bottom: 1px solid #ddd;
  padding-left: 0;
  padding-bottom: 10px;
}

.minimal-form :deep(.el-input__wrapper:hover) {
  border-bottom-color: #999;
}

.minimal-form :deep(.el-input__wrapper.is-focus) {
  border-bottom-color: #2C4C3B;
  box-shadow: none !important;
}

.minimal-form :deep(.el-input__inner) {
  text-align: center;
  font-size: 16px;
  color: #333;
  height: 40px;
}

/* Code input wrapper */
.code-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  white-space: nowrap;
  background-color: #2C4C3B;
  color: white;
  border: none;
  padding: 10px 15px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.send-code-btn:hover:not(:disabled) {
  background-color: #1e3529;
}

.send-code-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* Button Styling */
.auth-btn {
  width: 100%;
  padding: 14px;
  background-color: #2C4C3B;
  color: white;
  border: none;
  font-size: 16px;
  letter-spacing: 4px;
  cursor: pointer;
  margin-top: 20px;
  transition: all 0.3s ease;
  border-radius: 2px;
  font-weight: 500;
}

.auth-btn:hover {
  background-color: #1e3529;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(44, 76, 59, 0.2);
}

.auth-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Login options */
.login-options {
  margin-top: 15px;
  font-size: 13px;
}

.login-options .link {
  color: #2C4C3B;
  cursor: pointer;
  text-decoration: none;
}

.login-options .link:hover {
  text-decoration: underline;
}

.login-options .divider {
  color: #ddd;
  margin: 0 10px;
}

.auth-footer {
  margin-top: 25px;
  font-size: 14px;
}

.auth-footer .text {
  color: #888;
  margin-right: 5px;
}

.auth-footer .link {
  color: #2C4C3B;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
}

.auth-footer .link:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* Dialog Customization */
:deep(.el-dialog) {
  border-radius: 8px;
  box-shadow: 0 25px 50px rgba(0,0,0,0.15);
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-input__prefix) {
  color: #2C4C3B;
  font-size: 18px;
  margin-right: 5px;
}

/* Align error message to start (left) to match the reference image */
:deep(.el-form-item__error) {
  text-align: left;
  padding-left: 5px;
}
</style>
