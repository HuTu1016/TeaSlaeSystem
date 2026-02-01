<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories, createProduct, updateProduct, getMerchantProducts } from '@/api/merchant' 
// Note: We might need a separate getDetail API for merchant or reuse public product detail if sufficient
import { getProductDetail } from '@/api/merchant' // Need to ensure this exists or use public

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const categoryOptions = ref([]) // Tree structure

const formRef = ref(null)
const form = reactive({
    id: null,
    categoryId: null,
    title: '',
    subtitle: '',
    origin: '',
    process: '',
    taste: '',
    brewMethod: '',
    environment: '',
    mainImage: '',
    images: [],
    skus: []
})

const rules = {
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
    mainImage: [{ required: true, message: '请输入主图链接', trigger: 'blur' }],
    skus: [{ required: true, message: '请至少添加一个SKU', trigger: 'change' }]
}

// Helper: Flatten tree or use Cascader
const loadCategories = async () => {
    try {
        const res = await getCategories()
        categoryOptions.value = res
    } catch (e) { console.error(e) }
}

const addSku = () => {
    form.skus.push({
        skuName: '',
        price: 0,
        stock: 100,
        status: 1
    })
}

const removeSku = (index) => {
    form.skus.splice(index, 1)
}

const addImage = () => {
    form.images.push('')
}
const removeImage = (index) => {
    form.images.splice(index, 1)
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                // Filter empty images
                const payload = {
                    ...form,
                    images: form.images.filter(img => img.trim() !== '')
                }
                
                if (form.id) {
                    await updateProduct(form.id, payload)
                    ElMessage.success('更新成功')
                } else {
                    await createProduct(payload)
                    ElMessage.success('发布成功')
                }
                router.push('/merchant/products/list')
            } catch (e) {
                console.error(e)
            } finally {
                loading.value = false
            }
        }
    })
}

onMounted(async () => {
    await loadCategories()
    const id = route.query.id
    if (id) {
        // Edit mode: Load detail
        // For MVP, assuming we can get detail. 
        // If public API returns everything needed, we can use it.
        // Or fetch from list (not ideal but works for simple cache)
        // Ideally we need getProductDetail(id)
    }
})
</script>

<template>
  <div class="product-publish">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-card class="mb-20" header="基本信息">
        <el-form-item label="商品分类" prop="categoryId">
           <el-cascader 
             v-model="form.categoryId" 
             :options="categoryOptions" 
             :props="{ value: 'id', label: 'name', children: 'children', emitPath: false }"
             placeholder="选择分类"
           />
        </el-form-item>
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="如：特级西湖龙井" />
        </el-form-item>
        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="form.subtitle" placeholder="促销语或卖点" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产地">
              <el-input v-model="form.origin" placeholder="如：杭州" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺">
              <el-input v-model="form.process" placeholder="如：炒青" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="口感描述">
           <el-input type="textarea" v-model="form.taste" />
        </el-form-item>
      </el-card>

      <el-card class="mb-20" header="图片管理">
        <el-form-item label="主图链接" prop="mainImage">
           <el-input v-model="form.mainImage" placeholder="请输入图片URL">
             <template #append v-if="form.mainImage">
               <el-image :src="form.mainImage" style="width: 30px; height: 30px" />
             </template>
           </el-input>
        </el-form-item>
        <el-form-item label="详情图片">
            <div v-for="(img, idx) in form.images" :key="idx" class="image-row">
               <el-input v-model="form.images[idx]" placeholder="详情图URL">
                 <template #append>
                   <el-button @click="removeImage(idx)">删除</el-button>
                 </template>
               </el-input>
            </div>
            <el-button type="primary" link @click="addImage">+ 添加详情图</el-button>
        </el-form-item>
      </el-card>

      <el-card class="mb-20" header="规格库存 (SKU)">
         <el-table :data="form.skus" border>
            <el-table-column label="规格名称" required>
               <template #default="scope">
                  <el-input v-model="scope.row.skuName" placeholder="如：250g/罐" />
               </template>
            </el-table-column>
            <el-table-column label="价格(元)" width="150" required>
               <template #default="scope">
                  <el-input-number v-model="scope.row.price" :min="0" :precision="2" :step="0.1" controls-position="right" style="width: 100%" />
               </template>
            </el-table-column>
            <el-table-column label="库存" width="150" required>
               <template #default="scope">
                  <el-input-number v-model="scope.row.stock" :min="0" controls-position="right" style="width: 100%" />
               </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
               <template #default="scope">
                  <el-button type="danger" link @click="removeSku(scope.$index)">删除</el-button>
               </template>
            </el-table-column>
         </el-table>
         <div class="mt-10">
            <el-button type="primary" plain @click="addSku">+ 添加规格</el-button>
         </div>
      </el-card>

      <div class="form-footer">
        <el-button @click="router.back()">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">保存并发布</el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.product-publish { padding: 20px; }
.mb-20 { margin-bottom: 20px; }
.mt-10 { margin-top: 10px; }
.image-row { margin-bottom: 10px; display: flex; align-items: center; gap: 10px; }
.form-footer { margin-top: 30px; text-align: center; }
</style>
