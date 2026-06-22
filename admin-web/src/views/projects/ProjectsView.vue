<template>
  <div class="projects-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Projects</span>
        <h2 class="page-title">项目管理</h2>
        <p class="admin-page-copy">维护项目草稿、发布、下架、删除和首页精选展示。</p>
      </div>
      <el-button type="primary" @click="showCreate">新建项目</el-button>
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

      <el-select
        v-model="featuredFilter"
        placeholder="按精选筛选"
        clearable
        @change="handleFilterChange"
        style="width: 160px; margin-left: 12px"
      >
        <el-option label="全部" :value="undefined" />
        <el-option label="精选" :value="1" />
        <el-option label="非精选" :value="0" />
      </el-select>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无项目"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectType" label="类型" width="100">
          <template #default="{ row }">
            {{ row.projectType || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ STATUS_MAP[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="featured" label="精选" width="70">
          <template #default="{ row }">
            <el-tag v-if="row.featured === 1" type="warning" size="small">精选</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column label="发布时间" width="170">
          <template #default="{ row }">
            {{ row.publishedAt || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="340" fixed="right">
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
              title="确定删除该项目？删除后可在数据库中恢复。"
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
      :title="editingId ? '编辑项目' : '新建项目'"
      width="780px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入项目名称" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="项目简介">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="可选，一句话简介" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="项目类型">
          <el-input v-model="form.projectType" placeholder="例如：Web、AI、IoT、课程设计、竞赛作品" maxlength="50" />
        </el-form-item>
        <el-form-item label="技术栈">
          <el-input v-model="form.techStack" placeholder="例如：Java / Spring Boot / Vue / MySQL" maxlength="500" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leaderName" placeholder="负责人姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="参与成员">
          <el-input v-model="form.membersText" placeholder="参与成员，文本描述" maxlength="1000" />
        </el-form-item>
        <el-form-item label="封面图片">
          <CoverUpload v-model="form.coverUrl" usage-type="project_cover" placeholder="可选，粘贴URL或点击上传封面图片" />
        </el-form-item>
        <el-form-item label="代码仓库">
          <el-input v-model="form.repoUrl" placeholder="可选，GitHub/Gitee 链接" maxlength="500" />
        </el-form-item>
        <el-form-item label="演示地址">
          <el-input v-model="form.demoUrl" placeholder="可选，在线演示链接" maxlength="500" />
        </el-form-item>
        <el-form-item label="文档地址">
          <el-input v-model="form.documentUrl" placeholder="可选，文档链接" maxlength="500" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="首页精选">
              <el-switch
                v-model="form.featured"
                :active-value="1"
                :inactive-value="0"
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="项目详情" prop="descriptionMarkdown">
          <el-input
            v-model="form.descriptionMarkdown"
            type="textarea"
            :rows="14"
            placeholder="请输入 Markdown 格式的项目详细介绍"
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
import type { FormInstance, FormRules } from 'element-plus'
import CoverUpload from '@/components/CoverUpload.vue'
import {
  getProjects,
  createProject,
  updateProject,
  publishProject,
  offlineProject,
  deleteProject,
  STATUS_MAP,
  STATUS_TAG_TYPE
} from '@/api/project'
import type { ProjectItem } from '@/api/project'

const list = ref<ProjectItem[]>([])
const loading = ref(false)
const statusFilter = ref('')
const featuredFilter = ref<number | undefined>(undefined)
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
  descriptionMarkdown: '',
  projectType: '',
  techStack: '',
  leaderName: '',
  membersText: '',
  repoUrl: '',
  demoUrl: '',
  documentUrl: '',
  featured: 0 as number,
  sortOrder: 0
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  descriptionMarkdown: [{ required: true, message: '请输入项目详情', trigger: 'blur' }]
}

function statusTagType(status: string): 'warning' | 'primary' | '' | 'success' | 'danger' | 'info' {
  return STATUS_TAG_TYPE[status] || 'info'
}

function resetForm() {
  form.title = ''
  form.summary = ''
  form.coverUrl = ''
  form.descriptionMarkdown = ''
  form.projectType = ''
  form.techStack = ''
  form.leaderName = ''
  form.membersText = ''
  form.repoUrl = ''
  form.demoUrl = ''
  form.documentUrl = ''
  form.featured = 0
  form.sortOrder = 0
  editingId.value = null
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getProjects({
      page: page.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
      featured: featuredFilter.value
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

async function showEdit(row: ProjectItem) {
  try {
    const res = await getProjects({ page: 1, pageSize: 100 })
    const project = (res.data.records || []).find((p: ProjectItem) => p.id === row.id)
    if (project) {
      form.title = project.title
      form.summary = project.summary || ''
      form.coverUrl = project.coverUrl || ''
      form.descriptionMarkdown = project.descriptionMarkdown || ''
      form.projectType = project.projectType || ''
      form.techStack = project.techStack || ''
      form.leaderName = project.leaderName || ''
      form.membersText = project.membersText || ''
      form.repoUrl = project.repoUrl || ''
      form.demoUrl = project.demoUrl || ''
      form.documentUrl = project.documentUrl || ''
      form.featured = project.featured || 0
      form.sortOrder = project.sortOrder || 0
      editingId.value = project.id
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
      await updateProject(editingId.value, { ...form })
      ElMessage.success('项目已更新')
    } else {
      await createProject({ ...form })
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
    await publishProject(id)
    ElMessage.success('项目已发布')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function handleOffline(id: number) {
  try {
    await offlineProject(id)
    ElMessage.success('项目已下架')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function handleDelete(id: number) {
  try {
    await deleteProject(id)
    ElMessage.success('项目已删除')
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
.projects-page {
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

.text-muted {
  color: #c0c4cc;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
