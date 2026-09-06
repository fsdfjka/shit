<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminListCategories, deleteCategory, saveCategory, type Category } from '@/api/catalog'

const list = ref<Category[]>([])
const dialog = ref(false)
const editing = ref<Category | null>(null)
const form = reactive({ name: '', parentId: 0, sort: 0 })

async function load() {
  list.value = await adminListCategories()
}

function openCreate() {
  editing.value = null
  form.name = ''
  form.parentId = 0
  form.sort = 0
  dialog.value = true
}

function openEdit(row: Category) {
  editing.value = row
  form.name = row.name
  form.parentId = row.parentId
  form.sort = row.sort
  dialog.value = true
}

async function submit() {
  await saveCategory({ id: editing.value?.id, name: form.name, parentId: form.parentId, sort: form.sort })
  ElMessage.success(editing.value ? '已保存' : '已创建')
  dialog.value = false
  load()
}

async function remove(row: Category) {
  await ElMessageBox.confirm(`删除类目「${row.name}」？`, '提醒', { type: 'warning' })
  await deleteCategory(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="head">
      <h1 class="title">类目管理</h1>
      <el-button type="primary" @click="openCreate">新建类目</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column label="编码" width="90">
        <template #default="{ row }"><span class="md-num">C{{ String(row.id).padStart(2, '0') }}</span></template>
      </el-table-column>
      <el-table-column prop="name" label="类目名" />
      <el-table-column label="层级" width="90">
        <template #default="{ row }">{{ row.parentId === 0 ? '一级' : '二级' }}</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editing ? '编辑类目' : '新建类目'" width="440">
      <el-form label-width="70px">
        <el-form-item label="类目名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父类目">
          <el-select v-model="form.parentId" style="width: 100%">
            <el-option :value="0" label="一级类目（根）" />
            <el-option v-for="c in list.filter((x) => x.parentId === 0)" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
</style>
