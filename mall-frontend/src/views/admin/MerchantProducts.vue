<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteProduct, getMyProducts, saveProduct, updateProductStatus, type Product, type Sku } from '@/api/product'
import { getCategories, type Category } from '@/api/catalog'

const list = ref<Product[]>([])
const total = ref(0)
const page = ref(1)
const categories = ref<Category[]>([])
const dialog = ref(false)
const editingId = ref<number>()

const form = reactive({
  categoryId: 0,
  title: '',
  subtitle: '',
  mainImg: '',
  detail: '',
  status: 0,
  skus: [] as Sku[],
})

async function load() {
  const res = await getMyProducts(page.value, 10)
  list.value = res.records
  total.value = res.total
}

function defaultSku(): Sku {
  return { specJson: '', price: 0, stock: 0, status: 0, remark: '' }
}

function openCreate() {
  editingId.value = undefined
  Object.assign(form, {
    categoryId: categories.value[0]?.id ?? 0,
    title: '',
    subtitle: '',
    mainImg: '',
    detail: '',
    status: 0,
    skus: [defaultSku()],
  })
  dialog.value = true
}

async function openEdit(row: Product) {
  const detail = await import('@/api/product').then((m) => m.getProductDetail(row.id))
  editingId.value = row.id
  Object.assign(form, {
    categoryId: row.categoryId,
    title: row.title,
    subtitle: row.subtitle ?? '',
    mainImg: row.mainImg,
    detail: detail.detail ?? '',
    status: row.status,
    skus: detail.skus.length ? detail.skus : [defaultSku()],
  })
  dialog.value = true
}

async function submit() {
  await saveProduct({ id: editingId.value, ...form })
  ElMessage.success('已保存')
  dialog.value = false
  load()
}

async function toggle(row: Product) {
  await updateProductStatus(row.id, row.status === 0 ? 1 : 0)
  ElMessage.success(row.status === 0 ? '已下架' : '已上架')
  load()
}

async function remove(row: Product) {
  await ElMessageBox.confirm(`删除商品「${row.title}」（连同规格）？`, '提醒', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(async () => {
  categories.value = await getCategories()
  load()
})
</script>

<template>
  <div>
    <div class="head">
      <h1 class="title">商品管理</h1>
      <el-button type="primary" @click="openCreate">新建商品</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="商品名" />
      <el-table-column label="最低价" width="120">
        <template #default="{ row }"><span class="md-num">¥ {{ row.minPrice?.toFixed(2) }}</span></template>
      </el-table-column>
      <el-table-column prop="saleCount" label="销量" width="90" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '在售' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 0 ? 'warning' : 'success'" @click="toggle(row)">
            {{ row.status === 0 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      class="pager"
      layout="prev, pager, next, total"
      :total="total"
      @current-change="load"
    />

    <el-dialog v-model="dialog" :title="editingId ? '编辑商品' : '新建商品'" width="680">
      <el-form label-width="80px">
        <el-form-item label="商品名">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" />
        </el-form-item>
        <el-form-item label="类目">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="主图 URL">
          <el-input v-model="form.mainImg" />
        </el-form-item>
        <el-form-item label="图文详情">
          <el-input v-model="form.detail" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="规格 SKU">
          <div class="sku-editor">
            <div v-for="(sku, i) in form.skus" :key="i" class="sku-row">
              <el-input v-model="sku.specJson" placeholder='{"颜色":"红"}' class="sku-spec" />
              <el-input-number v-model="sku.price" :precision="2" :min="0" placeholder="价格" class="sku-price" />
              <el-input-number v-model="sku.stock" :min="0" placeholder="库存" class="sku-stock" />
              <el-input v-model="sku.remark" placeholder="备注" class="sku-remark" />
              <el-button link type="danger" :disabled="form.skus.length <= 1" @click="form.skus.splice(i, 1)">删</el-button>
            </div>
            <el-button link type="primary" @click="form.skus.push(defaultSku())">+ 添加规格</el-button>
          </div>
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
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.sku-editor {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sku-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
.sku-spec {
  flex: 2;
}
.sku-price {
  width: 130px;
}
.sku-stock {
  width: 110px;
}
.sku-remark {
  flex: 1;
}
</style>
