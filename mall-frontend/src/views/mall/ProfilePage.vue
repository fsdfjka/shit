<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAddress, getAddresses, getProfile, saveAddress, updateProfile, type Address, type Profile } from '@/api/user'

const profile = ref<Profile | null>(null)
const addresses = ref<Address[]>([])
const dialog = ref(false)
const editing = ref<Address | null>(null)
const form = reactive<Address>({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

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
      <router-link class="brand" to="/">MX<span class="brand-sub">个人中心</span></router-link>
      <router-link class="nav-link" to="/orders">我的订单</router-link>
    </header>

    <section v-if="profile" class="block">
      <h2 class="sub-title">资料</h2>
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
        <el-button size="small" @click="openCreate">新增地址</el-button>
      </div>
      <el-table :data="addresses" border>
        <el-table-column prop="receiver" label="收件人" width="110" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column label="地址" min-width="220">
          <template #default="{ row }">
            <span v-if="row.province" class="md-num">{{ row.province }}{{ row.city }}{{ row.district }}</span>
            {{ row.detail }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" size="small" type="warning">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
  max-width: 860px;
  margin: 0 auto;
  padding: 24px 24px 64px;
}
.head {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--md-color-line);
}
.brand {
  font-family: var(--md-font-display);
  font-weight: 700;
  font-size: 20px;
  color: var(--md-color-primary);
}
.brand-sub {
  font-size: 13px;
  font-weight: 400;
  color: var(--md-color-ink-sub);
  margin-left: 8px;
}
.nav-link {
  font-size: 14px;
  color: var(--md-color-ink-sub);
}
.block {
  margin-top: 24px;
}
.sub-title {
  margin: 0 0 14px;
  font-size: 16px;
  font-weight: 600;
}
.form {
  max-width: 460px;
}
.addr-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
