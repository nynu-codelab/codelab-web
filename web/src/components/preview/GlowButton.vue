<template>
  <RouterLink v-if="to" :to="to" class="glow-button" :class="variant">
    <span>{{ label }}</span>
  </RouterLink>
  <a v-else :href="href" class="glow-button" :class="variant">
    <span>{{ label }}</span>
  </a>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string
    to?: string
    href?: string
    variant?: 'primary' | 'secondary' | 'ghost'
  }>(),
  {
    href: '#',
    variant: 'primary'
  }
)
</script>

<style scoped>
.glow-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  padding: 12px 18px;
  overflow: hidden;
  border: 1px solid rgba(153, 217, 255, 0.22);
  border-radius: 999px;
  color: var(--preview-text);
  font-size: 14px;
  font-weight: 720;
  line-height: 1;
  white-space: nowrap;
  transition:
    transform 220ms ease,
    border-color 220ms ease,
    box-shadow 220ms ease,
    background 220ms ease;
}

.glow-button::before {
  content: "";
  position: absolute;
  inset: -1px;
  background: linear-gradient(120deg, transparent 20%, rgba(255, 255, 255, 0.7), transparent 42%);
  opacity: 0;
  transform: translateX(-72%);
}

.glow-button span {
  position: relative;
  z-index: 1;
}

.glow-button.primary {
  border-color: rgba(83, 231, 255, 0.58);
  color: #041017;
  background: linear-gradient(135deg, var(--preview-cyan), var(--preview-teal));
  box-shadow: 0 16px 54px rgba(47, 240, 182, 0.26);
}

.glow-button.secondary {
  background: rgba(255, 255, 255, 0.07);
}

.glow-button.ghost {
  color: #bfeeff;
  background: transparent;
}

.glow-button:hover {
  transform: translateY(-2px);
  border-color: rgba(83, 231, 255, 0.72);
  box-shadow: 0 22px 70px rgba(83, 231, 255, 0.2);
}

.glow-button:hover::before {
  animation: button-sheen 900ms ease;
}

@keyframes button-sheen {
  0% {
    opacity: 0;
    transform: translateX(-72%);
  }
  32% {
    opacity: 0.8;
  }
  100% {
    opacity: 0;
    transform: translateX(72%);
  }
}
</style>
