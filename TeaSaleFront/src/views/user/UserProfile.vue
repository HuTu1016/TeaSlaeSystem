<template>
  <div class="user-profile">
    <h2>个人资料</h2>
    <el-form label-width="80px" style="max-width: 400px;">
      <el-form-item label="头像">
        <el-avatar :size="60" :src="userInfo.avatar || defaultAvatar" />
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
          accept="image/*"
        >
          <el-button link type="primary" style="margin-left: 10px;">修改头像</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="userInfo.username" disabled />
      </el-form-item>
      <el-form-item label="手机号">
         <el-input v-model="userInfo.phone" disabled />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="userInfo.nickname" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, uploadImage } from '@/api/user'

const userStore = useUserStore()
const userInfo = ref({ ...userStore.userInfo })
const saving = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 初始化时从后端获取最新用户信息
onMounted(async () => {
  try {
    const data = await getUserProfile()
    userInfo.value = data
    userStore.setUserInfo(data)
  } catch (e) {
    // 如果获取失败，使用本地存储的信息
    console.error('获取用户信息失败', e)
  }
})

/**
 * 上传前校验
 */
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

/**
 * 自定义上传处理
 */
const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    // 上传成功后更新头像URL
    userInfo.value.avatar = res.url
    
    // 立即保存到后端
    await updateUserProfile({ avatar: res.url })
    userStore.setUserInfo({ ...userStore.userInfo, avatar: res.url })
    
    ElMessage.success('头像上传成功')
  } catch (e) {
    ElMessage.error('头像上传失败')
    console.error(e)
  }
}

/**
 * 保存用户资料
 */
const saveProfile = async () => {
  saving.value = true
  try {
    const data = await updateUserProfile({
      nickname: userInfo.value.nickname,
      avatar: userInfo.value.avatar
    })
    userStore.setUserInfo(data)
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
    console.error(e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.user-profile h2 {
  margin-bottom: 30px;
  color: #333;
}

.avatar-uploader {
  display: inline-block;
}
</style>
