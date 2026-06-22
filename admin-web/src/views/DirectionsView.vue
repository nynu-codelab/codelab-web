<template>
  <div class="directions-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Directions</span>
        <h2 class="page-title">技术方向管理</h2>
        <p class="admin-page-copy">维护技术方向编码、名称、简介、排序和启用状态。</p>
      </div>
      <el-button type="primary" @click="showCreate">新建方向</el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select
        v-model="statusFilter"
        placeholder="按状态筛选"
        clearable
        @change="fetchList"
        style="width: 160px"
      >
        <el-option label="全部" :value="undefined" />
        <el-option label="启用" :value="1" />
        <el-option label="停用" :value="0" />
      </el-select>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无数据"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="code" label="编码" width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="summary" label="简介" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.summary || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="showEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              link
              type="success"
              @click="handleEnable(row.id)"
            >启用</el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              link
              type="warning"
              @click="handleDisable(row.id)"
            >停用</el-button>
            <el-popconfirm
              title="确定删除该方向？"
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

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑方向' : '新建方向'"
      width="620px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入技术方向名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="唯一标识，例如：java-backend" maxlength="50" />
        </el-form-item>
        <el-form-item label="简介" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入方向简介" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="可选，详细描述" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder='可选，JSON数组，例如：["Java","Spring"]' maxlength="300" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="可选，图标名称或URL" maxlength="200" />
        </el-form-item>
        <el-form-item label="封面URL">
          <el-input v-model="form.coverUrl" placeholder="可选，封面图片URL" maxlength="500" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="submitForm"
          :loading="submitting"
        >{{ editingId ? '保存修改' : '创建方向' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getDirections,
  createDirection,
  updateDirection,
  enableDirection,
  disableDirection,
  deleteDirection
} from '@/api/direction'
import type { DirectionItem } from '@/api/direction'

const list = ref<DirectionItem[]>([])
const loading = ref(false)
const statusFilter = ref<number | undefined>(undefined)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  code: '',
  summary: '',
  description: '',
  tags: '',
  icon: '',
  coverUrl: '',
  sortOrder: 0
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入技术方向名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  summary: [{ required: true, message: '请输入简介', trigger: 'blur' }]
}

function resetForm() {
  form.name = ''
  form.code = ''
  form.summary = ''
  form.description = ''
  form.tags = ''
  form.icon = ''
  form.coverUrl = ''
  form.sortOrder = 0
  editingId.value = null
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getDirections(statusFilter.value)
    list.value = res.data || []
  } catch {
    ElMessage.error('加载方向列表失败')
  } finally {
    loading.value = false
  }
}

function showCreate() {
  resetForm()
  editingId.value = null
  dialogVisible.value = true
}

async function showEdit(row: DirectionItem) {
  try {
    const res = await getDirections()
    const direction = (res.data || []).find(d => d.id === row.id)
    if (direction) {
      form.name = direction.name
      form.code = direction.code
      form.summary = direction.summary
      form.description = direction.description || ''
      form.tags = direction.tags || ''
      form.icon = direction.icon || ''
      form.coverUrl = direction.coverUrl || ''
      form.sortOrder = direction.sortOrder || 0
      editingId.value = direction.id
      dialogVisible.value = true
    }
  } catch {
    ElMessage.error('获取方向信息失败')
  }
}

async function submitForm() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (editingId.value) {
      await updateDirection(editingId.value, { ...form })
      ElMessage.success('方向已更新')
    } else {
      await createDirection({ ...form })
      ElMessage.success('方向已创建')
    }
    dialogVisible.value = false
    await fetchList()
  } catch {
    ElMessage.error(editingId.value ? '更新方向失败' : '创建方向失败')
  } finally {
    submitting.value = false
  }
}

async function handleEnable(id: number) {
  try {
    await enableDirection(id)
    ElMessage.success('方向已启用')
    await fetchList()
  } catch {
    ElMessage.error('启用方向失败')
  }
}

async function handleDisable(id: number) {
  try {
    await disableDirection(id)
    ElMessage.success('方向已停用')
    await fetchList()
  } catch {
    ElMessage.error('停用方向失败')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteDirection(id)
    ElMessage.success('方向已删除')
    await fetchList()
  } catch {
    ElMessage.error('删除方向失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.directions-page {
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
</style>
