<template>
  <div class="recruit-page">
    <h2 class="page-title">报名管理</h2>

    <el-card shadow="never">
      <el-table :data="[]" empty-text="暂无报名数据">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="direction" label="方向" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default>
            <el-tag type="info">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="报名时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="success" link size="small">通过</el-button>
            <el-button type="danger" link size="small">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// Placeholder — will be implemented when backend APIs are ready
</script>

<style scoped>
.recruit-page {
  padding: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}
</style>
