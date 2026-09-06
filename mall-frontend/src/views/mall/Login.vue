<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    userStore.setLogin(data)
    ElMessage.success(`欢迎回来，${data.nickname ?? data.username}`)
    // 用户回前台，管理员/商家进后台；商家侧菜单由后台按 type 区分
    router.push(data.type === 0 ? '/' : '/admin')
  } catch {
    /* 错误提示由拦截器统一处理 */
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login">
    <aside class="panel">
      <p class="panel-eyebrow md-num">MALL-X / 登录</p>
      <h1 class="panel-title">一单一台账，<br />每位卖家都值得被看见。</h1>
      <p class="panel-sub">用户 · 平台管理员 · 商家 —— 三个角色一个入口</p>
    </aside>
    <main class="form-area">
      <el-form label-position="top" class="form" @submit.prevent="submit">
        <h2 class="form-title">登录</h2>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" inputmode="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="submit">
          登录
        </el-button>
        <p class="links">
          <router-link to="/register">注册用户</router-link>
          <span class="links-gap">·</span>
          <router-link to="/register?role=merchant">商家入驻申请</router-link>
        </p>
      </el-form>
    </main>
  </div>
</template>

<style scoped>
.login {
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
.links-gap {
  margin: 0 6px;
}

@media (max-width: 800px) {
  .login {
    grid-template-columns: 1fr;
  }
  .panel {
    padding: 40px 28px;
  }
}
</style>
