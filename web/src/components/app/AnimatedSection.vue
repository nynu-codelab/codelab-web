<template>
  <section ref="sectionRef" class="app-section section-reveal" :class="{ 'is-visible': visible }">
    <slot />
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

const sectionRef = ref<HTMLElement | null>(null)
const visible = ref(false)
let observer: IntersectionObserver | null = null

onMounted(() => {
  if (!sectionRef.value || !('IntersectionObserver' in window)) {
    visible.value = true
    return
  }

  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry?.isIntersecting) {
        visible.value = true
        observer?.disconnect()
      }
    },
    { threshold: 0.18 }
  )

  observer.observe(sectionRef.value)
})

onBeforeUnmount(() => observer?.disconnect())
</script>
