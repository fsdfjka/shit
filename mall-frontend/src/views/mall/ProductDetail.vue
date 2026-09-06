<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, type Sku } from '@/api/product'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref<Awaited<ReturnType<typeof getProductDetail>> | null>(null)
const selectedSku = ref<Sku | null>(null)

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

async function load() {
  try {
    product.value = await getProductDetail(route.params.id as string)
    const firstSku = product.value.skus.length ? product.value.skus[0] : null
    selectedSku.value = firstSku
  } catch {
    ElMessage.warning('商品不存在或已下架')
    router.replace('/')
  }
}

function selectSku(sku: Sku) {
  selectedSku.value = sku
}

function addCart() {
  if (!userStore.token) {
    ElMessage.info('请先登录（购物车功能随交易域里程碑上线）')
    router.push('/login')
    return
  }
  ElMessage.info(`已选规格：${currentSku.value?.specJson} —— 购物车功能在里程碑 5 接入`)
}

onMounted(load)
</script>

<template>
  <div v-if="product" class="detail">
    <div class="gallery">
      <img :src="product.mainImg" :alt="product.title" />
    </div>
    <div class="info">
      <p class="info-eyebrow md-num">SKU 全档 · {{ product.shopName }}</p>
      <h1 class="info-title">{{ product.title }}</h1>
      <p class="info-subtitle">{{ product.subtitle }}</p>

      <div class="price-row">
        <span class="price-mdn">¥</span>
        <span class="price-num md-num">{{ (currentSku?.price ?? product.minPrice ?? 0).toFixed(2) }}</span>
        <span class="md-tag-price md-num price-min">最低 ¥{{ (product.minPrice ?? 0).toFixed(2) }}</span>
      </div>

      <div v-for="[key, values] in specGroups" :key="key" class="spec-group">
        <p class="spec-label">{{ key }}</p>
        <div class="spec-options">
          <button
            v-for="v in values"
            :key="v"
            class="spec-pill"
            :class="{ spec_active: currentSku?.specJson?.includes(v) }"
            @click="selectSku(product.skus.find((s) => s.specJson.includes(v)) ?? product.skus[0])"
          >
            {{ v }}
          </button>
        </div>
      </div>

      <div class="stock md-num">
        库存：{{ currentSku?.stock ?? '—' }}
        <span v-if="currentSku" class="stock-remark">{{ currentSku.remark }}</span>
      </div>

      <el-button type="primary" size="large" class="buy" @click="addCart">
        加入购物车
      </el-button>
      <router-link class="back" to="/">← 回到货架</router-link>
    </div>
  </div>
</template>

<style scoped>
.detail {
  max-width: 1160px;
  margin: 0 auto;
  padding: 32px 24px 64px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}
.gallery img {
  width: 100%;
  border-radius: var(--md-radius);
  background: var(--md-color-bg-tint);
  aspect-ratio: 1;
  object-fit: cover;
}
.info-eyebrow {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.16em;
  color: var(--md-color-ink-sub);
}
.info-title {
  margin: 0 0 6px;
  font-family: var(--md-font-display);
  font-size: 26px;
  font-weight: 700;
}
.info-subtitle {
  margin: 0 0 18px;
  color: var(--md-color-ink-sub);
  font-size: 14px;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 16px 0;
  border-top: 1px solid var(--md-color-line);
  border-bottom: 1px solid var(--md-color-line);
}
.price-mdn {
  font-size: 18px;
  font-weight: 700;
  color: var(--md-color-primary);
}
.price-num {
  font-size: 38px;
  font-weight: 600;
  color: var(--md-color-primary);
  line-height: 1;
}
.price-min {
  margin-left: 8px;
  font-size: 12px;
}
.spec-group {
  margin: 18px 0;
}
.spec-label {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
}
.spec-options {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.spec-pill {
  padding: 7px 16px;
  border: 1px solid var(--md-color-line);
  border-radius: 999px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  color: var(--md-color-ink);
}
.spec-pill:hover {
  border-color: var(--md-color-accent);
}
.spec_active {
  border-color: var(--md-color-accent);
  background: var(--md-color-accent-soft);
  color: var(--md-color-primary);
  font-weight: 600;
}
.stock {
  margin: 8px 0 26px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
}
.stock-remark {
  margin-left: 10px;
}
.buy {
  width: 100%;
  max-width: 320px;
}
.back {
  display: inline-block;
  margin-top: 14px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
}

@media (max-width: 800px) {
  .detail {
    grid-template-columns: 1fr;
  }
}
</style>
