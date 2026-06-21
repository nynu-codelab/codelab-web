<template>
  <header class="preview-header" :class="{ 'is-scrolled': scrolled }">
    <div class="preview-header__inner">
      <RouterLink to="/design-preview" class="preview-header__brand" @click="closeMenu">
        <span class="preview-header__mark">CL</span>
        <span>
          <strong>NYNU Code Lab</strong>
          <small>Design Preview</small>
        </span>
      </RouterLink>

      <button class="preview-header__menu" type="button" @click="toggleMenu">
        {{ open ? '关闭' : '菜单' }}
      </button>

      <nav class="preview-header__nav" :class="{ 'is-open': open }">
        <a v-for="item in navItems" :key="item.href" :href="item.href" @click="closeMenu">
          {{ item.label }}
        </a>
        <RouterLink to="/recruit" class="preview-header__cta" @click="closeMenu">招新报名</RouterLink>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

const open = ref(false)
const scrolled = ref(false)

const navItems = [
  { label: '首页', href: '#hero' },
  { label: '技术方向', href: '#directions' },
  { label: '项目成果', href: '#projects' },
  { label: '学习文章', href: '#articles' },
  { label: '联系我们', href: '#footer' }
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

onMounted(() => {
  updateScroll()
  window.addEventListener('scroll', updateScroll, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', updateScroll)
})
</script>

<style scoped>
.preview-header {
  position: sticky;
  top: 0;
  z-index: 20;
  border-bottom: 1px solid transparent;
  background: rgba(6, 9, 15, 0.5);
  backdrop-filter: blur(18px);
  transition:
    border-color 220ms ease,
    background 220ms ease,
    box-shadow 220ms ease;
}

.preview-header.is-scrolled {
  border-color: rgba(153, 217, 255, 0.16);
  background: rgba(6, 9, 15, 0.82);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.28);
}

.preview-header__inner {
  width: var(--preview-width);
  min-height: 72px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.preview-header__brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.preview-header__mark {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border: 1px solid rgba(83, 231, 255, 0.45);
  border-radius: 12px;
  color: #041017;
  background: linear-gradient(135deg, var(--preview-cyan), var(--preview-teal));
  box-shadow: 0 0 36px rgba(83, 231, 255, 0.22);
  font-family: var(--preview-font-data);
  font-size: 13px;
  font-weight: 800;
}

.preview-header__brand strong {
  display: block;
  color: #f8fcff;
  font-size: 15px;
  line-height: 1.2;
}

.preview-header__brand small {
  display: block;
  color: var(--preview-muted);
  font-family: var(--preview-font-data);
  font-size: 11px;
}

.preview-header__nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.preview-header__nav a {
  color: #c4d2e1;
  font-size: 14px;
  transition: color 180ms ease;
}

.preview-header__nav a:hover {
  color: var(--preview-cyan);
}

.preview-header__cta {
  min-height: 38px;
  padding: 10px 16px;
  border: 1px solid rgba(83, 231, 255, 0.5);
  border-radius: 999px;
  color: #041017 !important;
  background: linear-gradient(135deg, var(--preview-cyan), var(--preview-teal));
  font-weight: 720;
}

.preview-header__menu {
  display: none;
  min-height: 38px;
  padding: 8px 14px;
  border: 1px solid rgba(153, 217, 255, 0.2);
  border-radius: 999px;
  color: var(--preview-text);
  background: rgba(255, 255, 255, 0.06);
}

@media (max-width: 820px) {
  .preview-header__inner {
    min-height: 66px;
  }

  .preview-header__menu {
    display: inline-flex;
    align-items: center;
  }

  .preview-header__nav {
    position: absolute;
    left: 14px;
    right: 14px;
    top: calc(100% + 8px);
    display: grid;
    gap: 4px;
    padding: 14px;
    border: 1px solid var(--preview-line);
    border-radius: var(--preview-radius);
    background: rgba(8, 14, 24, 0.96);
    box-shadow: var(--preview-shadow);
    opacity: 0;
    pointer-events: none;
    transform: translateY(-8px);
    transition:
      opacity 200ms ease,
      transform 200ms ease;
  }

  .preview-header__nav.is-open {
    opacity: 1;
    pointer-events: auto;
    transform: translateY(0);
  }

  .preview-header__nav a {
    padding: 12px;
    border-radius: 12px;
  }

  .preview-header__cta {
    justify-content: center;
    text-align: center;
  }
}
</style>
