<script setup lang="ts">
/** 前台首页骨架：类目走廊 + 价签 Hero + 商品目录网格（假数据，接 API 后替换） */
type Product = {
  sku: string
  title: string
  seller: string
  price: string
  img: string
}

const cats = [
  { code: 'C01', name: '手机数码' },
  { code: 'C02', name: '家用电器' },
  { code: 'C03', name: '服饰鞋帽' },
]

const products: Product[] = [
  { sku: 'SKU-256-A', title: 'NovaX 5G 手机 曜石黑', seller: '极客数码旗舰店', price: '3299.00', img: 'linear-gradient(135deg,#dbe3f0,#9fb3d1)' },
  { sku: 'SKU-256-B', title: 'NovaX 5G 手机 星光银', seller: '极客数码旗舰店', price: '3299.00', img: 'linear-gradient(135deg,#eceff2,#b8c0cc)' },
  { sku: 'SKU-001-X', title: '蓝牙耳机 Pro 白色', seller: '极客数码旗舰店', price: '199.00', img: 'linear-gradient(135deg,#fdf3dd,#e8b04b)' },
  { sku: 'SKU-073-L', title: '纯棉基础款白T恤 L', seller: '悦动服饰官方店', price: '79.00', img: 'linear-gradient(135deg,#f5f7fa,#d9dee6)' },
  { sku: 'SKU-074-X', title: '纯棉基础款黑T恤 XL', seller: '悦动服饰官方店', price: '79.00', img: 'linear-gradient(135deg,#e7e9ed,#9aa2b1)' },
  { sku: 'SKU-118-K', title: '降噪耳机 Pro 珍珠白', seller: '悦动服饰官方店', price: '499.00', img: 'linear-gradient(135deg,#eef1f6,#c7d0de)' },
]
</script>

<template>
  <div class="home">
    <header class="nav">
      <router-link class="brand" to="/">
        MX<span class="brand-sub">多商家商城</span>
      </router-link>
      <input class="search" type="search" placeholder="搜索商品 / SKU" />
      <nav class="nav-right">
        <router-link to="/login" class="nav-link">登录</router-link>
        <router-link to="/login" class="nav-link">购物车</router-link>
      </nav>
    </header>

    <nav class="cat-walk">
      <span v-for="c in cats" :key="c.code" class="cat">
        <b class="md-num">{{ c.code }}</b>
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
        <span class="md-num hero-tag-label">今日价签 · 手机专场</span>
        <span class="md-num hero-tag-price">¥ 3299</span>
        <span class="md-num hero-tag-sku">SKU-256-A</span>
      </div>
    </section>

    <section class="grid">
      <article v-for="p in products" :key="p.sku" class="card">
        <div class="card-img" :style="{ background: p.img }" />
        <div class="card-body">
          <p class="card-title">{{ p.title }}</p>
          <p class="card-seller md-num">{{ p.seller }}</p>
          <span class="md-tag-price md-num">¥ {{ p.price }}</span>
        </div>
      </article>
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
  max-width: 420px;
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
}
.nav-link {
  font-size: 14px;
  color: var(--md-color-ink-sub);
}
.nav-link:hover {
  color: var(--md-color-primary);
}

.cat-walk {
  display: flex;
  gap: 8px;
  padding: 14px 0;
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
.cat:hover {
  border-color: var(--md-color-accent);
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin: 26px 0 40px;
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
