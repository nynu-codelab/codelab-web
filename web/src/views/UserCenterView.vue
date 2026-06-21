<template>
  <div class="user-page">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">软工实验室</router-link>
        <router-link to="/" class="back-link">&larr; 返回首页</router-link>
      </div>
    </header>

    <main class="user-main" v-if="userStore.userInfo">
      <!-- 用户信息头部 -->
      <section class="profile-header">
        <div class="avatar">{{ avatarLetter }}</div>
        <div class="profile-meta">
          <h2 class="profile-name">{{ userStore.userInfo.realName }}</h2>
          <p class="profile-username">@{{ userStore.userInfo.username }}</p>
        </div>
      </section>

      <!-- 功能区块 -->
      <section class="profile-grid">
        <div class="profile-card">
          <div class="card-icon" style="background-color: rgba(100, 181, 246, 0.15);">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#64b5f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <h3>我的资料</h3>
          <p>查看和编辑个人资料信息</p>
          <div class="card-detail">
            <span>年级：{{ userStore.userInfo.grade }}</span>
            <span>专业：{{ userStore.userInfo.major }}</span>
            <span>班级：{{ userStore.userInfo.className }}</span>
            <span>手机：{{ userStore.userInfo.phone }}</span>
          </div>
        </div>

        <div class="profile-card">
          <div class="card-icon" style="background-color: rgba(129, 199, 132, 0.15);">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#81c784" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/>
              <rect x="8" y="2" width="8" height="4" rx="1" ry="1"/>
            </svg>
          </div>
          <h3>我的报名</h3>
          <p>查看招新报名记录和状态</p>
          <span class="card-placeholder">暂无报名记录</span>
        </div>

        <div class="profile-card">
          <div class="card-icon" style="background-color: rgba(255, 183, 77, 0.15);">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ffb74d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
          </div>
          <h3>修改密码</h3>
          <p>更改账户登录密码</p>
          <span class="card-placeholder">功能开发中</span>
        </div>

        <div
          class="profile-card card-logout"
          @click="handleLogout"
        >
          <div class="card-icon" style="background-color: rgba(239, 83, 80, 0.15);">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ef5350" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </div>
          <h3>退出登录</h3>
          <p>安全退出当前账户</p>
        </div>
      </section>
    </main>

    <!-- 加载中 -->
    <main class="user-main loading" v-else>
      <p>加载中...</p>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const avatarLetter = computed(() => {
  const name = userStore.userInfo?.realName || ''
  return name ? name.charAt(name.length - 1) : '?'
})

async function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(async () => {
  if (!userStore.userInfo) {
    try {
      await userStore.fetchMe()
    } catch {
      // fetchMe failed — likely not logged in, guard will redirect
    }
  }
})
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background: linear-gradient(160deg, #0d1b2a 0%, #13263a 50%, #0f1923 100%);
}

/* 导航栏 */
.navbar {
  background-color: rgba(20, 30, 44, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
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

.back-link {
  font-size: 13px;
  color: #546e7a;
  transition: color 0.2s;
}

.back-link:hover {
  color: #b0bec5;
}

/* 主体 */
.user-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 24px 60px;
}

.user-main.loading {
  text-align: center;
  color: #78909c;
  padding-top: 120px;
}

/* 用户头部 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
  padding-bottom: 28px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #64b5f6, #1976d2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  flex-shrink: 0;
}

.profile-name {
  font-size: 22px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.profile-username {
  font-size: 14px;
  color: #78909c;
}

/* 卡片网格 */
.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.profile-card {
  background-color: #1a2a3a;
  border-radius: 12px;
  padding: 28px 24px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  transition: border-color 0.2s, transform 0.2s;
}

.profile-card:hover {
  transform: translateY(-2px);
}

.card-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.profile-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 6px;
}

.profile-card > p {
  font-size: 13px;
  color: #78909c;
  margin-bottom: 12px;
}

.card-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
  font-size: 13px;
  color: #b0bec5;
}

.card-placeholder {
  font-size: 13px;
  color: #546e7a;
}

.card-logout {
  cursor: pointer;
}

.card-logout:hover {
  border-color: rgba(239, 83, 80, 0.3);
}

@media (max-width: 640px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
