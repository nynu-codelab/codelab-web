<template>
  <div class="my-app-page">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">Code Lab</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/recruit">招新报名</router-link>
          <router-link to="/user" class="btn-user">个人中心</router-link>
          <a href="#" class="btn-logout" @click.prevent="handleLogout">退出</a>
        </nav>
      </div>
    </header>

    <main class="my-app-main">
      <!-- 加载中 -->
      <div class="loading" v-if="loading">
        <p>加载中...</p>
      </div>

      <!-- 无报名记录 -->
      <div class="empty-card" v-else-if="!record && !loading">
        <h2>暂无报名记录</h2>
        <p>你还没有提交过招新报名</p>
        <router-link to="/recruit" class="btn-primary">立即报名</router-link>
      </div>

      <!-- 查看模式 -->
      <div class="record-card" v-else-if="record && !editing">
        <div class="record-header">
          <h2>我的报名</h2>
          <span class="status-badge" :style="{ backgroundColor: statusColor }">
            {{ statusText }}
          </span>
        </div>

        <!-- 审核备注 -->
        <div class="review-remark" v-if="record.reviewRemark">
          <h4>审核备注</h4>
          <p>{{ record.reviewRemark }}</p>
        </div>

        <div class="record-sections">
          <div class="info-section">
            <h3>基本信息</h3>
            <div class="info-grid">
              <div class="info-item"><label>姓名</label><span>{{ record.realName }}</span></div>
              <div class="info-item"><label>年级</label><span>{{ record.grade }}</span></div>
              <div class="info-item"><label>专业</label><span>{{ record.major }}</span></div>
              <div class="info-item"><label>班级</label><span>{{ record.className }}</span></div>
            </div>
          </div>
          <div class="info-section">
            <h3>联系方式</h3>
            <div class="info-grid">
              <div class="info-item"><label>手机号</label><span>{{ record.phone }}</span></div>
              <div class="info-item"><label>QQ号</label><span>{{ record.qq }}</span></div>
            </div>
          </div>
          <div class="info-section">
            <h3>技术背景</h3>
            <div class="info-grid">
              <div class="info-item"><label>意向方向</label><span>{{ record.direction }}</span></div>
              <div class="info-item"><label>编程基础</label><span>{{ record.hasProgrammingBasis ? '有' : '无' }}</span></div>
              <div class="info-item full-width" v-if="record.skills"><label>已掌握技术</label><span>{{ record.skills }}</span></div>
            </div>
          </div>
          <div class="info-section">
            <h3>个人陈述</h3>
            <div class="info-grid">
              <div class="info-item full-width" v-if="record.introduction"><label>个人介绍</label><span>{{ record.introduction }}</span></div>
              <div class="info-item full-width" v-if="record.reason"><label>加入原因</label><span>{{ record.reason }}</span></div>
              <div class="info-item"><label>每周可投入时间</label><span>{{ record.weeklyAvailableTime || '-' }}</span></div>
              <div class="info-item"><label>项目/作品链接</label><span>{{ record.portfolioUrl || '-' }}</span></div>
            </div>
          </div>
        </div>

        <div class="record-footer">
          <p class="record-time">提交时间：{{ record.createTime }}</p>
          <button
            v-if="record.status === 'PENDING'"
            class="btn-edit"
            @click="startEdit"
          >修改报名信息</button>
        </div>
      </div>

      <!-- 编辑模式 -->
      <form class="record-card" v-else @submit.prevent="handleUpdate">
        <div class="record-header">
          <h2>修改报名</h2>
          <span class="status-badge" :style="{ backgroundColor: '#ffb74d' }">待审核</span>
        </div>

        <fieldset class="form-section">
          <legend>基本信息</legend>
          <div class="form-row">
            <div class="form-group">
              <label>姓名 <span class="required">*</span></label>
              <input v-model.trim="editForm.realName" type="text" />
              <span class="field-error" v-if="err.realName">{{ err.realName }}</span>
            </div>
            <div class="form-group">
              <label>年级 <span class="required">*</span></label>
              <select v-model="editForm.grade">
                <option value="" disabled>请选择年级</option>
                <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
              </select>
              <span class="field-error" v-if="err.grade">{{ err.grade }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>专业 <span class="required">*</span></label>
              <input v-model.trim="editForm.major" type="text" />
              <span class="field-error" v-if="err.major">{{ err.major }}</span>
            </div>
            <div class="form-group">
              <label>班级 <span class="required">*</span></label>
              <input v-model.trim="editForm.className" type="text" />
              <span class="field-error" v-if="err.className">{{ err.className }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="form-section">
          <legend>联系方式</legend>
          <div class="form-row">
            <div class="form-group">
              <label>手机号 <span class="required">*</span></label>
              <input v-model.trim="editForm.phone" type="text" />
              <span class="field-error" v-if="err.phone">{{ err.phone }}</span>
            </div>
            <div class="form-group">
              <label>QQ号 <span class="required">*</span></label>
              <input v-model.trim="editForm.qq" type="text" />
              <span class="field-error" v-if="err.qq">{{ err.qq }}</span>
            </div>
          </div>
        </fieldset>

        <fieldset class="form-section">
          <legend>技术背景</legend>
          <div class="form-group">
            <label>意向技术方向 <span class="required">*</span></label>
            <select v-model="editForm.direction">
              <option value="" disabled>请选择意向方向</option>
              <option value="前端开发">前端开发</option>
              <option value="后端开发">后端开发</option>
              <option value="移动开发">移动开发</option>
              <option value="人工智能">人工智能</option>
              <option value="数据分析">数据分析</option>
              <option value="UI设计">UI设计</option>
              <option value="网络安全">网络安全</option>
              <option value="游戏开发">游戏开发</option>
              <option value="运维/DevOps">运维/DevOps</option>
              <option value="其它">其它</option>
            </select>
            <span class="field-error" v-if="err.direction">{{ err.direction }}</span>
          </div>
          <div class="form-group">
            <label>是否有编程基础</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model.number="editForm.hasProgrammingBasis" :value="1" />
                <span>有</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model.number="editForm.hasProgrammingBasis" :value="0" />
                <span>无</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>已掌握技术</label>
            <textarea v-model="editForm.skills" rows="2"></textarea>
          </div>
        </fieldset>

        <fieldset class="form-section">
          <legend>个人陈述</legend>
          <div class="form-group">
            <label>个人介绍</label>
            <textarea v-model="editForm.introduction" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>加入实验室的原因</label>
            <textarea v-model="editForm.reason" rows="3"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>每周可投入时间</label>
              <input v-model.trim="editForm.weeklyAvailableTime" type="text" />
            </div>
            <div class="form-group">
              <label>项目/作品链接</label>
              <input v-model.trim="editForm.portfolioUrl" type="text" />
            </div>
          </div>
        </fieldset>

        <p class="form-error" v-if="formError">{{ formError }}</p>

        <div class="edit-actions">
          <button type="submit" class="btn-save" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存修改' }}
          </button>
          <button type="button" class="btn-cancel" @click="cancelEdit">取消</button>
        </div>
      </form>
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <p class="footer-copy">&copy; {{ currentYear }} 南阳师范学院 Code Lab 实验室</p>
        <p class="footer-disclaimer">本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMyApply, updateMyApply, STATUS_MAP, STATUS_COLORS } from '@/api/application'
import type { ApplyRecord } from '@/api/application'

const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())
const currentYearNum = new Date().getFullYear()
const gradeOptions = Array.from({ length: 4 }, (_, i) => `${currentYearNum - i}级`)

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
  hasProgrammingBasis: 0 as number,
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

