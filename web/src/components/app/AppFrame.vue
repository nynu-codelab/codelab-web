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
import { onBeforeUnmount } from 'vue'
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

let pointerFrameId = 0
let pointerX = 0
let pointerY = 0

function applyPointer() {
  pointerFrameId = 0
  document.documentElement.style.setProperty('--app-pointer-x', `${pointerX}px`)
  document.documentElement.style.setProperty('--app-pointer-y', `${pointerY}px`)
}

function handlePointerMove(event: PointerEvent) {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    return
  }

  pointerX = event.clientX
  pointerY = event.clientY
  if (!pointerFrameId) {
    pointerFrameId = requestAnimationFrame(applyPointer)
  }
}

onBeforeUnmount(() => {
  cancelAnimationFrame(pointerFrameId)
})
</script>
