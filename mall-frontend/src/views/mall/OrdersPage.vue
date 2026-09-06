<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, getMyOrders, receiveOrder, STATUS_TEXT, type OrderVO } from '@/api/order'
import { requestRefund } from '@/api/pay'

const TABS = [
  { label: '全部', value: undefined as number | undefined },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已发货', value: 2 },
  { label: '已收货', value: 3 },
  { label: '已取消', value: 4 },
]

const orders = ref<OrderVO[]>([])
const total = ref(0)
const page = ref(1)
const status = ref<number | undefined>(undefined)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await getMyOrders(status.value, page.value, 10)
    orders.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function pickTab(v: number | undefined) {
  status.value = v
  page.value = 1
  load()
}

async function cancel(order: OrderVO) {
  await ElMessageBox.confirm(`取消订单 ${order.orderNo}？`, '提醒', { type: 'warning' })
  await cancelOrder(order.orderNo)
  ElMessage.success('订单已取消（库存已回补，支付单已关闭）')
  load()
}

async function receive(order: OrderVO) {
  await ElMessageBox.confirm(`确认收货：${order.orderNo}？`, '确认', { type: 'success' })
  await receiveOrder(order.orderNo)
  ElMessage.success('确认收货成功')
  load()
}

async function refund(order: OrderVO) {
  const { value } = await ElMessageBox.prompt('请输入退款原因', '申请退款', { inputValue: '' })
  await requestRefund(order.orderNo, value ?? '用户申请')
  ElMessage.success('退款成功（演示渠道即时到账），订单已退款')
  load()
}

onMounted(load)
</script>

<template>
  <div class="orders">
    <header class="head">
      <router-link class="brand" to="/">MX<span class="brand-sub">我的订单</span></router-link>
      <router-link class="nav-link" to="/cart">购物车</router-link>
    </header>

    <el-tabs v-model="status" class="tabs" @tab-change="() => {}">
      <el-tab-pane v-for="t in TABS" :key="t.label" :name="t.value ?? 'all'" :label="t.label" />
    </el-tabs>

    <el-skeleton v-if="loading" :rows="6" animated />
    <template v-else>
      <article v-for="o in orders" :key="o.orderNo" class="order">
        <header class="order-head">
          <span class="md-num order-no">{{ o.orderNo }}</span>
          <span class="order-status md-num">ST{{ o.status }}</span>
          <span class="order-status-text">{{ STATUS_TEXT[o.status] }}</span>
          <span class="order-time md-num">{{ o.createTime?.replace('T', ' ').slice(0, 19) }}</span>
        </header>
        <section class="order-items">
          <p v-for="(it, i) in o.items" :key="i" class="item">
            <span class="item-title">{{ it.title }}</span>
            <span class="md-num item-spec">{{ it.specJson }}</span>
            <span class="md-num item-qty">x{{ it.count }}</span>
            <span class="md-num item-amount">¥ {{ it.amount.toFixed(2) }}</span>
          </p>
        </section>
        <footer class="order-foot">
          <span class="md-num foot-amount">实付 ¥ {{ o.payAmount.toFixed(2) }}</span>
          <span v-if="o.trackingNo" class="foot-log md-num">{{ o.logisticsCompany }} · {{ o.trackingNo }}</span>
          <div class="foot-actions">
            <el-button v-if="o.status === 0" size="small" type="primary" @click="$router.push(`/pay/${o.orderNo}`)">
              去支付
            </el-button>
            <el-button v-if="o.status === 0" size="small" @click="cancel(o)">取消订单</el-button>
            <el-button v-if="o.status === 1 || o.status === 2" size="small" type="warning" @click="refund(o)">
              申请退款
            </el-button>
            <el-button v-if="o.status === 2" size="small" type="success" @click="receive(o)">确认收货</el-button>
          </div>
        </footer>
      </article>

      <el-empty v-if="!orders.length" description="暂无订单" />
      <el-pagination
        v-if="total > 10"
        v-model:current-page="page"
        class="pager"
        layout="prev, pager, next"
        :total="total"
        @current-change="load"
      />
    </template>
  </div>
</template>

<style scoped>
.orders {
  max-width: 900px;
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
.tabs {
  margin-bottom: 8px;
}
.order {
  border: 1px solid var(--md-color-line);
  border-radius: var(--md-radius);
  margin-bottom: 14px;
  overflow: hidden;
}
.order-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  background: var(--md-color-bg-tint);
  font-size: 13px;
}
.order-no {
  font-weight: 600;
  color: var(--md-color-primary);
}
.order-status {
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--md-color-ink-sub);
}
.order-status-text {
  color: var(--md-color-ink);
}
.order-time {
  margin-left: auto;
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.order-items {
  padding: 6px 18px;
}
.item {
  display: flex;
  gap: 12px;
  align-items: center;
  margin: 8px 0;
  font-size: 14px;
}
.item-title {
  flex: 1;
}
.item-spec {
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.item-qty {
  color: var(--md-color-ink-sub);
}
.item-amount {
  min-width: 90px;
  text-align: right;
  font-weight: 600;
}
.order-foot {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 18px;
  border-top: 1px solid var(--md-color-line);
}
.foot-amount {
  margin-left: auto;
  font-weight: 600;
  color: var(--md-color-primary);
}
.foot-log {
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.foot-actions {
  display: flex;
  gap: 8px;
}
.pager {
  justify-content: center;
  margin-top: 10px;
}
</style>
