<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantOrders, shipOrder } from '@/api/merchant'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const queryParams = ref({
    page: 1,
    pageSize: 10,
    status: ''
})

const shipDialogVisible = ref(false)
const shipFormRef = ref(null)
const shipForm = reactive({
    orderId: null,
    carrier: '',
    trackingNo: ''
})

const rules = {
    carrier: [{ required: true, message: '请输入快递公司', trigger: 'blur' }],
    trackingNo: [{ required: true, message: '请输入快递单号', trigger: 'blur' }]
}

const statusMap = {
    'PENDING_PAYMENT': '待付款',
    'PENDING_SHIPMENT': '待发货',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDING': '售后中'
}

const loadData = async () => {
    loading.value = true
    try {
        const res = await getMerchantOrders(queryParams.value)
        if (res.list) {
            orders.value = res.list
            total.value = res.total
        } else {
            orders.value = []
            total.value = 0
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleFilter = () => {
    queryParams.value.page = 1
    loadData()
}

const openShipDialog = (row) => {
    shipForm.orderId = row.id
    shipForm.carrier = ''
    shipForm.trackingNo = ''
    shipDialogVisible.value = true
}

const handleShip = async () => {
    if (!shipFormRef.value) return
    await shipFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await shipOrder(shipForm.orderId, {
                    carrier: shipForm.carrier,
                    trackingNo: shipForm.trackingNo
                })
                ElMessage.success('发货成功')
                shipDialogVisible.value = false
                loadData()
            } catch (e) {
                console.error(e)
            }
        }
    })
}

onMounted(() => {
    loadData()
})
</script>

<template>
  <div class="order-list">
    <div class="toolbar">
        <el-select v-model="queryParams.status" placeholder="订单状态" clearable @change="handleFilter">
            <el-option label="待发货" value="PENDING_SHIPMENT" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="售后中" value="REFUNDING" />
        </el-select>
        <el-button type="primary" @click="handleFilter">刷新</el-button>
    </div>

    <el-table :data="orders" v-loading="loading" border style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="商品信息" min-width="250">
            <template #default="scope">
                <div v-for="item in scope.row.items" :key="item.skuId" class="order-item">
                    <el-image :src="item.image" style="width: 40px; height: 40px; margin-right: 10px" />
                    <div>
                        <div>{{ item.productTitle }}</div>
                        <div class="sub-text">{{ item.skuName }} x {{ item.quantity }}</div>
                    </div>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
            <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column label="状态" width="100">
            <template #default="scope">
                <el-tag>{{ statusMap[scope.row.status] || scope.row.status }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
                <el-button 
                    v-if="scope.row.status === 'PENDING_SHIPMENT'"
                    type="primary" 
                    size="small" 
                    @click="openShipDialog(scope.row)"
                >
                    发货
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

    <el-dialog v-model="shipDialogVisible" title="订单发货" width="400px">
        <el-form ref="shipFormRef" :model="shipForm" :rules="rules" label-width="80px">
            <el-form-item label="快递公司" prop="carrier">
                <el-input v-model="shipForm.carrier" placeholder="如：顺丰速运" />
            </el-form-item>
            <el-form-item label="快递单号" prop="trackingNo">
                <el-input v-model="shipForm.trackingNo" placeholder="请输入单号" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="shipDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleShip">确认发货</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-list { padding: 20px; background: white; border-radius: 4px; }
.toolbar { margin-bottom: 20px; display: flex; gap: 10px; }
.order-item { display: flex; align-items: center; margin-bottom: 5px; }
.sub-text { font-size: 12px; color: #999; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
