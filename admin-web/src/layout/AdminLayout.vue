<template>
  <el-container class="layout-container">
    <div class="layout-signal" aria-hidden="true"></div>
    <div class="layout-status-rail" aria-hidden="true">
      <span>AUTH</span>
      <span>CMS</span>
      <span>RECRUIT</span>
    </div>
    <el-aside :width="isCollapse ? '78px' : '246px'" class="layout-aside">
      <button class="aside-logo" type="button" @click="toggleCollapse">
        <span class="logo-mark">
          <img :src="brandMark" alt="" aria-hidden="true" />
        </span>
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
          <div class="header-command" aria-label="后台运行状态">
            <span class="header-command__dot"></span>
            <code>admin.guard --role ADMIN</code>
            <small>LIVE</small>
          </div>
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="34" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || "管理员" }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showChangePwdDialog = true">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
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

  <!-- 修改密码弹层 -->
  <el-dialog v-model="showChangePwdDialog" title="修改密码" width="420px" :close-on-click-modal="false">
    <el-form
      ref="pwdFormRef"
      :model="pwdForm"
      :rules="pwdRules"
      label-width="0"
      @submit.prevent="handleChangePassword"
    >
      <el-form-item prop="oldPassword">
        <el-input
          v-model="pwdForm.oldPassword"
          type="password"
          placeholder="旧密码"
          show-password
          size="large"
        />
      </el-form-item>
      <el-form-item prop="newPassword">
        <el-input
          v-model="pwdForm.newPassword"
          type="password"
          placeholder="新密码（至少 6 位）"
          show-password
          size="large"
        />
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="pwdForm.confirmPassword"
          type="password"
          placeholder="确认新密码"
          show-password
          size="large"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showChangePwdDialog = false">取消</el-button>
      <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">
        确认修改
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import {
  ArrowDown,
  Collection,
  Connection,
  DataAnalysis,
  Document,
  Expand,
  FolderOpened,
  Fold,
  Lock,
  Reading,
  Setting,
  SwitchButton,
  Upload,
  User,
  UserFilled,
} from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { changePassword } from "@/api/auth";
import brandMark from "@/assets/brand/nynu-code-lab-mark.svg";

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

// --------------- 修改密码 ---------------
const showChangePwdDialog = ref(false);
const pwdLoading = ref(false);
const pwdFormRef = ref<FormInstance>();

const pwdForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error("两次输入的新密码不一致"));
  } else {
    callback();
  }
};

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "新密码长度不能少于 6 位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { validator: validateConfirm, trigger: "blur" },
  ],
};

async function handleChangePassword() {
  if (!pwdFormRef.value) return;
  const valid = await pwdFormRef.value.validate().catch(() => false);
  if (!valid) return;

  pwdLoading.value = true;
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
      confirmPassword: pwdForm.confirmPassword,
    });
    ElMessage.success("密码修改成功，请重新登录");
    showChangePwdDialog.value = false;
    pwdForm.oldPassword = "";
    pwdForm.newPassword = "";
    pwdForm.confirmPassword = "";
    // 密码修改成功后需重新登录
    userStore.logout();
    router.push({ name: "Login" });
  } catch (e: any) {
    const msg = e?.response?.data?.message || e?.message || "修改失败";
    ElMessage.error(msg);
  } finally {
    pwdLoading.value = false;
  }
}
</script>

<style scoped>
.layout-container {
  position: relative;
  height: 100vh;
  color: var(--admin-text);
  background:
    radial-gradient(circle at 16% 8%, rgba(83, 231, 255, 0.16), transparent 30rem),
    radial-gradient(circle at 88% 12%, rgba(169, 139, 255, 0.13), transparent 30rem),
    linear-gradient(135deg, var(--admin-bg), var(--admin-bg-2) 54%, #05070c);
}

.layout-signal {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(115deg, transparent 16%, rgba(83, 231, 255, 0.08), transparent 58%),
    linear-gradient(rgba(83, 231, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.035) 1px, transparent 1px);
  background-size: auto, 76px 76px, 76px 76px;
  mask-image: radial-gradient(circle at 62% 18%, black, transparent 72%);
}

.layout-status-rail {
  position: fixed;
  z-index: 2;
  right: 18px;
  top: 96px;
  display: grid;
  gap: 10px;
  pointer-events: none;
}

.layout-status-rail span {
  writing-mode: vertical-rl;
  padding: 9px 6px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: 999px;
  color: rgba(199, 215, 232, 0.58);
  background: rgba(4, 10, 18, 0.58);
  box-shadow: 0 0 24px rgba(83, 231, 255, 0.05);
  font-family: var(--admin-font-data);
  font-size: 10px;
  letter-spacing: 0;
}

.layout-aside {
  position: relative;
  z-index: 1;
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
  position: relative;
  isolation: isolate;
  overflow: hidden;
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  border: 1px solid rgba(83, 231, 255, 0.28);
  border-radius: 14px;
  background: rgba(4, 10, 18, 0.78);
  box-shadow:
    0 0 32px rgba(83, 231, 255, 0.18),
    inset 0 0 24px rgba(83, 231, 255, 0.06);
}

.logo-mark::after {
  content: "";
  position: absolute;
  inset: -40% -70%;
  z-index: 1;
  background: linear-gradient(115deg, transparent 40%, rgba(255, 255, 255, 0.26), transparent 60%);
  transform: translateX(-72%) rotate(8deg);
  transition: transform 520ms ease;
}

.aside-logo:hover .logo-mark::after {
  transform: translateX(72%) rotate(8deg);
}

.logo-mark img {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: block;
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
  position: relative;
  z-index: 1;
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-command {
  display: inline-grid;
  grid-template-columns: 8px minmax(0, auto) auto;
  gap: 9px;
  align-items: center;
  min-height: 38px;
  padding: 0 12px;
  border: 1px solid rgba(153, 217, 255, 0.15);
  border-radius: 999px;
  color: var(--admin-muted);
  background: rgba(255, 255, 255, 0.045);
  box-shadow: inset 0 0 20px rgba(83, 231, 255, 0.035);
}

.header-command__dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--admin-teal);
  box-shadow: 0 0 16px rgba(47, 240, 182, 0.62);
}

.header-command code {
  max-width: 240px;
  overflow: hidden;
  color: rgba(216, 247, 255, 0.88);
  font-family: var(--admin-font-data);
  font-size: 11px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-command small {
  color: var(--admin-teal);
  font-family: var(--admin-font-data);
  font-size: 10px;
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

  .layout-status-rail,
  .header-command {
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
