<template>
  <div class="cart-page">


    <div class="cart-container" v-if="cartItems.length > 0">
      <div class="cart-top-bar" style="margin-bottom: 20px;">
        <div class="cart-title-row" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <h1 class="cart-title" style="margin: 0; font-size: 24px;">购物车</h1>
          <el-button link type="danger" @click="handleClearCart">清空购物车</el-button>
        </div>
        <div class="address-bar-container" v-if="!loadingAddresses">
           <div class="current-address-bar" @click="openAddressDialog" :class="{'no-address': !selectedAddressId}">
              <!-- 背景提示文字 -->
              <span class="address-hint">请选择修改或更换收货地址</span>
              <el-icon class="loc-icon"><Location /></el-icon>
              <div class="addr-info" v-if="selectedAddress">
                 <span class="addr-contact">{{ selectedAddress.receiverName }} {{ selectedAddress.receiverPhone }}</span>
                 <span class="addr-text">{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}</span>
              </div>
              <div class="addr-placeholder" v-else>
                 请选择收货地址
              </div>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
           </div>
        </div>
      </div>
      <div class="cart-list">
        <div class="cart-header-row">
          <div class="col-check">
             <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAllChange">全选</el-checkbox>
          </div>
          <div class="col-info">商品信息</div>
          <div class="col-price">单价</div>
          <div class="col-qty">数量</div>
          <div class="col-subtotal">小计</div>
          <div class="col-action">操作</div>
        </div>

        <div class="cart-item" v-for="(item, index) in cartItems" :key="item.id">
          <div class="col-check">
            <el-checkbox v-model="item.checked" />
          </div>
          <div class="col-info">
            <img :src="item.image" class="item-img" />
            <div class="item-meta">
              <h3 class="item-title" @click="$router.push(`/products/${item.productId}`)">{{ item.productTitle }}</h3>
              <p class="item-subtitle" v-if="item.subtitle">{{ item.subtitle }}</p>
              <p class="item-sku">{{ item.skuName }}</p>
            </div>
          </div>
          <div class="col-price">¥{{ item.price }}</div>
          <div class="col-qty">
            <el-input-number 
              v-model="item.quantity" 
              :min="1" 
              size="small" 
              @change="(val) => handleQuantityChange(item, val)"
            />
          </div>
          <div class="col-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <div class="col-action">
            <el-button link class="delete-btn" @click="removeItem(index)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <router-link to="/products" class="continue-shop">继续购物</router-link>
        </div>
        <div class="footer-right">
          <div class="total-info-col">
            <div class="price-row">
              <span class="label">商品总额:</span>
              <span>¥{{ totalPrice }}</span>
            </div>
            <div class="price-row member-discount-row" v-if="memberDiscountAmount > 0">
              <span class="label">
                <el-tag size="small" type="warning">{{ memberInfo.currentLevel?.name }}</el-tag>
                会员{{ memberDiscountText }}:
              </span>
              <span class="member-discount">-¥{{ memberDiscountAmount.toFixed(2) }}</span>
            </div>
            <div class="price-row discount-row" @click="openCouponDialog">
              <span class="label">优惠券:</span>
              <div class="discount-val">
                 <span v-if="discountAmount > 0">-¥{{ discountAmount.toFixed(2) }}</span>
                 <span v-else class="no-coupon">无可用优惠券</span>
                 <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
            <div class="price-row final">
              <span class="label">合计 (不含运费):</span>
              <span class="total-price">¥{{ finalTotalPrice }}</span>
            </div>
          </div>
          <button class="checkout-btn" @click="submitOrder" :disabled="selectedCount === 0 || !selectedAddressId || submitting">
            {{ submitting ? '提交中...' : `提交订单 (${selectedCount})` }}
          </button>
        </div>
      </div>
    </div>
    <!-- Empty Cart -->
    <div class="empty-cart" v-else>
      <el-empty description="购物车空空如也" :image-size="200">
          <el-button size="large" class="go-shop-btn" @click="$router.push('/products')" 
            style="margin-top: 20px; padding: 12px 30px; font-size: 16px; background-color: #C1E1C1; border-color: #C1E1C1; color: #000;">
            去挑选好茶
          </el-button>
      </el-empty>
    </div>

    <!-- Coupon Dialog -->
    <el-dialog v-model="couponDialogVisible" title="选择优惠券" width="400px">
       <div class="coupon-list">
          <div 
             class="coupon-item" 
             v-for="coupon in availableCoupons" 
             :key="coupon.id"
             :class="{ active: selectedCouponId === coupon.id }"
             @click="selectCoupon(coupon)"
          >
             <div class="cp-left">
                <span class="cp-amount" v-if="coupon.type === 'AMOUNT'">¥{{ coupon.amount }}</span>
                <span class="cp-amount" v-else>{{ coupon.discountPercent / 10 }}折</span>
                <span class="cp-cond">满{{ coupon.minAmount }}可用</span>
             </div>
             <div class="cp-right">
                <span class="cp-name">{{ coupon.name || '优惠券' }}</span>
                <span class="cp-date">有效期至 {{ formatDate(coupon.validTo) }}</span>
             </div>
             <el-icon class="check-mark" v-if="selectedCouponId === coupon.id"><Check /></el-icon>
          </div>
          <div class="no-coupon-option" @click="selectCoupon(null)" :class="{ active: selectedCouponId === null }">
             不使用优惠券
             <el-icon class="check-mark" v-if="selectedCouponId === null"><Check /></el-icon>
          </div>
       </div>
    </el-dialog>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :append-to-body="true">
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
                  <el-input v-model="form.detail" type="textarea" placeholder="街道门牌信息" :rows="2"></el-input>
              </el-form-item>
              <el-form-item label="默认地址">
                  <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0"></el-switch>
              </el-form-item>
          </el-form>
          <div style="text-align: right; margin-top: 20px; padding-top: 15px; border-top: 1px solid #eee;">
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleSaveAddress">保存</el-button>
          </div>
    </el-dialog>
    
    <!-- Address Selection List Dialog (Optional, or integrate into main dialog) -->
    <!-- For simplicity, use a separate selection dialog or enhance the top bar to show list -->
    <el-dialog v-model="addressListVisible" title="选择收货地址" width="600px">
       <div class="address-list-select">
          <div 
             class="addr-item-row" 
             v-for="addr in addresses" 
             :key="addr.id"
             :class="{ active: selectedAddressId === addr.id }"
             @click="selectAddress(addr)"
          >
             <div class="row-info">
                <div class="row-contact">{{ addr.receiverName }} {{ addr.receiverPhone }}</div>
                <div class="row-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
             </div>
             <div class="row-ops">
                <el-button link type="primary" @click.stop="openEdit(addr)">编辑</el-button>
                <el-button link type="danger" @click.stop="handleDeleteAddress(addr.id)">删除</el-button>
             </div>
             <el-icon class="check-mark" v-if="selectedAddressId === addr.id"><Check /></el-icon>
          </div>
          <div class="add-new-btn" @click="openAdd">
             <el-icon><Plus /></el-icon> 新增地址
          </div>
       </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCart, deleteCartItem, updateCartItem, clearCart } from '@/api/cart'
