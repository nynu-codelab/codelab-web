<template>
  <div class="project-detail-page">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">Code Lab</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <a href="#">技术方向</a>
          <router-link to="/projects" class="active">项目成果</router-link>
          <router-link to="/articles">学习文章</router-link>
          <router-link to="/recruit">招新报名</router-link>
          <template v-if="userStore.isLoggedIn">
            <router-link to="/user" class="btn-user">个人中心</router-link>
            <a href="#" class="btn-logout" @click.prevent="handleLogout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主体 -->
    <main class="main-content">
      <div class="content-inner">
        <!-- 加载中 -->
        <div v-if="loading" class="state-box">
          <p class="state-text">加载中...</p>
        </div>

        <!-- 项目不存在 -->
        <div v-else-if="notFound" class="state-box">
          <p class="state-text">项目不存在或未发布</p>
          <router-link to="/projects" class="btn-back">返回项目列表</router-link>
        </div>

        <!-- 加载失败 -->
        <div v-else-if="error" class="state-box">
          <p class="state-text error">{{ error }}</p>
          <button class="btn-retry" @click="fetchProject">重新加载</button>
        </div>

        <!-- 项目详情 -->
        <article v-else-if="project" class="project-body">
          <!-- 项目头部 -->
          <div class="project-header">
            <div class="project-meta-top">
              <span v-if="project.projectType" class="project-type-tag">{{ project.projectType }}</span>
              <span class="project-date">{{ formatDate(project.publishedAt || project.createTime) }}</span>
            </div>
            <h1 class="project-title">{{ project.title }}</h1>

            <div class="project-info-grid">
              <div v-if="project.leaderName" class="info-item">
                <span class="info-label">负责人</span>
                <span class="info-value">{{ project.leaderName }}</span>
              </div>
              <div v-if="project.techStack" class="info-item">
                <span class="info-label">技术栈</span>
                <span class="info-value">{{ project.techStack }}</span>
              </div>
              <div v-if="project.membersText" class="info-item">
                <span class="info-label">参与成员</span>
                <span class="info-value">{{ project.membersText }}</span>
              </div>
            </div>

            <div class="project-links" v-if="project.repoUrl || project.demoUrl || project.documentUrl">
              <a v-if="project.repoUrl" :href="project.repoUrl" target="_blank" rel="noopener noreferrer" class="link-btn">🔗 代码仓库</a>
              <a v-if="project.demoUrl" :href="project.demoUrl" target="_blank" rel="noopener noreferrer" class="link-btn">🚀 在线演示</a>
              <a v-if="project.documentUrl" :href="project.documentUrl" target="_blank" rel="noopener noreferrer" class="link-btn">📄 文档</a>
            </div>

            <div class="project-stats">
              <span>{{ project.viewCount }} 次浏览</span>
            </div>
          </div>

          <div class="project-divider"></div>

          <!-- Markdown 详情 -->
          <div class="markdown-body" v-html="renderedMarkdown"></div>

          <div class="project-footer-nav">
            <router-link to="/projects" class="back-link">← 返回项目列表</router-link>
          </div>
        </article>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-inner">
        <p class="footer-copy">&copy; {{ currentYear }} 南阳师范学院 Code Lab 实验室</p>
        <p class="footer-disclaimer">
          本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getProject, type ProjectItem } from '@/api/project'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())

const project = ref<ProjectItem | null>(null)
const loading = ref(true)
const error = ref('')
const notFound = ref(false)

// Markdown 渲染器：关闭 HTML 标签解析以防御 XSS（与文章模块一致）
const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

const renderedMarkdown = computed(() => {
  if (!project.value) return ''
  return md.render(project.value.descriptionMarkdown || '')
})

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

