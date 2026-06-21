<template>
  <div class="dashboard admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Overview</span>
        <h2 class="admin-page-title">数据概览</h2>
        <p class="admin-page-copy">当前统计接口尚未实现，因此保持占位，不编造注册人数、报名人数或内容数量。</p>
      </div>
    </div>

    <div class="stats-grid">
      <el-card shadow="hover" class="stat-card" v-for="item in stats" :key="item.label">
        <div class="stat-content">
          <div class="stat-icon" :class="item.className">
            <el-icon :size="30">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">-</p>
            <p class="stat-label">{{ item.label }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="dashboard-panels">
      <el-card shadow="never">
        <div class="panel-title">当前可管理内容</div>
        <div class="pipeline-list">
          <span>报名审核</span>
          <span>文章草稿/发布/下架</span>
          <span>项目草稿/发布/精选</span>
        </div>
      </el-card>
      <el-card shadow="never">
        <div class="panel-title">待接入模块</div>
        <div class="pipeline-list muted">
          <span>成员管理</span>
          <span>方向管理</span>
          <span>站点配置</span>
          <span>文件上传</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Clock, Document, Reading, User } from "@element-plus/icons-vue";

const stats = [
  { label: "注册用户数", icon: User, className: "user-icon" },
  { label: "报名人数", icon: Document, className: "recruit-icon" },
  { label: "待审核人数", icon: Clock, className: "pending-icon" },
  { label: "文章数量", icon: Reading, className: "article-icon" },
];
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 18px;
  color: #041017;
}

.user-icon {
  background: linear-gradient(135deg, var(--admin-cyan), var(--admin-blue));
}

.recruit-icon {
  background: linear-gradient(135deg, var(--admin-teal), #7df5cf);
}

.pending-icon {
  background: linear-gradient(135deg, var(--admin-amber), #fff0a8);
}

.article-icon {
  background: linear-gradient(135deg, #a98bff, var(--admin-cyan));
}

.stat-value {
  color: var(--admin-text-strong);
  font-family: var(--admin-font-data);
  font-size: 32px;
  font-weight: 820;
  line-height: 1;
}

.stat-label {
  margin-top: 7px;
  color: var(--admin-muted);
}

.dashboard-panels {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.panel-title {
  margin-bottom: 14px;
  color: var(--admin-text-strong);
  font-size: 18px;
  font-weight: 720;
}

.pipeline-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pipeline-list span {
  padding: 8px 11px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 999px;
  color: var(--admin-cyan);
  background: rgba(83, 231, 255, 0.07);
}

.pipeline-list.muted span {
  color: var(--admin-muted);
  background: rgba(255, 255, 255, 0.045);
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .stats-grid,
  .dashboard-panels {
    grid-template-columns: 1fr;
  }
}
</style>
