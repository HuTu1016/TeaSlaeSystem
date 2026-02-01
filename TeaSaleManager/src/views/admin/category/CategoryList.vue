<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCategories, createCategory, updateCategory, enableCategory, disableCategory } from '@/api/admin'

const loading = ref(false)
const list = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新建分类')
const formRef = ref(null)
const form = ref({
    id: null,
    parentId: 0,
    name: '',
    icon: '',
    sort: 0,
    status: 1
})

const rules = {
    name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// Build tree structure from flat list
const buildTree = (items, parentId = 0) => {
    return items
        .filter(item => item.parentId === parentId)
        .map(item => ({
            ...item,
            children: buildTree(items, item.id)
        }))
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getAllCategories()
        list.value = buildTree(res || [])
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const openCreateDialog = (parentId = 0) => {
    dialogTitle.value = parentId === 0 ? '新建顶级分类' : '新建子分类'
    form.value = {
        id: null,
        parentId: parentId,
        name: '',
        icon: '',
        sort: 0,
        status: 1
    }
    dialogVisible.value = true
}

const openEditDialog = (row) => {
    dialogTitle.value = '编辑分类'
    form.value = { ...row }
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (form.value.id) {
                    await updateCategory(form.value.id, form.value)
                    ElMessage.success('更新成功')
                } else {
                    await createCategory(form.value)
                    ElMessage.success('创建成功')
                }
                dialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            }
        }
    })
}

const handleToggleStatus = async (row) => {
    const action = row.status === 1 ? '禁用' : '启用'
    try {
        await ElMessageBox.confirm(`确定要${action}分类 "${row.name}" 吗?`, '提示', { type: 'warning' })
        if (row.status === 1) {
            await disableCategory(row.id)
        } else {
            await enableCategory(row.id)
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
  <div class="category-list">
    <div class="toolbar">
        <el-button type="success" @click="openCreateDialog(0)">新建顶级分类</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column label="图标" width="100">
            <template #default="scope">
                <el-image v-if="scope.row.icon" :src="scope.row.icon" style="width: 30px; height: 30px" fit="cover" />
                <span v-else>-</span>
            </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                    {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
            <template #default="scope">
                <el-button link type="success" @click="openCreateDialog(scope.row.id)">添加子分类</el-button>
                <el-button link type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
                <el-button 
                    link 
                    :type="scope.row.status === 1 ? 'warning' : 'success'"
                    @click="handleToggleStatus(scope.row)"
                >
                    {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
            </template>
        </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="分类名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入分类名称" />
            </el-form-item>
            <el-form-item label="图标URL" prop="icon">
                <el-input v-model="form.icon" placeholder="图标图片链接 (可选)">
                    <template #append v-if="form.icon">
                        <el-image :src="form.icon" style="width: 30px; height: 30px" />
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :value="1">启用</el-radio>
                    <el-radio :value="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.category-list { padding: 20px; background: white; border-radius: 4px; }
.toolbar { margin-bottom: 20px; }
</style>
