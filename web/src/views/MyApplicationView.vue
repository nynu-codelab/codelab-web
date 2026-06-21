<template>
  <AppFrame>
    <PageHero
      eyebrow="My Application"
      title="我的报名"
      description="查看招新报名记录、审核备注与当前状态。待审核状态下可修改报名信息。"
    >
      <template #actions>
        <AppButton label="返回个人中心" to="/profile" variant="secondary" />
      </template>
    </PageHero>

    <main class="application-page app-container narrow">
      <StateView
        v-if="loading"
        title="正在加载报名记录"
        message="正在读取你的报名信息。"
      />

      <StateView
        v-else-if="!record && !loading"
        title="暂无报名记录"
        message="你还没有提交过招新报名，可以进入报名页提交信息。"
      >
        <template #actions>
          <AppButton label="立即报名" to="/recruit" />
        </template>
      </StateView>

      <article v-else-if="record && !editing" class="application-card glass-card">
        <div class="application-card__header">
          <div>
            <span class="app-eyebrow">Application Status</span>
            <h2>{{ record.realName }} 的报名记录</h2>
          </div>
          <span class="status-pill" :class="statusClass(record.status)">{{ statusText }}</span>
        </div>

        <div class="review-note" v-if="record.reviewRemark">
          <strong>审核备注</strong>
          <p>{{ record.reviewRemark }}</p>
        </div>

        <div class="info-groups">
          <section>
            <h3>基本信息</h3>
            <dl>
              <div><dt>姓名</dt><dd>{{ record.realName }}</dd></div>
              <div><dt>年级</dt><dd>{{ record.grade }}</dd></div>
              <div><dt>专业</dt><dd>{{ record.major }}</dd></div>
              <div><dt>班级</dt><dd>{{ record.className }}</dd></div>
            </dl>
          </section>
          <section>
            <h3>联系方式</h3>
            <dl>
              <div><dt>手机号</dt><dd>{{ record.phone }}</dd></div>
              <div><dt>QQ号</dt><dd>{{ record.qq }}</dd></div>
            </dl>
          </section>
          <section>
            <h3>技术背景</h3>
            <dl>
              <div><dt>意向方向</dt><dd>{{ record.direction }}</dd></div>
              <div><dt>编程基础</dt><dd>{{ record.hasProgrammingBasis ? '有' : '无' }}</dd></div>
              <div v-if="record.skills" class="wide"><dt>已掌握技术</dt><dd>{{ record.skills }}</dd></div>
            </dl>
          </section>
          <section>
            <h3>个人陈述</h3>
            <dl>
              <div v-if="record.introduction" class="wide"><dt>个人介绍</dt><dd>{{ record.introduction }}</dd></div>
              <div v-if="record.reason" class="wide"><dt>加入原因</dt><dd>{{ record.reason }}</dd></div>
              <div><dt>每周可投入时间</dt><dd>{{ record.weeklyAvailableTime || '-' }}</dd></div>
              <div><dt>项目/作品链接</dt><dd>{{ record.portfolioUrl || '-' }}</dd></div>
            </dl>
          </section>
        </div>

        <div class="application-card__footer">
          <p>提交时间：{{ formatDate(record.createTime) }}</p>
          <AppButton
            v-if="record.status === 'PENDING'"
            label="修改报名信息"
            variant="secondary"
            @click="startEdit"
          />
        </div>
      </article>

      <form v-else class="application-card glass-card" @submit.prevent="handleUpdate">
        <div class="application-card__header">
          <div>
            <span class="app-eyebrow">Edit Application</span>
            <h2>修改报名信息</h2>
          </div>
          <span class="status-pill warning">待审核</span>
        </div>

        <fieldset class="edit-section">
          <legend>基本信息</legend>
          <div class="form-grid">
            <div class="form-field">
              <label>姓名 <span class="required">*</span></label>
              <input v-model.trim="editForm.realName" type="text" />
              <span class="field-error" v-if="err.realName">{{ err.realName }}</span>
            </div>
            <div class="form-field">
              <label>年级 <span class="required">*</span></label>
              <select v-model="editForm.grade">
                <option value="" disabled>请选择年级</option>
                <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
              </select>
              <span class="field-error" v-if="err.grade">{{ err.grade }}</span>
            </div>
            <div class="form-field">
              <label>专业 <span class="required">*</span></label>
              <input v-model.trim="editForm.major" type="text" />
              <span class="field-error" v-if="err.major">{{ err.major }}</span>
            </div>
            <div class="form-field">
              <label>班级 <span class="required">*</span></label>
              <input v-model.trim="editForm.className" type="text" />
              <span class="field-error" v-if="err.className">{{ err.className }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="edit-section">
          <legend>联系方式</legend>
          <div class="form-grid">
            <div class="form-field">
              <label>手机号 <span class="required">*</span></label>
              <input v-model.trim="editForm.phone" type="text" />
              <span class="field-error" v-if="err.phone">{{ err.phone }}</span>
            </div>
            <div class="form-field">
              <label>QQ号 <span class="required">*</span></label>
              <input v-model.trim="editForm.qq" type="text" />
              <span class="field-error" v-if="err.qq">{{ err.qq }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="edit-section">
          <legend>技术背景</legend>
          <div class="form-field">
            <label>意向技术方向 <span class="required">*</span></label>
            <select v-model="editForm.direction">
              <option value="" disabled>请选择意向方向</option>
              <option v-for="item in directionOptions" :key="item" :value="item">{{ item }}</option>
            </select>
            <span class="field-error" v-if="err.direction">{{ err.direction }}</span>
          </div>
          <div class="form-field">
            <label>是否有编程基础</label>
            <div class="radio-group">
              <label><input type="radio" v-model.number="editForm.hasProgrammingBasis" :value="1" /><span>有基础</span></label>
              <label><input type="radio" v-model.number="editForm.hasProgrammingBasis" :value="0" /><span>暂时没有</span></label>
            </div>
          </div>
          <div class="form-field">
            <label>已掌握技术</label>
            <textarea v-model="editForm.skills" rows="2"></textarea>
          </div>
        </fieldset>

        <fieldset class="edit-section">
          <legend>个人陈述</legend>
          <div class="form-field">
            <label>个人介绍</label>
            <textarea v-model="editForm.introduction" rows="3"></textarea>
          </div>
          <div class="form-field">
            <label>加入实验室的原因</label>
            <textarea v-model="editForm.reason" rows="3"></textarea>
          </div>
          <div class="form-grid">
            <div class="form-field">
              <label>每周可投入时间</label>
              <input v-model.trim="editForm.weeklyAvailableTime" type="text" />
            </div>
            <div class="form-field">
              <label>项目/作品链接</label>
              <input v-model.trim="editForm.portfolioUrl" type="text" />
            </div>
          </div>
        </fieldset>

        <p class="form-alert error" v-if="formError">{{ formError }}</p>

        <div class="edit-actions">
          <AppButton
            :label="submitting ? '保存中...' : '保存修改'"
            type="submit"
            :disabled="submitting"
          />
          <AppButton label="取消" variant="secondary" @click="cancelEdit" />
        </div>
      </form>
    </main>
  </AppFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
import { getMyApply, updateMyApply, STATUS_MAP } from '@/api/application'
import type { ApplyRecord } from '@/api/application'
import { formatDate } from '@/utils/content'

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

const record = ref<ApplyRecord | null>(null)
const loading = ref(true)
const editing = ref(false)
const submitting = ref(false)
const formError = ref('')

const editForm = reactive({
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

const err = reactive<Record<string, string>>({})

const statusText = computed(() => {
  if (!record.value) return ''
  return STATUS_MAP[record.value.status] || record.value.status
})

function statusClass(status: string) {
  if (status === 'PASSED' || status === 'PRELIMINARY_PASSED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

function startEdit() {
  if (!record.value) return
  editForm.realName = record.value.realName
  editForm.grade = record.value.grade
  editForm.major = record.value.major
  editForm.className = record.value.className
  editForm.phone = record.value.phone
  editForm.qq = record.value.qq
  editForm.direction = record.value.direction
  editForm.hasProgrammingBasis = record.value.hasProgrammingBasis
  editForm.skills = record.value.skills || ''
  editForm.introduction = record.value.introduction || ''
  editForm.reason = record.value.reason || ''
  editForm.weeklyAvailableTime = record.value.weeklyAvailableTime || ''
  editForm.portfolioUrl = record.value.portfolioUrl || ''
  editing.value = true
}

function cancelEdit(event?: MouseEvent) {
  event?.preventDefault()
  editing.value = false
  formError.value = ''
  for (const key of Object.keys(err)) delete err[key]
}

function validate(): boolean {
  let valid = true
  for (const key of Object.keys(err)) delete err[key]

  if (!editForm.realName) { err.realName = '请输入姓名'; valid = false }
  if (!editForm.grade) { err.grade = '请选择年级'; valid = false }
  if (!editForm.major) { err.major = '请输入专业'; valid = false }
  if (!editForm.className) { err.className = '请输入班级'; valid = false }
  if (!editForm.phone) { err.phone = '请输入手机号'; valid = false }
  else if (!/^1[3-9]\d{9}$/.test(editForm.phone)) { err.phone = '手机号格式不正确'; valid = false }
  if (!editForm.qq) { err.qq = '请输入 QQ 号'; valid = false }
  if (!editForm.direction) { err.direction = '请选择意向技术方向'; valid = false }

  return valid
}

async function handleUpdate() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await updateMyApply({ ...editForm })
    record.value = await getMyApply()
    editing.value = false
  } catch (errObj: any) {
    formError.value = errObj?.response?.data?.message || errObj?.message || '修改失败'
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    record.value = await getMyApply()
  } catch {
    record.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.application-page {
  padding: 0 0 92px;
}

.application-card {
  display: grid;
  gap: 24px;
  padding: clamp(24px, 5vw, 44px);
}

.application-card__header {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 20px;
}

.application-card__header h2 {
  color: var(--app-text-strong);
  font-size: 30px;
}

.review-note {
  padding: 18px;
  border: 1px solid rgba(255, 211, 106, 0.24);
  border-radius: var(--app-radius-sm);
  color: var(--app-soft);
  background: rgba(255, 211, 106, 0.08);
}

.review-note strong {
  color: var(--app-amber);
}

.review-note p {
  margin-top: 8px;
  color: var(--app-soft);
}

.info-groups {
  display: grid;
  gap: 16px;
}

.info-groups section,
.edit-section {
  padding: 20px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius);
  background: rgba(255, 255, 255, 0.035);
}

.info-groups h3,
.edit-section legend {
  color: var(--app-cyan);
  font-size: 16px;
}

.edit-section legend {
  padding: 0 10px;
  font-weight: 720;
}

.info-groups dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.info-groups div {
  min-width: 0;
}

.info-groups .wide {
  grid-column: 1 / -1;
}

.info-groups dt {
  color: var(--app-muted);
  font-size: 12px;
}

.info-groups dd {
  margin-top: 4px;
  color: var(--app-text-strong);
  word-break: break-word;
}

.application-card__footer,
.edit-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  color: var(--app-muted);
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

@media (max-width: 640px) {
  .application-card__header,
  .application-card__footer,
  .edit-actions {
    display: grid;
  }

  .info-groups dl {
    grid-template-columns: 1fr;
  }
}
</style>
