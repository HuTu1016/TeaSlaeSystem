<template>
  <div class="trace-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" icon="Plus" @click="handleAdd">新增溯源</el-button>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="traceCode" label="溯源码" width="180" />
        <el-table-column prop="productName" label="关联商品" min-width="150">
             <template #default="scope">
                 <!-- 暂时显示ID，如果能关联显示名称更好 -->
                 商品ID: {{ scope.row.productId }}
             </template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次号" width="120" />
        <el-table-column prop="pickDate" label="采摘日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rejectReason" label="驳回原因" min-width="150" show-overflow-tooltip>
            <template #default="scope">
                <span v-if="scope.row.status === 'REJECTED'" class="text-danger">{{ scope.row.rejectReason }}</span>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button 
                size="small"
                v-if="['DRAFT', 'REJECTED'].includes(scope.row.status)"
                @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
                size="small" 
                type="primary" 
                v-if="['DRAFT', 'REJECTED'].includes(scope.row.status)"
                @click="handleSubmit(scope.row)">提交审核</el-button>
            <el-button 
                size="small" 
                type="danger" 
                v-if="['DRAFT', 'REJECTED'].includes(scope.row.status)"
                @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 编辑/新增弹窗 -->
    <el-dialog :title="form.id ? '编辑溯源' : '新增溯源'" v-model="dialogVisible" width="600px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="关联商品ID" prop="productId">
                <el-input v-model="form.productId" placeholder="请输入关联商品ID" :disabled="!!form.id" />
                <!-- 实际项目中应为选择器 -->
            </el-form-item>
            <el-form-item label="批次号" prop="batchNo">
                <el-input v-model="form.batchNo" placeholder="请输入生产批次号" />
            </el-form-item>
            <el-form-item label="采摘日期" prop="pickDate">
                <el-date-picker v-model="form.pickDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
            <el-form-item label="产地" prop="origin">
                <el-input v-model="form.origin" placeholder="详细产地" />
            </el-form-item>
            <el-form-item label="工艺" prop="process">
                <el-input v-model="form.process" placeholder="制作工艺" />
            </el-form-item>
            <el-form-item label="生产商" prop="producer">
                <el-input v-model="form.producer" placeholder="生产商名称" />
            </el-form-item>
            <el-form-item label="报告链接" prop="inspectionReportUrl">
                <el-input v-model="form.inspectionReportUrl" placeholder="检测报告图片URL" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitForm" :loading="formLoading">保存</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  status: ''
})

const dialogVisible = ref(false)
const formLoading = ref(false)
const formRef = ref(null)
const form = reactive({
    id: null,
    productId: '',
    batchNo: '',
    origin: '',
    pickDate: '',
    process: '',
    producer: '',
    inspectionReportUrl: '',
    status: 'DRAFT'
})

const rules = {
    productId: [{ required: true, message: '请输入商品ID', trigger: 'blur' }],
    batchNo: [{ required: true, message: '请输入批次号', trigger: 'blur' }],
    pickDate: [{ required: true, message: '请选择采摘日期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/trace/list', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        status: queryForm.status || undefined
      }
    })
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
    currentPage.value = 1
    loadData()
}

const handleAdd = () => {
    Object.assign(form, {
        id: null,
        productId: '',
        batchNo: '',
        origin: '',
        pickDate: '',
        process: '',
        producer: '',
        inspectionReportUrl: '',
        status: 'DRAFT'
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    Object.assign(form, row)
    dialogVisible.value = true
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            formLoading.value = true
            try {
                if (form.id) {
                    await request.put(`/merchant/trace/${form.id}`, form)
                } else {
                    await request.post('/merchant/trace', form)
                }
                ElMessage.success('保存成功')
                dialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            } finally {
                formLoading.value = false
            }
        }
    })
}

const handleSubmit = (row) => {
    ElMessageBox.confirm('确定提交审核吗？提交后不可修改', '提示', {
        type: 'warning'
    }).then(async () => {
        await request.post(`/merchant/trace/${row.id}/submit`)
        ElMessage.success('提交成功')
        loadData()
    }).catch(() => {})
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该记录吗？', '警告', {
        type: 'warning'
    }).then(async () => {
        await request.delete(`/merchant/trace/${row.id}`)
        ElMessage.success('删除成功')
        loadData()
    }).catch(() => {})
}

const getStatusText = (status) => {
    const map = { 'DRAFT': '草稿', 'PENDING': '待审核', 'APPROVED': '已通过', 'REJECTED': '已驳回' }
    return map[status] || status
}

const getStatusType = (status) => {
    const map = { 'DRAFT': 'info', 'PENDING': 'warning', 'APPROVED': 'success', 'REJECTED': 'danger' }
    return map[status] || ''
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.toolbar {
    margin-bottom: 20px;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
.text-danger {
    color: #F56C6C;
}
</style>
