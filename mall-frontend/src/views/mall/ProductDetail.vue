<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, type Sku } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref<Awaited<ReturnType<typeof getProductDetail>> | null>(null)
const selectedSku = ref<Sku | null>(null)
/** 当前已选规格键值对（规格选择器精确匹配用） */
const selectedSpec = ref<Record<string, string>>({})

/** 规格选择：SKU 全量加载 + 前端过滤（数据库文档注 1） */
const specGroups = computed(() => {
  if (!product.value) return []
  const groups: Record<string, string[]> = {}
  product.value.skus.forEach((sku) => {
    try {
      const spec: Record<string, string> = JSON.parse(sku.specJson)
      Object.entries(spec).forEach(([k, v]) => {
        groups[k] = groups[k] ?? []
        if (!groups[k].includes(v)) groups[k].push(v)
      })
    } catch {
      /* 非法 specJson 忽略 */
    }
  })
  return Object.entries(groups)
})

const currentSku = computed(() => selectedSku.value)

function specOf(sku: Sku): Record<string, string> {
  try {
    return JSON.parse(sku.specJson)
  } catch {
    return {}
  }
}

async function load() {
  try {
    product.value = await getProductDetail(route.params.id as string)
    const firstSku = product.value.skus.length ? product.value.skus[0] : null
    selectedSku.value = firstSku
    if (firstSku) selectedSpec.value = specOf(firstSku)
  } catch {
    ElMessage.warning('商品不存在或已下架')
    router.replace('/')
  }
}

/** 点击规格值：合并已选项后按键值精确匹配 SKU，组合不存在则提示 */
function selectSpec(key: string, value: string) {
  const chosen = { ...selectedSpec.value, [key]: value }
  const match = product.value?.skus.find((s) => {
    const spec = specOf(s)
    return Object.entries(chosen).every(([k, v]) => spec[k] === v)
  })
  if (match) {
    selectedSpec.value = chosen
    selectedSku.value = match
  } else {
    ElMessage.warning('该规格组合不可选，请更换搭配')
  }
}

/** 游客车 key 与 CartPage 共用 */
const GUEST_KEY = 'mall_guest_cart'

function readGuestCart(): Array<Record<string, unknown>> {
  try {
    return JSON.parse(localStorage.getItem(GUEST_KEY) ?? '[]')
  } catch {
    return []
  }
}

/** 加入购物车：游客存本地（登录后自动合并），登录用户写服务端 cart 表 */
async function addCart() {
  const sku = currentSku.value
  if (!sku || !sku.id || !product.value) {
    ElMessage.warning('请先选择规格')
    return
  }
  if (!userStore.token) {
    const list = readGuestCart()
    const exist = list.find((i) => i.skuId === sku.id)
    if (exist) {
      exist.count = Number(exist.count ?? 0) + 1
    } else {
      list.push({
        localId: crypto.randomUUID(),
        skuId: sku.id,
        productId: product.value.id,
        count: 1,
        checked: 1,
        specJson: sku.specJson,
        price: sku.price,
        stock: sku.stock,
        productTitle: product.value.title,
        mainImg: product.value.mainImg,
      })
    }
    localStorage.setItem(GUEST_KEY, JSON.stringify(list))
    ElMessage.success('已加入本地购物车（登录后自动合并到账户）')
    return
  }
  try {
    await addToCart(sku.id, 1)
    ElMessage.success('已加入购物车')
  } catch {
    /* 错误提示由拦截器统一处理 */
  }
}

onMounted(load)
</script>