const statusColor = computed(() => {
  if (!record.value) return '#78909c'
  return STATUS_COLORS[record.value.status] || '#78909c'
})

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

function cancelEdit() {
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
  if (!editForm.qq) { err.qq = '请输入QQ号'; valid = false }
  if (!editForm.direction) { err.direction = '请选择意向技术方向'; valid = false }

  return valid
}

async function handleUpdate() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await updateMyApply({ ...editForm })
    // 刷新数据
    const updated = await getMyApply()
    record.value = updated
    editing.value = false
  } catch (errObj: any) {
    const msg = errObj?.response?.data?.message || errObj?.message || '修改失败'
    formError.value = msg
  } finally {
    submitting.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(async () => {
  try {
    record.value = await getMyApply()
  } catch {
    // 暂无报名记录
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.my-app-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 50%, #0f1923 100%);
}

/* 导航栏 */
.navbar { background-color: rgba(20, 30, 44, 0.95); backdrop-filter: blur(10px); border-bottom: 1px solid rgba(255, 255, 255, 0.06); position: sticky; top: 0; z-index: 100; }
.navbar-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; height: 60px; display: flex; align-items: center; justify-content: space-between; }
.logo { font-size: 20px; font-weight: 700; color: #64b5f6; letter-spacing: 1px; }
.nav-links { display: flex; align-items: center; gap: 28px; font-size: 14px; }
.nav-links a { color: #b0bec5; transition: color 0.2s; }
.nav-links a:hover { color: #ffffff; }
.btn-user { color: #64b5f6; }
.btn-logout { color: #ef5350; border: 1px solid #ef5350; padding: 6px 18px; border-radius: 6px; font-size: 13px; }
.btn-logout:hover { background-color: rgba(239, 83, 80, 0.1); }

/* 主体 */
.my-app-main { flex: 1; max-width: 800px; width: 100%; margin: 0 auto; padding: 40px 24px 60px; }

.loading { text-align: center; color: #78909c; padding-top: 120px; }

/* 空状态 */
.empty-card {
  text-align: center; padding: 60px 40px;
  background-color: #1a2a3a; border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.04);
}
.empty-card h2 { font-size: 22px; color: #e0e0e0; margin-bottom: 12px; }
.empty-card p { color: #78909c; margin-bottom: 24px; }
.btn-primary {
  padding: 10px 28px; border-radius: 8px; font-size: 15px; font-weight: 600;
  background-color: #64b5f6; color: #0d1b2a;
}

/* 记录卡片 */
.record-card {
  background-color: #1a2a3a; border-radius: 12px; padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.04);
}
.record-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.record-header h2 { font-size: 24px; font-weight: 700; color: #ffffff; }
.status-badge { padding: 4px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: #ffffff; }

.review-remark { background-color: rgba(255, 183, 77, 0.08); border: 1px solid rgba(255, 183, 77, 0.2); border-radius: 8px; padding: 16px; margin-bottom: 24px; }
.review-remark h4 { font-size: 13px; color: #ffb74d; margin-bottom: 6px; }
.review-remark p { font-size: 13px; color: #b0bec5; line-height: 1.6; }

.info-section { margin-bottom: 24px; }
.info-section h3 { font-size: 15px; font-weight: 600; color: #64b5f6; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid rgba(255, 255, 255, 0.04); }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 20px; }
.info-item.full-width { grid-column: 1 / -1; }
.info-item label { display: block; font-size: 12px; color: #546e7a; margin-bottom: 2px; }
.info-item span { font-size: 14px; color: #e0e0e0; }

.record-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 24px; border-top: 1px solid rgba(255, 255, 255, 0.06); margin-top: 8px; flex-wrap: wrap; gap: 12px; }
.record-time { font-size: 12px; color: #546e7a; }
.btn-edit {
  padding: 8px 20px; border-radius: 6px; border: 1px solid #64b5f6; color: #64b5f6;
  background: transparent; cursor: pointer; font-size: 13px; transition: all 0.2s;
}
.btn-edit:hover { background-color: rgba(100, 181, 246, 0.1); }

/* 编辑表单 */
.form-section { border: 1px solid rgba(255, 255, 255, 0.06); border-radius: 8px; padding: 24px; margin-bottom: 24px; }
.form-section legend { font-size: 16px; font-weight: 600; color: #64b5f6; padding: 0 12px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
@media (max-width: 600px) { .form-row { grid-template-columns: 1fr; } }
.form-group { margin-bottom: 20px; }
.form-section .form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; font-size: 14px; color: #b0bec5; margin-bottom: 6px; }
.required { color: #ef5350; }
.form-group input, .form-group select, .form-group textarea {
  width: 100%; padding: 10px 14px; border-radius: 6px;
  background-color: #0f1923; border: 1px solid rgba(255, 255, 255, 0.1);
  color: #e0e0e0; font-size: 14px;
}
.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
  outline: none; border-color: #64b5f6;
}
.form-group textarea { resize: vertical; }
.radio-group { display: flex; gap: 24px; }
.radio-label { display: flex; align-items: center; gap: 6px; cursor: pointer; font-size: 14px; color: #e0e0e0; }
.field-error { display: block; font-size: 12px; color: #ef5350; margin-top: 4px; }
.form-error { padding: 10px 14px; background-color: rgba(239, 83, 80, 0.1); border: 1px solid rgba(239, 83, 80, 0.3); border-radius: 6px; color: #ef5350; font-size: 13px; margin-bottom: 20px; }

.edit-actions { display: flex; gap: 16px; }
.btn-save {
  padding: 10px 28px; border-radius: 8px; border: none;
  background-color: #64b5f6; color: #0d1b2a; font-size: 15px; font-weight: 600; cursor: pointer;
}
.btn-save:hover { background-color: #90caf9; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel {
  padding: 10px 28px; border-radius: 8px; border: 1px solid #546e7a;
  background: transparent; color: #b0bec5; font-size: 15px; cursor: pointer;
}
.btn-cancel:hover { border-color: #90caf9; color: #e0e0e0; }

/* 页脚 */
.footer { border-top: 1px solid rgba(255, 255, 255, 0.06); background-color: #0a1520; padding: 32px 24px; text-align: center; }
.footer-inner { max-width: 1200px; margin: 0 auto; }
.footer-copy { font-size: 13px; color: #546e7a; margin-bottom: 8px; }
.footer-disclaimer { font-size: 12px; color: #455a64; }
</style>
