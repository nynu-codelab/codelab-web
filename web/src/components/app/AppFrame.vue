<template>
  <div class="app-page" @pointermove="handlePointerMove">
    <AppAnimatedBackground />
    <div class="app-shell">
      <AppHeader />
      <main>
        <slot />
      </main>
      <AppFooter v-if="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import AppAnimatedBackground from './AppAnimatedBackground.vue'
import AppFooter from './AppFooter.vue'
import AppHeader from './AppHeader.vue'

withDefaults(
  defineProps<{
    footer?: boolean
  }>(),
  {
    footer: true
  }
)

function handlePointerMove(event: PointerEvent) {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    return
  }

  document.documentElement.style.setProperty('--app-pointer-x', `${event.clientX}px`)
  document.documentElement.style.setProperty('--app-pointer-y', `${event.clientY}px`)
}
</script>
