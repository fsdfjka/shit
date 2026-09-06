<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAddress, getAddresses, getProfile, saveAddress, updateProfile, type Address, type Profile } from '@/api/user'

const profile = ref<Profile | null>(null)
const addresses = ref<Address[]>([])
const dialog = ref(false)
const editing = ref<Address | null>(null)
const form = reactive<Address>({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

const defaultAddr = computed(() => addresses.value.find((a) => a.isDefault === 1))

async function load() {
  const [p, addr] = await Promise.all([getProfile(), getAddresses()])
  profile.value = p
  addresses.value = addr
}

function openCreate() {
  editing.value = null
  Object.assign(form, { receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })
  dialog.value = true
}

function openEdit(row: Address) {
  editing.value = row
  Object.assign(form, row)
  dialog.value = true
}

async function submit() {
  if (!form.receiver || !form.phone || !form.detail) {
    ElMessage.warning('收件人/电话/详细地址必填')
    return
  }
  await saveAddress({ ...(editing.value?.id ? { id: editing.value.id } : {}), ...form })
  ElMessage.success('已保存')
  dialog.value = false
  load()
}

async function remove(row: Address) {
  await ElMessageBox.confirm('删除该收货地址？', '提醒', { type: 'warning' })
  await deleteAddress(row.id!)
  ElMessage.success('已删除')
  load()
}

async function saveProfile() {
  if (!profile.value) return
  await updateProfile({ nickname: profile.value.nickname, phone: profile.value.phone, email: profile.value.email })
  ElMessage.success('资料已更新')
}

onMounted(load)
</script>

<template>
  <div class="profile">
    <header class="head">
      <router-link class="brand" to="/"><span class="brand-logo">MX</span><span class="brand-sub">个人中心</span></router-link>
      <router-link class="nav-link" to="/orders">我的订单</router-link>
    </header>

    <section v-if="profile" class="hero-card">
      <div class="hero-avatar ml">{{ (profile.nickname || profile.username || 'M').slice(0, 1).toUpperCase() }}</div>
      <div class="hero-info">
        <p class="hero-name">{{ profile.nickname || profile.username }}</p>
        <p class="hero-sub">@{{ profile.username }} · 普通用户</p>
      </div>
      <div class="hero-stats">
        <div class="hero-stat"><b class="md-num">{{ addresses.length }}</b><span>收货地址</span></div>
        <div class="hero-stat"><b class="md-num">{{ defaultAddr ? 1 : 0 }}</b><span>默认地址</span></div>
        <div class="hero-stat"><b class="md-num">{{ defaultAddr ? 1 : 0 }}</b><span>手机号 {{ profile.phone ? '已绑定' : '未绑定' }}</span></div>
      </div>
    </section>

    <section v-if="profile" class="block">
      <h2 class="sub-title">账号资料</h2>
      <el-form label-width="80px" class="form">
        <el-form-item label="用户名">
          <el-input :model-value="profile.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="profile.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profile.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profile.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存资料</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="block">
      <div class="addr-head">
        <h2 class="sub-title">收货地址</h2>
        <el-button size="small" type="primary" @click="openCreate">+ 新增地址</el-button>
      </div>
      <div class="addr-grid">
        <article v-for="row in addresses" :key="String(row.id)" class="addr-card">
          <div class="addr-top">
            <span class="addr-receiver">{{ row.receiver }}</span>
            <el-tag v-if="row.isDefault === 1" size="small" type="warning">默认</el-tag>
          </div>
          <p class="addr-phone md-num">{{ row.phone }}</p>
          <p class="addr-detail">
            <span v-if="row.province" class="md-num">{{ row.province }} {{ row.city }} {{ row.district }}</span>
            {{ row.detail }}
          </p>
          <div class="addr-ops">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </div>
        </article>
        <button class="addr-add" @click="openCreate">+ 添加收货地址</button>
      </div>
      <el-empty v-if="!addresses.length" :image-size="60" description="还没有收货地址" />
    </section>

    <el-dialog v-model="dialog" :title="editing ? '编辑地址' : '新增地址'" width="460">
      <el-form label-width="80px">
        <el-form-item label="收件人"><el-input v-model="form.receiver" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="省市区"><el-input v-model="form.province" placeholder="省 市 区（可空格分隔）" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.detail" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px 24px 64px;
}
.head {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--mx-line);
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
}
.brand-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: var(--mx-red);
  color: #fff;
  font-weight: 800;
  font-size: 17px;
}
.brand-sub {
  font-size: 15px;
  font-weight: 600;
  color: var(--mx-ink);
}
.nav-link {
  font-size: 14px;
  color: var(--mx-ink-2);
}
.nav-link:hover {
  color: var(--mx-red);
}

/* 顶部用户卡 */
.hero-card {
  margin-top: 18px;
  padding: 26px 30px;
  border-radius: 12px;
  background: linear-gradient(120deg, #ff5000, #e8761f);
  color: #fff;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}
.hero-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  font-size: 30px;
  font-weight: 800;
  flex-shrink: 0;
}
.hero-info {
  flex: 1;
  min-width: 160px;
}
.hero-name {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 700;
}
.hero-sub {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
}
.hero-stats {
  display: flex;
  gap: 28px;
  margin-left: auto;
}
.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.hero-stat b {
  font-size: 22px;
  font-weight: 700;
}
.hero-stat span {
  font-size: 12px;
  opacity: 0.85;
}

.block {
  margin-top: 26px;
}
.sub-title {
  margin: 0 0 14px;
  font-size: 16px;
  font-weight: 600;
  color: var(--mx-ink);
}
.form {
  max-width: 460px;
}
.addr-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 地址卡片 */
.addr-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 14px;
}
.addr-card {
  border: 1px solid var(--mx-line);
  border-radius: 10px;
  padding: 16px 18px;
  background: #fff;
  transition: box-shadow 0.15s ease;
}
.addr-card:hover {
  box-shadow: var(--md-shadow-card);
}
.addr-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.addr-receiver {
  font-size: 15px;
  font-weight: 600;
  color: var(--mx-ink);
}
.addr-phone {
  margin: 6px 0;
  font-size: 13px;
  color: var(--mx-ink-2);
}
.addr-detail {
  margin: 0;
  font-size: 13px;
  color: var(--mx-ink);
  line-height: 1.6;
  min-height: 42px;
}
.addr-ops {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed var(--mx-line);
}
.addr-add {
  border: 1px dashed var(--mx-line);
  border-radius: 10px;
  background: transparent;
  color: var(--mx-ink-2);
  font-family: var(--md-font-body);
  font-size: 14px;
  cursor: pointer;
  min-height: 120px;
  transition: all 0.15s ease;
}
.addr-add:hover {
  border-color: var(--mx-red);
  color: var(--mx-red);
}
</style>
