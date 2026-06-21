<template>
  <article class="preview-card direction-card">
    <div class="direction-card__top">
      <span class="direction-card__code">{{ code }}</span>
      <span v-if="badge" class="direction-card__badge">{{ badge }}</span>
    </div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <div class="preview-pill-row">
      <span v-for="item in tags" :key="item" class="preview-pill">{{ item }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  code: string
  title: string
  description: string
  tags: string[]
  badge?: string
}>()
</script>

<style scoped>
.direction-card {
  min-height: 286px;
  padding: 24px;
}

.direction-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 28px;
}

.direction-card__code {
  color: rgba(83, 231, 255, 0.84);
  font-family: var(--preview-font-data);
  font-size: 13px;
}

.direction-card__badge {
  padding: 5px 9px;
  border: 1px solid rgba(255, 211, 106, 0.24);
  border-radius: 999px;
  color: var(--preview-amber);
  background: rgba(255, 211, 106, 0.08);
  font-size: 12px;
}

.direction-card h3 {
  color: #f9fcff;
  font-size: 22px;
  line-height: 1.25;
}

.direction-card p {
  min-height: 74px;
  margin: 12px 0 20px;
  color: var(--preview-muted);
  font-size: 14px;
  line-height: 1.75;
}
</style>
