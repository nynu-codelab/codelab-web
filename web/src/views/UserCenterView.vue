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
      <template #visual>
        <CommandConsole
          title="profile.session"
          :commands="['fetchCurrentUser()', 'readApplicationStatus()', 'protectAccountData()']"
        />
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
        <button class="action-card glass-card is-clickable" type="button" @click="showChangePwd = true">
          <span class="status-pill">密码安全</span>
          <h3>修改密码</h3>
          <p>更新当前账号的登录密码，修改成功后需重新登录。</p>
        </button>
      </section>
    </main>

    <main class="profile-page app-container narrow" v-else>
      <StateView title="正在加载用户信息" message="正在读取当前登录用户信息。" />
    </main>

    <!-- 修改密码弹层 -->
    <Teleport to="body">
      <transition name="pwd-fade">
        <div v-if="showChangePwd" class="pwd-overlay" @click.self="closeChangePwd">
          <div class="pwd-dialog glass-card">
            <h3>修改密码</h3>
            <form @submit.prevent="handleChangePassword">
              <label>
                <span>旧密码</span>
                <input v-model="pwdForm.oldPassword" type="password" placeholder="输入当前密码" />
              </label>
              <label>
                <span>新密码</span>
                <input v-model="pwdForm.newPassword" type="password" placeholder="至少 6 位" />
              </label>
              <label>
                <span>确认新密码</span>
                <input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" />
              </label>
              <p v-if="pwdError" class="pwd-error">{{ pwdError }}</p>
              <div class="pwd-actions">
                <button type="button" class="app-btn app-btn--ghost" @click="closeChangePwd">取消</button>
                <button type="submit" class="app-btn app-btn--primary" :disabled="pwdLoading">
                  {{ pwdLoading ? '提交中…' : '确认修改' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </transition>
    </Teleport>
  </AppFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/auth'

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

// --------------- 修改密码 ---------------
const showChangePwd = ref(false)
const pwdLoading = ref(false)
const pwdError = ref('')

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function closeChangePwd() {
  showChangePwd.value = false
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdError.value = ''
}

async function handleChangePassword() {
  pwdError.value = ''

  if (!pwdForm.oldPassword) {
    pwdError.value = '请输入旧密码'
    return
  }
  if (pwdForm.newPassword.length < 6) {
    pwdError.value = '新密码长度不能少于 6 位'
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    pwdError.value = '两次输入的新密码不一致'
    return
  }

  pwdLoading.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
      confirmPassword: pwdForm.confirmPassword
    })
    closeChangePwd()
    // 密码修改成功后需重新登录
    userStore.logout()
    router.push('/login')
  } catch (e: any) {
    const msg = e?.response?.data?.message || e?.message || '修改失败'
    pwdError.value = msg
  } finally {
    pwdLoading.value = false
  }
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
  font-size: 42px;
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

  .profile-panel__meta h2 {
    font-size: 30px;
  }
}

/* --------------- 修改密码弹层 --------------- */
.pwd-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(4, 10, 18, 0.72);
  backdrop-filter: blur(12px);
}

.pwd-dialog {
  width: 100%;
  max-width: 440px;
  padding: 32px;
}

.pwd-dialog h3 {
  margin-bottom: 24px;
  color: var(--app-text-strong);
  font-size: 24px;
}

.pwd-dialog form {
  display: grid;
  gap: 18px;
}

.pwd-dialog label {
  display: grid;
  gap: 6px;
}

.pwd-dialog label span {
  color: var(--app-muted);
  font-size: 13px;
}

.pwd-dialog input {
  width: 100%;
  min-height: 44px;
  padding: 0 14px;
  border: 1px solid var(--app-line);
  border-radius: 12px;
  color: var(--app-text);
  background: rgba(255, 255, 255, 0.06);
  font-size: 15px;
  outline: none;
  transition: border-color 180ms ease;
}

.pwd-dialog input:focus {
  border-color: var(--app-cyan);
  box-shadow: 0 0 0 3px rgba(83, 231, 255, 0.14);
}

.pwd-error {
  color: #ff6b7a;
  font-size: 13px;
}

.pwd-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 6px;
}

.app-btn--ghost {
  min-height: 40px;
  padding: 0 18px;
  border: 1px solid var(--app-line);
  border-radius: 10px;
  color: var(--app-text);
  background: transparent;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 180ms ease;
}

.app-btn--ghost:hover {
  border-color: var(--app-cyan);
}

.app-btn--primary {
  min-height: 40px;
  padding: 0 18px;
  border: 0;
  border-radius: 10px;
  color: #041017;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  font-size: 14px;
  font-weight: 680;
  cursor: pointer;
}

.app-btn--primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.is-clickable {
  cursor: pointer;
  transition: border-color 180ms ease;
}

.is-clickable:hover {
  border-color: var(--app-cyan);
}

.pwd-fade-enter-active,
.pwd-fade-leave-active {
  transition: opacity 220ms ease;
}

.pwd-fade-enter-from,
.pwd-fade-leave-to {
  opacity: 0;
}
</style>
