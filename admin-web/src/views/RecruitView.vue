<template>
  <div class="recruit-page admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Applications</span>
        <h2 class="admin-page-title">报名管理</h2>
        <p class="admin-page-copy">查看报名信息、筛选审核状态，并完成管理员审核流程。</p>
      </div>
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
        <el-option label="待处理" value="PENDING" />
        <el-option label="已查看" value="VIEWED" />
        <el-option label="通过初筛" value="PRELIMINARY_PASSED" />
        <el-option label="面试中" value="INTERVIEWING" />
        <el-option label="已通过" value="PASSED" />
        <el-option label="已拒绝" value="REJECTED" />
        <el-option label="已联系" value="CONTACTED" />
      </el-select>

      <el-select
        v-model="directionFilter"
        placeholder="按方向筛选"
        clearable
        @change="handleFilterChange"
        style="width: 160px; margin-left: 12px"
      >
        <el-option label="全部" value="" />
        <el-option label="前端" value="frontend" />
        <el-option label="后端" value="backend" />
        <el-option label="全栈" value="fullstack" />
        <el-option label="AI/算法" value="ai" />
        <el-option label="产品" value="product" />
        <el-option label="设计" value="design" />
        <el-option label="其他" value="other" />
      </el-select>

      <el-input
        v-model="keyword"
        placeholder="搜索姓名/手机号"
        clearable
        @keyup.enter="handleFilterChange"
        @clear="handleFilterChange"
        style="width: 220px; margin-left: 12px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-card shadow="never">
      <el-table
        :data="list"
        empty-text="暂无报名数据"
        v-loading="loading"
        @row-click="showDetail"
        style="cursor: pointer"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="major" label="专业" width="140" />
        <el-table-column prop="direction" label="方向" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ STATUS_MAP[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="170" />
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

    <!-- 详情/审核对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="报名详情"
      width="720px"
      destroy-on-close
    >
      <template v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detail.status)">
              {{ STATUS_MAP[detail.status] || detail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detail.realName }}</el-descriptions-item>
          <el-descriptions-item label="年级">{{ detail.grade }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ detail.major }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detail.className }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="QQ号">{{ detail.qq }}</el-descriptions-item>
          <el-descriptions-item label="意向方向">{{ detail.direction }}</el-descriptions-item>
          <el-descriptions-item label="编程基础">
            {{ detail.hasProgrammingBasis ? '有' : '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="已掌握技术" :span="2">{{ detail.skills || '-' }}</el-descriptions-item>
          <el-descriptions-item label="个人介绍" :span="2">{{ detail.introduction || '-' }}</el-descriptions-item>
          <el-descriptions-item label="加入原因" :span="2">{{ detail.reason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="每周可投入时间">{{ detail.weeklyAvailableTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目/作品链接">{{ detail.portfolioUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报名时间">{{ detail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="审核备注" :span="2">
            {{ detail.reviewRemark || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 审核操作区 -->
        <div class="review-section">
          <h4>审核操作</h4>
          <el-form :model="reviewForm" label-width="80px">
            <el-form-item label="审核状态">
              <el-select v-model="reviewForm.status" placeholder="请选择审核结果">
                <el-option label="待处理" value="PENDING" />
                <el-option label="已查看" value="VIEWED" />
                <el-option label="通过初筛" value="PRELIMINARY_PASSED" />
                <el-option label="面试中" value="INTERVIEWING" />
                <el-option label="已通过" value="PASSED" />
                <el-option label="已拒绝" value="REJECTED" />
                <el-option label="已联系" value="CONTACTED" />
              </el-select>
            </el-form-item>
            <el-form-item label="审核备注">
              <el-input
                v-model="reviewForm.reviewRemark"
                type="textarea"
                :rows="3"
                placeholder="可选，填写审核意见"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="submitReview"
                :loading="reviewing"
              >提交审核</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import {
  getApplications,
  getApplicationDetail,
  reviewApplication,
  STATUS_MAP,
  STATUS_TAG_TYPE
} from '@/api/application'
import type { ApplyRecord } from '@/api/application'

const list = ref<ApplyRecord[]>([])
const loading = ref(false)
const statusFilter = ref('')
const directionFilter = ref('')
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const detail = ref<ApplyRecord | null>(null)
const reviewing = ref(false)

const reviewForm = reactive({
  status: '',
  reviewRemark: ''
})

function statusTagType(status: string): 'warning' | 'primary' | '' | 'success' | 'danger' | 'info' {
  return STATUS_TAG_TYPE[status] || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getApplications({
      page: page.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
      direction: directionFilter.value || undefined,
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

async function showDetail(row: ApplyRecord) {
  try {
    const res = await getApplicationDetail(row.id)
    detail.value = res.data
    reviewForm.status = detail.value?.status || ''
    reviewForm.reviewRemark = detail.value?.reviewRemark || ''
    dialogVisible.value = true
  } catch {
    // error handled by interceptor
  }
}

async function submitReview() {
  if (!detail.value) return
  if (!reviewForm.status) {
    ElMessage.warning('请选择审核状态')
    return
  }

  reviewing.value = true
  try {
    await reviewApplication(detail.value.id, {
      status: reviewForm.status,
      reviewRemark: reviewForm.reviewRemark
    })
    ElMessage.success('审核完成')
    dialogVisible.value = false
    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    reviewing.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.recruit-page {
  padding: 0;
}

.filter-bar {
  margin-bottom: 16px;
}

.review-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--admin-line);
}

.review-section h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--admin-text-strong);
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
