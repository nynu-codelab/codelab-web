<template>
  <div class="articles-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Articles</span>
        <h2 class="page-title">文章管理</h2>
        <p class="admin-page-copy">维护学习文章草稿、发布、下架和删除流程。</p>
      </div>
      <el-button type="primary" @click="showCreate">新建文章</el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select
        v-model="statusFilter"
        placeholder="按状态筛选"
        clearable
        @change="handleFilterChange"
        style="width: 160px"
      >
        <el-option label="全部" value="" />
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已发布" value="PUBLISHED" />
        <el-option label="已下架" value="OFFLINE" />
      </el-select>

      <el-input
        v-model="keyword"
        placeholder="搜索文章标题"
        clearable
        @keyup.enter="handleFilterChange"
        @clear="handleFilterChange"
        style="width: 240px; margin-left: 12px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无文章"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            {{ row.category || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ STATUS_MAP[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="70" />
        <el-table-column label="发布时间" width="170">
          <template #default="{ row }">
            {{ row.publishedAt || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="showEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'DRAFT'"
              size="small"
              link
              type="success"
              @click="handlePublish(row.id)"
            >发布</el-button>
            <el-button
              v-if="row.status === 'PUBLISHED'"
              size="small"
              link
              type="warning"
              @click="handleOffline(row.id)"
            >下架</el-button>
            <el-popconfirm
              title="确定删除该文章？删除后可在数据库中恢复。"
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

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="fetchList"
      />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑文章' : '新建文章'"
      width="780px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="可选，文章摘要" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="例如：学习笔记、技术分享" maxlength="50" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder='可选，JSON数组格式，例如：["Java","Spring"]' maxlength="500" />
        </el-form-item>
        <el-form-item label="封面图片">
          <CoverUpload v-model="form.coverUrl" usage-type="article_cover" placeholder="可选，粘贴URL或点击上传封面图片" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="正文" prop="contentMarkdown">
          <el-input
            v-model="form.contentMarkdown"
            type="textarea"
            :rows="14"
            placeholder="请输入 Markdown 正文"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="submitForm"
          :loading="submitting"
        >{{ editingId ? '保存修改' : '创建草稿' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import CoverUpload from '@/components/CoverUpload.vue'
import {
  getArticles,
  getArticle,
  createArticle,
  updateArticle,
  publishArticle,
  offlineArticle,
  deleteArticle,
  STATUS_MAP,
  STATUS_TAG_TYPE
} from '@/api/article'
import type { ArticleItem } from '@/api/article'

const list = ref<ArticleItem[]>([])
const loading = ref(false)
const statusFilter = ref('')
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  title: '',
  summary: '',
  coverUrl: '',
  contentMarkdown: '',
  category: '',
  tags: '',
  sortOrder: 0
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  contentMarkdown: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

function statusTagType(status: string): 'warning' | 'primary' | '' | 'success' | 'danger' | 'info' {
  return STATUS_TAG_TYPE[status] || 'info'
}

function resetForm() {
  form.title = ''
  form.summary = ''
  form.coverUrl = ''
  form.contentMarkdown = ''
  form.category = ''
  form.tags = ''
  form.sortOrder = 0
  editingId.value = null
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getArticles({
      page: page.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
      keyword: keyword.value || undefined
    })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  page.value = 1
  fetchList()
}

function showCreate() {
  resetForm()
  editingId.value = null
  dialogVisible.value = true
}

async function showEdit(row: ArticleItem) {
  try {
    const res = await getArticle(row.id)
    const article = res.data
    if (article) {
      form.title = article.title
      form.summary = article.summary || ''
      form.coverUrl = article.coverUrl || ''
      form.contentMarkdown = article.contentMarkdown || ''
      form.category = article.category || ''
      form.tags = article.tags || ''
      form.sortOrder = article.sortOrder || 0
      editingId.value = article.id
      dialogVisible.value = true
    }
  } catch {
    // error handled by interceptor
  }
}

async function submitForm() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (editingId.value) {
      await updateArticle(editingId.value, { ...form })
      ElMessage.success('文章已更新')
    } else {
      await createArticle({ ...form })
      ElMessage.success('草稿已创建')
    }
    dialogVisible.value = false
    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

async function handlePublish(id: number) {
  try {
    await publishArticle(id)
    ElMessage.success('文章已发布')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function handleOffline(id: number) {
  try {
    await offlineArticle(id)
    ElMessage.success('文章已下架')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function handleDelete(id: number) {
  try {
    await deleteArticle(id)
    ElMessage.success('文章已删除')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.articles-page {
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

.filter-bar {
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
