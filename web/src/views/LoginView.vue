<template>
  <AuthShell
    eyebrow="Account Access"
    title="登录 Code Lab"
    description="进入个人中心、提交招新报名并查看审核状态。"
  >
    <div class="auth-card__header">
      <h2>账号登录</h2>
      <p>欢迎回到南阳师范学院 Code Lab 实验室</p>
    </div>

    <form class="form-panel" @submit.prevent="handleSubmit">
      <div class="form-field">
        <label for="username">用户名</label>
        <input
          id="username"
          v-model.trim="form.username"
          type="text"
          placeholder="请输入用户名"
          autocomplete="username"
        />
        <span class="field-error" v-if="errors.username">{{ errors.username }}</span>
      </div>

      <div class="form-field">
        <label for="password">密码</label>
        <input
          id="password"
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          autocomplete="current-password"
        />
        <span class="field-error" v-if="errors.password">{{ errors.password }}</span>
      </div>

      <p class="form-alert error" v-if="formError">{{ formError }}</p>

      <AppButton
        :label="submitting ? '登录中...' : '登录'"
        type="submit"
        size="lg"
        :disabled="submitting"
      />
    </form>

    <div class="auth-card__links">
      <RouterLink to="/register">还没有账号，立即注册</RouterLink>
      <RouterLink to="/">返回首页</RouterLink>
    </div>
  </AuthShell>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthShell from '@/components/app/AuthShell.vue'
import AppButton from '@/components/app/AppButton.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const formError = ref('')
const submitting = ref(false)

function validate(): boolean {
  let valid = true
  errors.username = ''
  errors.password = ''

  if (!form.username) {
    errors.username = '请输入用户名'
    valid = false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    valid = false
  }

  return valid
}

async function handleSubmit() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password
    })

    const redirect = (route.query.redirect as string) || '/profile'
    router.push(redirect)
  } catch (err: any) {
    formError.value = err?.response?.data?.message || err?.message || '登录失败'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-card__header {
  margin-bottom: 24px;
}

.auth-card__header h2 {
  color: var(--app-text-strong);
  font-size: 28px;
}

.auth-card__header p {
  margin-top: 8px;
  color: var(--app-muted);
}

.auth-card__links {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px;
  margin-top: 22px;
  color: var(--app-muted);
  font-size: 14px;
}

.auth-card__links a:hover {
  color: var(--app-cyan);
}
</style>
