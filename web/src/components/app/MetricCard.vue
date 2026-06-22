<template>
  <article class="metric-card glass-card is-hoverable">
    <span class="metric-card__value"><DataCounter :value="value" /></span>
    <h3>{{ label }}</h3>
    <p>{{ caption }}</p>
  </article>
</template>

<script setup lang="ts">
import DataCounter from './DataCounter.vue'

defineProps<{
  value: string
  label: string
  caption: string
}>()
</script>

<style scoped>
.metric-card {
  position: relative;
  padding: 24px;
}

.metric-card::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.16), transparent);
  opacity: 0;
  transform: translateX(-100%);
  transition: opacity 220ms ease;
}

.metric-card:hover::before {
  opacity: 1;
  animation: metric-scan 1.2s ease;
}

.metric-card__value {
  display: block;
  margin-bottom: 12px;
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 42px;
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

@keyframes metric-scan {
  to {
    transform: translateX(100%);
  }
}

@media (max-width: 720px) {
  .metric-card__value {
    font-size: 32px;
  }
}
</style>
