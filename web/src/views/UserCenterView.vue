<template>
  <AppFrame>
    <PageHero
      eyebrow="Profile"
      title="个人中心"
      description="管理账号基础信息，查看招新报名记录与审核状态。"
    >
      <template #actions>
        <AppButton label="我的报名" to="/my-application" />
        <AppButton label="退出登录" variant="danger" @click="handleLogout" />
      </template>
    </PageHero>

    <main class="profile-page app-container" v-if="userStore.userInfo">
      <section class="profile-panel glass-card">
        <div class="profile-panel__avatar">{{ avatarLetter }}</div>
        <div class="profile-panel__meta">
          <span class="app-eyebrow">Account</span>
          <h2>{{ userStore.userInfo.realName }}</h2>
          <p>@{{ userStore.userInfo.username }} · {{ userStore.userInfo.role }}</p>
        </div>
      </section>

      <section class="app-grid four">
        <article class="profile-card glass-card">
          <span>年级</span>
          <strong>{{ userStore.userInfo.grade || '-' }}</strong>
        </article>
        <article class="profile-card glass-card">
          <span>专业</span>
          <strong>{{ userStore.userInfo.major || '-' }}</strong>
        </article>
        <article class="profile-card glass-card">
          <span>班级</span>
          <strong>{{ userStore.userInfo.className || '-' }}</strong>
        </article>
        <article class="profile-card glass-card">
          <span>手机号</span>
          <strong>{{ userStore.userInfo.phone || '-' }}</strong>
        </article>
      </section>

      <section class="app-grid three profile-actions">
        <RouterLink to="/my-application" class="action-card glass-card is-hoverable">
          <span class="status-pill">报名流程</span>
          <h3>我的报名</h3>
          <p>查看招新报名记录、审核备注和当前审核状态。</p>
        </RouterLink>
        <RouterLink to="/recruit" class="action-card glass-card is-hoverable">
          <span class="status-pill">招新入口</span>
          <h3>提交报名</h3>
          <p>如果还没有报名记录，可以进入招新页面填写并提交。</p>
        </RouterLink>
        <article class="action-card glass-card">
          <span class="status-pill warning">待实现</span>
          <h3>修改密码</h3>
          <p>需求文档已规划修改密码接口，当前后端接口尚未实现。</p>
        </article>
      </section>
    </main>

    <main class="profile-page app-container narrow" v-else>
      <StateView title="正在加载用户信息" message="正在读取当前登录用户信息。" />
    </main>
  </AppFrame>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const avatarLetter = computed(() => {
  const name = userStore.userInfo?.realName || userStore.userInfo?.username || ''
  return name ? name.charAt(0).toUpperCase() : 'CL'
})

function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(async () => {
  if (!userStore.userInfo) {
    try {
      await userStore.fetchMe()
    } catch {
      userStore.logout()
      router.push('/login')
    }
  }
})
</script>

<style scoped>
.profile-page {
  display: grid;
  gap: 22px;
  padding: 0 0 92px;
}

.profile-panel {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 28px;
}

.profile-panel__avatar {
  display: grid;
  place-items: center;
  width: 86px;
  height: 86px;
  border: 1px solid rgba(83, 231, 255, 0.42);
  border-radius: 24px;
  color: #041017;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  box-shadow: 0 0 42px rgba(83, 231, 255, 0.24);
  font-family: var(--app-font-data);
  font-size: 28px;
  font-weight: 860;
}

.profile-panel__meta h2 {
  color: var(--app-text-strong);
  font-size: clamp(28px, 4vw, 42px);
}

.profile-panel__meta p {
  color: var(--app-muted);
}

.profile-card,
.action-card {
  padding: 24px;
}

.profile-card span {
  color: var(--app-muted);
  font-size: 13px;
}

.profile-card strong {
  display: block;
  margin-top: 8px;
  color: var(--app-text-strong);
  font-size: 20px;
  word-break: break-word;
}

.profile-actions {
  margin-top: 8px;
}

.action-card {
  display: grid;
  gap: 14px;
  color: inherit;
}

.action-card h3 {
  color: var(--app-text-strong);
  font-size: 22px;
}

.action-card p {
  color: var(--app-muted);
  line-height: 1.75;
}

@media (max-width: 620px) {
  .profile-panel {
    display: grid;
  }
}
</style>
