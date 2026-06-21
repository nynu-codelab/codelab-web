<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="card-title">注册</h2>
      <p class="card-subtitle">加入 Code Lab 实验室</p>

      <form class="register-form" @submit.prevent="handleSubmit">
        <div class="form-row">
          <div class="form-group">
            <label for="username">用户名 <span class="required">*</span></label>
            <input
              id="username"
              v-model.trim="form.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
            />
            <span class="field-error" v-if="errors.username">{{ errors.username }}</span>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="password">密码 <span class="required">*</span></label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              autocomplete="new-password"
            />
            <span class="field-error" v-if="errors.password">{{ errors.password }}</span>
          </div>

          <div class="form-group">
            <label for="confirmPassword">确认密码 <span class="required">*</span></label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="new-password"
            />
            <span class="field-error" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="realName">姓名 <span class="required">*</span></label>
            <input
              id="realName"
              v-model.trim="form.realName"
              type="text"
              placeholder="请输入真实姓名"
            />
            <span class="field-error" v-if="errors.realName">{{ errors.realName }}</span>
          </div>

          <div class="form-group">
            <label for="phone">手机号 <span class="required">*</span></label>
            <input
              id="phone"
              v-model.trim="form.phone"
              type="text"
              placeholder="请输入手机号"
            />
            <span class="field-error" v-if="errors.phone">{{ errors.phone }}</span>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="grade">年级 <span class="required">*</span></label>
            <select id="grade" v-model="form.grade">
              <option value="" disabled>请选择年级</option>
              <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
            </select>
            <span class="field-error" v-if="errors.grade">{{ errors.grade }}</span>
          </div>

          <div class="form-group">
            <label for="major">专业 <span class="required">*</span></label>
            <input
              id="major"
              v-model.trim="form.major"
              type="text"
              placeholder="请输入专业名称"
            />
            <span class="field-error" v-if="errors.major">{{ errors.major }}</span>
          </div>

          <div class="form-group">
            <label for="className">班级 <span class="required">*</span></label>
            <input
              id="className"
              v-model.trim="form.className"
              type="text"
              placeholder="请输入班级"
            />
            <span class="field-error" v-if="errors.className">{{ errors.className }}</span>
          </div>
        </div>

        <p class="form-error" v-if="formError">{{ formError }}</p>
        <p class="form-success" v-if="formSuccess">{{ formSuccess }}</p>

        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '注册中...' : '注册' }}
        </button>
      </form>

      <p class="switch-link">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </p>

      <p class="back-link">
        <router-link to="/">&larr; 返回首页</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const currentYear = new Date().getFullYear()
const gradeOptions = Array.from(
  { length: 4 },
  (_, i) => `${currentYear - i}级`
)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  grade: '',
  major: '',
  className: ''
})

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  grade: '',
  major: '',
  className: ''
})

const formError = ref('')
const formSuccess = ref('')
const submitting = ref(false)

function validate(): boolean {
  let valid = true
  // Reset all errors
  for (const key of Object.keys(errors) as (keyof typeof errors)[]) {
    errors[key] = ''
  }

  if (!form.username) {
    errors.username = '请输入用户名'
    valid = false
  } else if (form.username.length < 3) {
    errors.username = '用户名至少3个字符'
    valid = false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少6位'
    valid = false
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    valid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次密码输入不一致'
    valid = false
  }

  if (!form.realName) {
    errors.realName = '请输入姓名'
    valid = false
  }

  if (!form.phone) {
    errors.phone = '请输入手机号'
    valid = false
  } else if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    errors.phone = '手机号格式不正确'
    valid = false
  }

  if (!form.grade) {
    errors.grade = '请选择年级'
    valid = false
  }

  if (!form.major) {
    errors.major = '请输入专业'
    valid = false
  }

  if (!form.className) {
    errors.className = '请输入班级'
    valid = false
  }

  return valid
}

async function handleSubmit() {
  formError.value = ''
  formSuccess.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      confirmPassword: form.confirmPassword,
      realName: form.realName,
      phone: form.phone,
      grade: form.grade,
      major: form.major,
      className: form.className
    })

    formSuccess.value = '注册成功！正在跳转到登录页...'
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || '注册失败'
    formError.value = msg
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 50%, #0f1923 100%);
}

.register-card {
  width: 100%;
  max-width: 560px;
  background-color: #1a2a3a;
  border-radius: 12px;
  padding: 40px 36px;
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

.register-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row > .form-group {
  flex: 1;
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

.required {
  color: #ef5350;
}

.form-group input,
.form-group select {
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

.form-group input:focus,
.form-group select:focus {
  border-color: #64b5f6;
}

.form-group select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%2378909c' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  cursor: pointer;
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

.form-success {
  font-size: 13px;
  color: #66bb6a;
  text-align: center;
  background-color: rgba(102, 187, 106, 0.08);
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
  margin-top: 4px;
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

@media (max-width: 480px) {
  .form-row {
    flex-direction: column;
  }
}
</style>
