<template>
  <RouterLink :to="to" class="article-card glass-card is-hoverable">
    <div class="article-card__rail" aria-hidden="true">
      <span></span>
      <span></span>
      <span></span>
    </div>
    <div class="article-card__code" aria-hidden="true">
      <span>function read(note) {</span>
      <span>  return buildKnowledge(note)</span>
      <span>}</span>
    </div>
    <div class="article-card__topography" aria-hidden="true">
      <i></i>
      <i></i>
      <i></i>
    </div>
    <div class="article-card__meta">
      <span class="status-pill">{{ category || '技术文章' }}</span>
      <span>{{ dateLabel }}</span>
    </div>
    <div class="article-card__labnote">
      <span>research log</span>
      <span>markdown safe</span>
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
  position: relative;
  display: flex;
  min-height: 260px;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  color: inherit;
  isolation: isolate;
}

.article-card__rail {
  position: absolute;
  inset: 18px auto 18px 14px;
  display: grid;
  align-content: space-between;
  width: 1px;
  background: linear-gradient(180deg, transparent, rgba(83, 231, 255, 0.34), transparent);
  opacity: 0.56;
}

.article-card__rail span {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: rgba(83, 231, 255, 0.72);
  box-shadow: 0 0 18px rgba(83, 231, 255, 0.46);
  transform: translateX(-3px);
}

.article-card__code {
  position: absolute;
  right: 18px;
  bottom: 16px;
  display: grid;
  gap: 3px;
  pointer-events: none;
  color: rgba(83, 231, 255, 0.16);
  font-family: var(--app-font-data);
  font-size: 11px;
  line-height: 1.2;
  text-align: right;
}

.article-card__topography {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  opacity: 0.42;
  mask-image: linear-gradient(135deg, transparent, black 28%, transparent 78%);
}

.article-card__topography::before,
.article-card__topography::after,
.article-card__topography i {
  content: "";
  position: absolute;
  border: 1px solid rgba(83, 231, 255, 0.16);
  border-radius: 999px;
}

.article-card__topography::before {
  width: 280px;
  height: 120px;
  right: -110px;
  top: 44px;
  transform: rotate(-24deg);
}

.article-card__topography::after {
  width: 210px;
  height: 90px;
  right: -72px;
  top: 90px;
  transform: rotate(-24deg);
}

.article-card__topography i:nth-child(1) {
  width: 140px;
  height: 58px;
  right: -38px;
  top: 136px;
  transform: rotate(-24deg);
}

.article-card__topography i:nth-child(2) {
  left: 34px;
  bottom: 28px;
  width: 7px;
  height: 7px;
  background: var(--app-cyan);
  box-shadow: 0 0 18px rgba(83, 231, 255, 0.58);
}

.article-card__topography i:nth-child(3) {
  left: 74px;
  bottom: 64px;
  width: 5px;
  height: 5px;
  background: var(--app-amber);
  box-shadow: 0 0 18px rgba(255, 211, 106, 0.44);
}

.article-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  color: var(--app-muted);
  font-size: 12px;
}

.article-card__labnote {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: rgba(216, 247, 255, 0.58);
  font-family: var(--app-font-data);
  font-size: 10px;
}

.article-card__labnote span {
  padding: 5px 7px;
  border: 1px solid rgba(153, 217, 255, 0.11);
  border-radius: var(--app-radius-sm);
  background: rgba(255, 255, 255, 0.035);
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
  transition:
    letter-spacing 220ms ease,
    text-shadow 220ms ease;
}

.article-card:hover .article-card__link {
  letter-spacing: 0.03em;
  text-shadow: 0 0 20px rgba(83, 231, 255, 0.48);
}

.article-card:hover .article-card__code {
  color: rgba(83, 231, 255, 0.22);
}

.article-card:hover .article-card__rail {
  opacity: 0.9;
}

.article-card:hover .article-card__topography {
  opacity: 0.62;
}
</style>
