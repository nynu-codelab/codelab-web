<template>
  <canvas ref="canvasRef" class="code-rain" aria-hidden="true"></canvas>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
const glyphs = '01{}[]<>/\\$#includeconstletvarpublicclassCodeLabVueSpringDockerMySQL'
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let columns = 0
let drops: number[] = []
let width = 0
let height = 0
let dpr = 1
let frame = 0
let active = false

function shouldAnimate() {
  return window.innerWidth >= 1024 && !window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function resizeCanvas() {
  const canvas = canvasRef.value
  if (!canvas || !ctx) return

  dpr = Math.min(window.devicePixelRatio || 1, 1.5)
  width = window.innerWidth
  height = window.innerHeight
  canvas.width = Math.floor(width * dpr)
  canvas.height = Math.floor(height * dpr)
  canvas.style.width = `${width}px`
  canvas.style.height = `${height}px`
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)

  columns = Math.floor(width / 24)
  drops = Array.from({ length: columns }, () => Math.random() * -height)
}

function draw() {
  if (!ctx || !active) return

  frame += 1
  if (frame % 2 === 0) {
    ctx.fillStyle = 'rgba(6, 9, 15, 0.08)'
    ctx.fillRect(0, 0, width, height)
    ctx.font = '13px SFMono-Regular, Cascadia Code, Roboto Mono, Menlo, monospace'
    ctx.textBaseline = 'top'

    for (let i = 0; i < columns; i += 1) {
      const char = glyphs[Math.floor(Math.random() * glyphs.length)]
      const x = i * 24
      const y = drops[i]
      const alpha = Math.min(0.42, Math.max(0.05, y / height))
      ctx.fillStyle = `rgba(83, 231, 255, ${alpha})`
      ctx.fillText(char, x, y)

      drops[i] += 19 + Math.random() * 9
      if (drops[i] > height + Math.random() * 1200) {
        drops[i] = Math.random() * -260
      }
    }
  }

  animationId = requestAnimationFrame(draw)
}

function start() {
  const canvas = canvasRef.value
  if (!canvas || !shouldAnimate()) return

  ctx = canvas.getContext('2d')
  if (!ctx) return

  active = true
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  animationId = requestAnimationFrame(draw)
}

function stop() {
  active = false
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resizeCanvas)
}

onMounted(start)
onBeforeUnmount(stop)
</script>

<style scoped>
.code-rain {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  opacity: 0.22;
  mask-image: linear-gradient(90deg, transparent, black 18%, black 82%, transparent);
}

@media (max-width: 1023px) {
  .code-rain {
    display: none;
  }
}
</style>
