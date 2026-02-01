<template>
  <div class="shop-settings-container">
    <el-row :gutter="20">
      <!-- 店铺信息卡片 -->
      <el-col :span="14">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>店铺信息</span>
            </div>
          </template>
          
          <el-form :model="shopForm" :rules="shopRules" ref="shopFormRef" label-width="100px">
            <el-form-item label="店铺Logo">
              <div class="logo-upload">
                <el-upload
                  class="logo-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleLogoSuccess"
                  :before-upload="beforeUpload"
                  accept="image/*">
                  <el-image v-if="shopForm.shopLogo" :src="shopForm.shopLogo" class="logo-preview" fit="cover" />
                  <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <span class="upload-tip">建议尺寸200x200，支持jpg、png格式</span>
              </div>
            </el-form-item>
            
            <el-form-item label="店铺名称" prop="shopName">
              <el-input v-model="shopForm.shopName" placeholder="请输入店铺名称" maxlength="50" show-word-limit />
            </el-form-item>
            
            <el-form-item label="店铺描述" prop="shopDesc">
              <el-input 
                v-model="shopForm.shopDesc" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入店铺描述，让顾客更了解您的店铺" 
                maxlength="500"
                show-word-limit />
            </el-form-item>
            
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="shopForm.contactPhone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveShopInfo" :loading="saveShopLoading">保存店铺信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 个人信息卡片 -->
      <el-col :span="10">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          
          <el-form :model="profileForm" ref="profileFormRef" label-width="80px">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-upload
                  class="avatar-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeUpload"
                  accept="image/*">
                  <el-avatar v-if="profileForm.avatar" :size="80" :src="profileForm.avatar" />
                  <el-avatar v-else :size="80">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                </el-upload>
              </div>
            </el-form-item>
            
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="50" />
            </el-form-item>
            
            <el-form-item label="手机号">
              <el-input :model-value="shopInfo.phone" disabled />
              <span class="form-tip">手机号暂不支持修改</span>
            </el-form-item>
            
            <el-form-item label="邮箱">
              <el-input :model-value="shopInfo.email || '未绑定'" disabled />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saveProfileLoading">保存个人信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 店铺状态卡片 -->
        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <span>店铺状态</span>
            </div>
          </template>
          
          <div class="status-info">
            <div class="status-item">
              <span class="label">店铺状态：</span>
              <el-tag :type="shopInfo.status === 1 ? 'success' : 'danger'">
                {{ shopInfo.status === 1 ? '正常营业' : '已关闭' }}
              </el-tag>
            </div>
            <div class="status-item">
              <span class="label">入驻时间：</span>
              <span>{{ shopInfo.createdAt || '-' }}</span>
            </div>
            <div class="status-item" v-if="shopInfo.businessLicense">
              <span class="label">营业执照：</span>
              <el-link type="primary" @click="previewLicense">查看</el-link>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 营业执照预览弹窗 -->
    <el-dialog title="营业执照" v-model="licenseDialogVisible" width="600px">
      <el-image :src="shopInfo.businessLicense" fit="contain" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { Plus, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const loading = ref(false)
const saveShopLoading = ref(false)
const saveProfileLoading = ref(false)
const licenseDialogVisible = ref(false)

const shopFormRef = ref(null)
const profileFormRef = ref(null)

const shopInfo = reactive({
  id: null,
  shopName: '',
  shopDesc: '',
  shopLogo: '',
  contactPhone: '',
  businessLicense: '',
  status: 1,
  createdAt: '',
  nickname: '',
  avatar: '',
  phone: '',
  email: ''
})

const shopForm = reactive({
  shopName: '',
  shopDesc: '',
  shopLogo: '',
  contactPhone: ''
})

const profileForm = reactive({
  nickname: '',
  avatar: ''
})

const shopRules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

const uploadUrl = computed(() => {
  return import.meta.env.VITE_API_BASE_URL + '/upload/image'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + userStore.token
  }
})

const loadShopInfo = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/shop/info')
    Object.assign(shopInfo, res)
    
    // 同步到表单
    shopForm.shopName = res.shopName || ''
    shopForm.shopDesc = res.shopDesc || ''
    shopForm.shopLogo = res.shopLogo || ''
    shopForm.contactPhone = res.contactPhone || ''
    
    profileForm.nickname = res.nickname || ''
    profileForm.avatar = res.avatar || ''
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleLogoSuccess = (response) => {
  if (response.code === 0 || response.code === 200) {
    shopForm.shopLogo = response.data
    ElMessage.success('Logo上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleAvatarSuccess = (response) => {
  if (response.code === 0 || response.code === 200) {
    profileForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

const saveShopInfo = async () => {
  if (!shopFormRef.value) return
  await shopFormRef.value.validate(async (valid) => {
    if (valid) {
      saveShopLoading.value = true
      try {
        await request.put('/merchant/shop/info', shopForm)
        ElMessage.success('店铺信息保存成功')
        loadShopInfo()
      } catch (e) {
        console.error(e)
      } finally {
        saveShopLoading.value = false
      }
    }
  })
}

const saveProfile = async () => {
  saveProfileLoading.value = true
  try {
    await request.put('/merchant/shop/profile', profileForm)
    ElMessage.success('个人信息保存成功')
    loadShopInfo()
  } catch (e) {
    console.error(e)
  } finally {
    saveProfileLoading.value = false
  }
}

const previewLicense = () => {
  licenseDialogVisible.value = true
}

onMounted(() => {
  loadShopInfo()
})
</script>

<style scoped>
.mt-4 {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}
.logo-uploader:hover {
  border-color: #409eff;
}
.logo-preview {
  width: 120px;
  height: 120px;
  display: block;
}
.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
.avatar-upload {
  display: flex;
  justify-content: center;
}
.avatar-uploader {
  cursor: pointer;
}
.avatar-uploader:hover :deep(.el-avatar) {
  opacity: 0.8;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
.status-info {
  padding: 10px 0;
}
.status-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.status-item:last-child {
  margin-bottom: 0;
}
.status-item .label {
  color: #606266;
  min-width: 80px;
}
</style>