import { getAvailableCouponsForAmount } from '@/api/coupon'
import { getAddresses, addAddress, updateAddress, deleteAddress } from '@/api/address'
import { createOrder, payOrder } from '@/api/order'
import { getMemberCenter } from '@/api/wallet'
import { Delete, Location, ArrowDown, ArrowRight, Check, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])
const discountAmount = ref(0)
const bestCoupon = ref(null)

// Address Logic
const addresses = ref([])
const selectedAddressId = ref(null)
const loadingAddresses = ref(true)
const addressListVisible = ref(false)
const submitting = ref(false)

const selectedAddress = computed(() => addresses.value.find(a => a.id === selectedAddressId.value))

// 会员信息
const memberInfo = reactive({
  currentLevel: null,
  discountRate: 100
})

const fetchMemberInfo = async () => {
  try {
    const res = await getMemberCenter()
    if (res) {
      memberInfo.currentLevel = res.currentLevel
      memberInfo.discountRate = res.currentLevel?.discountRate || 100
    }
  } catch (e) {
    console.error('获取会员信息失败', e)
  }
}

// 会员折扣金额
const memberDiscountAmount = computed(() => {
  if (memberInfo.discountRate >= 100) return 0
  const total = parseFloat(totalPrice.value) || 0
  return total * (100 - memberInfo.discountRate) / 100
})

// 会员折扣文案 (如"9.5折")
const memberDiscountText = computed(() => {
  if (memberInfo.discountRate >= 100) return ''
  return (memberInfo.discountRate / 10).toFixed(1) + '折'
})

