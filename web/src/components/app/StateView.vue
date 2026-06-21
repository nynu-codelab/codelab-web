<template>
  <div class="state-view glass-card">
    <span class="state-view__signal"></span>
    <h2>{{ title }}</h2>
    <p>{{ message }}</p>
    <div class="state-view__actions" v-if="$slots.actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  message: string
}>()
</script>

<style scoped>
.state-view {
  display: grid;
  place-items: center;
  gap: 14px;
  min-height: 300px;
  padding: 42px 24px;
  text-align: center;
}

.state-view__signal {
  width: 64px;
  height: 64px;
  border: 1px solid rgba(83, 231, 255, 0.42);
  border-radius: 20px;
  background:
    linear-gradient(135deg, rgba(83, 231, 255, 0.88), rgba(47, 240, 182, 0.88)),
    radial-gradient(circle, #fff, transparent);
  box-shadow: 0 0 38px rgba(83, 231, 255, 0.28);
}

.state-view h2 {
  color: var(--app-text-strong);
  font-size: 24px;
}

.state-view p {
  max-width: 520px;
  color: var(--app-muted);
  line-height: 1.8;
}

.state-view__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
  margin-top: 10px;
}
</style>
