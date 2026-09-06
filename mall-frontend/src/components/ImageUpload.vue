<script setup lang="ts">
/**
 * 通用图片上传组件：选图 → 上传（由 props.upload 提供实现）→ 回填 URL。
 * 预览 + 可粘贴 URL 兜底；上传过程 loading。
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: string
  /** 上传实现：返回 URL 字符串 */
  upload: (file: File) => Promise<{ url: string }>
  placeholder?: string
}>()

const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const uploading = ref(false)

function pick() {
  const input = document.getElementById('iup-input') as HTMLInputElement | null
  input?.click()
}

async function onFileChange(ev: Event) {
  const file = (ev.target as HTMLInputElement).files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const res = await props.upload(file)
    emit('update:modelValue', res.url)
    ElMessage.success('图片已上传')
  } catch {
    /* 拦截器统一提示 */
  } finally {
    uploading.value = false
    ;(ev.target as HTMLInputElement).value = ''
  }
}
</script>

<template>
  <div class="iup">
    <img v-if="modelValue" :src="modelValue" class="iup-preview" alt="" />
    <div v-else class="iup-placeholder">图片预览</div>
    <div class="iup-side">
      <el-button size="small" type="primary" :loading="uploading" @click="pick">
        {{ uploading ? '上传中…' : '上传图片' }}
      </el-button>
      <el-input :model-value="modelValue" size="small" :placeholder="placeholder ?? '或粘贴图片 URL'" class="iup-url" @update:model-value="(v: string) => emit('update:modelValue', v)" />
    </div>
    <input id="iup-input" type="file" accept="image/*" hidden @change="onFileChange" />
  </div>
</template>

<style scoped>
.iup {
  display: flex;
  gap: 12px;
  align-items: center;
}
.iup-preview {
  width: 82px;
  height: 62px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid var(--md-color-line);
  background: var(--md-color-bg-tint);
}
.iup-placeholder {
  width: 82px;
  height: 62px;
  border-radius: 6px;
  border: 1px dashed var(--md-color-line);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--md-color-ink-sub);
  font-size: 12px;
  background: var(--md-color-bg-tint);
}
.iup-side {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}
</style>
