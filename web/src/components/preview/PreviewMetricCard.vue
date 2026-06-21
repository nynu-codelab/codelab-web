<template>
  <div class="preview-card metric-card">
    <span class="metric-card__value">{{ value }}</span>
    <span class="metric-card__label">{{ label }}</span>
    <p>{{ description }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  value: string
  label: string
  description: string
}>()
</script>

<style scoped>
.metric-card {
  min-height: 164px;
  padding: 22px;
}

.metric-card__value {
  display: block;
  margin-bottom: 8px;
  color: var(--preview-cyan);
  font-family: var(--preview-font-data);
  font-size: clamp(28px, 4vw, 44px);
  font-weight: 800;
  line-height: 1;
  text-shadow: 0 0 28px rgba(83, 231, 255, 0.34);
}

.metric-card__label {
  display: block;
  color: #f8fcff;
  font-size: 16px;
  font-weight: 760;
}

.metric-card p {
  margin-top: 12px;
  color: var(--preview-muted);
  font-size: 14px;
  line-height: 1.7;
}
</style>
