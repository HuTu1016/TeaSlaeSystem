<template>
  <div class="address-manage">
    <h2>收货地址管理</h2>
    <el-button type="primary" icon="Plus" style="margin-bottom: 20px;" @click="handleAdd">新增收货地址</el-button>

    <el-table :data="addresses" style="width: 100%" stripe v-loading="loading">
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column prop="receiverPhone" label="手机号码" width="150" />
      <el-table-column label="所在地区" width="180">
        <template #default="scope">{{ scope.row.province }} {{ scope.row.city }} {{ scope.row.district }}</template>
      </el-table-column>
      <el-table-column prop="detail" label="详细地址" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          <el-tag v-if="scope.row.isDefault === 1" size="small" type="success" style="margin-left: 10px;">默认</el-tag>
          <el-button v-else link type="primary" size="small" @click="handleSetDefault(scope.row.id)">设为默认</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-empty v-if="addresses.length === 0 && !loading" description="暂无收货地址" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号码" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="所在地区" prop="selectedRegion">
          <el-cascader
            v-model="addressForm.selectedRegion"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="addressForm.detail" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'
import { regionData } from 'element-china-area-data'

const loading = ref(false)
const addresses = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增收货地址')
const submitting = ref(false)
const addressFormRef = ref(null)
const editingId = ref(null)

// 处理省市区数据，将 value 改为 label，以便与后端直接对接文本
const processRegionData = (data) => {
  return data.map(item => {
    const newItem = {
      value: item.label,
      label: item.label
    }
    if (item.children && item.children.length) {
      newItem.children = processRegionData(item.children)
    }
    return newItem
  })
}

const regionOptions = processRegionData(regionData)

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  selectedRegion: [], // [省, 市, 区]
  detail: '',
  isDefault: 0
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  selectedRegion: [{ required: true, message: '请选择所在地区', trigger: 'change', type: 'array' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 加载地址列表
const loadAddresses = async () => {
  loading.value = true
  try {
    addresses.value = await getAddresses()
  } catch (error) {
    console.error('加载地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增收货地址'
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑收货地址'
  editingId.value = row.id
  Object.assign(addressForm, {
    receiverName: row.receiverName,
    receiverPhone: row.receiverPhone,
    selectedRegion: [row.province, row.city, row.district],
    detail: row.detail,
    isDefault: row.isDefault
  })
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!addressFormRef.value) return
  
  try {
    await addressFormRef.value.validate()
    submitting.value = true
    
    const submitData = {
      receiverName: addressForm.receiverName,
      receiverPhone: addressForm.receiverPhone,
      province: addressForm.selectedRegion[0],
      city: addressForm.selectedRegion[1],
      district: addressForm.selectedRegion[2],
      detail: addressForm.detail,
      isDefault: addressForm.isDefault
    }
    
    if (editingId.value) {
      await updateAddress(editingId.value, submitData)
      ElMessage.success('地址修改成功')
    } else {
      await addAddress(submitData)
      ElMessage.success('地址添加成功')
    }
    
    dialogVisible.value = false
    loadAddresses()
  } catch (error) {
    if (error instanceof Error) {
      return
    }
    console.error('保存地址失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAddress(id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除地址失败:', error)
  }
}

// 设为默认
const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    loadAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(addressForm, {
    receiverName: '',
    receiverPhone: '',
    selectedRegion: [],
    detail: '',
    isDefault: 0
  })
  addressFormRef.value?.clearValidate()
}

onMounted(() => {
  loadAddresses()
})
</script>

