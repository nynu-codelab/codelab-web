<template>
  <section class="page-hero">
    <div class="page-hero__halo" aria-hidden="true"></div>
    <div class="app-container page-hero__inner">
      <div class="page-hero__content">
        <span class="app-eyebrow">{{ eyebrow }}</span>
        <h1 class="app-title-xl">{{ title }}</h1>
        <p class="page-hero__description">{{ description }}</p>
        <div class="page-hero__actions" v-if="$slots.actions">
          <slot name="actions" />
        </div>
      </div>

      <div class="page-hero__visual" v-if="$slots.visual">
        <slot name="visual" />
      </div>
      <div class="page-hero__visual page-hero__visual--auto" v-else>
        <div class="page-hero__auto glass-card">
          <span>route.signal</span>
          <strong>CodeLab interface online</strong>
          <small>project_practice / tech_sharing / review_flow</small>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  description: string
}>()
</script>

<style scoped>
.page-hero {
  position: relative;
  padding: 88px 0 62px;
  overflow: hidden;
}

.page-hero__halo {
  position: absolute;
  inset: 10% 0 auto;
  height: 360px;
  pointer-events: none;
  background:
    linear-gradient(115deg, transparent 18%, rgba(83, 231, 255, 0.1), transparent 58%),
    radial-gradient(circle at 72% 18%, rgba(47, 240, 182, 0.14), transparent 32%);
  filter: blur(8px);
}

.page-hero__inner {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(300px, 0.62fr);
  gap: 42px;
  align-items: center;
}

.page-hero__content {
  min-width: 0;
}

.page-hero__description {
  max-width: 760px;
  margin-top: 22px;
  color: var(--app-soft);
  font-size: 22px;
  line-height: 1.78;
}

.page-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 32px;
}

.page-hero__visual {
  min-width: 0;
}

.page-hero__auto {
  display: grid;
  gap: 10px;
  min-height: 220px;
  align-content: end;
  padding: 24px;
  background:
    linear-gradient(rgba(83, 231, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.07) 1px, transparent 1px),
    var(--app-surface);
  background-size: 24px 24px;
}

.page-hero__auto span,
.page-hero__auto small {
  color: var(--app-muted);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.page-hero__auto strong {
  color: var(--app-text-strong);
  font-size: 22px;
}

@media (max-width: 920px) {
  .page-hero {
    padding: 66px 0 36px;
  }

  .page-hero__inner {
    grid-template-columns: 1fr;
  }

  .page-hero__visual--auto {
    display: none;
  }

  .page-hero__description {
    font-size: 18px;
  }
}
</style>