async function fetchProject() {
  const id = Number(route.params.id)
  if (!id) {
    notFound.value = true
    loading.value = false
    return
  }
  loading.value = true
  error.value = ''
  notFound.value = false
  try {
    const res = await getProject(id)
    project.value = res.data
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || ''
    if (msg.includes('不存在') || msg.includes('未发布')) {
      notFound.value = true
    } else {
      error.value = msg || '加载失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(() => {
  fetchProject()
})
</script>

<style scoped>
.project-detail-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 导航栏 */
.navbar {
  background-color: rgba(20, 30, 44, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #64b5f6;
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 28px;
  font-size: 14px;
}

.nav-links a {
  color: #b0bec5;
  transition: color 0.2s;
}

.nav-links a:hover,
.nav-links a.active {
  color: #ffffff;
}

.nav-links a.active {
  font-weight: 600;
}

.btn-login,
.btn-register,
.btn-user,
.btn-logout {
  padding: 6px 18px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.2s;
}

.btn-login {
  border: 1px solid #64b5f6;
  color: #64b5f6;
}

.btn-login:hover {
  background-color: rgba(100, 181, 246, 0.1);
}

.btn-register {
  background-color: #64b5f6;
  color: #0d1b2a;
  font-weight: 600;
}

.btn-register:hover {
  background-color: #90caf9;
}

.btn-user {
  color: #64b5f6;
}

.btn-logout {
  color: #ef5350;
  border: 1px solid #ef5350;
}

.btn-logout:hover {
  background-color: rgba(239, 83, 80, 0.1);
}

/* 主体 */
.main-content {
  flex: 1;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 40%, #0f1923 100%);
  padding: 60px 24px;
}

.content-inner {
  max-width: 860px;
  margin: 0 auto;
}

/* 状态 */
.state-box {
  text-align: center;
  padding: 80px 24px;
}

.state-text {
  color: #78909c;
  font-size: 16px;
}

.state-text.error {
  color: #ef5350;
  margin-bottom: 16px;
}

.btn-retry,
.btn-back {
  display: inline-block;
  padding: 8px 24px;
  border: 1px solid #64b5f6;
  border-radius: 6px;
  background: transparent;
  color: #64b5f6;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-retry:hover,
.btn-back:hover {
  background-color: rgba(100, 181, 246, 0.1);
}

/* 项目详情 */
.project-body {
  background-color: #1a2a3a;
  border-radius: 12px;
  padding: 40px 48px;
  border: 1px solid rgba(255, 255, 255, 0.04);
}

.project-header {
  margin-bottom: 24px;
}

.project-meta-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.project-type-tag {
  font-size: 13px;
  color: #64b5f6;
  background: rgba(100, 181, 246, 0.1);
  padding: 3px 12px;
  border-radius: 4px;
}

.project-date {
  font-size: 13px;
  color: #546e7a;
}

.project-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.4;
  margin-bottom: 20px;
}

.project-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #546e7a;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 14px;
  color: #b0bec5;
}

.project-links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.link-btn {
  font-size: 13px;
  padding: 6px 16px;
  border: 1px solid rgba(100, 181, 246, 0.3);
  border-radius: 6px;
  color: #64b5f6;
  transition: background 0.2s;
}

.link-btn:hover {
  background: rgba(100, 181, 246, 0.1);
}

.project-stats {
  font-size: 13px;
  color: #546e7a;
}

.project-divider {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 32px;
}

.project-footer-nav {
  margin-top: 48px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.back-link {
  font-size: 14px;
  color: #64b5f6;
  transition: color 0.2s;
}

.back-link:hover {
  color: #90caf9;
}

/* Markdown 渲染样式 — 与文章详情保持一致 */
.markdown-body {
  color: #e0e0e0;
  line-height: 1.8;
  font-size: 15px;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4) {
  color: #ffffff;
  margin-top: 32px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.3;
}

.markdown-body :deep(h1) { font-size: 28px; }
.markdown-body :deep(h2) { font-size: 24px; border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px; }
.markdown-body :deep(h3) { font-size: 20px; }
.markdown-body :deep(h4) { font-size: 18px; }

.markdown-body :deep(p) {
  margin-bottom: 16px;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin-bottom: 16px;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin-bottom: 6px;
}

.markdown-body :deep(code) {
  background-color: rgba(100, 181, 246, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #64b5f6;
  font-family: 'Fira Code', 'Consolas', monospace;
}

.markdown-body :deep(pre) {
  background-color: #0d1b2a;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 20px 24px;
  overflow-x: auto;
  margin-bottom: 20px;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
  color: #e0e0e0;
  font-size: 13px;
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid #64b5f6;
  padding: 12px 20px;
  margin: 20px 0;
  background: rgba(100, 181, 246, 0.05);
  color: #b0bec5;
}

.markdown-body :deep(a) {
  color: #64b5f6;
  transition: color 0.2s;
}

.markdown-body :deep(a:hover) {
  color: #90caf9;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 10px 14px;
  text-align: left;
}

.markdown-body :deep(th) {
  background: rgba(255, 255, 255, 0.04);
  font-weight: 600;
  color: #ffffff;
}

.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  margin: 32px 0;
}

.markdown-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}

.markdown-body :deep(strong) {
  color: #ffffff;
  font-weight: 600;
}

/* 页脚 */
.footer {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  background-color: #0a1520;
  padding: 32px 24px;
  text-align: center;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-copy {
  font-size: 13px;
  color: #546e7a;
  margin-bottom: 8px;
}

.footer-disclaimer {
  font-size: 12px;
  color: #455a64;
}
</style>
