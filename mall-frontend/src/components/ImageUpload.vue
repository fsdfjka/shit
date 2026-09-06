<script setup lang="ts">
/**
 * 通用图片上传组件（Element Plus el-upload 形态，纯上传无 URL 输入）：
 * 点击选图 → 上传（props.upload 实现）→ 预览回填。
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps, UploadRawFile } from 'element-plus'

const props = defineProps<{
  modelValue: string
  /** 上传实现：返回 URL 字符串 */
  upload: (file: File) => Promise<{ url: string }>
}>()

const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const uploading = ref(false)

/** el-upload：上传后回填 URL */
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
  <el-upload
    :show-file-list="false"
    :http-request="httpRequest"
    :before-upload="beforeUpload"
    :disabled="uploading"
    accept="image/*"
    class="iup"
  >
    <img v-if="modelValue" :src="modelValue" class="iup-preview" alt="" />
    <div v-else class="iup-placeholder">
      <el-icon class="iup-plus"><Plus /></el-icon>
      <span>点击上传图片</span>
      <small>jpg / png / webp，20MB 以内</small>
    </div>
  </el-upload>
</template>

<style scoped>
.iup,
.iup :deep(.el-upload) {
  display: inline-block;
}
.iup-preview {
  display: block;
  width: 140px;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid var(--md-color-line);
  background: var(--md-color-bg-tint);
  transition: opacity 0.15s ease;
}
.iup-preview:hover {
  opacity: 0.85;
}
.iup-placeholder {
  width: 140px;
  height: 140px;
  border-radius: 10px;
  border: 1px dashed var(--mx-line);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--mx-ink-2);
  font-size: 13px;
  background: var(--mx-bg-2);
  cursor: pointer;
  transition: all 0.15s ease;
}
.iup-placeholder:hover {
  border-color: var(--mx-red);
  color: var(--mx-red);
}
.iup-placeholder small {
  font-size: 11px;
  opacity: 0.7;
}
.iup-plus {
  font-size: 22px;
}
</style>
