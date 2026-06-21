<template>
  <AuthShell
    eyebrow="Create Account"
    title="注册账号"
    description="先创建账号，再登录系统提交招新报名信息。"
  >
    <div class="auth-card__header">
      <h2>加入 NYNU Code Lab</h2>
      <p>请填写真实基础信息，用于报名与审核流程。</p>
    </div>

    <form class="form-panel" @submit.prevent="handleSubmit">
      <div class="form-field">
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

      <div class="form-grid">
        <div class="form-field">
          <label for="password">密码 <span class="required">*</span></label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="至少 6 位"
            autocomplete="new-password"
          />
          <span class="field-error" v-if="errors.password">{{ errors.password }}</span>
        </div>

        <div class="form-field">
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

      <div class="form-grid">
        <div class="form-field">
          <label for="realName">姓名 <span class="required">*</span></label>
          <input id="realName" v-model.trim="form.realName" type="text" placeholder="请输入真实姓名" />
          <span class="field-error" v-if="errors.realName">{{ errors.realName }}</span>
        </div>

        <div class="form-field">
          <label for="phone">手机号 <span class="required">*</span></label>
          <input id="phone" v-model.trim="form.phone" type="text" placeholder="请输入手机号" />
          <span class="field-error" v-if="errors.phone">{{ errors.phone }}</span>
        </div>
      </div>

      <div class="form-grid three">
        <div class="form-field">
          <label for="grade">年级 <span class="required">*</span></label>
          <select id="grade" v-model="form.grade">
            <option value="" disabled>请选择年级</option>
            <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
          </select>
          <span class="field-error" v-if="errors.grade">{{ errors.grade }}</span>
        </div>

        <div class="form-field">
          <label for="major">专业 <span class="required">*</span></label>
          <input id="major" v-model.trim="form.major" type="text" placeholder="请输入专业名称" />
          <span class="field-error" v-if="errors.major">{{ errors.major }}</span>
        </div>

        <div class="form-field">
          <label for="className">班级 <span class="required">*</span></label>
          <input id="className" v-model.trim="form.className" type="text" placeholder="请输入班级" />
          <span class="field-error" v-if="errors.className">{{ errors.className }}</span>
        </div>
      </div>

      <p class="form-alert error" v-if="formError">{{ formError }}</p>
      <p class="form-alert success" v-if="formSuccess">{{ formSuccess }}</p>

      <AppButton
        :label="submitting ? '注册中...' : '注册账号'"
        type="submit"
        size="lg"
        :disabled="submitting"
      />
    </form>

    <div class="auth-card__links">
      <RouterLink to="/login">已有账号，立即登录</RouterLink>
      <RouterLink to="/">返回首页</RouterLink>
    </div>
  </AuthShell>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthShell from '@/components/app/AuthShell.vue'
import AppButton from '@/components/app/AppButton.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const currentYear = new Date().getFullYear()
const gradeOptions = Array.from({ length: 4 }, (_, i) => `${currentYear - i}级`)

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
    await userStore.register({ ...form })
    formSuccess.value = '注册成功！正在跳转到登录页...'
    setTimeout(() => {
      router.push('/login')
    }, 1200)
  } catch (err: any) {
    formError.value = err?.response?.data?.message || err?.message || '注册失败'
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

.form-grid.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
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

@media (max-width: 760px) {
  .form-grid.three {
    grid-template-columns: 1fr;
  }
}
</style>
