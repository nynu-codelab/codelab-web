<template>
  <RouterLink :to="to" class="project-card glass-card is-hoverable">
    <div class="project-card__visual" :style="coverStyle">
      <div class="project-card__scan"></div>
      <div class="project-card__terminal" v-if="!coverUrl">
        <span></span>
        <span></span>
        <span></span>
        <code>deploy preview --pipeline project</code>
      </div>
    </div>
    <div class="project-card__body">
      <div class="project-card__meta">
        <span class="status-pill">{{ type || '项目实践' }}</span>
        <span class="project-card__pass" v-if="meta">{{ meta }}</span>
        <span class="project-card__pass" v-else>BUILD PASS</span>
      </div>
      <h3>{{ title }}</h3>
      <p>{{ summary }}</p>
      <div class="pill-row" v-if="tags.length">
        <span class="tech-pill" v-for="tag in tags" :key="tag">{{ tag }}</span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  title: string
  summary: string
  to: string
  type?: string
  meta?: string
  tags?: string[]
  coverUrl?: string
}>()

const tags = computed(() => props.tags ?? [])
const coverStyle = computed(() =>
  props.coverUrl
    ? { backgroundImage: `linear-gradient(180deg, rgba(5, 9, 16, 0.08), rgba(5, 9, 16, 0.52)), url("${props.coverUrl}")` }
    : {}
)
</script>

<style scoped>
.project-card {
  display: grid;
  min-height: 100%;
  color: inherit;
}

.project-card__visual {
  position: relative;
  overflow: hidden;
  min-height: 190px;
  background:
    radial-gradient(circle at 24% 20%, rgba(83, 231, 255, 0.24), transparent 28%),
    radial-gradient(circle at 86% 12%, rgba(169, 139, 255, 0.22), transparent 30%),
    linear-gradient(135deg, rgba(13, 22, 34, 0.9), rgba(5, 10, 18, 0.92));
  background-position: center;
  background-size: cover;
  border-bottom: 1px solid rgba(153, 217, 255, 0.14);
}

.project-card__visual::after {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(135deg, transparent 42%, rgba(83, 231, 255, 0.12), transparent 58%),
    linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 100% 100%, 100% 10px;
  opacity: 0.74;
}

.project-card__scan {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.28), transparent);
  opacity: 0;
  transform: translateX(-120%);
}

.project-card:hover .project-card__scan {
  opacity: 1;
  animation: project-scan 1.4s ease;
}

.project-card__terminal {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 8px 8px 8px minmax(0, 1fr);
  gap: 7px;
  align-items: center;
  margin: 24px;
  padding: 14px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 12px;
  background: rgba(2, 8, 15, 0.68);
  box-shadow: inset 0 0 34px rgba(83, 231, 255, 0.06);
}

.project-card__terminal span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--app-cyan);
}

.project-card__terminal span:nth-child(2) {
  background: var(--app-amber);
}

.project-card__terminal span:nth-child(3) {
  background: var(--app-danger);
}

.project-card__terminal code {
  min-width: 0;
  overflow: hidden;
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.project-card__body {
  display: grid;
  gap: 14px;
  padding: 24px;
}

.project-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  font-size: 12px;
}

.project-card h3 {
  color: var(--app-text-strong);
  font-size: 21px;
  line-height: 1.3;
}

.project-card p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.75;
}

.project-card__pass {
  color: var(--app-success);
  font-family: var(--app-font-data);
  font-size: 11px;
}

@keyframes project-scan {
  to {
    transform: translateX(120%);
  }
}
</style>