// 最终价格 = 商品总额 - 会员折扣 - 优惠券折扣
const finalTotalPrice = computed(() => {
  const total = parseFloat(totalPrice.value) || 0
  const memberDiscount = memberDiscountAmount.value
  const couponDiscount = discountAmount.value
  return (total - memberDiscount - couponDiscount).toFixed(2)
})

const fetchAddresses = async () => {
    try {
        const res = await getAddresses()
        if (res) {
            addresses.value = res
            if (!selectedAddressId.value) {
                const def = res.find(a => a.isDefault === 1)
                if (def) selectedAddressId.value = def.id
                else if (res.length > 0) selectedAddressId.value = res[0].id
            }
        }
    } catch (e) {
        console.error(e)
    } finally {
        loadingAddresses.value = false
    }
}

const openAddressDialog = () => {
    addressListVisible.value = true
}

const selectAddress = (addr) => {
    selectedAddressId.value = addr.id
    addressListVisible.value = false
}

// CRUD
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const formRef = ref(null)
const form = ref({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })
const isEdit = ref(false)
const editId = ref(null)

const rules = {
    receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
    receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
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

const loadCart = async () => {
    try {
        const res = await getCart()
        if (res && res.items) {
            cartItems.value = res.items.map(item => ({ ...item, checked: false }))
        } else if (Array.isArray(res)) {
            cartItems.value = res.map(item => ({ ...item, checked: false }))
        }
    } catch (e) {
        console.error(e)
    }
}

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.checked)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.checked).length
})

const checkAll = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(i => i.checked),
  set: (val) => handleCheckAllChange(val)
})

const isIndeterminate = computed(() => {
  const checkedCount = selectedCount.value
  return checkedCount > 0 && checkedCount < cartItems.value.length
})

const handleCheckAllChange = (val) => {
  cartItems.value.forEach(item => item.checked = val)
}

const removeItem = async (index) => {
  const item = cartItems.value[index]
  try {
      await deleteCartItem(item.id)
      cartItems.value.splice(index, 1)
      ElMessage.success('已移除')
  } catch (e) {
      console.error(e)
  }
}

