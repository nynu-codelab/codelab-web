<template>
  <header class="app-header" :class="{ 'is-scrolled': scrolled }">
    <div class="app-header__inner">
      <RouterLink to="/" class="app-header__brand" @click="closeMenu">
        <span class="app-header__mark">
          <img :src="brandMark" alt="" aria-hidden="true" />
        </span>
        <span>
          <strong>NYNU Code Lab</strong>
          <small>南阳师范学院 Code Lab 实验室</small>
        </span>
      </RouterLink>

      <button class="app-header__menu" type="button" @click="toggleMenu">
        {{ open ? '关闭' : '菜单' }}
      </button>

      <nav class="app-header__nav" :class="{ 'is-open': open }">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" @click="closeMenu">
          {{ item.label }}
        </RouterLink>
        <template v-if="userStore.isLoggedIn">
          <RouterLink to="/profile" class="app-header__soft" @click="closeMenu">个人中心</RouterLink>
          <button class="app-header__logout" type="button" @click="handleLogout">退出</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="app-header__soft" @click="closeMenu">登录</RouterLink>
          <RouterLink to="/register" class="app-header__cta" @click="closeMenu">注册</RouterLink>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import brandMark from '@/assets/brand/nynu-code-lab-mark.svg'

const router = useRouter()
const userStore = useUserStore()
const open = ref(false)
const scrolled = ref(false)

const navItems = [
  { label: '首页', to: '/' },
  { label: '实验室介绍', to: '/about' },
  { label: '部门与方向', to: '/directions' },
  { label: '核心成员', to: '/members' },
  { label: '项目成果', to: '/projects' },
  { label: '学习文章', to: '/articles' },
  { label: '招新报名', to: '/recruit' }
]

function toggleMenu() {
  open.value = !open.value
}

function closeMenu() {
  open.value = false
}

function updateScroll() {
  scrolled.value = window.scrollY > 18
}

function handleLogout() {
  userStore.logout()
  closeMenu()
  router.push('/')
}

onMounted(() => {
  updateScroll()
  window.addEventListener('scroll', updateScroll, { passive: true })
})

onBeforeUnmount(() => window.removeEventListener('scroll', updateScroll))
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 30;
  border-bottom: 1px solid transparent;
  background: rgba(6, 9, 15, 0.52);
  backdrop-filter: blur(18px);
  transition:
    border-color 220ms ease,
    background 220ms ease,
    box-shadow 220ms ease;
}

.app-header.is-scrolled {
  border-color: rgba(153, 217, 255, 0.16);
  background: rgba(6, 9, 15, 0.84);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.28);
}

.app-header__inner {
  width: var(--app-width);
  min-height: 72px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.app-header__brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.app-header__mark {
  position: relative;
  isolation: isolate;
  overflow: hidden;
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  flex: 0 0 auto;
  border: 1px solid rgba(83, 231, 255, 0.28);
  border-radius: 12px;
  background: rgba(4, 10, 18, 0.78);
  box-shadow:
    0 0 30px rgba(83, 231, 255, 0.18),
    inset 0 0 22px rgba(83, 231, 255, 0.06);
}

.app-header__mark::after {
  content: "";
  position: absolute;
  inset: -40% -70%;
  z-index: 1;
  background: linear-gradient(115deg, transparent 40%, rgba(255, 255, 255, 0.28), transparent 60%);
  transform: translateX(-70%) rotate(8deg);
  transition: transform 520ms ease;
}

.app-header__brand:hover .app-header__mark::after {
  transform: translateX(70%) rotate(8deg);
}

.app-header__mark img {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: block;
}

.app-header__brand strong {
  display: block;
  color: var(--app-text-strong);
  font-size: 15px;
  line-height: 1.2;
}

.app-header__brand small {
  display: block;
  max-width: 220px;
  overflow: hidden;
  color: var(--app-muted);
  font-size: 11px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.app-header__nav {
  display: flex;
  align-items: center;
  gap: 16px;
}

.app-header__nav a,
.app-header__logout {
  color: #c4d2e1;
  background: transparent;
  border: 0;
  font-size: 14px;
  transition: color 180ms ease;
}

.app-header__nav a:hover,
.app-header__nav a.router-link-active,
.app-header__logout:hover {
  color: var(--app-cyan);
}

.app-header__soft,
.app-header__logout {
  min-height: 36px;
  padding: 9px 13px;
  border: 1px solid rgba(153, 217, 255, 0.18) !important;
  border-radius: 999px;
}

.app-header__cta {
  min-height: 38px;
  padding: 10px 16px;
  border: 1px solid rgba(83, 231, 255, 0.5);
  border-radius: 999px;
  color: #041017 !important;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  font-weight: 720;
}

.app-header__menu {
  display: none;
  min-height: 38px;
  padding: 8px 14px;
  border: 1px solid rgba(153, 217, 255, 0.2);
  border-radius: 999px;
  color: var(--app-text);
  background: rgba(255, 255, 255, 0.06);
}

@media (max-width: 1080px) {
  .app-header__menu {
    display: inline-flex;
    align-items: center;
  }

  .app-header__nav {
    position: absolute;
    left: 14px;
    right: 14px;
    top: calc(100% + 8px);
    display: grid;
    gap: 4px;
    padding: 14px;
    border: 1px solid var(--app-line);
    border-radius: var(--app-radius);
    background: rgba(8, 14, 24, 0.97);
    box-shadow: var(--app-shadow);
    opacity: 0;
    pointer-events: none;
    transform: translateY(-8px);
    transition:
      opacity 200ms ease,
      transform 200ms ease;
  }

  .app-header__nav.is-open {
    opacity: 1;
    pointer-events: auto;
    transform: translateY(0);
  }

  .app-header__nav a,
  .app-header__logout {
    padding: 12px;
    border-radius: 12px;
    text-align: left;
  }

  .app-header__cta {
    text-align: center;
  }
}

@media (max-width: 520px) {
  .app-header__brand small {
    display: none;
  }
}
</style>
