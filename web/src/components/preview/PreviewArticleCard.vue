<template>
  <article class="preview-card article-card">
    <div class="article-card__meta">
      <span>{{ category }}</span>
      <span>{{ readTime }}</span>
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
  category: string
  readTime: string
  title: string
  description: string
  tags: string[]
}>()
</script>

<style scoped>
.article-card {
  min-height: 276px;
  padding: 24px;
}

.article-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 34px;
  color: var(--preview-cyan);
  font-family: var(--preview-font-data);
  font-size: 12px;
}

.article-card h3 {
  color: #f9fcff;
  font-size: 22px;
  line-height: 1.28;
}

.article-card p {
  min-height: 82px;
  margin: 12px 0 20px;
  color: var(--preview-muted);
  font-size: 14px;
  line-height: 1.78;
}
</style>
