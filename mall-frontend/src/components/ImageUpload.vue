<script setup lang="ts">
/**
 * 通用图片上传组件（Element Plus el-upload 形态）：选图 → 上传（props.upload 实现）→ 回填 URL。
 * 预览 + 可粘贴 URL 兜底；上传过程 el-upload 自带 loading。
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Link, Plus } from '@element-plus/icons-vue'
import type { UploadProps, UploadRawFile } from 'element-plus'

const props = defineProps<{
  modelValue: string
  /** 上传实现：返回 URL 字符串 */
  upload: (file: File) => Promise<{ url: string }>
  placeholder?: string
}>()

const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const uploading = ref(false)

/** el-upload：限制图片、单文件 */
const httpRequest: UploadProps['httpRequest'] = async (options) => {
  const raw = options.file as UploadRawFile
  if (!raw) return
  uploading.value = true
  try {
    const res = await props.upload(raw)
    emit('update:modelValue', res.url)
    ElMessage.success('图片已上传')
  } catch (e) {
    ElMessage.error('图片上传失败，请稍后重试')
    throw e
  } finally {
    uploading.value = false
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file: UploadRawFile) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('仅支持图片文件')
    return false
  }
  if (file.size > 20 * 1024 * 1024) {
    ElMessage.warning('文件超过 20MB，请压缩后重试')
    return false
  }
  return true
}

</script>

<template>
  <div class="iup">
    <el-upload
      :show-file-list="false"
      :http-request="httpRequest"
      :before-upload="beforeUpload"
      :disabled="uploading"
      accept="image/*"
      class="iup-upload"
    >
      <img v-if="modelValue" :src="modelValue" class="iup-preview" alt="" />
      <div v-else class="iup-placeholder">
        <el-icon class="iup-plus"><Plus /></el-icon>
        <span>{{ uploading ? '上传中…' : '上传图片' }}</span>
      </div>
    </el-upload>
    <div class="iup-side">
      <el-input
        :model-value="modelValue"
        size="small"
        :placeholder="placeholder ?? '或粘贴图片 URL'"
        class="iup-url"
        :prefix-icon="Link"
        @update:model-value="(v: string) => emit('update:modelValue', v)"
      />
      <span class="iup-tip">支持 jpg/png/webp，20MB 以内</span>
    </div>
  </div>
</template>

<style scoped>
.iup {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}
.iup-upload {
  flex-shrink: 0;
}
.iup-preview {
  display: block;
  width: 112px;
  height: 112px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--md-color-line);
  background: var(--md-color-bg-tint);
  transition: opacity 0.15s ease;
}
.iup-preview:hover {
  opacity: 0.85;
}
.iup-placeholder {
  width: 112px;
  height: 112px;
  border-radius: 8px;
  border: 1px dashed var(--mx-line);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--mx-ink-2);
  font-size: 12px;
  background: var(--mx-bg-2);
  cursor: pointer;
  transition: all 0.15s ease;
}
.iup-placeholder:hover {
  border-color: var(--mx-red);
  color: var(--mx-red);
}
.iup-plus {
  font-size: 20px;
}
.iup-side {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}
.iup-tip {
  font-size: 12px;
  color: var(--mx-ink-2);
}
</style>
