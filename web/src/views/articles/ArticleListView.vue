<template>
  <div class="article-list-page">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">Code Lab</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <a href="#">技术方向</a>
          <a href="#">项目成果</a>
          <router-link to="/articles" class="active">学习文章</router-link>
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
        <div class="page-header">
          <h1 class="page-title">学习文章</h1>
          <p class="page-desc">技术分享、学习笔记与项目复盘</p>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="state-box">
          <p class="state-text">加载中...</p>
        </div>

        <!-- 加载失败 -->
        <div v-else-if="error" class="state-box">
          <p class="state-text error">{{ error }}</p>
          <button class="btn-retry" @click="fetchArticles">重新加载</button>
        </div>

        <!-- 空列表 -->
        <div v-else-if="articles.length === 0" class="state-box">
          <p class="state-text">暂无文章，敬请期待</p>
        </div>

        <!-- 文章列表 -->
        <div v-else class="article-grid">
          <article
            v-for="item in articles"
            :key="item.id"
            class="article-card"
            @click="goDetail(item.id)"
          >
            <div class="card-body">
              <div class="card-meta">
                <span v-if="item.category" class="card-category">{{ item.category }}</span>
                <span class="card-date">{{ formatDate(item.publishedAt || item.createTime) }}</span>
              </div>
              <h2 class="card-title">{{ item.title }}</h2>
              <p v-if="item.summary" class="card-summary">{{ item.summary }}</p>
              <div class="card-footer">
                <span v-if="item.tags" class="card-tags">
                  <span v-for="tag in parseTags(item.tags)" :key="tag" class="tag">{{ tag }}</span>
                </span>
                <span class="card-views">{{ item.viewCount }} 次阅读</span>
              </div>
            </div>
          </article>
        </div>
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
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getArticles, type ArticleItem } from '@/api/article'

const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())

const articles = ref<ArticleItem[]>([])
const loading = ref(true)
const error = ref('')

function parseTags(tags: string): string[] {
  try {
    const parsed = JSON.parse(tags)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return tags ? tags.split(',').map(t => t.trim()).filter(Boolean) : []
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function goDetail(id: number) {
  router.push(`/articles/${id}`)
}

async function fetchArticles() {
  loading.value = true
  error.value = ''
  try {
    const res = await getArticles()
    articles.value = res.data || []
  } catch (err: any) {
    error.value = err?.response?.data?.message || err?.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.article-list-page {
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
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 12px;
}

.page-desc {
  font-size: 16px;
  color: #78909c;
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

.btn-retry {
  padding: 8px 24px;
  border: 1px solid #64b5f6;
  border-radius: 6px;
  background: transparent;
  color: #64b5f6;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-retry:hover {
  background-color: rgba(100, 181, 246, 0.1);
}

/* 文章卡片 */
.article-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  background-color: #1a2a3a;
  border-radius: 10px;
  padding: 28px 32px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(255, 255, 255, 0.04);
}

.article-card:hover {
  background-color: #1e3045;
  border-color: rgba(100, 181, 246, 0.2);
  transform: translateY(-1px);
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.card-category {
  font-size: 12px;
  color: #64b5f6;
  background: rgba(100, 181, 246, 0.1);
  padding: 2px 10px;
  border-radius: 4px;
}

.card-date {
  font-size: 13px;
  color: #546e7a;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 10px;
  line-height: 1.4;
}

.card-summary {
  font-size: 14px;
  color: #78909c;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  font-size: 12px;
  color: #78909c;
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 4px;
}

.card-views {
  font-size: 13px;
  color: #546e7a;
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
