<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBannerList, createBanner, updateBanner, deleteBanner, enableBanner, disableBanner } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10,
    status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新建轮播图')
const formRef = ref(null)
const form = ref({
    id: null,
    title: '',
    imageUrl: '',
    linkUrl: '',
    sort: 0,
    status: 1
})

const rules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }]
}

// 图片上传配置
const uploadUrl = '/api/upload/image'
const uploadHeaders = ref({})
const uploading = ref(false)

// 上传前检查
const beforeUpload = (file) => {
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
    uploading.value = true
    return true
}

// 上传成功回调
const handleUploadSuccess = (response) => {
    uploading.value = false
    if (response.code === 0 && response.data) {
        form.value.imageUrl = response.data.url
        ElMessage.success('图片上传成功')
    } else {
        ElMessage.error(response.message || '上传失败')
    }
}

// 上传失败回调
const handleUploadError = (error) => {
    uploading.value = false
    ElMessage.error('图片上传失败')
    console.error(error)
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getBannerList(queryParams.value)
        if (res.items) {
            list.value = res.items
            total.value = res.total
        }
    } catch (e) { console.error(e) }
    finally { loading.value = false }
}

const openDialog = (row) => {
    if (row) {
        dialogTitle.value = '编辑轮播图'
        form.value = { ...row }
    } else {
        dialogTitle.value = '新建轮播图'
        form.value = { id: null, title: '', imageUrl: '', linkUrl: '', sort: 0, status: 1 }
    }
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (form.value.id) await updateBanner(form.value.id, form.value)
                else await createBanner(form.value)
                ElMessage.success('保存成功')
                dialogVisible.value = false
                loadData()
            } catch (e) { console.error(e) }
        }
    })
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(`确定删除?`, '警告', { type: 'warning' })
        await deleteBanner(row.id)
        ElMessage.success('删除成功')
        loadData()
    } catch (e) {}
}

const toggleStatus = async (row) => {
    try {
        if (row.status === 1) await disableBanner(row.id)
        else await enableBanner(row.id)
        ElMessage.success('操作成功')
        loadData()
    } catch (e) {}
}

onMounted(() => {
    // 设置上传请求的认证 header
    const userStore = useUserStore()
    uploadHeaders.value = {
        'Authorization': `Bearer ${userStore.token}`
    }
    loadData()
})
</script>

<template>
  <div class="banner-list">
    <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新建轮播图</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="150">
            <template #default="scope">
                <el-image :src="scope.row.imageUrl" style="width: 120px; height: 60px" fit="cover" :preview-src-list="[scope.row.imageUrl]" />
            </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="linkUrl" label="链接" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
                <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
                <el-button link :type="scope.row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(scope.row)">
                    {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入轮播图标题" />
            </el-form-item>
            <el-form-item label="图片" prop="imageUrl">
                <div class="image-uploader">
                    <el-upload
                        class="avatar-uploader"
                        :action="uploadUrl"
                        :headers="uploadHeaders"
                        :show-file-list="false"
                        :before-upload="beforeUpload"
                        :on-success="handleUploadSuccess"
                        :on-error="handleUploadError"
                        accept="image/*"
                    >
                        <div v-if="form.imageUrl" class="uploaded-image-wrapper">
                            <el-image :src="form.imageUrl" class="uploaded-image" fit="cover" />
                            <div class="image-actions">
                                <span class="action-text">点击更换</span>
                            </div>
                        </div>
                        <div v-else class="upload-placeholder" v-loading="uploading">
                            <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                            <span class="upload-text">点击上传图片</span>
                        </div>
                    </el-upload>
                    <div class="upload-tip">建议尺寸：1920 x 600 像素，支持 jpg/png，不超过 5MB</div>
                </div>
            </el-form-item>
            <el-form-item label="跳转链接" prop="linkUrl">
                <el-input v-model="form.linkUrl" placeholder="点击轮播图后跳转的地址，如 /products/123" />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :min="0" />
                <span class="form-tip">数字越小越靠前</span>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
                <span class="form-tip">{{ form.status === 1 ? '已启用' : '已禁用' }}</span>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="uploading">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.banner-list { padding: 20px; background: white; }
.toolbar { margin-bottom: 20px; }

.image-uploader {
    width: 100%;
}

.avatar-uploader {
    width: 360px;
    height: 150px;
}

.avatar-uploader :deep(.el-upload) {
    width: 100%;
    height: 100%;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
    border-color: #409eff;
}

.upload-placeholder {
    width: 100%;
    height: 148px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;
}

.avatar-uploader-icon {
    font-size: 36px;
    color: #8c939d;
    margin-bottom: 8px;
}

.upload-text {
    font-size: 12px;
}

.uploaded-image-wrapper {
    width: 100%;
    height: 148px;
    position: relative;
}

.uploaded-image {
    width: 100%;
    height: 100%;
}

.image-actions {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
}

.uploaded-image-wrapper:hover .image-actions {
    opacity: 1;
}

.action-text {
    color: white;
    font-size: 14px;
}

.upload-tip {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
}

.form-tip {
    margin-left: 10px;
    font-size: 12px;
    color: #909399;
}
</style>
