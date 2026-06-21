<template>
  <div class="home">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">Code Lab</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <a href="#">技术方向</a>
          <a href="#">项目成果</a>
          <a href="#">学习文章</a>
          <a href="#">招新报名</a>
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

    <!-- 主体内容 -->
    <main class="hero">
      <div class="hero-content">
        <h1 class="hero-title">南阳师范学院 Code Lab 实验室</h1>
        <p class="hero-subtitle">
          探索软件工程前沿，培养卓越技术人才
        </p>
        <p class="hero-desc">
          致力于软件工程理论与实践相结合，为学生提供一流的科研与创新平台。
        </p>
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const currentYear = computed(() => new Date().getFullYear())

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.home {
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

.nav-links a:hover {
  color: #ffffff;
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
.hero {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 60px 24px;
  background: linear-gradient(
    160deg,
    #0d1b2a 0%,
    #13263a 40%,
    #0f1923 100%
  );
}

.hero-content {
  max-width: 680px;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 20px;
  letter-spacing: 2px;
  line-height: 1.3;
}

.hero-subtitle {
  font-size: 20px;
  color: #64b5f6;
  margin-bottom: 16px;
  font-weight: 300;
  letter-spacing: 1px;
}

.hero-desc {
  font-size: 16px;
  color: #78909c;
  line-height: 1.8;
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
