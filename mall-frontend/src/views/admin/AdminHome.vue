<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getDashboard, type Dashboard } from '@/api/report'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const $chart = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
const data = ref<Dashboard | null>(null)

async function load() {
  // 商家看本店口径（传自己的 merchantId），管理员看全平台
  const merchantId = userStore.type === 2 ? userStore.id : undefined
  data.value = await getDashboard(merchantId)
  renderChart()
}

function renderChart() {
  if (!$chart.value || !data.value) return
  chart ??= echarts.init($chart.value)
  const trend = data.value.dailyTrend ?? []
  chart.setOption({
    grid: { left: 10, right: 10, top: 30, bottom: 10, containLabel: true },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trend.map((t) => t.day), axisLine: { lineStyle: { color: '#c9d2e0' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } } },
    series: [
      {
        name: '营收',
        type: 'line',
        smooth: true,
        symbolSize: 6,
        data: trend.map((t) => Number(t.amount)),
        itemStyle: { color: '#e8b04b' },
        lineStyle: { color: '#e8b04b', width: 2.5 },
        areaStyle: { color: 'rgba(232, 176, 75, 0.12)' },
      },
    ],
  })
}

function onResize() {
  chart?.resize()
}

onMounted(() => {
  load()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  chart?.dispose()
})
</script>

<template>
  <div class="dash">
    <p class="eyebrow md-num">MALL-X / DASHBOARD</p>
    <h1 class="title">{{ userStore.type === 2 ? '本店看板' : '平台数据看板' }}</h1>

    <template v-if="data">
      <div class="cards">
        <div class="card">
          <span class="label">营收（含已发货/已收货）</span>
          <span class="md-num value">¥ {{ (data.revenue ?? 0).toFixed(2) }}</span>
        </div>
        <div class="card">
          <span class="label">进账</span>
          <span class="md-num value">¥ {{ (data.incoming ?? 0).toFixed(2) }}</span>
        </div>
        <div class="card">
          <span class="label">出账（退款+提现）</span>
          <span class="md-num value">¥ {{ (data.outgoing ?? 0).toFixed(2) }}</span>
        </div>
        <div class="card">
          <span class="label">{{ userStore.type === 2 ? '本店' : '入驻商家' }}</span>
          <span class="md-num value">{{ data.merchantCount ?? 0 }}</span>
        </div>
      </div>

      <div class="panel chart-panel">
        <p class="panel-title">近 7 日营收趋势</p>
        <div ref="$chart" class="chart" />
      </div>

      <div v-if="data.merchantStats" class="cards sub">
        <div class="card sm">
          <span class="label">入驻统计（通过/待审/驳回）</span>
          <span class="md-num value">
            {{ data.merchantStats.approved }} / {{ data.merchantStats.pending }} / {{ data.merchantStats.rejected }}
          </span>
        </div>
      </div>
    </template>
    <el-skeleton v-else :rows="5" animated />
  </div>
</template>

<style scoped>
.dash {
  padding: 8px 4px;
}
.eyebrow {
  font-size: 12px;
  letter-spacing: 0.18em;
  color: var(--md-color-ink-sub);
  margin: 0 0 6px;
}
.title {
  margin: 0 0 22px;
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 26px;
  color: var(--md-color-ink);
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}
.card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid var(--md-color-line);
  border-radius: var(--md-radius);
}
.card.sm {
  grid-column: 1 / -1;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
.label {
  font-size: 12px;
  color: var(--md-color-ink-sub);
}
.value {
  font-size: 24px;
  font-weight: 600;
  color: var(--md-color-primary);
}
.panel {
  background: #fff;
  border: 1px solid var(--md-color-line);
  border-radius: var(--md-radius);
  padding: 18px 20px;
}
.panel-title {
  margin: 0 0 14px;
  font-size: 14px;
  font-weight: 600;
}
.chart {
  height: 300px;
}
.cards.sub {
  margin-top: 16px;
}
</style>
