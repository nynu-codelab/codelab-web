<template>
  <RouterLink v-if="to" :to="to" class="app-button" :class="[variant, size]" @click="emit('click', $event)">
    <span>{{ label }}</span>
  </RouterLink>
  <a
    v-else-if="href"
    :href="href"
    :target="target"
    :rel="target === '_blank' ? 'noopener noreferrer' : undefined"
    class="app-button"
    :class="[variant, size]"
    @click="emit('click', $event)"
  >
    <span>{{ label }}</span>
  </a>
  <button
    v-else
    class="app-button"
    :class="[variant, size]"
    :type="type"
    :disabled="disabled"
    @click="emit('click', $event)"
  >
    <span>{{ label }}</span>
  </button>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string
    to?: string
    href?: string
    variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
    size?: 'sm' | 'md' | 'lg'
    type?: 'button' | 'submit'
    disabled?: boolean
    target?: '_self' | '_blank'
  }>(),
  {
    variant: 'primary',
    size: 'md',
    type: 'button',
    disabled: false,
    target: '_self'
  }
)

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()
</script>

<style scoped>
.app-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 1px solid rgba(153, 217, 255, 0.22);
  border-radius: 999px;
  color: var(--app-text);
  font-size: 14px;
  font-weight: 720;
  line-height: 1;
  white-space: nowrap;
  transition:
    transform 220ms ease,
    border-color 220ms ease,
    box-shadow 220ms ease,
    background 220ms ease,
    opacity 220ms ease;
}

.app-button.sm {
  min-height: 34px;
  padding: 9px 13px;
  font-size: 13px;
}

.app-button.md {
  min-height: 44px;
  padding: 12px 18px;
}

.app-button.lg {
  min-height: 50px;
  padding: 15px 22px;
}

.app-button::before {
  content: "";
  position: absolute;
  inset: -1px;
  background: linear-gradient(120deg, transparent 20%, rgba(255, 255, 255, 0.68), transparent 42%);
  opacity: 0;
  transform: translateX(-72%);
}

.app-button span {
  position: relative;
  z-index: 1;
}

.app-button.primary {
  border-color: rgba(83, 231, 255, 0.58);
  color: #041017;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  box-shadow: 0 16px 54px rgba(47, 240, 182, 0.25);
}

.app-button.secondary {
  background: rgba(255, 255, 255, 0.07);
}

.app-button.ghost {
  color: #bfeeff;
  background: transparent;
}

.app-button.danger {
  border-color: rgba(255, 107, 138, 0.32);
  color: var(--app-danger);
  background: rgba(255, 107, 138, 0.08);
}

.app-button:hover:not(:disabled) {
  transform: translateY(-2px);
  border-color: rgba(83, 231, 255, 0.72);
  box-shadow: 0 22px 70px rgba(83, 231, 255, 0.18);
}

.app-button:hover:not(:disabled)::before {
  animation: button-sheen 900ms ease;
}

.app-button:disabled {
  cursor: not-allowed;
  opacity: 0.56;
}
</style>
