<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCart, mergeCart, removeCart, updateCartChecked, updateCartCount, type CartItem } from '@/api/cart'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

/** 游客车（localStorage；登录后合并到 cart 表） */
interface GuestItem extends CartItem {
  localId: string
}

const items = ref<CartItem[] | GuestItem[]>([])
const loading = ref(false)
const checkoutDialog = ref(false)
const payDialog = ref(false)
const createdOrders = ref<string[]>([])
const receiver = reactive({ name: '', phone: '', address: '' })

const isLogged = computed(() => !!userStore.token)
const checkedItems = computed(() => items.value.filter((i) => i.checked === 1))
const totalAmount = computed(() => checkedItems.value.reduce((s, i) => s + (i.price ?? 0) * i.count, 0))

const GUEST_KEY = 'mall_guest_cart'

function readGuestCart(): GuestItem[] {
  try {
    return JSON.parse(localStorage.getItem(GUEST_KEY) ?? '[]')
  } catch {
    return []
  }
}

function writeGuestCart(list: GuestItem[]) {
  localStorage.setItem(GUEST_KEY, JSON.stringify(list))
}

/** 游客本地购物车接口占位（加购走 ProductDetail；合并见下方流程） */
async function load() {
  loading.value = true
  try {
    if (!isLogged.value) {
      items.value = readGuestCart()
      return
    }
    // 登录后：先合并本地车（同 SKU 由后端累加），清空本地
    const local = readGuestCart()
    if (local.length) {
      await mergeCart(local.map((l) => ({ skuId: l.skuId, count: l.count })))
      writeGuestCart([])
    }
    items.value = await getCart()
  } finally {
    loading.value = false
  }
}

function guestById(id: number, list: GuestItem[] = readGuestCart()) {
  return list.find((i) => i.skuId === id)
}

async function onCountChange(item: CartItem | GuestItem, count: number) {
  if (!isLogged.value) {
    const list = readGuestCart()
    const g = list.find((i) => i.skuId === item.skuId)
    if (g) g.count = count
    writeGuestCart(list)
    return
  }
  await updateCartCount(item.skuId, count)
}

async function onChecked(item: CartItem | GuestItem, checked: number) {
  if (!isLogged.value) {
    const list = readGuestCart()
    const g = list.find((i) => i.skuId === item.skuId)
    if (g) g.checked = checked
    writeGuestCart(list)
    return
  }
  await updateCartChecked(item.skuId, checked)
}

async function remove(item: CartItem | GuestItem) {
  await ElMessageBox.confirm('从购物车删除该商品？', '提醒', { type: 'warning' })
  if (!isLogged.value) {
    writeGuestCart(readGuestCart().filter((i) => i.skuId !== item.skuId))
    await load()
    return
  }
  await removeCart(item.skuId)
  await load()
}

function openCheckout() {
  if (!isLogged.value) {
    ElMessage.info('请先登录再结算（游客购物车将在登录后自动合并）')
    router.push('/login')
    return
  }
  if (!checkedItems.value.length) {
    ElMessage.warning('请先勾选商品')
    return
  }
  receiver.name = ''
  receiver.phone = ''
  receiver.address = ''
  checkoutDialog.value = true
}

async function submitOrder() {
  if (!receiver.name || !receiver.phone || !receiver.address) {
    ElMessage.warning('请填写完整收货信息')
    return
  }
  try {
    createdOrders.value = await createOrder({
      reqId: crypto.randomUUID(),
      items: checkedItems.value.map((i) => ({ skuId: i.skuId, count: i.count })),
      receiverName: receiver.name,
      receiverPhone: receiver.phone,
      receiverAddress: receiver.address,
      fromCart: true,
    })
    checkoutDialog.value = false
    payDialog.value = true
    await load()
  } catch {
    /* 拦截器统一提示 */
  }
}

function goOrders() {
  payDialog.value = false
  router.push('/orders')
}

onMounted(load)
</script>

