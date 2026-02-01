<template>
  <div class="product-reviews-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品评论管理</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <div class="review-list" v-loading="loading">
        <el-empty v-if="!loading && list.length === 0" description="暂无评论" />
        
        <div v-for="item in list" :key="item.id" class="review-item">
            <div class="review-header">
                <div class="user-info">
                    <el-avatar :size="32" :src="item.userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                    <span class="username">{{ item.userName }}</span>
                    <el-rate v-model="item.rating" disabled show-score text-color="#ff9900" />
                </div>
                <div class="time">{{ item.createdAt }}</div>
            </div>
            
            <div class="review-content">{{ item.content }}</div>
            
            <div class="review-images" v-if="item.images && item.images.length > 0">
                <el-image 
                    v-for="(img, idx) in item.images" 
                    :key="idx" 
                    :src="img" 
                    :preview-src-list="item.images"
                    class="review-img"
                    fit="cover" />
            </div>

            <!-- 商家回复区域 -->
            <div class="merchant-reply" v-if="item.replies && item.replies.length > 0">
                <div class="reply-title">商家回复：</div>
                <div class="reply-content">{{ item.replies[0].content }}</div>
                <div class="reply-time">{{ item.replies[0].createdAt }}</div>
            </div>
            
            <div class="actions" v-else>
                <el-button type="primary" link @click="handleReply(item)">回复</el-button>
            </div>
        </div>
      </div>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 回复弹窗 -->
    <el-dialog title="回复评论" v-model="replyDialogVisible" width="500px">
        <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef">
            <el-form-item prop="content">
                <el-input 
                    v-model="replyForm.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入回复内容" 
                    maxlength="500"
                    show-word-limit />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="replyDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmReply" :loading="replyLoading">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const productId = route.params.id

const loading = ref(false)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const replyDialogVisible = ref(false)
const replyLoading = ref(false)
const replyFormRef = ref(null)
const currentReviewId = ref(null)
const replyForm = reactive({
    content: ''
})

const replyRules = {
    content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get(`/merchant/reviews/product/${productId}`, {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    list.value = res.items || res.list // 兼容PageResult字段
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleReply = (item) => {
    currentReviewId.value = item.id
    replyForm.content = ''
    replyDialogVisible.value = true
}

const confirmReply = async () => {
    if (!replyFormRef.value) return
    await replyFormRef.value.validate(async (valid) => {
        if (valid) {
            replyLoading.value = true
            try {
                await request.post(`/merchant/reviews/${currentReviewId.value}/reply`, {
                    content: replyForm.content
                })
                ElMessage.success('回复成功')
                replyDialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            } finally {
                replyLoading.value = false
            }
        }
    })
}

onMounted(() => {
    if (productId) {
        loadData()
    }
})
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.review-item {
    padding: 20px;
    border-bottom: 1px solid #EBEEF5;
}
.review-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
}
.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
}
.username {
    font-weight: bold;
    font-size: 14px;
}
.time {
    color: #909399;
    font-size: 12px;
}
.review-content {
    margin-bottom: 10px;
    line-height: 1.5;
    color: #303133;
}
.review-images {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
}
.review-img {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    cursor: pointer;
}
.merchant-reply {
    background-color: #f8fafc;
    padding: 10px;
    border-radius: 4px;
    margin-top: 10px;
}
.reply-title {
    font-weight: bold;
    color: #E6A23C;
    margin-bottom: 5px;
}
.reply-content {
    color: #606266;
    font-size: 13px;
}
.reply-time {
    text-align: right;
    color: #909399;
    font-size: 12px;
    margin-top: 5px;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
