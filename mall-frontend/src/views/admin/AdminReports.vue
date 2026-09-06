<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getDashboard, type Dashboard } from '@/api/report'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const data = ref<Dashboard | null>(null)

const BIZ_TEXT: Record<string, string> = {
  order_in: '订单进账',
  refund_out: '退款出账',
  withdrawal_out: '提现出账',
}

async function load() {
  const merchantId = userStore.type === 2 ? userStore.id : undefined
  data.value = await getDashboard(merchantId)
}

onMounted(load)
</script>

<template>
  <div>
    <div class="head">
      <h1 class="title">统计报表</h1>
      <el-button size="small" @click="load">刷新</el-button>
    </div>

    <template v-if="data">
      <div class="flow">
        <p class="sub-title">进出账流水（近 20 笔）</p>
        <el-table :data="data.financeFlow ?? []" border>
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <el-tag size="small" :type="row.biz === 'order_in' ? 'success' : 'danger'">
                {{ BIZ_TEXT[row.biz as string] ?? row.biz }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="关联单号" min-width="200">
            <template #default="{ row }"><span class="md-num">{{ row.ref }}</span></template>
          </el-table-column>
          <el-table-column label="金额" width="120">
            <template #default="{ row }"><span class="md-num">¥ {{ Number(row.amount).toFixed(2) }}</span></template>
          </el-table-column>
          <el-table-column label="时间" width="180">
            <template #default="{ row }">
              <span class="md-num">{{ String(row.createTime).replace('T', ' ').slice(0, 19) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="data.merchantStats" class="stats md-num">
        入驻统计 —— 通过 {{ data.merchantStats.approved }} · 待审 {{ data.merchantStats.pending }} · 驳回 {{ data.merchantStats.rejected }}
      </div>
    </template>
    <el-empty v-else description="暂无数据" />
  </div>
</template>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.title {
  margin: 0;
  font-family: var(--md-font-display);
  font-size: 22px;
  font-weight: 700;
}
.sub-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 10px;
}
.stats {
  margin-top: 16px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
}
</style>
