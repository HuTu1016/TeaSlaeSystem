<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createArticle, updateArticle, getArticleDetail } from '@/api/admin'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const uploadLoading = ref(false)

const formRef = ref(null)
const form = reactive({
    id: null,
    title: '',
    type: 'CULTURE',
    coverUrl: '',
    summary: '',
    content: ''
})

const rules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const typeOptions = [
    { label: '茶文化', value: 'CULTURE' },
    { label: '冲泡教程', value: 'BREW_TUTORIAL' },
    { label: '新闻资讯', value: 'NEWS' }
]

/**
 * 加载文章详情（编辑模式）
 */
const loadDetail = async (id) => {
    try {
        loading.value = true
        const res = await getArticleDetail(id)
        if (res) {
            form.id = res.id
            form.title = res.title
            form.type = res.type
            form.coverUrl = res.coverUrl || ''
            form.summary = res.summary || ''
            form.content = res.content || ''
        }
    } catch (e) {
        console.error('Load Detail Failed', e)
        ElMessage.error('加载文章详情失败')
    } finally {
        loading.value = false
    }
}

/**
 * 封面图片上传前验证
 */
const beforeUpload = (file) => {
    // 验证文件类型（仅允许 png 和 jpg）
    const isValidType = file.type === 'image/png' || file.type === 'image/jpeg'
    if (!isValidType) {
        ElMessage.error('封面图只支持 PNG 或 JPG 格式')
        return false
    }

    // 验证文件大小（最大 5MB）
    const isLtSize = file.size / 1024 / 1024 < 5
    if (!isLtSize) {
        ElMessage.error('图片大小不能超过 5MB')
        return false
    }

    return true
}

/**
 * 自定义上传处理
 */
const handleUpload = async (options) => {
    const formData = new FormData()
    formData.append('file', options.file)

    uploadLoading.value = true
    try {
        const res = await request.post('/upload/image', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
        if (res && res.url) {
            form.coverUrl = res.url
            ElMessage.success('封面图上传成功')
        }
    } catch (e) {
        console.error('Upload failed', e)
        ElMessage.error('封面图上传失败')
    } finally {
        uploadLoading.value = false
    }
}

/**
 * 删除封面图
 */
const handleRemoveCover = () => {
    form.coverUrl = ''
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                if (form.id) {
                    await updateArticle(form.id, form)
                    ElMessage.success('更新成功')
                } else {
                    await createArticle(form)
                    ElMessage.success('创建成功')
                }
                router.push('/admin/contents')
            } catch (e) {
                console.error(e)
            } finally {
                loading.value = false
            }
        }
    })
}

onMounted(() => {
    const id = route.query.id
    if (id) {
        loadDetail(id)
    }
})
</script>

<template>
  <div class="content-edit" v-loading="loading">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="文章类型" prop="type">
            <el-select v-model="form.type">
                <el-option v-for="opt in typeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverUrl">
            <div class="cover-upload-container">
                <!-- 已有封面图时显示预览 -->
                <div v-if="form.coverUrl" class="cover-preview">
                    <el-image 
                        :src="form.coverUrl" 
                        fit="cover"
                        class="cover-image"
                    />
                    <div class="cover-actions">
                        <el-button type="danger" size="small" @click="handleRemoveCover">删除</el-button>
                    </div>
                </div>
                
                <!-- 无封面图时显示上传组件 -->
                <el-upload
                    v-else
                    class="cover-uploader"
                    action="#"
                    :http-request="handleUpload"
                    :before-upload="beforeUpload"
                    :show-file-list="false"
                    accept=".png,.jpg,.jpeg"
                >
                    <div class="upload-trigger" v-loading="uploadLoading">
                        <el-icon class="upload-icon"><Plus /></el-icon>
                        <span class="upload-text">点击上传封面</span>
                        <span class="upload-tip">支持 PNG、JPG 格式，最大 5MB</span>
                    </div>
                </el-upload>
            </div>
        </el-form-item>
        
        <el-form-item label="摘要" prop="summary">
            <el-input v-model="form.summary" type="textarea" rows="3" placeholder="文章简介" />
        </el-form-item>
        
        <el-form-item label="正文内容" prop="content">
            <el-input 
                v-model="form.content" 
                type="textarea" 
                :rows="15" 
                placeholder="请输入文章内容" 
            />
        </el-form-item>
        
        <el-form-item>
            <el-button @click="router.back()">取消</el-button>
            <el-button type="primary" :loading="loading" @click="handleSubmit">提交保存</el-button>
        </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.content-edit { 
    padding: 20px; 
    background: white; 
    border-radius: 4px; 
}

.cover-upload-container {
    width: 100%;
}

.cover-preview {
    display: inline-block;
    position: relative;
}

.cover-image {
    width: 200px;
    height: 120px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
}

.cover-actions {
    margin-top: 8px;
    text-align: center;
}

.cover-uploader {
    width: 200px;
}

.upload-trigger {
    width: 200px;
    height: 120px;
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: border-color 0.3s;
}

.upload-trigger:hover {
    border-color: #409eff;
}

.upload-icon {
    font-size: 28px;
    color: #8c939d;
}

.upload-text {
    margin-top: 8px;
    font-size: 14px;
    color: #606266;
}

.upload-tip {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
}
</style>
