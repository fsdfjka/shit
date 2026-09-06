<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdverts, getCategories, type Advert, type Category } from '@/api/catalog'
import { getProductList, type Product } from '@/api/product'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const categories = ref<Category[]>([])
const adverts = ref<Advert[]>([])
const products = ref<Product[]>([])
const total = ref(0)
const loading = ref(false)
const activeCategory = ref<number>()
const keyword = ref('')

async function loadCategories() {
  categories.value = await getCategories()
}

async function loadAdverts() {
  adverts.value = await getAdverts()
}

async function loadProducts() {
  loading.value = true
  try {
    const page = await getProductList({
      page: 1,
      size: 12,
      categoryId: activeCategory.value,
      keyword: keyword.value || undefined,
    })
    products.value = page.records
    total.value = page.total
  } catch {
    ElMessage.warning('商品接口暂不可用（服务未启动或未联调）')
  } finally {
    loading.value = false
  }
}

function pickCategory(id?: number) {
  activeCategory.value = id
  loadProducts()
}

function onSearch() {
  loadProducts()
}

onMounted(() => {
  loadCategories()
  loadAdverts()
  loadProducts()
})
</script>

<template>
  <div class="home">
    <header class="nav">
      <router-link class="brand" to="/">
        MX<span class="brand-sub">多商家商城</span>
      </router-link>
      <input v-model="keyword" class="search" type="search" placeholder="搜索商品 / SKU" @keyup.enter="onSearch" />
      <nav class="nav-right">
        <template v-if="userStore.token">
          <span class="nav-link hi">你好，{{ userStore.nickname || userStore.username }}</span>
          <router-link class="nav-link" to="/admin">工作台</router-link>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </nav>
    </header>

    <nav class="cat-walk">
      <span v-for="c in categories" :key="c.id" class="cat" :class="{ cat_active: activeCategory === c.id && c.parentId === 0 }" @click="pickCategory(c.parentId === 0 ? c.id : undefined)">
        <b class="md-num">C{{ String(c.id).padStart(2, '0') }}</b>
        {{ c.name }}
      </span>
    </nav>

    <section class="hero">
      <div class="hero-copy">
        <p class="hero-eyebrow md-num">MALL-X / 今日精选</p>
        <h1 class="hero-title">把价格贴在货架上，<br />也贴在你心里</h1>
        <p class="hero-sub">多商家入驻 · 每笔订单独立价签</p>
      </div>
      <div class="hero-tag">
        <span class="md-num hero-tag-label">今日价签</span>
        <span class="md-num hero-tag-price">
          ¥ {{ products.length && products[0].minPrice ? products[0].minPrice.toFixed(2) : '—' }}
        </span>
        <span class="md-num hero-tag-sku">共 {{ total }} 件在售</span>
      </div>
    </section>

    <section v-if="adverts.length" class="advert-strip">
      <a v-for="ad in adverts" :key="ad.id" class="advert" href="#" @click.prevent>
        <span class="md-num advert-tag">AD-{{ String(ad.id).padStart(2, '0') }}</span>
        <span class="advert-title">{{ ad.title }}</span>
        <span class="advert-link">{{ ad.linkUrl || '内部位' }}</span>
      </a>
    </section>

    <section v-loading="loading" class="grid">
      <router-link v-for="p in products" :key="p.id" class="card" :to="`/product/${p.id}`">
        <div class="card-img"><img :src="p.mainImg" :alt="p.title" /></div>
        <div class="card-body">
          <p class="card-title">{{ p.title }}</p>
          <p class="card-seller md-num">{{ p.shopName }}</p>
          <span class="md-tag-price md-num">¥ {{ p.minPrice?.toFixed(2) }}</span>
        </div>
      </router-link>
      <p v-if="!loading && !products.length" class="empty">空货架 —— 商家上架后自动出现</p>
    </section>
  </div>
</template>

