<template>
  <div class="login-container">
    <div class="login-visual">
      <span class="admin-page-kicker">NYNU Code Lab</span>
      <h1>南阳师范学院 Code Lab 实验室</h1>
      <p>后台管理系统用于维护招新报名、学习文章、项目成果和后续站点内容。</p>
      <div class="terminal-panel">
        <div>
          <span></span>
          <span></span>
          <span></span>
        </div>
        <code>admin-console --secure --role ADMIN</code>
      </div>
    </div>

    <el-card class="login-card" shadow="always">
      <template #header>
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>仅 ADMIN 角色可访问后台</p>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登录后台
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { Lock, User } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: "",
  password: "",
});

const rules: FormRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

async function handleLogin() {
  if (!formRef.value) return;

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.login(form.username, form.password);

    if (userStore.userInfo?.role !== "ADMIN") {
      userStore.logout();
      ElMessage.warning("您没有管理员权限");
      return;
    }

    ElMessage.success("登录成功");
    const redirect = (route.query.redirect as string) || "/dashboard";
    router.push(redirect);
  } catch {
    // Error message is handled by the interceptor.
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(360px, 0.48fr);
  gap: 48px;
  align-items: center;
  min-height: 100vh;
  padding: clamp(24px, 6vw, 72px);
  overflow: hidden;
  background:
    linear-gradient(rgba(83, 231, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.05) 1px, transparent 1px),
    radial-gradient(circle at 18% 12%, rgba(83, 231, 255, 0.22), transparent 30rem),
    radial-gradient(circle at 80% 12%, rgba(169, 139, 255, 0.16), transparent 30rem),
    linear-gradient(135deg, var(--admin-bg), var(--admin-bg-2) 55%, #05070c);
  background-size: 34px 34px, 34px 34px, auto, auto, auto;
}

.login-visual {
  position: relative;
  z-index: 1;
}

.login-visual h1 {
  max-width: 780px;
  color: var(--admin-text-strong);
  font-size: 78px;
  line-height: 1.04;
}

.login-visual p {
  max-width: 620px;
  margin-top: 22px;
  color: var(--admin-muted);
  font-size: 18px;
  line-height: 1.8;
}

.terminal-panel {
  display: grid;
  gap: 16px;
  max-width: 560px;
  margin-top: 34px;
  padding: 20px;
  border: 1px solid var(--admin-line);
  border-radius: var(--admin-radius);
  background: rgba(4, 10, 18, 0.68);
  box-shadow: var(--admin-shadow);
  backdrop-filter: blur(18px);
}

.terminal-panel div {
  display: flex;
  gap: 8px;
}

.terminal-panel span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--admin-danger);
}

.terminal-panel span:nth-child(2) {
  background: var(--admin-amber);
}

.terminal-panel span:nth-child(3) {
  background: var(--admin-teal);
}

.terminal-panel code {
  color: var(--admin-cyan);
  font-family: var(--admin-font-data);
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
}

.login-header h2 {
  color: var(--admin-text-strong);
  font-size: 26px;
}

.login-header p {
  margin-top: 6px;
  color: var(--admin-muted);
}

.login-btn {
  width: 100%;
}

@media (max-width: 900px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .login-visual h1 {
    font-size: 42px;
  }
}
</style>
