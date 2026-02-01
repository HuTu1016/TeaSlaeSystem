<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCouponList, createCoupon, updateCoupon, enableCoupon, disableCoupon } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10,
    status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新建优惠券')
const formRef = ref(null)

// 优惠券类型选项
const couponTypeOptions = [
    { label: '满减券', value: 'AMOUNT' },
    { label: '折扣券', value: 'PERCENT' }
]

// 表单数据，字段名与后端Coupon实体对齐
const form = ref({
    id: null,
    name: '',
    type: 'AMOUNT',           // 类型：AMOUNT-满减券, PERCENT-折扣券（必填）
    amount: null,             // 优惠金额（满减券）
    discountPercent: null,    // 折扣比例（折扣券，如90表示9折）
    minAmount: null,          // 使用门槛金额
    maxDiscount: null,        // 最大抵扣金额（折扣券）
    totalCount: 100,          // 总发行量
    limitPerUser: 1,          // 每人限领数量
    validDays: null,          // 有效天数（领取后N天有效）
    validFrom: null,          // 固定开始时间
    validTo: null,            // 固定结束时间
    status: 'ACTIVE'
})

// 是否为满减券
const isAmountType = computed(() => form.value.type === 'AMOUNT')

const rules = {
    name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    amount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
    discountPercent: [{ required: true, message: '请输入折扣比例', trigger: 'blur' }],
    totalCount: [{ required: true, message: '请输入发行量', trigger: 'blur' }]
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getCouponList(queryParams.value)
        if (res.items) {
            list.value = res.items
            total.value = res.total
        }
    } catch (e) { console.error(e) }
    finally { loading.value = false }
}

const openDialog = (row) => {
    if (row) {
        dialogTitle.value = '编辑优惠券'
        form.value = { ...row }
    } else {
        dialogTitle.value = '新建优惠券'
        form.value = { 
            id: null, 
            name: '', 
            type: 'AMOUNT',
            amount: null, 
            discountPercent: null,
            minAmount: null, 
            maxDiscount: null,
            totalCount: 100, 
            limitPerUser: 1, 
            validDays: null,
            validFrom: null, 
            validTo: null, 
            status: 'ACTIVE' 
        }
    }
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (form.value.id) await updateCoupon(form.value.id, form.value)
                else await createCoupon(form.value)
                ElMessage.success('保存成功')
                dialogVisible.value = false
                loadData()
            } catch (e) {}
        }
    })
}

const toggleStatus = async (row) => {
    try {
        if (row.status === 'ACTIVE') await disableCoupon(row.id)
        else await enableCoupon(row.id)
        ElMessage.success('操作成功')
        loadData()
    } catch (e) {}
}

onMounted(() => loadData())
</script>

<template>
  <div class="coupon-list">
    <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新建优惠券</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column label="类型" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.type === 'AMOUNT' ? 'primary' : 'warning'">
                    {{ scope.row.type === 'AMOUNT' ? '满减券' : '折扣券' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="优惠" width="120">
            <template #default="scope">
                <span v-if="scope.row.type === 'AMOUNT'">¥{{ scope.row.amount }}</span>
                <span v-else>{{ scope.row.discountPercent / 10 }}折</span>
            </template>
        </el-table-column>
        <el-table-column label="门槛" width="100">
            <template #default="scope">满¥{{ scope.row.minAmount || 0 }}</template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发行量" width="100" />
        <el-table-column prop="receivedCount" label="已领取" width="100" />
        <el-table-column label="有效期" width="300">
            <template #default="scope">
                <span v-if="scope.row.validDays">领取后{{ scope.row.validDays }}天有效</span>
                <span v-else>{{ scope.row.validFrom }} ~ {{ scope.row.validTo }}</span>
            </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
                    {{ scope.row.status === 'ACTIVE' ? '可用' : '停用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
                <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
                <el-button link :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'" @click="toggleStatus(scope.row)">
                    {{ scope.row.status === 'ACTIVE' ? '停用' : '启用' }}
                </el-button>
            </template>
        </el-table-column>
    </el-table>
    
    <!-- 新建/编辑优惠券弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入优惠券名称" />
            </el-form-item>
            <el-form-item label="类型" prop="type">
                <el-radio-group v-model="form.type">
                    <el-radio v-for="item in couponTypeOptions" :key="item.value" :value="item.value">
                        {{ item.label }}
                    </el-radio>
                </el-radio-group>
            </el-form-item>
            
            <!-- 满减券：优惠金额 -->
            <el-form-item v-if="isAmountType" label="优惠金额" prop="amount">
                <el-input-number v-model="form.amount" :min="0" :precision="2" placeholder="优惠金额" />
                <span class="form-tip">元</span>
            </el-form-item>
            
            <!-- 折扣券：折扣比例 -->
            <el-form-item v-else label="折扣比例" prop="discountPercent">
                <el-input-number v-model="form.discountPercent" :min="1" :max="99" placeholder="折扣比例" />
                <span class="form-tip">%（如90表示9折）</span>
            </el-form-item>
            
            <el-form-item label="使用门槛">
                <el-input-number v-model="form.minAmount" :min="0" :precision="2" placeholder="满多少可用" />
                <span class="form-tip">元（0表示无门槛）</span>
            </el-form-item>
            
            <!-- 折扣券：最大抵扣 -->
            <el-form-item v-if="!isAmountType" label="最大抵扣">
                <el-input-number v-model="form.maxDiscount" :min="0" :precision="2" placeholder="最大抵扣金额" />
                <span class="form-tip">元（不填表示不限）</span>
            </el-form-item>
            
            <el-form-item label="发行量" prop="totalCount">
                <el-input-number v-model="form.totalCount" :min="1" placeholder="总发行量" />
            </el-form-item>
            <el-form-item label="每人限领">
                <el-input-number v-model="form.limitPerUser" :min="1" />
            </el-form-item>
            
            <el-divider content-position="left">有效期设置（二选一）</el-divider>
            
            <el-form-item label="有效天数">
                <el-input-number v-model="form.validDays" :min="1" placeholder="领取后N天有效" />
                <span class="form-tip">天（领取后生效）</span>
            </el-form-item>
            <el-form-item label="固定开始">
                <el-date-picker 
                    v-model="form.validFrom" 
                    type="datetime" 
                    placeholder="开始时间" 
                    value-format="YYYY-MM-DDTHH:mm:ss" 
                    style="width: 100%"
                />
            </el-form-item>
            <el-form-item label="固定结束">
                <el-date-picker 
                    v-model="form.validTo" 
                    type="datetime" 
                    placeholder="结束时间" 
                    value-format="YYYY-MM-DDTHH:mm:ss" 
                    style="width: 100%"
                />
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
.coupon-list { padding: 20px; background: white; }
.toolbar { margin-bottom: 20px; }
.form-tip { margin-left: 8px; color: #999; font-size: 12px; }
</style>
