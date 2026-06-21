<template>
  <article class="preview-card project-card">
    <div class="project-card__visual">
      <span>{{ code }}</span>
      <div class="project-card__bars">
        <i></i>
        <i></i>
        <i></i>
      </div>
    </div>
    <div class="project-card__body">
      <span class="project-card__type">{{ type }}</span>
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
      <div class="preview-pill-row">
        <span v-for="item in tags" :key="item" class="preview-pill">{{ item }}</span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  code: string
  type: string
  title: string
  description: string
  tags: string[]
}>()
</script>

<style scoped>
.project-card {
  min-height: 360px;
}

.project-card__visual {
  display: flex;
  min-height: 144px;
  padding: 18px;
  align-items: flex-start;
  justify-content: space-between;
  border-bottom: 1px solid rgba(153, 217, 255, 0.14);
  background:
    radial-gradient(circle at 26% 30%, rgba(83, 231, 255, 0.26), transparent 34%),
    linear-gradient(135deg, rgba(83, 231, 255, 0.14), rgba(169, 139, 255, 0.12));
}

.project-card__visual span {
  font-family: var(--preview-font-data);
  color: #dff8ff;
  font-size: 13px;
}

.project-card__bars {
  display: grid;
  gap: 7px;
  width: 116px;
}

.project-card__bars i {
  display: block;
  height: 7px;
  border-radius: 999px;
  background: rgba(238, 247, 255, 0.22);
  transform-origin: right;
  animation: project-bar 2.8s ease-in-out infinite;
}

.project-card__bars i:nth-child(2) {
  width: 74%;
  animation-delay: -0.4s;
}

.project-card__bars i:nth-child(3) {
  width: 54%;
  animation-delay: -0.9s;
}

.project-card__body {
  padding: 22px;
}

.project-card__type {
  color: var(--preview-cyan);
  font-family: var(--preview-font-data);
  font-size: 12px;
}

.project-card h3 {
  margin: 10px 0;
  color: #f9fcff;
  font-size: 21px;
  line-height: 1.3;
}

.project-card p {
  min-height: 76px;
  margin-bottom: 18px;
  color: var(--preview-muted);
  font-size: 14px;
  line-height: 1.75;
}

@keyframes project-bar {
  0%,
  100% {
    transform: scaleX(0.55);
    opacity: 0.52;
  }
  50% {
    transform: scaleX(1);
    opacity: 1;
  }
}
</style>