const handleClearCart = async () => {
    try {
        await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        await clearCart()
        cartItems.value = []
        ElMessage.success('购物车已清空')
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

const handleQuantityChange = async (item, val) => {
    try {
        await updateCartItem(item.id, { quantity: val })
    } catch (e) {
        console.error(e)
    }
}

const availableCoupons = ref([])
const couponDialogVisible = ref(false)
const selectedCouponId = ref(null) // ID of manually or auto selected coupon

const formatDate = (dateStr) => {
    if (!dateStr) return ''
    return dateStr.split('T')[0]
}

const openCouponDialog = () => {
    if (availableCoupons.value.length === 0) return ElMessage.info('暂无可用优惠券')
    couponDialogVisible.value = true
}

const selectCoupon = (coupon) => {
    if (coupon) {
        selectedCouponId.value = coupon.id
        discountAmount.value = calculateDiscountFor(coupon, parseFloat(totalPrice.value))
    } else {
        selectedCouponId.value = null
        discountAmount.value = 0
    }
    couponDialogVisible.value = false
}

const calculateDiscountFor = (coupon, total) => {
    let discount = 0
    if (coupon.type === 'AMOUNT') { 
        discount = Number(coupon.amount)
    } else if (coupon.type === 'PERCENT') { 
        const discountRate = 1 - (coupon.discountPercent / 100)
        discount = total * discountRate
        if (coupon.maxDiscount && discount > coupon.maxDiscount) {
            discount = Number(coupon.maxDiscount)
        }
    }
    return discount
}

const calculateBestDiscount = async () => {
    const total = parseFloat(totalPrice.value)
    if (total <= 0) {
        discountAmount.value = 0
        availableCoupons.value = []
        selectedCouponId.value = null
        return
    }

    try {
        const res = await getAvailableCouponsForAmount(total)
        let coupons = []
        if (res && Array.isArray(res)) coupons = res
        else if (res && res.data) coupons = res.data

        // Filter expired
        const now = new Date()
        coupons = coupons.filter(c => !c.validTo || new Date(c.validTo) > now)
        
        availableCoupons.value = coupons

        if (coupons.length === 0) {
            discountAmount.value = 0
            selectedCouponId.value = null
            return
        }

        // Only auto-select if user hasn't manually selected (or if previous selection is no longer valid)
        // For simplicity, always auto-select the best one initially
        // If we want to persist manual selection, we need to check if selectedCouponId is in new list.
        
        let best = null
        let maxDiscount = 0

        coupons.forEach(c => {
            const d = calculateDiscountFor(c, total)
            if (d > maxDiscount) {
                maxDiscount = d
                best = c
            }
        })
        
        // Auto select best
        if (best) {
            selectedCouponId.value = best.id
            discountAmount.value = maxDiscount
        }
    } catch (e) {
        console.error(e)
    }
}

watch(totalPrice, () => {
    calculateBestDiscount()
})

const submitOrder = async () => {
  const selected = cartItems.value.filter(item => item.checked)
  if (selected.length === 0) return ElMessage.warning('请选择要结算的商品')
  if (!selectedAddressId.value) return ElMessage.warning('请选择收货地址')

  submitting.value = true
  try {
      const orderPayload = {
          addressId: selectedAddressId.value,
          couponId: selectedCouponId.value, // 传递选中的优惠券ID
          items: selected.map(item => ({
              skuId: item.skuId, 
              quantity: item.quantity
          }))
      }

      // 1. 创建订单
      const res = await createOrder(orderPayload)
      const orderId = res.orderId
      
      // 2. 模拟支付
      await payOrder(orderId)
      
      // 3. 删除已购买的购物车项
      for (const item of selected) {
          await deleteCartItem(item.id)
      }
      
      // 4. 漂亮的成功弹窗
      ElMessageBox.confirm(
        '您的订单已支付成功，商家将尽快为您发货！',
        '支付成功',
        {
          confirmButtonText: '查看订单',
          cancelButtonText: '继续购物',
          type: 'success',
          center: true,
          showClose: false,
          closeOnClickModal: false,
          distinguishCancelAndClose: true
        }
      )
        .then(() => {
          // 点击"查看订单"
          router.push('/user/orders')
        })
        .catch((action) => {
          // 点击"继续购物" (cancel)
          if (action === 'cancel') {
            router.push('/products')
          }
        })
      
  } catch (e) {
      console.error(e)
  } finally {
      submitting.value = false
  }
}

onMounted(async () => {
    await loadCart()
    await fetchAddresses()
    await fetchMemberInfo()
})
</script>

<style scoped>
/* Hide global footer on cart page */
:deep(.site-footer),
:deep(.footer),
:deep(footer) {
  display: none !important;
}

/* Ensure dialog footer is visible */
:deep(.el-dialog) {
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
}

:deep(.el-dialog__footer) {
  flex-shrink: 0;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.cart-page {
  padding-bottom: 120px;
}

.page-header {
  text-align: center;
  margin: 40px 0;
}

.page-title {
  font-family: var(--font-heading);
  font-size: 32px;
  color: var(--color-heading);
  margin-bottom: 5px;
}

.page-subtitle {
  font-family: 'Playfair Display', serif;
  font-style: italic;
  color: #999;
}

.cart-container {
  background: white;
  border-radius: 4px;
  padding: 0 20px;
}

.cart-header-row {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid var(--color-border);
  color: #666;
  font-size: 14px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 25px 0;
  border-bottom: 1px solid #eee;
}

.col-check { 
  width: 80px; 
  display: flex; 
  align-items: center; 
  justify-content: center;
}
.col-info { flex: 1; display: flex; align-items: center; }
.col-price { width: 120px; text-align: center; color: #666; }
.col-qty { width: 150px; text-align: center; }
.col-subtotal { width: 120px; text-align: center; font-weight: bold; color: var(--vt-c-tea-green-dark); }
.col-action { width: 100px; text-align: center; }

.item-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 20px;
  border-radius: 2px;
}

.item-meta {
  flex: 1;
}

.item-title {
  font-family: var(--font-heading);
  font-size: 16px;
  color: #333;
  margin: 0 0 5px;
  cursor: pointer;
}

.item-title:hover {
  color: var(--vt-c-tea-green-dark);
}

.item-subtitle {
  font-size: 13px;
  color: #888;
  margin-bottom: 4px;
  line-height: 1.4;
}

.item-sku {
  font-size: 12px;
  color: #999;
}

.delete-btn {
  color: #999;
}
.delete-btn:hover {
  color: #f56c6c;
}

.cart-page {
  padding-bottom: 150px;
}
/* ... existing styles ... */
.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 50px;
  background: #fcfbf7;
  border-top: 1px solid var(--color-border);
  box-shadow: 0 -4px 12px rgba(0,0,0,0.05);
}

.continue-shop {
  background-color: #2c4c3b;
  color: white;
  padding: 12px 30px;
  text-decoration: none;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s;
  display: inline-block;
}
.continue-shop:hover {
  background-color: #1e3529;
  text-decoration: none;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-info-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.price-row {
  font-size: 14px;
  color: #666;
  display: flex;
  gap: 10px;
}

.price-row.discount {
  color: #f56c6c;
}

.price-row.member-discount-row {
  align-items: center;
}

.price-row.member-discount-row .label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.member-discount {
  color: #e6a23c;
  font-weight: 600;
}

.price-row.final {
  margin-top: 5px;
}

.price-row.final .label {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 24px;
  color: var(--vt-c-tea-green-dark);
  font-weight: bold;
  font-family: 'Lato', sans-serif;
}

/* Address Bar */
.address-bar-container {
  margin-top: 15px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}
.address-bar-container:hover {
  border-color: var(--vt-c-tea-green-dark);
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.current-address-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
}

/* 背景提示文字样式 */
.address-hint {
  position: absolute;
  right: 50px;
  top: 50%;
  transform: translateY(-50%);
  color: #ccc;
  font-size: 14px;
  pointer-events: none;
  z-index: 0;
}


.loc-icon {
  font-size: 24px;
  color: var(--vt-c-tea-green-dark);
}

.addr-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.addr-contact {
  font-weight: bold;
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.addr-text {
  font-size: 13px;
  color: #666;
}

.arrow-icon {
  margin-left: auto;
  color: #ccc;
}

.addr-placeholder {
  flex: 1;
  color: #999;
  font-size: 16px;
}

.no-address .loc-icon {
  color: #999;
}

/* Address List Dialog */
.address-list-select {
  max-height: 400px;
  overflow-y: auto;
}

.addr-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  position: relative;
}

.addr-item-row:hover {
  background: #f9f9f9;
}

.addr-item-row.active {
  background: rgba(44, 76, 59, 0.05);
}

.row-info {
  flex: 1;
}

.row-contact {
  font-weight: bold;
  margin-bottom: 5px;
}

.row-detail {
  font-size: 12px;
  color: #666;
}

.row-ops {
  margin-right: 30px;
  display: flex;
}

.check-mark {
  color: var(--vt-c-tea-green-dark);
  font-size: 20px;
}

.add-new-btn {
  text-align: center;
  padding: 15px;
  color: var(--vt-c-tea-green-dark);
  cursor: pointer;
  font-weight: bold;
  border-top: 1px solid #eee;
  margin-top: 10px;
}

.add-new-btn:hover {
  background: #f9f9f9;
}

.checkout-btn {
  background-color: #2c4c3b;
  color: white;
  border: none;
  padding: 12px 40px;
  font-size: 16px;
  cursor: pointer;
  letter-spacing: 1px;
  transition: all 0.3s;
}

.checkout-btn:hover {
  background-color: #1e3529;
}

.checkout-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.empty-cart {
  text-align: center;
  padding: 60px 0;
}

.go-shop-btn {
  background-color: var(--vt-c-tea-green-dark);
  border-color: var(--vt-c-tea-green-dark);
  padding: 12px 30px;
}

/* Coupon UI */
.discount-row {
  cursor: pointer;
}
.discount-val {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #f56c6c;
}
.no-coupon {
  color: #999;
  font-size: 12px;
}

/* Coupon List */
.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.coupon-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 4px;
  cursor: pointer;
  background: #fff;
  transition: all 0.2s;
}

.coupon-item:hover {
  border-color: #f56c6c;
}

.coupon-item.active {
  background: #fff0f0;
  border-color: #f56c6c;
}

.cp-left {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  color: #f56c6c;
}

.cp-amount {
  font-size: 20px;
  font-weight: bold;
}

.cp-cond {
  font-size: 12px;
}

.cp-right {
  flex: 1;
  margin-left: 15px;
  display: flex;
  flex-direction: column;
}

.cp-name {
  font-weight: bold;
  color: #333;
}

.cp-date {
  font-size: 12px;
  color: #999;
}

.no-coupon-option {
  padding: 15px;
  text-align: center;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.no-coupon-option.active {
  border-color: #333;
  font-weight: bold;
}
</style>
