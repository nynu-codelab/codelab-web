<template>
  <article class="direction-card glass-card is-hoverable">
    <div class="direction-card__topline">
      <span class="direction-card__index">{{ index }}</span>
      <span class="status-pill" :class="{ warning: badge.includes('拓展') }">{{ badge }}</span>
    </div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <div class="pill-row">
      <span class="tech-pill" v-for="tag in tags" :key="tag">{{ tag }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  index: string
  title: string
  description: string
  badge: string
  tags: string[]
}>()
</script>

<style scoped>
.direction-card {
  display: flex;
  min-height: 250px;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
}

.direction-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.direction-card__index {
  color: rgba(83, 231, 255, 0.72);
  font-family: var(--app-font-data);
  font-size: 13px;
}

.direction-card h3 {
  color: var(--app-text-strong);
  font-size: 22px;
  line-height: 1.25;
}

.direction-card p {
  flex: 1;
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.75;
}
</style>
