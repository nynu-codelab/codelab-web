<template>
  <article class="metric-card glass-card is-hoverable">
    <span class="metric-card__value">{{ value }}</span>
    <h3>{{ label }}</h3>
    <p>{{ caption }}</p>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  value: string
  label: string
  caption: string
}>()
</script>

<style scoped>
.metric-card {
  padding: 24px;
}

.metric-card__value {
  display: block;
  margin-bottom: 12px;
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: clamp(28px, 4vw, 42px);
  font-weight: 820;
  line-height: 1;
  text-shadow: 0 0 26px rgba(83, 231, 255, 0.38);
}

.metric-card h3 {
  margin-bottom: 8px;
  color: var(--app-text-strong);
  font-size: 17px;
}

.metric-card p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.7;
}
</style>
