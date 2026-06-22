<template>
  <div class="upload-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Upload</span>
        <h2 class="page-title">文件上传</h2>
        <p class="admin-page-copy">上传图片资源，支持 JPG、PNG、WebP 格式，单文件最大 10MB。</p>
      </div>
    </div>

    <!-- 上传区域 -->
    <el-card shadow="never" class="upload-card">
      <div class="upload-area">
        <div class="upload-actions">
          <el-select
            v-model="usageType"
            placeholder="选择文件用途"
            style="width: 200px; margin-right: 16px"
          >
            <el-option label="文章封面" value="article_cover" />
            <el-option label="项目封面" value="project_cover" />
            <el-option label="成员头像" value="member_avatar" />
            <el-option label="其他" value="other" />
          </el-select>
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            :accept="acceptTypes"
            :on-change="handleFileChange"
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                仅支持 JPG / PNG / WebP 格式，单文件不超过 10MB
              </div>
            </template>
          </el-upload>
        </div>

        <!-- 待上传文件预览 -->
        <div v-if="pendingFile" class="pending-file">
          <div class="pending-file-info">
            <el-icon><PictureFilled /></el-icon>
            <span class="pending-file-name">{{ pendingFile.name }}</span>
            <span class="pending-file-size">{{ formatFileSize(pendingFile.size) }}</span>
          </div>
          <div class="pending-file-actions">
            <el-button type="primary" :loading="uploading" @click="doUpload">上传</el-button>
            <el-button @click="pendingFile = null">取消</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 已上传文件列表 -->
    <el-card shadow="never" style="margin-top: 20px">
      <el-table
        :data="fileList"
        empty-text="暂无上传文件"
        v-loading="loading"
      >
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <img
              v-if="isImage(row.mimeType)"
              :src="row.fileUrl"
              class="file-thumb"
              @error="onImgError"
            />
            <el-icon v-else :size="32"><Document /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="originalName" label="原始文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="mimeType" label="类型" width="120" />
        <el-table-column label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="用途" width="120">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ usageTypeLabel(row.usageType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="170">
          <template #default="{ row }">
            {{ row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="copyUrl(row.fileUrl)">
              复制URL
            </el-button>
            <el-popconfirm
              title="确定删除该文件？删除后不可恢复。"
              confirm-button-text="删除"
              cancel-button-text="取消"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button size="small" link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, PictureFilled, Document } from '@element-plus/icons-vue'
import { uploadFile, getUploadedFiles, deleteUploadedFile, type UploadFileItem } from '@/api/upload'

const fileList = ref<UploadFileItem[]>([])
const loading = ref(false)
const uploading = ref(false)
const usageType = ref('other')
const pendingFile = ref<File | null>(null)

const acceptTypes = 'image/jpeg,image/png,image/webp'
const MAX_SIZE = 10 * 1024 * 1024 // 10MB

function isImage(mimeType: string): boolean {
  return /^image\/(jpeg|png|webp)$/.test(mimeType)
}

function formatFileSize(bytes: number): string {
  if (bytes == null || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const USAGE_TYPE_MAP: Record<string, string> = {
  article_cover: '文章封面',
  project_cover: '项目封面',
  member_avatar: '成员头像',
  other: '其他'
}

function usageTypeLabel(type: string): string {
  return USAGE_TYPE_MAP[type] || type || '其他'
}

function handleFileChange(file: any) {
  const raw = file.raw as File
  if (!raw) return

  if (!acceptTypes.split(',').includes(raw.type)) {
    ElMessage.warning('仅支持 JPG、PNG、WebP 格式')
    return
  }
  if (raw.size > MAX_SIZE) {
    ElMessage.warning('文件大小不能超过 10MB')
    return
  }

  pendingFile.value = raw
}

async function doUpload() {
  if (!pendingFile.value) return

  uploading.value = true
  try {
    await uploadFile(pendingFile.value, usageType.value)
    ElMessage.success('文件上传成功')
    pendingFile.value = null
    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    uploading.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getUploadedFiles()
    fileList.value = res.data || []
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function copyUrl(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('URL 已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteUploadedFile(id)
    ElMessage.success('文件已删除')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.upload-page {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-size: 26px;
  font-weight: 780;
  color: var(--admin-text-strong);
  margin: 0;
}

.upload-card {
  margin-bottom: 0;
}

.upload-area {
  padding: 8px 0;
}

.upload-actions {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.pending-file {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding: 12px 16px;
  background: var(--el-color-primary-light-9);
  border-radius: 6px;
}

.pending-file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pending-file-name {
  font-weight: 500;
  color: var(--admin-text-strong);
}

.pending-file-size {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.pending-file-actions {
  display: flex;
  gap: 8px;
}

.file-thumb {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid var(--el-border-color-light);
}
</style>
