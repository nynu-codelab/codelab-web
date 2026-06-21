<template>
  <div class="login-container">
    <div class="login-backdrop"></div>
    <el-card class="login-card" shadow="always">
      <template #header>
        <div class="login-header">
          <h1 class="login-title">南阳师范学院 Code Lab 实验室</h1>
          <p class="login-subtitle">后台管理系统</p>
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
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
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
    // Error message is handled by the interceptor
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  overflow: hidden;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #1e3c72 100%);
}

.login-card {
  position: relative;
  width: 420px;
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.95);
}

.login-header {
  text-align: center;
  padding: 8px 0;
}

.login-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e3c72;
  margin-bottom: 4px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 0;
}

.login-btn {
  width: 100%;
}
</style>
