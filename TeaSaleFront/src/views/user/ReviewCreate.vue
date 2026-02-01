<template>
  <div class="review-create">
    <h2>发表评价</h2>
    
    <div class="reviews-container" v-loading="loading">
      <template v-if="orderDetail">
        <div class="order-info">
          <span>订单号：{{ orderDetail.orderNo }}</span>
          <span class="time">下单时间：{{ formatDate(orderDetail.createdAt) }}</span>
        </div>

        <div class="review-list">
          <div v-for="(item, index) in orderDetail.items" :key="item.id" class="review-item-card">
            <div class="product-info">
              <img :src="item.image" class="thumb" />
              <div class="detail">
                <p class="name">{{ item.title }}</p>
                <p class="sku">{{ item.skuName }}</p>
              </div>
            </div>

            <el-form :model="reviews[index]" :rules="rules" ref="formRefs" label-width="80px" class="review-form">
              <el-form-item label="商品评分" prop="rating">
                <el-rate v-model="reviews[index].rating" show-text />
              </el-form-item>
              
              <el-form-item label="评价内容" prop="content">
                <el-input 
                  v-model="reviews[index].content" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="商品满足您的期待吗？说说它的优点和不足吧"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="晒图" prop="images">
                <el-upload
                  action="/api/upload/image"
                  list-type="picture-card"
                  :on-preview="handlePictureCardPreview"
                  :on-remove="(file) => handleRemove(file, index)"
                  :on-success="(res, file) => handleUploadSuccess(res, file, index)"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders"
                  :file-list="reviews[index].fileList"
                  :limit="6"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              
              <el-form-item label="匿名评价">
                <el-switch v-model="reviews[index].anonymous" />
                <span class="tip">开启后，您的头像和昵称将对其他用户隐藏</span>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="actions">
          <el-button type="primary" size="large" @click="submitAll" :loading="submitting">发布评价</el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </div>
      </template>
    </div>

    <!-- 图片预览 -->
    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getOrderDetail } from '@/api/order'
import { createReview } from '@/api/review'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const orderDetail = ref(null)
const reviews = ref([])
const formRefs = ref([])

// 图片预览
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

const rules = {
  rating: [{ required: true, message: '请打分', trigger: 'change' }],
  content: [
    { required: true, message: '请填写评价内容', trigger: 'blur' },
    { min: 5, message: '评价内容至少5个字', trigger: 'blur' }
  ]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const loadData = async () => {
  const orderId = route.query.orderId
  if (!orderId) {
    ElMessage.error('参数错误')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getOrderDetail(orderId)
    orderDetail.value = res
    // 初始化每个商品的评价表单数据
    reviews.value = res.items.map(item => ({
      productId: item.productId,
      orderItemId: item.id, // 可能需要传，但目前DTO只要orderId和productId
      rating: 5,
      content: '',
      anonymous: false,
      images: [],
      fileList: [] // 用于el-upload显示
    }))
  } catch (error) {
    console.error(error)
    ElMessage.error('加载订详情失败')
  } finally {
    loading.value = false
  }
}

// 上传相关
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片格式必须是 JPG 或 PNG')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response, uploadFile, index) => {
  if (response.code === 0) {
    reviews.value[index].images.push(response.data.url)
  } else {
    ElMessage.error(response.message || '上传失败')
    // Remove file from list
    const fileList = reviews.value[index].fileList
    const i = fileList.indexOf(uploadFile)
    if (i !== -1) fileList.splice(i, 1)
  }
}

const handleRemove = (uploadFile, index) => {
  const fileUrl = uploadFile.response ? uploadFile.response.data.url : uploadFile.url
  const review = reviews.value[index]
  const i = review.images.indexOf(fileUrl)
  if (i !== -1) {
    review.images.splice(i, 1)
  }
}

const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url || uploadFile.response.data.url
  dialogVisible.value = true
}

const submitAll = async () => {
  // 简单验证：所有必填项是否有值
  let valid = true
  for (const review of reviews.value) {
    if (!review.rating || !review.content || review.content.length < 5) {
      valid = false
      break
    }
  }
  
  if (!valid) {
    ElMessage.warning('请完善所有评价信息（评分和至少5字评价内容）')
    return
  }

  submitting.value = true
  let successCount = 0
  let failCount = 0
  
  try {
    // 并发提交所有评价
    // 注意：createReview 接受单个评价请求
    const promises = reviews.value.map(review => {
      // 构造请求数据
      // 需要 orderId，而 reviews 数据中只有 productId
      return createReview({
        orderId: orderDetail.value.id,
        productId: review.productId,
        rating: review.rating,
        content: review.content,
        images: review.images,
        anonymous: review.anonymous
      })
    })

    const results = await Promise.allSettled(promises)
    
    results.forEach(res => {
      if (res.status === 'fulfilled') {
        successCount++
      } else {
        failCount++
        console.error('评价提交失败:', res.reason)
      }
    })

    if (failCount === 0) {
      ElMessage.success('评价发布成功')
      router.push('/user/reviews')
    } else {
      ElMessage.warning(`成功发布 ${successCount} 条，失败 ${failCount} 条`)
      // 如果部分失败，可能需要留在这个页面让用户重试？
      // 简单处理：如果全部成功就跳转，否则提示。
      if (successCount > 0) {
          // 部分成功，跳转。
          router.push('/user/reviews')
      }
    }

  } catch (error) {
    console.error(error)
    ElMessage.error('提交发生错误')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.review-create {
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 50px;
}
.order-info {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}
.review-item-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}
.product-info {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}
.thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 15px;
  border-radius: 4px;
}
.name {
  font-weight: bold;
  margin: 0 0 5px;
}
.sku {
  color: #999;
  font-size: 12px;
  margin: 0;
}
.review-form {
  max-width: 600px;
}
.tip {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
}
.actions {
  margin-top: 30px;
  text-align: center;
}
</style>
