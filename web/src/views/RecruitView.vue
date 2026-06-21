<template>
  <div class="recruit-page">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">Code Lab</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/recruit">招新报名</router-link>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/user" class="btn-user">个人中心</router-link>
            <a href="#" class="btn-logout" @click.prevent="handleLogout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-login">登录</router-link>
          </template>
        </nav>
      </div>
    </header>

    <main class="recruit-main">
      <!-- 成功提示 -->
      <div class="success-card" v-if="submitted">
        <div class="success-icon">✓</div>
        <h2>报名提交成功！</h2>
        <p>你的报名信息已提交，实验室管理员将尽快审核。</p>
        <div class="success-actions">
          <router-link to="/my-application" class="btn-primary">查看我的报名</router-link>
          <router-link to="/" class="btn-secondary">返回首页</router-link>
        </div>
      </div>

      <!-- 报名表单 -->
      <form class="recruit-form" v-else @submit.prevent="handleSubmit">
        <h2 class="form-title">招新报名</h2>
        <p class="form-subtitle">加入南阳师范学院 Code Lab 实验室</p>

        <!-- 基本信息 -->
        <fieldset class="form-section">
          <legend>基本信息</legend>
          <div class="form-row">
            <div class="form-group">
              <label>姓名 <span class="required">*</span></label>
              <input v-model.trim="form.realName" type="text" placeholder="请输入真实姓名" />
              <span class="field-error" v-if="errors.realName">{{ errors.realName }}</span>
            </div>
            <div class="form-group">
              <label>年级 <span class="required">*</span></label>
              <select v-model="form.grade">
                <option value="" disabled>请选择年级</option>
                <option v-for="g in gradeOptions" :key="g" :value="g">{{ g }}</option>
              </select>
              <span class="field-error" v-if="errors.grade">{{ errors.grade }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>专业 <span class="required">*</span></label>
              <input v-model.trim="form.major" type="text" placeholder="请输入专业名称" />
              <span class="field-error" v-if="errors.major">{{ errors.major }}</span>
            </div>
            <div class="form-group">
              <label>班级 <span class="required">*</span></label>
              <input v-model.trim="form.className" type="text" placeholder="请输入班级" />
              <span class="field-error" v-if="errors.className">{{ errors.className }}</span>
            </div>
          </div>
        </fieldset>

        <!-- 联系方式 -->
        <fieldset class="form-section">
          <legend>联系方式</legend>
          <div class="form-row">
            <div class="form-group">
              <label>手机号 <span class="required">*</span></label>
              <input v-model.trim="form.phone" type="text" placeholder="请输入手机号" />
              <span class="field-error" v-if="errors.phone">{{ errors.phone }}</span>
            </div>
            <div class="form-group">
              <label>QQ号 <span class="required">*</span></label>
              <input v-model.trim="form.qq" type="text" placeholder="请输入QQ号" />
              <span class="field-error" v-if="errors.qq">{{ errors.qq }}</span>
            </div>
          </div>
        </fieldset>

        <!-- 技术背景 -->
        <fieldset class="form-section">
          <legend>技术背景</legend>
          <div class="form-group">
            <label>意向技术方向 <span class="required">*</span></label>
            <select v-model="form.direction">
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
            <span class="field-error" v-if="errors.direction">{{ errors.direction }}</span>
          </div>
          <div class="form-group">
            <label>是否有编程基础</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model.number="form.hasProgrammingBasis" :value="1" />
                <span>有</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model.number="form.hasProgrammingBasis" :value="0" />
                <span>无</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>已掌握技术</label>
            <textarea v-model="form.skills" rows="2" placeholder="例如：HTML/CSS、JavaScript、Python、Java 等"></textarea>
          </div>
        </fieldset>

        <!-- 个人陈述 -->
        <fieldset class="form-section">
          <legend>个人陈述</legend>
          <div class="form-group">
            <label>个人介绍</label>
            <textarea v-model="form.introduction" rows="3" placeholder="请简要介绍自己..."></textarea>
          </div>
          <div class="form-group">
            <label>加入实验室的原因</label>
            <textarea v-model="form.reason" rows="3" placeholder="请说明为什么想加入 Code Lab 实验室..."></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>每周可投入时间</label>
              <input v-model.trim="form.weeklyAvailableTime" type="text" placeholder="例如：10小时以上" />
            </div>
            <div class="form-group">
              <label>项目/作品链接</label>
              <input v-model.trim="form.portfolioUrl" type="text" placeholder="GitHub / 个人网站 / 作品链接" />
            </div>
          </div>
        </fieldset>

        <p class="form-error" v-if="formError">{{ formError }}</p>

        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交报名' }}
        </button>
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
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { submitApply } from '@/api/application'

const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())

const currentYearNum = new Date().getFullYear()
const gradeOptions = Array.from({ length: 4 }, (_, i) => `${currentYearNum - i}级`)

