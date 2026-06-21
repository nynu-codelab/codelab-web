<template>
  <RouterLink :to="to" class="article-card glass-card is-hoverable">
    <div class="article-card__meta">
      <span class="status-pill">{{ category || '技术文章' }}</span>
      <span>{{ dateLabel }}</span>
    </div>
    <h3>{{ title }}</h3>
    <p>{{ summary }}</p>
    <div class="pill-row" v-if="tags.length">
      <span class="tech-pill" v-for="tag in tags" :key="tag">{{ tag }}</span>
    </div>
    <span class="article-card__link">阅读全文</span>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  title: string
  summary: string
  to: string
  category?: string
  tags?: string[]
  publishedAt?: string | null
}>()

const tags = computed(() => props.tags ?? [])
const dateLabel = computed(() => {
  if (!props.publishedAt) return '预览文章'
  return props.publishedAt.slice(0, 10)
})
</script>

<style scoped>
.article-card {
  display: flex;
  min-height: 260px;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  color: inherit;
}

.article-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  color: var(--app-muted);
  font-size: 12px;
}

.article-card h3 {
  color: var(--app-text-strong);
  font-size: 22px;
  line-height: 1.34;
}

.article-card p {
  flex: 1;
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.78;
}

.article-card__link {
  color: var(--app-cyan);
  font-weight: 720;
}
</style>
