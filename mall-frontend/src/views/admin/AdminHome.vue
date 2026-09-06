<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
/** 看板占位：里程碑 7 接入 /api/report/** 数据（ECharts 可视化） */
</script>

<template>
  <div class="admin-home">
    <p class="eyebrow md-num">MALL-X / DASHBOARD</p>
    <h1 class="title">看板</h1>
    <p class="desc">
      {{ userStore.type === 2 ? '店铺概览' : '平台数据看板' }} · 数据接口与图表将在统计报表里程碑接入。
      当前已完成：项目骨架、认证（登录/注册/入驻）、网关 JWT 鉴权。
    </p>
    <div class="placeholder">
      <span class="md-num">— — —</span>
      <span>营收 · 入驻 · 进出账 · 提现日志</span>
    </div>
  </div>
</template>

<style scoped>
.admin-home {
  padding: 12px 4px;
}
.eyebrow {
  font-size: 12px;
  letter-spacing: 0.18em;
  color: var(--md-color-ink-sub);
}
.title {
  margin: 6px 0 10px;
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 26px;
  color: var(--md-color-ink);
}
.desc {
  margin: 0 0 26px;
  font-size: 14px;
  color: var(--md-color-ink-sub);
}
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
  min-height: 320px;
  border: 1px dashed var(--md-color-line);
  border-radius: var(--md-radius);
  background: #fff;
  color: var(--md-color-ink-sub);
  font-size: 13px;
}
</style>