const form = reactive({
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
  if (!form.qq) { errors.qq = '请输入QQ号'; valid = false }
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
    const msg = err?.response?.data?.message || err?.message || '提交失败，请稍后重试'
    formError.value = msg
  } finally {
    submitting.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.recruit-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 50%, #0f1923 100%);
}

/* 导航栏 — 与 Home 保持一致 */
.navbar { background-color: rgba(20, 30, 44, 0.95); backdrop-filter: blur(10px); border-bottom: 1px solid rgba(255, 255, 255, 0.06); position: sticky; top: 0; z-index: 100; }
.navbar-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; height: 60px; display: flex; align-items: center; justify-content: space-between; }
.logo { font-size: 20px; font-weight: 700; color: #64b5f6; letter-spacing: 1px; }
.nav-links { display: flex; align-items: center; gap: 28px; font-size: 14px; }
.nav-links a { color: #b0bec5; transition: color 0.2s; }
.nav-links a:hover { color: #ffffff; }
.btn-login { padding: 6px 18px; border-radius: 6px; font-size: 13px; border: 1px solid #64b5f6; color: #64b5f6; }
.btn-login:hover { background-color: rgba(100, 181, 246, 0.1); }
.btn-user { color: #64b5f6; }
.btn-logout { color: #ef5350; border: 1px solid #ef5350; padding: 6px 18px; border-radius: 6px; font-size: 13px; }
.btn-logout:hover { background-color: rgba(239, 83, 80, 0.1); }

/* 主体 */
.recruit-main {
  flex: 1;
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 24px 60px;
}

/* 成功卡片 */
.success-card {
  text-align: center;
  padding: 60px 40px;
  background-color: #1a2a3a;
  border-radius: 12px;
  border: 1px solid rgba(100, 181, 246, 0.2);
}
.success-icon {
  width: 72px; height: 72px; border-radius: 50%;
  background: linear-gradient(135deg, #81c784, #388e3c);
  display: flex; align-items: center; justify-content: center;
  font-size: 32px; color: #fff; margin: 0 auto 24px;
}
.success-card h2 { font-size: 24px; color: #ffffff; margin-bottom: 12px; }
.success-card > p { color: #78909c; margin-bottom: 32px; }
.success-actions { display: flex; gap: 16px; justify-content: center; flex-wrap: wrap; }
.btn-primary {
  padding: 10px 28px; border-radius: 8px; font-size: 15px; font-weight: 600;
  background-color: #64b5f6; color: #0d1b2a; transition: background-color 0.2s;
}
.btn-primary:hover { background-color: #90caf9; }
.btn-secondary {
  padding: 10px 28px; border-radius: 8px; font-size: 15px;
  border: 1px solid #546e7a; color: #b0bec5; transition: all 0.2s;
}
.btn-secondary:hover { border-color: #90caf9; color: #e0e0e0; }

/* 表单 */
.recruit-form {
  background-color: #1a2a3a;
  border-radius: 12px;
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.04);
}
.form-title { font-size: 28px; font-weight: 700; color: #ffffff; margin-bottom: 8px; }
.form-subtitle { font-size: 15px; color: #78909c; margin-bottom: 32px; }

.form-section {
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}
.form-section legend {
  font-size: 16px; font-weight: 600; color: #64b5f6;
  padding: 0 12px;
}

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
@media (max-width: 600px) { .form-row { grid-template-columns: 1fr; } }

.form-group { margin-bottom: 20px; }
.form-section .form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; font-size: 14px; color: #b0bec5; margin-bottom: 6px; }
.required { color: #ef5350; }

.form-group input[type="text"],
.form-group input[type="password"],
.form-group select,
.form-group textarea {
  width: 100%; padding: 10px 14px; border-radius: 6px;
  background-color: #0f1923; border: 1px solid rgba(255, 255, 255, 0.1);
  color: #e0e0e0; font-size: 14px; transition: border-color 0.2s;
}
.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
  outline: none; border-color: #64b5f6;
}
.form-group select { cursor: pointer; }
.form-group textarea { resize: vertical; }

.radio-group { display: flex; gap: 24px; }
.radio-label { display: flex; align-items: center; gap: 6px; cursor: pointer; font-size: 14px; color: #e0e0e0; }
.radio-label input[type="radio"] { accent-color: #64b5f6; }

.field-error { display: block; font-size: 12px; color: #ef5350; margin-top: 4px; }
.form-error { padding: 10px 14px; background-color: rgba(239, 83, 80, 0.1); border: 1px solid rgba(239, 83, 80, 0.3); border-radius: 6px; color: #ef5350; font-size: 13px; margin-bottom: 20px; }

.btn-submit {
  width: 100%; padding: 14px; border-radius: 8px; border: none;
  background-color: #64b5f6; color: #0d1b2a; font-size: 16px; font-weight: 600;
  cursor: pointer; transition: background-color 0.2s;
}
.btn-submit:hover { background-color: #90caf9; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

/* 页脚 */
.footer { border-top: 1px solid rgba(255, 255, 255, 0.06); background-color: #0a1520; padding: 32px 24px; text-align: center; }
.footer-inner { max-width: 1200px; margin: 0 auto; }
.footer-copy { font-size: 13px; color: #546e7a; margin-bottom: 8px; }
.footer-disclaimer { font-size: 12px; color: #455a64; }
</style>