<style scoped>
.home {
  max-width: 1160px;
  margin: 0 auto;
  padding: 0 24px 64px;
}
.nav {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 18px 0;
  border-bottom: 1px solid var(--md-color-line);
}
.brand {
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 20px;
  color: var(--md-color-primary);
  letter-spacing: 0.04em;
}
.brand-sub {
  font-family: var(--md-font-body);
  font-weight: 400;
  font-size: 13px;
  color: var(--md-color-ink-sub);
  margin-left: 8px;
}
.search {
  flex: 1;
  max-width: 360px;
  margin-left: auto;
  padding: 9px 14px;
  border: 1px solid var(--md-color-line);
  border-radius: var(--md-radius-sm);
  background: var(--md-color-bg-tint);
  font-family: var(--md-font-body);
}
.nav-right {
  display: flex;
  gap: 14px;
  align-items: center;
}
.nav-link {
  font-size: 14px;
  color: var(--md-color-ink-sub);
}
.nav-link:hover {
  color: var(--md-color-primary);
}
.hi {
  color: var(--md-color-primary);
  font-weight: 500;
}

.cat-walk {
  display: flex;
  gap: 8px;
  padding: 14px 0;
  flex-wrap: wrap;
}
.cat {
  padding: 7px 14px;
  border: 1px solid var(--md-color-line);
  border-radius: 999px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
  cursor: pointer;
}
.cat b {
  color: var(--md-color-primary);
  font-weight: 600;
  margin-right: 6px;
}
.cat:hover,
.cat_active {
  border-color: var(--md-color-accent);
  color: var(--md-color-primary);
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin: 26px 0 24px;
  padding: 44px 40px;
  border-radius: var(--md-radius);
  background: var(--md-color-primary);
  color: #fff;
}
.hero-eyebrow {
  margin: 0 0 12px;
  font-size: 12px;
  letter-spacing: 0.18em;
  color: var(--md-color-accent);
}
.hero-title {
  margin: 0 0 10px;
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 34px;
  line-height: 1.35;
}
.hero-sub {
  margin: 0;
  font-size: 13px;
  color: #aab3c4;
}
.hero-tag {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  padding: 22px 26px;
  background: var(--md-color-accent);
  border-radius: var(--md-radius);
  color: var(--md-color-primary);
}
.hero-tag-label {
  font-size: 12px;
  letter-spacing: 0.12em;
}
.hero-tag-price {
  font-size: 42px;
  font-weight: 600;
  line-height: 1;
}
.hero-tag-sku {
  font-size: 12px;
  opacity: 0.8;
}

.advert-strip {
  display: flex;
  gap: 12px;
  margin-bottom: 26px;
  flex-wrap: wrap;
}
.advert {
  flex: 1;
  min-width: 220px;
  padding: 12px 16px;
  border: 1px dashed var(--md-color-line);
  border-radius: var(--md-radius);
  background: var(--md-color-bg-tint);
  display: flex;
  align-items: center;
  gap: 10px;
}
.advert-tag {
  font-size: 11px;
  color: var(--md-color-accent);
  letter-spacing: 0.1em;
}
.advert-title {
  font-size: 14px;
  font-weight: 500;
}
.advert-link {
  margin-left: auto;
  font-size: 12px;
  color: var(--md-color-ink-sub);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}
.card {
  border: 1px solid var(--md-color-line);
  border-radius: var(--md-radius);
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.18s ease, transform 0.18s ease;
}
.card:hover {
  box-shadow: var(--md-shadow-card);
  transform: translateY(-2px);
}
.card-img {
  height: 150px;
  background: var(--md-color-bg-tint);
}
.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.card-body {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.card-title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}
.card-seller {
  margin: 0;
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.empty {
  grid-column: 1 / -1;
  text-align: center;
  color: var(--md-color-ink-sub);
  font-size: 14px;
  padding: 40px 0;
}

@media (max-width: 720px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 28px 24px;
  }
  .hero-tag {
    align-items: flex-start;
  }
}
</style>
