<template>
  <article class="member-card glass-card is-hoverable">
    <div class="member-card__avatar">{{ initials }}</div>
    <div>
      <span class="status-pill">{{ role }}</span>
      <h3>{{ title }}</h3>
      <p>{{ summary }}</p>
    </div>
    <div class="pill-row">
      <span class="tech-pill" v-for="tag in tags" :key="tag">{{ tag }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  initials: string
  role: string
  title: string
  summary: string
  tags: string[]
}>()
</script>

<style scoped>
.member-card {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.member-card__avatar {
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border: 1px solid rgba(83, 231, 255, 0.38);
  border-radius: 18px;
  color: #041017;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  box-shadow: 0 0 34px rgba(83, 231, 255, 0.2);
  font-family: var(--app-font-data);
  font-weight: 840;
}

.member-card h3 {
  margin: 14px 0 8px;
  color: var(--app-text-strong);
  font-size: 21px;
}

.member-card p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.75;
}
</style>
