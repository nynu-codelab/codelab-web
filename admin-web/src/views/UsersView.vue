<template>
  <div class="users-page admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Users</span>
        <h2 class="admin-page-title">用户管理</h2>
        <p class="admin-page-copy">用户管理接口尚未实现，当前保留占位表格，不接入虚假数据。</p>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="[]" empty-text="暂无用户数据">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// Placeholder: backend user management APIs are not implemented yet.
</script>
