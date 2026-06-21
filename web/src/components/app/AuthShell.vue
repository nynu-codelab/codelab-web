<template>
  <AppFrame :footer="false">
    <section class="auth-shell">
      <div class="auth-shell__intro">
        <span class="app-eyebrow">{{ eyebrow }}</span>
        <h1>{{ title }}</h1>
        <p>{{ description }}</p>
        <div class="auth-shell__panel glass-card">
          <span>NYNU Code Lab</span>
          <strong>项目实战 · 技术分享 · 工程能力培养</strong>
        </div>
      </div>
      <div class="auth-shell__card glass-card">
        <slot />
      </div>
    </section>
  </AppFrame>
</template>

<script setup lang="ts">
import AppFrame from './AppFrame.vue'

defineProps<{
  eyebrow: string
  title: string
  description: string
}>()
</script>

<style scoped>
.auth-shell {
  width: var(--app-width);
  min-height: calc(100vh - 72px);
  margin: 0 auto;
  padding: 56px 0;
  display: grid;
  grid-template-columns: minmax(0, 0.86fr) minmax(340px, 0.62fr);
  gap: 42px;
  align-items: center;
}

.auth-shell__intro h1 {
  color: var(--app-text-strong);
  font-size: clamp(40px, 6vw, 76px);
  line-height: 1.04;
}

.auth-shell__intro p {
  max-width: 620px;
  margin-top: 20px;
  color: var(--app-soft);
  font-size: 18px;
  line-height: 1.8;
}

.auth-shell__panel {
  display: grid;
  gap: 10px;
  max-width: 520px;
  margin-top: 32px;
  padding: 22px;
}

.auth-shell__panel span {
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.auth-shell__panel strong {
  color: var(--app-text-strong);
  font-size: 18px;
}

.auth-shell__card {
  padding: clamp(24px, 4vw, 34px);
}

@media (max-width: 900px) {
  .auth-shell {
    min-height: auto;
    grid-template-columns: 1fr;
    padding: 36px 0 58px;
  }
}
</style>