<template>
  <div class="cart">
    <header class="head">
      <router-link class="brand" to="/">MX<span class="brand-sub">购物车</span></router-link>
      <router-link class="nav-link" to="/orders">我的订单</router-link>
    </header>

    <p v-if="!isLogged" class="guest-tip">
      游客购物车暂存本机，<router-link to="/login">登录</router-link>后自动合并到账户。
    </p>

    <el-table :data="items" v-loading="loading" class="table">
      <el-table-column width="50">
        <template #default="{ row }">
          <el-checkbox :model-value="row.checked === 1" @change="(v: string | number | boolean) => onChecked(row, v ? 1 : 0)" />
        </template>
      </el-table-column>
      <el-table-column label="商品" min-width="240">
        <template #default="{ row }">
          <div class="goods">
            <div class="thumb">
              <img v-if="row.mainImg" :src="row.mainImg" alt="" />
            </div>
            <div>
              <p class="title">{{ row.productTitle || '商品' }}</p>
              <p class="spec md-num">{{ row.specJson || '『规格' }}</p>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          <span class="md-num price">¥ {{ (row.price ?? 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="160">
        <template #default="{ row }">
          <el-input-number
            :model-value="row.count"
            :min="1"
            size="small"
            @change="(v: number | undefined) => onCountChange(row, v ?? 1)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="items.length" class="bar">
      <span class="md-num bar-count">已勾选 {{ checkedItems.length }} 项</span>
      <span class="md-num bar-total">合计 ¥ {{ totalAmount.toFixed(2) }}</span>
      <el-button type="primary" size="large" @click="openCheckout">去结算</el-button>
    </div>
    <p v-else-if="!loading" class="empty">购物车空空如也——去货架挑点东西吧</p>

    <el-dialog v-model="checkoutDialog" title="填写收货信息（下单快照）" width="460">
      <el-form label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="receiver.name" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="receiver.phone" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="receiver.address" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutDialog = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="payDialog" title="下单成功" width="420">
      <p class="pay-tip">已拆分为 {{ createdOrders.length }} 笔订单（跨店购物车逐店一单）：</p>
      <p v-for="no in createdOrders" :key="no" class="md-num pay-no">{{ no }}</p>
      <p class="pay-note">支付功能在里程碑 6 接入（支付宝沙箱）。</p>
      <template #footer>
        <el-button type="primary" @click="goOrders">查看订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.cart {
  max-width: 1160px;
  margin: 0 auto;
  padding: 24px 24px 64px;
}
.head {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--md-color-line);
}
.brand {
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 20px;
  color: var(--md-color-primary);
}
.brand-sub {
  font-size: 13px;
  font-weight: 400;
  color: var(--md-color-ink-sub);
  margin-left: 8px;
}
.nav-link {
  font-size: 14px;
  color: var(--md-color-ink-sub);
}
.guest-tip {
  margin: 14px 0;
  font-size: 13px;
  color: var(--md-color-ink-sub);
}
.guest-tip a {
  color: var(--md-color-primary);
}
.table {
  margin-top: 14px;
}
.goods {
  display: flex;
  align-items: center;
  gap: 12px;
}
.thumb {
  width: 48px;
  height: 48px;
  background: var(--md-color-bg-tint);
  border-radius: var(--md-radius-sm);
  overflow: hidden;
  flex-shrink: 0;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.title {
  margin: 0;
  font-size: 14px;
}
.spec {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.price {
  font-weight: 600;
  color: var(--md-color-primary);
}
.bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 20px;
  padding: 16px 20px;
  background: var(--md-color-bg-tint);
  border-radius: var(--md-radius);
}
.bar-count {
  font-size: 13px;
  color: var(--md-color-ink-sub);
}
.bar-total {
  font-size: 22px;
  font-weight: 600;
  color: var(--md-color-primary);
}
.empty {
  text-align: center;
  color: var(--md-color-ink-sub);
  padding: 60px 0;
}
.pay-tip {
  font-size: 14px;
}
.pay-no {
  font-size: 13px;
  color: var(--md-color-primary);
  font-weight: 600;
}
.pay-note {
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
</style>
