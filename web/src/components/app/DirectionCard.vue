<template>
  <article class="direction-card glass-card is-hoverable">
    <div class="direction-card__mesh" aria-hidden="true"></div>
    <div class="direction-card__route" aria-hidden="true">
      <span></span>
      <span></span>
      <span></span>
    </div>
    <div class="direction-card__matrix" aria-hidden="true">
      <i v-for="cell in 12" :key="cell"></i>
    </div>
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
  position: relative;
  display: flex;
  min-height: 250px;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
  transform-origin: center top;
}

.direction-card__mesh {
  position: absolute;
  inset: auto 18px 18px auto;
  width: 92px;
  height: 92px;
  border: 1px solid rgba(83, 231, 255, 0.14);
  border-radius: 999px;
  background:
    radial-gradient(circle, rgba(83, 231, 255, 0.16), transparent 58%),
    conic-gradient(from 120deg, transparent, rgba(83, 231, 255, 0.38), transparent);
  opacity: 0.5;
  filter: blur(0.1px);
  animation: direction-orbit 10s linear infinite;
}

.direction-card__route {
  position: absolute;
  right: 26px;
  bottom: 28px;
  width: 128px;
  height: 74px;
  border-right: 1px solid rgba(153, 217, 255, 0.15);
  border-bottom: 1px solid rgba(153, 217, 255, 0.15);
  border-radius: 0 0 18px;
  opacity: 0.7;
}

.direction-card__route span {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--app-cyan);
  box-shadow: 0 0 18px rgba(83, 231, 255, 0.62);
}

.direction-card__route span:nth-child(1) {
  right: -4px;
  top: 0;
}

.direction-card__route span:nth-child(2) {
  left: 36px;
  bottom: -4px;
  background: var(--app-teal);
}

.direction-card__route span:nth-child(3) {
  right: 38px;
  bottom: -4px;
  background: var(--app-amber);
  box-shadow: 0 0 18px rgba(255, 211, 106, 0.5);
}

.direction-card__matrix {
  position: absolute;
  inset: auto 22px 22px auto;
  display: grid;
  grid-template-columns: repeat(4, 10px);
  gap: 6px;
  opacity: 0.48;
  transform: translateZ(20px);
}

.direction-card__matrix i {
  width: 10px;
  height: 10px;
  border: 1px solid rgba(153, 217, 255, 0.15);
  border-radius: 3px;
  background: rgba(83, 231, 255, 0.045);
  transition:
    background 220ms ease,
    border-color 220ms ease,
    transform 220ms ease;
}

.direction-card__matrix i:nth-child(3n + 1) {
  border-color: rgba(47, 240, 182, 0.22);
}

.direction-card__matrix i:nth-child(5),
.direction-card__matrix i:nth-child(10) {
  background: rgba(255, 211, 106, 0.12);
  border-color: rgba(255, 211, 106, 0.22);
}

.direction-card:hover .direction-card__matrix i {
  border-color: rgba(83, 231, 255, 0.35);
  background: rgba(83, 231, 255, 0.11);
  transform: translateY(-2px);
}

.direction-card:hover .direction-card__matrix i:nth-child(5),
.direction-card:hover .direction-card__matrix i:nth-child(10) {
  background: rgba(255, 211, 106, 0.2);
  border-color: rgba(255, 211, 106, 0.38);
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

@keyframes direction-orbit {
  to {
    transform: rotate(360deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .direction-card__mesh {
    animation: none;
  }
}
</style>
