<template>
  <div class="members-page admin-page">
    <div class="page-header">
      <div>
        <span class="admin-page-kicker">Members</span>
        <h2 class="page-title">成员管理</h2>
        <p class="admin-page-copy">维护成员姓名、角色、年级、邮箱、排序和展示状态。</p>
      </div>
      <el-button type="primary" @click="showCreate">新建成员</el-button>
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
        <el-table-column prop="name" label="姓名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="roleTitle" label="角色" min-width="120" show-overflow-tooltip />
        <el-table-column prop="grade" label="年级" width="100">
          <template #default="{ row }">
            {{ row.grade || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.email || '-' }}
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="showEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确定删除该成员？"
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
      :title="editingId ? '编辑成员' : '新建成员'"
      width="680px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="角色" prop="roleTitle">
          <el-input v-model="form.roleTitle" placeholder="例如：后端开发、前端开发" maxlength="50" />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="form.avatarUrl" placeholder="头像图片URL，或通过上传接口获取" maxlength="500" />
        </el-form-item>
        <el-form-item label="方向ID">
          <el-input-number v-model="form.directionId" :min="0" placeholder="可选，关联技术方向" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="form.grade" placeholder="例如：2023级" maxlength="20" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="3" placeholder="可选，个人简介" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="技能">
          <el-input v-model="form.skills" placeholder='可选，JSON数组，例如：["Java","Spring"]' maxlength="500" />
        </el-form-item>
        <el-form-item label="GitHub">
          <el-input v-model="form.githubUrl" placeholder="可选，GitHub 主页链接" maxlength="300" />
        </el-form-item>
        <el-form-item label="博客">
          <el-input v-model="form.blogUrl" placeholder="可选，博客链接" maxlength="300" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="可选，邮箱地址" maxlength="100" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="submitForm"
          :loading="submitting"
        >{{ editingId ? '保存修改' : '创建成员' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getMembers,
  getMember,
  createMember,
  updateMember,
  deleteMember
} from '@/api/member'
import type { MemberItem } from '@/api/member'

const list = ref<MemberItem[]>([])
const loading = ref(false)
const statusFilter = ref<number | undefined>(undefined)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  roleTitle: '',
  avatarUrl: '',
  directionId: null as number | null,
  grade: '',
  bio: '',
  skills: '',
  githubUrl: '',
  blogUrl: '',
  email: '',
  sortOrder: 0,
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleTitle: [{ required: true, message: '请输入角色', trigger: 'blur' }]
}

function resetForm() {
  form.name = ''
  form.roleTitle = ''
  form.avatarUrl = ''
  form.directionId = null
  form.grade = ''
  form.bio = ''
  form.skills = ''
  form.githubUrl = ''
  form.blogUrl = ''
  form.email = ''
  form.sortOrder = 0
  form.status = 1
  editingId.value = null
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMembers(statusFilter.value)
    list.value = res.data || []
  } catch {
    ElMessage.error('加载成员列表失败')
  } finally {
    loading.value = false
  }
}

function showCreate() {
  resetForm()
  editingId.value = null
  dialogVisible.value = true
}

async function showEdit(row: MemberItem) {
  try {
    const res = await getMember(row.id)
    const member = res.data
    if (member) {
      form.name = member.name
      form.roleTitle = member.roleTitle
      form.avatarUrl = member.avatarUrl || ''
      form.directionId = member.directionId
      form.grade = member.grade || ''
      form.bio = member.bio || ''
      form.skills = member.skills || ''
      form.githubUrl = member.githubUrl || ''
      form.blogUrl = member.blogUrl || ''
      form.email = member.email || ''
      form.sortOrder = member.sortOrder || 0
      form.status = member.status
      editingId.value = member.id
      dialogVisible.value = true
    }
  } catch {
    ElMessage.error('获取成员信息失败')
  }
}

async function submitForm() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (editingId.value) {
      await updateMember(editingId.value, { ...form })
      ElMessage.success('成员已更新')
    } else {
      await createMember({ ...form })
      ElMessage.success('成员已创建')
    }
    dialogVisible.value = false
    await fetchList()
  } catch {
    ElMessage.error(editingId.value ? '更新成员失败' : '创建成员失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await deleteMember(id)
    ElMessage.success('成员已删除')
    await fetchList()
  } catch {
    ElMessage.error('删除成员失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.members-page {
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
