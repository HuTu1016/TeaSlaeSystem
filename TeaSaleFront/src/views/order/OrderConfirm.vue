<template>
  <div class="checkout-page">
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="收货人" prop="receiverName">
                <el-input v-model="form.receiverName" placeholder="姓名"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="receiverPhone">
                <el-input v-model="form.receiverPhone" placeholder="手机号码"></el-input>
            </el-form-item>
            <el-form-item label="省份" prop="province">
                <el-input v-model="form.province" placeholder="省/自治区/直辖市"></el-input>
            </el-form-item>
            <el-form-item label="城市" prop="city">
                <el-input v-model="form.city" placeholder="城市"></el-input>
            </el-form-item>
            <el-form-item label="区/县" prop="district">
                <el-input v-model="form.district" placeholder="区/县"></el-input>
            </el-form-item>
            <el-form-item label="详细地址" prop="detail">
                <el-input v-model="form.detail" type="textarea" placeholder="街道门牌信息"></el-input>
            </el-form-item>
            <el-form-item label="默认地址">
                <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0"></el-switch>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveAddress">保存</el-button>
            </span>
        </template>
    </el-dialog>


    <div class="checkout-container">
      <!-- Address Selection -->
        <section class="checkout-section">
          <h3 class="section-title">收货地址</h3>
          <div class="address-grid">
            <div 
              class="address-card" 
              :class="{ active: selectedAddressId === addr.id }"
              v-for="addr in addresses" 
              :key="addr.id"
              @click="selectedAddressId = addr.id"
            >
              <div class="addr-actions">
                  <el-button type="primary" link @click.stop="openEdit(addr)">编辑</el-button>
                  <el-button type="danger" link @click.stop="handleDeleteAddress(addr.id)">删除</el-button>
              </div>
              <div class="addr-header">
                <span class="addr-name">{{ addr.receiverName }}</span>
                <span class="addr-phone">{{ addr.receiverPhone }}</span>
              </div>
              <div class="addr-body">
                {{ addr.province }} {{ addr.city }} {{ addr.district }}<br>
                {{ addr.detail }}
              </div>
              <div class="checked-icon" v-if="selectedAddressId === addr.id">
                <el-icon><Check /></el-icon>
              </div>
            </div>
            <div class="address-card add-new" @click="openAdd">
              <el-icon class="plus-icon"><Plus /></el-icon>
              <span>使用新地址</span>
            </div>
          </div>
        </section>

      <!-- Order Items -->
      <section class="checkout-section">
          <h3 class="section-title">商品清单</h3>
        <div class="order-items-list">
          <div class="order-item-header">
             <div class="col-goods">商品</div>
             <div class="col-price">单价</div>
             <div class="col-qty">数量</div>
             <div class="col-total">小计</div>
          </div>
          <div class="order-item" v-for="item in orderItems" :key="item.id">
             <div class="col-goods">
               <img :src="item.image" class="thumb" />
               <div class="info">
                 <div class="name">{{ item.title || item.productName }}</div>
                 <div class="sku">{{ item.skuName }}</div>
               </div>
             </div>
             <div class="col-price">¥{{ item.price }}</div>
             <div class="col-qty">x {{ item.quantity }}</div>
             <div class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </section>

      <!-- 支付方式选择 -->
      <section class="checkout-section">
        <h3 class="section-title">支付方式</h3>
        <div class="payment-options">
          <div 
            class="payment-option" 
            :class="{ active: paymentMethod === 'DIRECT' }"
            @click="paymentMethod = 'DIRECT'"
          >
            <div class="option-icon">
              <el-icon><CreditCard /></el-icon>
            </div>
            <div class="option-info">
              <div class="option-name">直接支付</div>
              <div class="option-desc">支付宝/微信支付</div>
            </div>
            <div class="checked-mark" v-if="paymentMethod === 'DIRECT'">
              <el-icon><Check /></el-icon>
            </div>
          </div>
          <div 
            class="payment-option" 
            :class="{ active: paymentMethod === 'BALANCE', disabled: !canUseBalance }"
            @click="canUseBalance && (paymentMethod = 'BALANCE')"
          >
            <div class="option-icon balance-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="option-info">
              <div class="option-name">余额支付</div>
              <div class="option-desc">
                可用余额：¥{{ walletBalance }}
                <span v-if="!canUseBalance" class="insufficient">(余额不足)</span>
              </div>
            </div>
            <div class="checked-mark" v-if="paymentMethod === 'BALANCE'">
              <el-icon><Check /></el-icon>
            </div>
          </div>
        </div>
      </section>

      <!-- Summary -->
      <section class="summary-section">
        <div class="summary-row">
          <span>商品金额</span>
          <span class="val">¥{{ totalPrice }}</span>
        </div>
        <div class="summary-row" v-if="memberDiscount > 0">
          <span>
            <el-tag size="small" type="warning">{{ memberLevel }}</el-tag>
            会员折扣
          </span>
          <span class="val discount">-¥{{ memberDiscount }}</span>
        </div>
        <div class="summary-row">
          <span>运费</span>
          <span class="val">¥0.00</span>
        </div>
        <div class="total-row">
          <span class="label">实付金额:</span>
          <span class="final-price">¥{{ finalPrice }}</span>
        </div>
        <div class="action-row">
          <button class="submit-btn" @click="submitOrder" :disabled="loading">
             {{ loading ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder } from '@/api/order'
import { getAddresses, addAddress, updateAddress, deleteAddress } from '@/api/address'
import { getMemberCenter } from '@/api/wallet'
import { Check, Plus, CreditCard, Wallet } from '@element-plus/icons-vue'

const router = useRouter()
const addresses = ref([])
const selectedAddressId = ref(null)
const orderItems = ref([])
const loading = ref(false)
const paymentMethod = ref('DIRECT')  // DIRECT-直接支付, BALANCE-余额支付
const walletBalance = ref('0.00')
const memberLevel = ref('')
const discountRate = ref(100)  // 折扣率，100表示无折扣

const fetchAddresses = async () => {
    try {
      const res = await getAddresses()
      if (res) {
          addresses.value = res
          // If no selected address, select default or first
          if (!selectedAddressId.value) {
              const def = res.find(a => a.isDefault === 1)
              if (def) selectedAddressId.value = def.id
              else if (res.length > 0) selectedAddressId.value = res[0].id
          }
      }
  } catch (e) {
      console.error(e)
  }
}

// 加载会员中心信息（余额和会员等级）
const fetchMemberCenter = async () => {
  try {
    const res = await getMemberCenter()
    if (res) {
      walletBalance.value = parseFloat(res.balance?.balance || 0).toFixed(2)
      memberLevel.value = res.currentLevel?.name || ''
      discountRate.value = res.currentLevel?.discountRate || 100
    }
  } catch (e) {
    console.error('获取会员信息失败', e)
  }
}

onMounted(async () => {
  await fetchAddresses()
  await fetchMemberCenter()

  // Fetch Items from Session (passed from Cart)
  const storedItems = sessionStorage.getItem('checkoutItems')
  if (storedItems) {
      orderItems.value = JSON.parse(storedItems)
  }
})

const totalPrice = computed(() => orderItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0).toFixed(2))

