<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, merchantApply } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
/** ?role=merchant 时即商家入驻申请表单（tab 切换同步 URL） */
const isMerchant = ref(route.query.role === 'merchant')

function switchRole(v: boolean) {
  isMerchant.value = v
  router.replace({ path: '/register', query: v ? { role: 'merchant' } : {} })
}

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  merchantName: '',
  shopName: '',
})

async function submit() {
  loading.value = true
  try {
    if (isMerchant.value) {
      await merchantApply({
        username: form.username,
        password: form.password,
        merchantName: form.merchantName,
        phone: form.phone,
        shopName: form.shopName,
      })
      ElMessage.success('入驻申请已提交，请等待平台审核')
    } else {
      await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname,
        phone: form.phone,
      })
      ElMessage.success('注册成功，请登录')
    }
    router.push('/login')
  } catch {
    /* 错误提示由拦截器统一处理 */
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register">
    <aside class="panel">
      <p class="panel-eyebrow md-num">MALL-X / {{ isMerchant ? '商家入驻' : '注册' }}</p>
      <h1 class="panel-title">{{ isMerchant ? '开一家店，只差一张申请表。' : '从购物车开始，<br />认识这间商城。' }}</h1>
      <p class="panel-sub">{{ isMerchant ? '提交后平台管理员审核通过即可登录' : '注册后自动登录即可浏览、加购' }}</p>
    </aside>
    <main class="form-area">
      <el-form label-position="top" class="form" @submit.prevent="submit">
        <div class="role-tabs">
          <button type="button" class="role-tab" :class="{ role_tab_active: !isMerchant }" @click="switchRole(false)">
            普通用户
          </button>
          <button type="button" class="role-tab" :class="{ role_tab_active: isMerchant }" @click="switchRole(true)">
            商家入驻
          </button>
        </div>
        <h2 class="form-title">{{ isMerchant ? '商家入驻申请' : '注册用户' }}</h2>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <template v-if="isMerchant">
          <el-form-item label="商家名">
            <el-input v-model="form.merchantName" placeholder="如：极客数码" size="large" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.phone" placeholder="手机号" size="large" />
          </el-form-item>
          <el-form-item label="店铺名（选填）">
            <el-input v-model="form.shopName" placeholder="如：极客数码旗舰店" size="large" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="昵称（选填）">
            <el-input v-model="form.nickname" placeholder="昵称" size="large" />
          </el-form-item>
          <el-form-item label="手机号（选填）">
            <el-input v-model="form.phone" placeholder="手机号" size="large" />
          </el-form-item>
        </template>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="submit">
          提交
        </el-button>
        <p class="links">
          <router-link to="/login">已有账号？去登录</router-link>
        </p>
      </el-form>
    </main>
  </div>
</template>

<style scoped>
.register {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
}
.panel {
  padding: 72px 56px;
  background: var(--md-color-primary);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}
.panel-eyebrow {
  margin: 0;
  color: var(--md-color-accent);
  font-size: 12px;
  letter-spacing: 0.18em;
}
.panel-title {
  margin: 0;
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 30px;
  line-height: 1.5;
}
.panel-sub {
  margin: 0;
  font-size: 13px;
  color: #aab3c4;
}
.form-area {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}
.form {
  width: 100%;
  max-width: 340px;
}
.form-title {
  margin: 0 0 24px;
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 22px;
  color: var(--md-color-ink);
}
.role-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 18px;
  padding: 4px;
  background: var(--md-color-bg-tint);
  border-radius: 999px;
}
.role-tab {
  flex: 1;
  padding: 9px 0;
  border: none;
  border-radius: 999px;
  background: transparent;
  font-family: var(--md-font-body);
  font-size: 14px;
  color: var(--md-color-ink-sub);
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}
.role-tab:hover {
  color: var(--md-color-primary);
}
.role_tab_active {
  background: var(--md-color-accent);
  color: var(--md-color-primary);
  font-weight: 600;
}
.submit {
  width: 100%;
  margin-top: 8px;
}
.links {
  margin-top: 18px;
  font-size: 13px;
  color: var(--md-color-ink-sub);
  text-align: center;
}
.links a {
  color: var(--md-color-primary);
}

@media (max-width: 800px) {
  .register {
    grid-template-columns: 1fr;
  }
  .panel {
    padding: 40px 28px;
  }
}
</style>
