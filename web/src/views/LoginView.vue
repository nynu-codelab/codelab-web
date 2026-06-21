<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="card-title">登录</h2>
      <p class="card-subtitle">欢迎回到 Code Lab 实验室</p>

      <form class="login-form" @submit.prevent="handleSubmit">
        <div class="form-group">
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

        <div class="form-group">
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

        <p class="form-error" v-if="formError">{{ formError }}</p>

        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '登录中...' : '登录' }}
        </button>
      </form>

      <p class="switch-link">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </p>

      <p class="back-link">
        <router-link to="/">&larr; 返回首页</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
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

    const redirect = (route.query.redirect as string) || '/user'
    router.push(redirect)
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || '登录失败'
    formError.value = msg
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 50%, #0f1923 100%);
}

.login-card {
  width: 100%;
  max-width: 400px;
  background-color: #1a2a3a;
  border-radius: 12px;
  padding: 40px 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-title {
  font-size: 26px;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 8px;
}

.card-subtitle {
  font-size: 14px;
  color: #78909c;
  text-align: center;
  margin-bottom: 32px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  color: #b0bec5;
  font-weight: 500;
}

.form-group input {
  height: 44px;
  padding: 0 14px;
  border: 1px solid #37474f;
  border-radius: 8px;
  background-color: #0f1923;
  color: #e0e0e0;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input::placeholder {
  color: #546e7a;
}

.form-group input:focus {
  border-color: #64b5f6;
}

.field-error {
  font-size: 12px;
  color: #ef5350;
}

.form-error {
  font-size: 13px;
  color: #ef5350;
  text-align: center;
  background-color: rgba(239, 83, 80, 0.08);
  padding: 8px 12px;
  border-radius: 6px;
}

.btn-submit {
  height: 44px;
  border: none;
  border-radius: 8px;
  background-color: #64b5f6;
  color: #0d1b2a;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background-color: #90caf9;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.switch-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #78909c;
}

.switch-link a {
  color: #64b5f6;
  font-weight: 500;
}

.back-link {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
}

.back-link a {
  color: #546e7a;
  transition: color 0.2s;
}

.back-link a:hover {
  color: #b0bec5;
}
</style>
