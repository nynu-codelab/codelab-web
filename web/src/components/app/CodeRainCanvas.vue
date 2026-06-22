<template>
  <canvas ref="canvasRef" class="code-rain" aria-hidden="true"></canvas>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
const glyphs = '01{}[]<>/\\$#includeconstletvarpublicclassCodeLabVueSpringDockerMySQL'
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let resizeFrameId = 0
let columns = 0
let drops: number[] = []
let width = 0
let height = 0
let dpr = 1
let active = false
let pageVisible = true
let lastFrameTime = 0

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

function scheduleResize() {
  if (resizeFrameId) return
  resizeFrameId = requestAnimationFrame(() => {
    resizeFrameId = 0
    resizeCanvas()
  })
}

function draw(time: number) {
  if (!ctx || !active) return

  const deltaMs = lastFrameTime > 0 ? time - lastFrameTime : 42
  if (deltaMs >= 42) {
    lastFrameTime = time
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

  if (active) animationId = requestAnimationFrame(draw)
}

function handleVisibilityChange() {
  pageVisible = !document.hidden
  updateLoopState()
}

function startLoop() {
  if (active || !ctx || !shouldAnimate() || !pageVisible) return

  active = true
  lastFrameTime = 0
  animationId = requestAnimationFrame(draw)
}

function pauseLoop() {
  if (!active) return

  active = false
  cancelAnimationFrame(animationId)
  animationId = 0
  lastFrameTime = 0
}

function updateLoopState() {
  if (pageVisible && shouldAnimate()) {
    startLoop()
  } else {
    pauseLoop()
  }
}

function setup() {
  const canvas = canvasRef.value
  if (!canvas || !shouldAnimate()) return

  ctx = canvas.getContext('2d')
  if (!ctx) return

  resizeCanvas()
  pageVisible = !document.hidden
  document.addEventListener('visibilitychange', handleVisibilityChange)
  window.addEventListener('resize', scheduleResize)
  updateLoopState()
}

function stop() {
  pauseLoop()
  cancelAnimationFrame(resizeFrameId)
  resizeFrameId = 0
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('resize', scheduleResize)
  ctx = null
  drops = []
}

onMounted(setup)
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
