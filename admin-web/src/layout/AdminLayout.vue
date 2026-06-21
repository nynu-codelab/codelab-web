<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '78px' : '246px'" class="layout-aside">
      <button class="aside-logo" type="button" @click="toggleCollapse">
        <span class="logo-mark">CL</span>
        <span v-show="!isCollapse" class="logo-text">
          <strong>NYNU Code Lab</strong>
          <small>Admin Console</small>
        </span>
      </button>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/recruit">
          <el-icon><Document /></el-icon>
          <template #title>报名管理</template>
        </el-menu-item>
        <el-menu-item index="/articles">
          <el-icon><Reading /></el-icon>
          <template #title>文章管理</template>
        </el-menu-item>
        <el-menu-item index="/projects">
          <el-icon><FolderOpened /></el-icon>
          <template #title>项目管理</template>
        </el-menu-item>
        <el-menu-item index="/members">
          <el-icon><Collection /></el-icon>
          <template #title>成员管理</template>
        </el-menu-item>
        <el-menu-item index="/directions">
          <el-icon><Connection /></el-icon>
          <template #title>方向管理</template>
        </el-menu-item>
        <el-menu-item index="/site">
          <el-icon><Setting /></el-icon>
          <template #title>站点配置</template>
        </el-menu-item>
        <el-menu-item index="/upload">
          <el-icon><Upload /></el-icon>
          <template #title>文件上传</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="layout-content">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <div>
            <span class="header-kicker">Management Workspace</span>
            <strong>{{ route.meta.title || "后台管理" }}</strong>
          </div>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="34" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || "管理员" }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ArrowDown,
  Collection,
  Connection,
  DataAnalysis,
  Document,
  Expand,
  FolderOpened,
  Fold,
  Reading,
  Setting,
  SwitchButton,
  Upload,
  User,
  UserFilled,
} from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isCollapse = ref(false);
const activeMenu = computed(() => route.path);

function toggleCollapse() {
  isCollapse.value = !isCollapse.value;
}

function handleLogout() {
  userStore.logout();
  router.push({ name: "Login" });
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  color: var(--admin-text);
  background:
    radial-gradient(circle at 16% 8%, rgba(83, 231, 255, 0.16), transparent 30rem),
    radial-gradient(circle at 88% 12%, rgba(169, 139, 255, 0.13), transparent 30rem),
    linear-gradient(135deg, var(--admin-bg), var(--admin-bg-2) 54%, #05070c);
}

.layout-aside {
  position: relative;
  overflow: hidden;
  border-right: 1px solid var(--admin-line);
  background: rgba(6, 11, 20, 0.82);
  box-shadow: 18px 0 60px rgba(0, 0, 0, 0.24);
  transition: width 0.3s ease;
}

.layout-aside::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(rgba(83, 231, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.05) 1px, transparent 1px);
  background-size: 34px 34px;
  mask-image: linear-gradient(180deg, #000, transparent 88%);
}

.aside-logo {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  min-height: 74px;
  padding: 0 18px;
  border: 0;
  border-bottom: 1px solid var(--admin-line);
  color: var(--admin-text);
  background: rgba(255, 255, 255, 0.035);
  cursor: pointer;
  text-align: left;
}

.logo-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  border: 1px solid rgba(83, 231, 255, 0.45);
  border-radius: 14px;
  color: #041017;
  background: linear-gradient(135deg, var(--admin-cyan), var(--admin-teal));
  font-family: var(--admin-font-data);
  font-weight: 860;
  box-shadow: 0 0 32px rgba(83, 231, 255, 0.22);
}

.logo-text strong,
.logo-text small {
  display: block;
}

.logo-text strong {
  color: var(--admin-text-strong);
  font-size: 15px;
}

.logo-text small {
  margin-top: 2px;
  color: var(--admin-muted);
  font-family: var(--admin-font-data);
  font-size: 11px;
}

.layout-aside :deep(.el-menu) {
  position: relative;
  z-index: 1;
  border-right: 0;
  background: transparent;
}

.layout-aside :deep(.el-menu-item) {
  height: 48px;
  margin: 6px 12px;
  border-radius: 14px;
  color: #c4d2e1;
}

.layout-aside :deep(.el-menu-item:hover),
.layout-aside :deep(.el-menu-item.is-active) {
  color: var(--admin-cyan);
  background: rgba(83, 231, 255, 0.1);
}

.layout-content {
  min-width: 0;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 74px;
  padding: 0 24px;
  border-bottom: 1px solid var(--admin-line);
  background: rgba(6, 9, 15, 0.7);
  backdrop-filter: blur(18px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.collapse-btn {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border: 1px solid rgba(153, 217, 255, 0.18);
  border-radius: 999px;
  color: var(--admin-text);
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
}

.collapse-btn:hover {
  color: var(--admin-cyan);
  border-color: rgba(83, 231, 255, 0.5);
}

.header-kicker {
  display: block;
  color: var(--admin-cyan);
  font-family: var(--admin-font-data);
  font-size: 11px;
}

.header-left strong {
  display: block;
  color: var(--admin-text-strong);
  font-size: 18px;
}

.user-info {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 42px;
  padding: 4px 10px 4px 4px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 999px;
  color: var(--admin-text);
  background: rgba(255, 255, 255, 0.055);
  cursor: pointer;
}

.username {
  max-width: 160px;
  overflow: hidden;
  font-size: 14px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.layout-main {
  min-width: 0;
  padding: 24px;
  overflow-y: auto;
}

@media (max-width: 760px) {
  .layout-aside {
    display: none;
  }

  .layout-header {
    padding: 0 14px;
  }

  .layout-main {
    padding: 16px;
  }
}
</style>
