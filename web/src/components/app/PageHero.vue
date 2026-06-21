<template>
  <section class="page-hero">
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
}

.page-hero__inner {
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
  font-size: clamp(18px, 2vw, 22px);
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

@media (max-width: 920px) {
  .page-hero {
    padding: 66px 0 36px;
  }

  .page-hero__inner {
    grid-template-columns: 1fr;
  }
}
</style>
