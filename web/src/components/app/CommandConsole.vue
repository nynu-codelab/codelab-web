<template>
  <div class="command-console" :class="{ 'glass-card': framed, 'command-console--inline': !framed }">
    <div class="command-console__bar">
      <span></span>
      <span></span>
      <span></span>
      <strong>{{ title }}</strong>
    </div>
    <div class="command-console__body">
      <p v-for="command in commands" :key="command">
        <span>$</span>
        <code>{{ command }}</code>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title?: string
    commands: string[]
    framed?: boolean
  }>(),
  {
    title: 'codelab.terminal',
    framed: true
  }
)
</script>

<style scoped>
.command-console {
  padding: 18px;
}

.command-console--inline {
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius-sm);
  background: rgba(4, 10, 18, 0.5);
  box-shadow: inset 0 0 34px rgba(83, 231, 255, 0.045);
}

.command-console__bar {
  display: grid;
  grid-template-columns: 9px 9px 9px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(153, 217, 255, 0.12);
}

.command-console__bar span {
  width: 9px;
  height: 9px;
  border-radius: 999px;
  background: var(--app-danger);
}

.command-console__bar span:nth-child(2) {
  background: var(--app-amber);
}

.command-console__bar span:nth-child(3) {
  background: var(--app-success);
}

.command-console__bar strong {
  justify-self: end;
  color: var(--app-muted);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.command-console__body {
  display: grid;
  gap: 12px;
  padding-top: 18px;
}

.command-console__body p {
  display: flex;
  min-width: 0;
  gap: 10px;
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 13px;
}

.command-console__body span {
  color: var(--app-cyan);
}

.command-console__body code {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
