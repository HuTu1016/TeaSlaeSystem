<template>
  <div class="product-edit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑商品' : '发布商品' }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" v-loading="loading">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row>
            <el-col :span="12">
                <el-form-item label="商品标题" prop="title">
                  <el-input v-model="form.title" placeholder="请输入商品标题" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="所属分类" prop="categoryId">
                    <el-cascader 
                        v-model="form.categoryId"
                        :options="categoryOptions"
                        :props="{ label: 'name', value: 'id', children: 'children', emitPath: false, checkStrictly: true }"
                        placeholder="请选择分类"
                        style="width: 100%"
                    />
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="form.subtitle" placeholder="请输入副标题" />
        </el-form-item>

        <el-row>
            <el-col :span="8">
                <el-form-item label="产地" prop="origin">
                    <el-input v-model="form.origin" placeholder="如：杭州" />
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="工艺" prop="process">
                    <el-input v-model="form.process" placeholder="如：炒青" />
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="口感" prop="taste">
                    <el-input v-model="form.taste" placeholder="如：鲜爽甘醇" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="主图链接" prop="mainImage">
             <el-input v-model="form.mainImage" placeholder="请输入图片URL" />
             <div class="image-preview" v-if="form.mainImage">
                 <img :src="form.mainImage" class="preview-img" />
             </div>
        </el-form-item>
        
        <el-form-item label="详情描述">
             <el-input type="textarea" :rows="4" v-model="form.description" placeholder="请输入商品详情描述（暂不支持富文本）" />
        </el-form-item>

        <!-- SKU 管理 -->
        <el-divider content-position="left">规格(SKU)与库存</el-divider>
        <div class="sku-toolbar">
            <el-button type="primary" size="small" @click="addSku">添加规格</el-button>
        </div>
        <el-table :data="form.skus" border style="margin-bottom: 20px">
            <el-table-column label="规格名称" prop="skuName">
                <template #default="{ row }">
                    <el-input v-model="row.skuName" placeholder="如：250g/罐" size="small" />
                </template>
            </el-table-column>
            <el-table-column label="价格(元)" prop="price" width="180">
                <template #default="{ row }">
                    <el-input-number v-model="row.price" :precision="2" :min="0" :step="1" size="small" style="width:100%" />
                </template>
            </el-table-column>
            <el-table-column label="库存" prop="stock" width="180">
                <template #default="{ row }">
                    <el-input-number v-model="row.stock" :min="0" :step="10" size="small" style="width:100%" />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ $index }">
                    <el-button type="danger" icon="Delete" circle size="small" @click="removeSku($index)" />
                </template>
            </el-table-column>
        </el-table>

        <el-form-item>
          <el-button type="primary" @click="submitForm">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const categoryOptions = ref([])

const productId = route.params.id
const isEdit = computed(() => !!productId)

const form = reactive({
  categoryId: null,
  title: '',
  subtitle: '',
  origin: '',
  process: '',
  taste: '',
  mainImage: '',
  description: '', // 注意：后端ProductSaveRequest似乎没有description字段？待确认。如果是brewMethod/environment等，需调整。
  // 查看ProductSaveRequest发现有brewMethod/environment，没有description。
  // 按照DTO调整：
  brewMethod: '',
  environment: '',
  
  images: [], // List<String>
  skus: []
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  mainImage: [{ required: true, message: '请输入主图链接', trigger: 'blur' }]
}

// 加载分类
const loadCategories = async () => {
    try {
        const res = await request.get('/categories')
        categoryOptions.value = res
    } catch(e) {}
}

const addSku = () => {
    form.skus.push({ skuName: '', price: 0, stock: 100, status: 1 })
}

const removeSku = (index) => {
    form.skus.splice(index, 1)
}

const loadData = async () => {
    if (!isEdit.value) {
        addSku() // 默认加一行
        return
    }
    loading.value = true
    try {
        const res = await request.get(`/merchant/products/${productId}`)
        Object.assign(form, res)
        // 兼容处理
        if (res.images && res.images.length > 0) {
            form.mainImage = res.images[0]
        }
    } finally {
        loading.value = false
    }
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            // 校验SKU
            if (form.skus.length === 0) {
                ElMessage.warning('请至少添加一个规格')
                return
            }
            for (let sku of form.skus) {
                if (!sku.skuName || !sku.price) {
                     ElMessage.warning('规格名称和价格不能为空')
                     return
                }
            }

            // 构造提交数据
            const data = {
                ...form,
                images: [form.mainImage] // 简单处理：mainImage即为images[0]
            }

            loading.value = true
            try {
                if (isEdit.value) {
                    await request.put(`/merchant/products/${productId}`, data)
                    ElMessage.success('更新成功')
                } else {
                    await request.post('/merchant/products', data)
                    ElMessage.success('创建成功')
                }
                router.push('/products')
            } catch (e) {
                console.error(e)
            } finally {
                loading.value = false
            }
        }
    })
}

onMounted(() => {
    loadCategories()
    loadData()
})
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.image-preview {
    margin-top: 10px;
}
.preview-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
}
.sku-toolbar {
    margin-bottom: 10px;
}
</style>
