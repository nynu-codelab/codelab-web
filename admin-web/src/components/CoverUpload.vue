<template>
  <div class="cover-upload">
    <div class="cover-upload__input-row">
      <el-input
        :model-value="modelValue"
        @update:model-value="$emit('update:modelValue', $event)"
        :placeholder="placeholder"
        :maxlength="500"
        clearable
        class="cover-upload__input"
      />
      <el-upload
        :show-file-list="false"
        :before-upload="handleBeforeUpload"
        :http-request="handleUpload"
        accept=".jpg,.jpeg,.png,.webp"
        class="cover-upload__upload-btn"
      >
        <el-button :loading="uploading" :disabled="uploading">
          {{ uploading ? '上传中...' : '上传封面' }}
        </el-button>
      </el-upload>
    </div>

    <!-- 上传中 loading -->
    <div v-if="uploading" class="cover-upload__loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>正在上传封面图片...</span>
    </div>

    <!-- 封面预览 -->
    <div v-if="modelValue && !uploading" class="cover-upload__preview">
      <img :src="modelValue" alt="封面预览" @error="handleImgError" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { uploadFile } from '@/api/upload'
import type { UploadProps, UploadRequestOptions } from 'element-plus'

const props = withDefaults(defineProps<{
  modelValue: string
  placeholder?: string
  usageType?: string
}>(), {
  placeholder: '可选，封面图片URL',
  usageType: 'cover'
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const uploading = ref(false)

/** 允许的文件类型 */
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp']
/** 最大文件大小：10MB */
const MAX_SIZE = 10 * 1024 * 1024

/**
 * 上传前校验（客户端第一道防线）
 */
const handleBeforeUpload: UploadProps['beforeUpload'] = (file) => {
  // 校验文件类型
  if (!ALLOWED_TYPES.includes(file.type)) {
    ElMessage.error('仅支持 JPG、PNG、WebP 格式的图片')
    return false
  }
  // 校验文件大小
  if (file.size > MAX_SIZE) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }
  return true
}

/**
 * 自定义上传逻辑
 */
const handleUpload = async (options: UploadRequestOptions) => {
  uploading.value = true
  try {
    const res = await uploadFile(options.file as File, props.usageType)
    if (res.code === 200 && res.data?.fileUrl) {
      const url = res.data.fileUrl
      emit('update:modelValue', url)
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error('上传失败：' + (res.message || '未知错误'))
    }
  } catch (e: any) {
    const msg = e?.response?.data?.message || e?.message || '上传失败，请重试'
    ElMessage.error(msg)
  } finally {
    uploading.value = false
  }
}

/**
 * 图片加载失败处理
 */
const handleImgError = () => {
  // 静默处理，预览区自然隐藏
}
</script>

<style scoped>
.cover-upload__input-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.cover-upload__input {
  flex: 1;
}

.cover-upload__upload-btn {
  flex-shrink: 0;
}

.cover-upload__loading {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.cover-upload__preview {
  margin-top: 8px;
  width: 200px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  background: var(--el-fill-color-light);
}

.cover-upload__preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
