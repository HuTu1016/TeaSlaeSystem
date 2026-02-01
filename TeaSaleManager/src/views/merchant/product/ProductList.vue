<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantProducts, onShelfProduct, offShelfProduct } from '@/api/merchant'

const router = useRouter()
const loading = ref(false)
const products = ref([])
const total = ref(0)
const queryParams = ref({
  page: 1,
  pageSize: 10,
  keyword: ''
})

const loadData = async () => {
    loading.value = true
    try {
        const res = await getMerchantProducts(queryParams.value)
        // Adapt response structure if needed
        if (res.list) {
            products.value = res.list
            total.value = res.total
        } else {
             products.value = []
             total.value = 0
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    queryParams.value.page = 1
    loadData()
}

const handleEdit = (id) => {
    // Navigate to edit page with ID
    router.push(`/merchant/products/publish?id=${id}`)
}

const handleToggleStatus = async (row) => {
    const action = row.status === 'ON_SHELF' ? '下架' : '上架'
    try {
        await ElMessageBox.confirm(`确认要${action}商品 "${row.title}" 吗?`, '提示', {
            type: 'warning'
        })
        
        if (row.status === 'ON_SHELF') {
            await offShelfProduct(row.id)
        } else {
            await onShelfProduct(row.id)
        }
        ElMessage.success(`${action}成功`)
        loadData()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

onMounted(() => {
    loadData()
})
</script>

<template>
  <div class="product-list">
    <div class="toolbar">
       <el-input 
         v-model="queryParams.keyword" 
         placeholder="输入商品名称搜索" 
         style="width: 200px" 
         clearable
         @clear="handleSearch"
         @keyup.enter="handleSearch"
       />
       <el-button type="primary" @click="handleSearch">搜索</el-button>
       <el-button type="success" @click="router.push('/merchant/products/publish')">发布商品</el-button>
    </div>
    
    <el-table :data="products" v-loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="主图" width="100">
        <template #default="scope">
           <el-image :src="scope.row.mainImage" style="width: 50px; height: 50px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="origin" label="产地" width="120" />
      <el-table-column label="价格范围" width="150">
        <template #default="scope">
           ¥{{ scope.row.minPrice }} - ¥{{ scope.row.maxPrice }}
        </template>
      </el-table-column>
      <el-table-column prop="salesCount" label="销量" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
           <el-tag :type="scope.row.status === 'ON_SHELF' ? 'success' : 'info'">
             {{ scope.row.status === 'ON_SHELF' ? '已上架' : '已下架' }}
           </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
           <el-button link type="primary" @click="handleEdit(scope.row.id)">编辑</el-button>
           <el-button 
             link 
             :type="scope.row.status === 'ON_SHELF' ? 'danger' : 'success'" 
             @click="handleToggleStatus(scope.row)"
           >
             {{ scope.row.status === 'ON_SHELF' ? '下架' : '上架' }}
           </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<style scoped>
.product-list {
  background: white;
  padding: 20px;
  border-radius: 4px;
}
.toolbar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