// 计算会员折扣金额
const memberDiscount = computed(() => {
  if (discountRate.value >= 100) return '0.00'
  const total = parseFloat(totalPrice.value)
  const discount = total * (100 - discountRate.value) / 100
  return discount.toFixed(2)
})

// 计算实付金额
const finalPrice = computed(() => {
  const total = parseFloat(totalPrice.value)
  const discount = parseFloat(memberDiscount.value)
  return (total - discount).toFixed(2)
})

// 是否可以使用余额支付
const canUseBalance = computed(() => {
  return parseFloat(walletBalance.value) >= parseFloat(finalPrice.value)
})

const submitOrder = async () => {
    if (!selectedAddressId.value) return ElMessage.warning('请选择收货地址')
    
    loading.value = true
    try {
        const payload = {
            addressId: selectedAddressId.value,
            items: orderItems.value.map(i => ({ skuId: i.skuId || i.id, quantity: i.quantity })) 
        }
        
        const res = await createOrder(payload)
        
        ElMessage.success('订单提交成功')
        setTimeout(() => {
             // 跳转到某个页面，这里暂时回商品页或者订单页
             router.push(`/products`) 
        }, 1000)

    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

// Address CRUD
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const formRef = ref(null)
const form = ref({
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: 0
})
const isEdit = ref(false)
const editId = ref(null)

const rules = {
    receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
    receiverPhone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
    city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
    district: [{ required: true, message: '请输入区/县', trigger: 'blur' }],
    detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const openAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增地址'
    form.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
    dialogVisible.value = true
}

const openEdit = (addr) => {
    isEdit.value = true
    editId.value = addr.id
    dialogTitle.value = '编辑地址'
    form.value = { 
        receiverName: addr.receiverName, 
        receiverPhone: addr.receiverPhone, 
        province: addr.province, 
        city: addr.city, 
        district: addr.district, 
        detail: addr.detail, 
        isDefault: addr.isDefault || 0
    }
    dialogVisible.value = true
}

const handleSaveAddress = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (isEdit.value) {
                    await updateAddress(editId.value, form.value)
                    ElMessage.success('地址更新成功')
                } else {
                    await addAddress(form.value)
                    ElMessage.success('地址添加成功')
                }
                dialogVisible.value = false
                fetchAddresses()
            } catch (e) {
                console.error(e)
            }
        }
    })
}

