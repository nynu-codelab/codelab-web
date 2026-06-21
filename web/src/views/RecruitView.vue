<template>
  <AppFrame>
    <PageHero
      eyebrow="Recruit"
      title="招新报名"
      description="注册账号、登录系统、填写报名信息、提交报名、管理员审核、用户查看审核状态。"
    >
      <template #actions>
        <AppButton label="查看我的报名" to="/my-application" variant="secondary" />
        <AppButton label="了解技术方向" to="/directions" variant="ghost" />
      </template>
    </PageHero>

    <main class="apply-page app-container narrow">
      <StateView
        v-if="submitted"
        title="报名提交成功"
        message="你的报名信息已提交，实验室管理员将尽快审核。后续可在我的报名中查看审核状态。"
      >
        <template #actions>
          <AppButton label="查看我的报名" to="/my-application" />
          <AppButton label="返回首页" to="/" variant="secondary" />
        </template>
      </StateView>

      <form v-else class="apply-form glass-card" @submit.prevent="handleSubmit">
        <div class="apply-form__header">
          <span class="app-eyebrow">Application Form</span>
          <h2>填写报名信息</h2>
          <p>请尽量真实、简洁地描述你的技术基础、投入时间和加入原因。</p>
        </div>

        <fieldset class="apply-section">
          <legend>基本信息</legend>
          <div class="form-grid">
            <div class="form-field">
              <label>姓名 <span class="required">*</span></label>
              <input v-model.trim="form.realName" type="text" placeholder="请输入真实姓名" />
              <span class="field-error" v-if="errors.realName">{{ errors.realName }}</span>
            </div>
            <div class="form-field">
              <label>年级 <span class="required">*</span></label>
              <select v-model="form.grade">
                <option value="" disabled>请选择年级</option>
                <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
              </select>
              <span class="field-error" v-if="errors.grade">{{ errors.grade }}</span>
            </div>
            <div class="form-field">
              <label>专业 <span class="required">*</span></label>
              <input v-model.trim="form.major" type="text" placeholder="请输入专业名称" />
              <span class="field-error" v-if="errors.major">{{ errors.major }}</span>
            </div>
            <div class="form-field">
              <label>班级 <span class="required">*</span></label>
              <input v-model.trim="form.className" type="text" placeholder="请输入班级" />
              <span class="field-error" v-if="errors.className">{{ errors.className }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="apply-section">
          <legend>联系方式</legend>
          <div class="form-grid">
            <div class="form-field">
              <label>手机号 <span class="required">*</span></label>
              <input v-model.trim="form.phone" type="text" placeholder="请输入手机号" />
              <span class="field-error" v-if="errors.phone">{{ errors.phone }}</span>
            </div>
            <div class="form-field">
              <label>QQ号 <span class="required">*</span></label>
              <input v-model.trim="form.qq" type="text" placeholder="请输入 QQ 号" />
              <span class="field-error" v-if="errors.qq">{{ errors.qq }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="apply-section">
          <legend>技术背景</legend>
          <div class="form-field">
            <label>意向技术方向 <span class="required">*</span></label>
            <select v-model="form.direction">
              <option value="" disabled>请选择意向方向</option>
              <option v-for="item in directionOptions" :key="item" :value="item">{{ item }}</option>
            </select>
            <span class="field-error" v-if="errors.direction">{{ errors.direction }}</span>
          </div>

          <div class="form-field">
            <label>是否有编程基础</label>
            <div class="radio-group">
              <label>
                <input type="radio" v-model.number="form.hasProgrammingBasis" :value="1" />
                <span>有基础</span>
              </label>
              <label>
                <input type="radio" v-model.number="form.hasProgrammingBasis" :value="0" />
                <span>暂时没有</span>
              </label>
            </div>
          </div>

          <div class="form-field">
            <label>已掌握技术</label>
            <textarea v-model="form.skills" rows="2" placeholder="例如：HTML/CSS、JavaScript、Python、Java 等"></textarea>
          </div>
        </fieldset>

        <fieldset class="apply-section">
          <legend>个人陈述</legend>
          <div class="form-field">
            <label>个人介绍</label>
            <textarea v-model="form.introduction" rows="3" placeholder="请简要介绍自己"></textarea>
          </div>
          <div class="form-field">
            <label>加入实验室的原因</label>
            <textarea v-model="form.reason" rows="3" placeholder="请说明为什么想加入 Code Lab 实验室"></textarea>
          </div>
          <div class="form-grid">
            <div class="form-field">
              <label>每周可投入时间</label>
              <input v-model.trim="form.weeklyAvailableTime" type="text" placeholder="例如：10 小时以上" />
            </div>
            <div class="form-field">
              <label>项目/作品链接</label>
              <input v-model.trim="form.portfolioUrl" type="text" placeholder="GitHub / 个人网站 / 作品链接" />
            </div>
          </div>
        </fieldset>

        <p class="form-alert error" v-if="formError">{{ formError }}</p>

        <AppButton
          :label="submitting ? '提交中...' : '提交报名'"
          type="submit"
          size="lg"
          :disabled="submitting"
        />
      </form>
    </main>
  </AppFrame>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
import { submitApply } from '@/api/application'

const currentYear = new Date().getFullYear()
const gradeOptions = Array.from({ length: 4 }, (_, i) => `${currentYear - i}级`)
const directionOptions = [
  'Java 后端',
  '前端开发',
  '微信小程序',
  '人工智能（拓展方向）',
  '数据库与运维（拓展方向）',
  '其它'
]

const form = reactive({
  realName: '',
  grade: '',
  major: '',
  className: '',
  phone: '',
  qq: '',
  direction: '',
  hasProgrammingBasis: 0,
  skills: '',
  introduction: '',
  reason: '',
  weeklyAvailableTime: '',
  portfolioUrl: ''
})

const errors = reactive<Record<string, string>>({})
const formError = ref('')
const submitting = ref(false)
const submitted = ref(false)

function validate(): boolean {
  let valid = true
  for (const key of Object.keys(errors)) delete errors[key]

  if (!form.realName) { errors.realName = '请输入姓名'; valid = false }
  if (!form.grade) { errors.grade = '请选择年级'; valid = false }
  if (!form.major) { errors.major = '请输入专业'; valid = false }
  if (!form.className) { errors.className = '请输入班级'; valid = false }
  if (!form.phone) { errors.phone = '请输入手机号'; valid = false }
  else if (!/^1[3-9]\d{9}$/.test(form.phone)) { errors.phone = '手机号格式不正确'; valid = false }
  if (!form.qq) { errors.qq = '请输入 QQ 号'; valid = false }
  if (!form.direction) { errors.direction = '请选择意向技术方向'; valid = false }

  return valid
}

async function handleSubmit() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await submitApply({ ...form })
    submitted.value = true
  } catch (err: any) {
    formError.value = err?.response?.data?.message || err?.message || '提交失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.apply-page {
  padding: 0 0 92px;
}

.apply-form {
  display: grid;
  gap: 24px;
  padding: clamp(24px, 5vw, 44px);
}

.apply-form__header h2 {
  color: var(--app-text-strong);
  font-size: 30px;
}

.apply-form__header p {
  margin-top: 8px;
  color: var(--app-muted);
}

.apply-section {
  display: grid;
  gap: 18px;
  min-width: 0;
  padding: 22px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius);
}

.apply-section legend {
  padding: 0 10px;
  color: var(--app-cyan);
  font-weight: 720;
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.radio-group label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 40px;
  padding: 8px 12px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 999px;
  color: var(--app-soft);
  background: rgba(255, 255, 255, 0.045);
}

.radio-group input {
  accent-color: var(--app-cyan);
}
</style>
