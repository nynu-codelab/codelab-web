<template>
  <div class="users-page admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Users</span>
        <h2 class="admin-page-title">用户管理</h2>
        <p class="admin-page-copy">管理系统用户，支持搜索、筛选、启用/禁用、角色变更和密码重置。</p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="query.keyword"
        placeholder="搜索用户名 / 姓名 / 手机号"
        clearable
        :prefix-icon="Search"
        style="width: 260px"
        @keyup.enter="handleQuery"
      />
      <el-select
        v-model="query.role"
        placeholder="角色筛选"
        clearable
        style="width: 140px"
      >
        <el-option label="全部角色" value="" />
        <el-option label="管理员" value="ADMIN" />
        <el-option label="普通用户" value="USER" />
      </el-select>
      <el-select
        v-model="query.status"
        placeholder="状态筛选"
        clearable
        style="width: 140px"
      >
        <el-option label="全部状态" :value="undefined" />
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleQuery">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无用户数据"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="ROLE_TAG_TYPE[row.role] || 'info'" size="small">
              {{ ROLE_MAP[row.role] || row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG_TYPE[row.status] || 'info'" size="small">
              {{ STATUS_MAP[row.status] ?? row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>

            <el-popconfirm
              v-if="row.status === 1"
              title="确定要禁用该用户吗？"
              @confirm="handleToggleStatus(row)"
            >
              <template #reference>
                <el-button type="warning" link size="small">禁用</el-button>
              </template>
            </el-popconfirm>

            <el-popconfirm
              v-else
              title="确定要启用该用户吗？"
              @confirm="handleToggleStatus(row)"
            >
              <template #reference>
                <el-button type="success" link size="small">启用</el-button>
              </template>
            </el-popconfirm>

            <el-button type="primary" link size="small" @click="showRoleDialog(row)">改角色</el-button>
            <el-button type="danger" link size="small" @click="showResetPwdDialog(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchList"
          @size-change="fetchList"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="用户详情"
      width="640px"
      destroy-on-close
    >
      <template v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ detail.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detail.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="年级">{{ detail.grade || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ detail.major || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detail.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="ROLE_TAG_TYPE[detail.role] || 'info'" size="small">
              {{ ROLE_MAP[detail.role] || detail.role }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="STATUS_TAG_TYPE[detail.status] || 'info'" size="small">
              {{ STATUS_MAP[detail.status] ?? detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 修改角色对话框 -->
    <el-dialog
      v-model="roleVisible"
      title="修改用户角色"
      width="420px"
      destroy-on-close
      @closed="roleForm.role = ''"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="当前角色">
          <el-tag :type="ROLE_TAG_TYPE[roleTarget?.role || ''] || 'info'" size="small">
            {{ ROLE_MAP[roleTarget?.role || ''] || roleTarget?.role }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="roleForm.role" placeholder="请选择新角色" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangeRole" :loading="roleSubmitting">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPwdVisible"
      title="重置用户密码"
      width="420px"
      destroy-on-close
      @closed="resetPwdForm.newPassword = ''"
    >
      <el-form :model="resetPwdForm" label-width="80px">
        <el-form-item label="目标用户">
          <span style="color: var(--admin-text-strong); font-weight: 600;">
            {{ resetPwdTarget?.username }}（{{ resetPwdTarget?.realName || '-' }}）
          </span>
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input
            v-model="resetPwdForm.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
            minlength="6"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" :loading="resetPwdSubmitting">
          确认重置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import {
  getUsers,
  getUserDetail,
  updateUserStatus,
  updateUserRole,
  resetUserPassword,
  ROLE_MAP,
  ROLE_TAG_TYPE,
  STATUS_MAP,
  STATUS_TAG_TYPE
} from '@/api/user'
import type { AdminUserVO } from '@/api/user'

// ---- 列表数据 ----
const list = ref<AdminUserVO[]>([])
const total = ref(0)
const loading = ref(false)

const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  role: '',
  status: undefined as number | undefined
})

// ---- 详情 ----
const detailVisible = ref(false)
const detail = ref<AdminUserVO | null>(null)

// ---- 角色修改 ----
const roleVisible = ref(false)
const roleTarget = ref<AdminUserVO | null>(null)
const roleForm = reactive({ role: '' })
const roleSubmitting = ref(false)

// ---- 重置密码 ----
const resetPwdVisible = ref(false)
const resetPwdTarget = ref<AdminUserVO | null>(null)
const resetPwdForm = reactive({ newPassword: '' })
const resetPwdSubmitting = ref(false)

// ---- 数据获取 ----
async function fetchList() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: query.page,
      pageSize: query.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.role) params.role = query.role
    if (query.status !== undefined) params.status = query.status

    const res = await getUsers(params as any)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  query.page = 1
  fetchList()
}

function handleReset() {
  query.keyword = ''
  query.role = ''
  query.status = undefined
  query.page = 1
  fetchList()
}

// ---- 详情 ----
async function showDetail(row: AdminUserVO) {
  try {
    const res = await getUserDetail(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch {
    // error handled by interceptor
  }
}

// ---- 启用/禁用 ----
async function handleToggleStatus(row: AdminUserVO) {
  // 防止管理员禁用自己
  const userStore = useUserStore()
  if (userStore.userInfo && row.id === userStore.userInfo.id) {
    ElMessage.warning('不能禁用当前登录的管理员账号')
    return
  }
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '用户已启用' : '用户已禁用')
    await fetchList()
  } catch {
    // error handled by interceptor
  }
}

// ---- 角色修改 ----
function showRoleDialog(row: AdminUserVO) {
  // 防止管理员降级自己
  const userStore = useUserStore()
  if (userStore.userInfo && row.id === userStore.userInfo.id) {
    ElMessage.warning('不能修改当前登录管理员的角色')
    return
  }
  roleTarget.value = row
  roleForm.role = ''
  roleVisible.value = true
}

async function handleChangeRole() {
  if (!roleTarget.value) return
  if (!roleForm.role) {
    ElMessage.warning('请选择新角色')
    return
  }
  if (roleForm.role === roleTarget.value.role) {
    ElMessage.warning('新角色与当前角色相同')
    return
  }

  roleSubmitting.value = true
  try {
    await updateUserRole(roleTarget.value.id, roleForm.role)
    ElMessage.success('角色修改成功')
    roleVisible.value = false
    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    roleSubmitting.value = false
  }
}

// ---- 重置密码 ----
function showResetPwdDialog(row: AdminUserVO) {
  resetPwdTarget.value = row
  resetPwdForm.newPassword = ''
  resetPwdVisible.value = true
}

async function handleResetPassword() {
  if (!resetPwdTarget.value) return
  if (!resetPwdForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (resetPwdForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }

  resetPwdSubmitting.value = true
  try {
    const res = await resetUserPassword(resetPwdTarget.value.id, resetPwdForm.newPassword)
    ElMessage.success(res.data || '密码重置成功')
    resetPwdVisible.value = false

    // 如果重置的是自己的密码，token 已失效 → 强制登出并跳转登录页
    const userStore = useUserStore()
    if (userStore.userInfo && resetPwdTarget.value.id === userStore.userInfo.id) {
      ElMessage.warning('密码已重置，请重新登录')
      await userStore.logout()
      const router = useRouter()
      router.push({ name: 'Login' })
      return
    }

    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    resetPwdSubmitting.value = false
  }
}

// ---- 初始化 ----
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.users-page {
  padding: 0;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