<template>
  <div v-if="product" class="detail">
    <div class="gallery">
      <img :src="product.mainImg" :alt="product.title" />
    </div>
    <div class="info">
      <h1 class="info-title">{{ product.title }}</h1>
      <p class="info-subtitle">{{ product.subtitle }}</p>

      <div class="price-box">
        <div class="price-row">
          <span class="price-currency">¥</span>
          <span class="price-num md-num">{{ (currentSku?.price ?? product.minPrice ?? 0).toFixed(2) }}</span>
          <span class="price-sale md-num">已售 {{ product.saleCount ?? 0 }}</span>
        </div>
        <div class="price-meta">
          <span class="meta-stock">库存
            <span class="md-num meta-val">{{ currentSku?.stock ?? '—' }}</span>
            <span v-if="currentSku" class="stock-remark">{{ currentSku.remark }}</span>
          </span>
        </div>
      </div>

      <div v-for="[key, values] in specGroups" :key="key" class="spec-group">
        <p class="spec-label"><b>{{ key }}</b></p>
        <div class="spec-options">
          <button
            v-for="v in values"
            :key="v"
            class="spec-pill"
            :class="{ spec_active: selectedSpec[key] === v }"
            @click="selectSpec(key, v)"
          >
            {{ v }}
          </button>
        </div>
      </div>

      <div class="actions">
        <el-button type="primary" size="large" class="btn-cart" @click="addCart">加入购物车</el-button>
        <el-button size="large" class="btn-buy" @click="addCart">立即购买</el-button>
      </div>

      <router-link class="shop-card" :to="`/product/${product.id}`" @click.prevent>
        <span class="shop-avatar">MX</span>
        <span class="shop-info">
          <span class="shop-name">{{ product.shopName }}</span>
          <span class="shop-desc">多商家入驻 · 每笔订单独立价签</span>
        </span>
        <span class="shop-enter">进店看更多 ›</span>
      </router-link>

      <router-link class="back" to="/">← 回到货架</router-link>
    </div>
  </div>
</template>

<style scoped>
.detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 24px 64px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 44px;
}
.gallery img {
  width: 100%;
  border-radius: 12px;
  background: var(--mx-bg-2);
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
}
.info-title {
  margin: 0 0 6px;
  font-family: var(--md-font-display);
  font-size: 26px;
  font-weight: 700;
  color: var(--mx-ink);
}
.info-subtitle {
  margin: 0 0 16px;
  color: var(--mx-ink-2);
  font-size: 14px;
}

/* 价格盒：橙红大字 + 销量 */
.price-box {
  padding: 16px 18px;
  border-radius: 10px;
  background: var(--mx-red-soft);
  margin-bottom: 20px;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.price-currency {
  font-size: 17px;
  font-weight: 700;
  color: var(--mx-red);
}
.price-num {
  font-size: 42px;
  font-weight: 700;
  color: var(--mx-red);
  line-height: 1.05;
}
.price-sale {
  margin-left: auto;
  font-size: 13px;
  color: var(--mx-ink-2);
}
.price-meta {
  margin-top: 10px;
}
.meta-stock {
  font-size: 13px;
  color: var(--mx-ink-2);
}
.meta-val {
  color: var(--mx-ink);
  font-weight: 600;
  margin-left: 4px;
}
.stock-remark {
  margin-left: 10px;
  color: var(--mx-red);
}

.spec-group {
  margin: 16px 0;
}
.spec-label {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--mx-ink-2);
}
.spec-options {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.spec-pill {
  padding: 7px 18px;
  border: 1px solid var(--mx-line);
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  font-family: var(--md-font-body);
  cursor: pointer;
  color: var(--mx-ink);
  transition: all 0.12s ease;
}
.spec-pill:hover {
  border-color: var(--mx-red);
  color: var(--mx-red);
}
.spec_active {
  border-color: var(--mx-red) !important;
  background: var(--mx-red-soft) !important;
  color: var(--mx-red) !important;
  font-weight: 600;
}

/* 双 CTA */
.actions {
  display: flex;
  gap: 12px;
  margin: 22px 0;
}
.btn-cart {
  flex: 1;
  height: 46px;
  font-size: 15px;
  border-radius: 24px;
}
.btn-buy {
  flex: 1;
  height: 46px;
  font-size: 15px;
  border-radius: 24px;
  border-color: var(--mx-red);
  color: var(--mx-red);
  background: #fff;
}
.btn-buy:hover {
  background: var(--mx-red-soft);
  border-color: var(--mx-red);
  color: var(--mx-red);
}

/* 店铺卡 */
.shop-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid var(--mx-line);
  border-radius: 10px;
  background: #fff;
  text-decoration: none;
}
.shop-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 8px;
  background: var(--mx-red);
  color: #fff;
  font-weight: 800;
  font-size: 15px;
}
.shop-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.shop-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--mx-ink);
}
.shop-desc {
  font-size: 12px;
  color: var(--mx-ink-2);
}
.shop-enter {
  margin-left: auto;
  font-size: 12px;
  color: var(--mx-ink-2);
}
.shop-card:hover .shop-enter {
  color: var(--mx-red);
}

.back {
  display: inline-block;
  margin-top: 14px;
  font-size: 13px;
  color: var(--mx-ink-2);
}
.back:hover {
  color: var(--mx-red);
}

@media (max-width: 800px) {
  .detail {
    grid-template-columns: 1fr;
  }
}
</style>
