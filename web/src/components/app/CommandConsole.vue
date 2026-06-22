<template>
  <div class="command-console" :class="{ 'glass-card': framed, 'command-console--inline': !framed }">
    <div class="command-console__bar">
      <span></span>
      <span></span>
      <span></span>
      <strong>{{ title }}</strong>
    </div>
    <div class="command-console__body">
      <p
        v-for="(command, index) in commands"
        :key="command"
        :class="{ active: index === activeIndex }"
      >
        <span>$</span>
        <code>{{ command }}</code>
      </p>
      <small class="command-console__status">watching pipeline events...</small>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

const props = withDefaults(
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

const activeIndex = ref(0)
let timer = 0

onMounted(() => {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches || props.commands.length <= 1) return

  timer = window.setInterval(() => {
    activeIndex.value = (activeIndex.value + 1) % props.commands.length
  }, 1500)
})

onBeforeUnmount(() => {
  window.clearInterval(timer)
})
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
  position: relative;
  display: flex;
  min-width: 0;
  gap: 10px;
  padding: 6px 8px;
  border: 1px solid transparent;
  border-radius: var(--app-radius-sm);
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 13px;
  transition:
    border-color 220ms ease,
    background 220ms ease,
    color 220ms ease,
    transform 220ms ease;
}

.command-console__body p.active {
  border-color: rgba(83, 231, 255, 0.18);
  color: var(--app-text-strong);
  background: rgba(83, 231, 255, 0.055);
  transform: translateX(3px);
}

.command-console__body p.active::after {
  content: "";
  position: absolute;
  right: 9px;
  top: 50%;
  width: 6px;
  height: 14px;
  background: var(--app-amber);
  box-shadow: 0 0 14px rgba(255, 211, 106, 0.5);
  transform: translateY(-50%);
  animation: command-cursor 1s steps(2, start) infinite;
}

.command-console__body span {
  color: var(--app-cyan);
}

.command-console__body code {
  min-width: 0;
  overflow: hidden;
  padding-right: 18px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.command-console__status {
  color: rgba(158, 177, 196, 0.68);
  font-family: var(--app-font-data);
  font-size: 11px;
}

@keyframes command-cursor {
  50% {
    opacity: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .command-console__body p.active {
    transform: none;
  }

  .command-console__body p.active::after {
    animation: none;
  }
}
</style>
