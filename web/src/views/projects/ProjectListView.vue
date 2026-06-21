<template>
  <div class="project-list-page">
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
        <div class="page-header">
          <h1 class="page-title">项目成果</h1>
          <p class="page-desc">实验室历年项目与成果展示</p>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="state-box">
          <p class="state-text">加载中...</p>
        </div>

        <!-- 加载失败 -->
        <div v-else-if="error" class="state-box">
          <p class="state-text error">{{ error }}</p>
          <button class="btn-retry" @click="fetchProjects">重新加载</button>
        </div>

        <!-- 空列表 -->
        <div v-else-if="projects.length === 0" class="state-box">
          <p class="state-text">暂无项目成果</p>
        </div>

        <!-- 项目列表 -->
        <div v-else class="project-grid">
          <div
            v-for="item in projects"
            :key="item.id"
            class="project-card"
            @click="goDetail(item.id)"
          >
            <div class="card-cover">
              <span v-if="!item.coverUrl" class="cover-placeholder">📁</span>
              <img
                v-else
                :src="item.coverUrl"
                :alt="item.title"
                class="cover-image"
                @error="(e) => { (e.target as HTMLImageElement).style.display = 'none'; (e.target as HTMLImageElement).nextElementSibling?.classList.remove('hidden') }"
              />
              <span class="cover-placeholder hidden">📁</span>
            </div>
            <div class="card-body">
              <div class="card-type" v-if="item.projectType">
                <span class="type-tag">{{ item.projectType }}</span>
              </div>
              <h2 class="card-title">{{ item.title }}</h2>
              <p class="card-summary">{{ item.summary || '暂无简介' }}</p>
              <div class="card-meta">
                <span v-if="item.techStack" class="card-tech">{{ item.techStack }}</span>
                <span v-if="item.leaderName" class="card-leader">{{ item.leaderName }}</span>
              </div>
            </div>
          </div>
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
import { getProjects, type ProjectItem } from '@/api/project'

const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())

const projects = ref<ProjectItem[]>([])
const loading = ref(true)
const error = ref('')

function goDetail(id: number) {
  router.push(`/projects/${id}`)
}

async function fetchProjects() {
  loading.value = true
  error.value = ''
  try {
    const res = await getProjects()
    projects.value = res.data || []
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
  fetchProjects()
})
</script>

<style scoped>
.project-list-page {
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
  max-width: 1100px;
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

.btn-retry:hover {
  background-color: rgba(100, 181, 246, 0.1);
}

/* 项目网格 */
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.project-card {
  background-color: #1a2a3a;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.project-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
  border-color: rgba(100, 181, 246, 0.2);
}

.card-cover {
  height: 180px;
  background: rgba(100, 181, 246, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.cover-placeholder {
  font-size: 48px;
  color: rgba(255, 255, 255, 0.1);
}

.cover-placeholder.hidden {
  display: none;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  padding: 20px 24px 24px;
}

.card-type {
  margin-bottom: 10px;
}

.type-tag {
  font-size: 12px;
  color: #64b5f6;
  background: rgba(100, 181, 246, 0.1);
  padding: 2px 10px;
  border-radius: 4px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 10px;
  line-height: 1.4;
}

.card-summary {
  font-size: 14px;
  color: #78909c;
  line-height: 1.6;
  margin-bottom: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #546e7a;
  flex-wrap: wrap;
}

.card-tech {
  color: #64b5f6;
}

.card-leader {
  color: #78909c;
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
