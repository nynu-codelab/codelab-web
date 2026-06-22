<template>
  <span ref="valueRef">{{ displayValue }}</span>
</template>

<script setup lang="ts">
import { CountUp } from 'countup.js'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps<{
  value: string
}>()

const valueRef = ref<HTMLElement | null>(null)
let counter: CountUp | null = null

const numericValue = computed(() => {
  if (!/^\d+(\.\d+)?$/.test(props.value)) return null
  return Number(props.value)
})

const displayValue = computed(() => (numericValue.value === null ? props.value : '0'))

onMounted(() => {
  if (!valueRef.value || numericValue.value === null) return
  counter = new CountUp(valueRef.value, numericValue.value, {
    duration: 1.8,
    useEasing: true,
    separator: ''
  })
  if (!counter.error) {
    counter.start()
  } else {
    valueRef.value.textContent = props.value
  }
})

onBeforeUnmount(() => {
  counter = null
})
</script>