const handleDeleteAddress = async (id) => {
    try {
        await deleteAddress(id)
        ElMessage.success('删除成功')
        if (selectedAddressId.value === id) selectedAddressId.value = null
        fetchAddresses()
    } catch (e) {
        console.error(e)
    }
}
</script>

<style scoped>
.checkout-page {
  padding-bottom: 80px;
}

.page-header {
  text-align: center;
  margin: 40px 0;
}

.page-title {
  font-family: var(--font-heading);
  font-size: 32px;
  color: var(--color-heading);
}

.page-subtitle {
  font-family: 'Playfair Display', serif;
  font-style: italic;
  color: #999;
}

.checkout-container {
  background: white;
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.03);
  border-radius: 4px;
}

.checkout-section {
  margin-bottom: 50px;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  border-left: 4px solid var(--vt-c-tea-gold);
  padding-left: 15px;
}

/* Address */
.address-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.address-card {
  border: 1px solid var(--color-border);
  padding: 20px;
  cursor: pointer;
  border-radius: 4px;
  position: relative;
  transition: all 0.3s;
}

.address-card:hover {
  border-color: #ccc;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.address-card.active {
  border-color: var(--vt-c-tea-green-dark);
  background-color: rgba(44, 76, 59, 0.02);
}

.addr-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-weight: bold;
  color: #333;
}

.addr-body {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.checked-icon {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 0;
  height: 0;
  border-bottom: 30px solid var(--vt-c-tea-green-dark);
  border-left: 30px solid transparent;
}

.checked-icon .el-icon {
  position: absolute;
  right: 2px;
  bottom: -28px; /* Adjustment for triangle */
  color: white;
  font-size: 14px;
}

.add-new {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  border-style: dashed;
}

.add-new:hover {
  color: var(--vt-c-tea-green-dark);
  border-color: var(--vt-c-tea-green-dark);
}

.plus-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

/* Order Items */
.order-items-list {
  border-top: 1px solid #eee;
}

.order-item-header {
  display: flex;
  padding: 15px 0;
  color: #999;
  font-size: 13px;
  border-bottom: 1px solid #eee;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.col-goods { flex: 1; display: flex; align-items: center; }
.col-price { width: 120px; text-align: center; }
.col-qty { width: 100px; text-align: center; }
.col-total { width: 120px; text-align: right; font-weight: bold; color: var(--vt-c-tea-green-dark); }

.thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 15px;
  border-radius: 2px;
}

.info .name {
  font-family: var(--font-heading);
  font-size: 15px;
  color: #333;
  margin-bottom: 5px;
}
.info .sku {
  font-size: 12px;
  color: #999;
}

/* Summary */
.summary-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 40px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  width: 300px;
  margin-bottom: 15px;
  color: #666;
}

.total-row {
  display: flex;
  justify-content: space-between;
  width: 300px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  align-items: baseline;
}

.total-row .label {
  font-size: 16px;
  color: #333;
}

.final-price {
  font-size: 32px;
  font-family: 'Lato', sans-serif;
  color: var(--vt-c-tea-green-dark);
  font-weight: bold;
}

.submit-btn {
  margin-top: 30px;
  background-color: var(--vt-c-tea-green-dark);
  color: white;
  border: none;
  padding: 15px 50px;
  font-size: 18px;
  cursor: pointer;
  letter-spacing: 1px;
  transition: all 0.3s;
}

.submit-btn:hover {
  background-color: #1e3529;
}

.submit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .address-grid {
    grid-template-columns: 1fr;
  }
}

.addr-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: none;
  background: rgba(255,255,255,0.9);
  padding: 2px 5px;
  border-radius: 4px;
}

.address-card:hover .addr-actions {
  display: block;
}

/* 支付方式选择样式 */
.payment-options {
  display: flex;
  gap: 20px;
}

.payment-option {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.payment-option:hover {
  border-color: #ccc;
}

.payment-option.active {
  border-color: var(--vt-c-tea-green-dark);
  background-color: rgba(44, 76, 59, 0.05);
}

.payment-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.option-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #e3f2fd;
  color: #2196f3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.option-icon.balance-icon {
  background: #fff3e0;
  color: #ff9800;
}

.option-info {
  flex: 1;
}

.option-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.option-desc {
  font-size: 13px;
  color: #999;
}

.option-desc .insufficient {
  color: #f56c6c;
}

.checked-mark {
  position: absolute;
  right: 16px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--vt-c-tea-green-dark);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 折扣金额样式 */
.summary-row .discount {
  color: #67c23a;
  font-weight: 500;
}

.summary-row .el-tag {
  margin-right: 6px;
}
</style>
