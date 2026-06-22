<template>
  <canvas ref="canvasRef" class="particle-universe" aria-hidden="true"></canvas>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

type Particle = {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  pulse: number
}

const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let resizeFrameId = 0
let particles: Particle[] = []
let width = 0
let height = 0
let dpr = 1
let pointer = { x: -9999, y: -9999 }
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

  const areaFactor = Math.sqrt((width * height) / (1440 * 1000))
  const count = Math.min(82, Math.max(52, Math.floor(62 * areaFactor + width / 44)))
  particles = Array.from({ length: count }, () => ({
    x: Math.random() * width,
    y: Math.random() * height,
    vx: (Math.random() - 0.5) * 0.24,
    vy: (Math.random() - 0.5) * 0.2,
    size: Math.random() * 1.9 + 0.7,
    pulse: Math.random() * Math.PI * 2
  }))
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

  const deltaMs = lastFrameTime > 0 ? time - lastFrameTime : 33
  if (deltaMs < 30) {
    animationId = requestAnimationFrame(draw)
    return
  }
  lastFrameTime = time
  ctx.clearRect(0, 0, width, height)
  ctx.globalCompositeOperation = 'lighter'

  for (const particle of particles) {
    const dxPointer = particle.x - pointer.x
    const dyPointer = particle.y - pointer.y
    const pointerDistance = Math.hypot(dxPointer, dyPointer)

    if (pointerDistance < 150) {
      const force = (150 - pointerDistance) / 150
      particle.vx += (dxPointer / Math.max(pointerDistance, 1)) * force * 0.018
      particle.vy += (dyPointer / Math.max(pointerDistance, 1)) * force * 0.018
    }

    particle.x += particle.vx
    particle.y += particle.vy
    particle.vx *= 0.993
    particle.vy *= 0.993
    particle.pulse += 0.018

    if (particle.x < -20) particle.x = width + 20
    if (particle.x > width + 20) particle.x = -20
    if (particle.y < -20) particle.y = height + 20
    if (particle.y > height + 20) particle.y = -20
  }

  for (let i = 0; i < particles.length; i += 1) {
    const a = particles[i]
    for (let j = i + 1; j < particles.length; j += 1) {
      const b = particles[j]
      const dx = a.x - b.x
      const dy = a.y - b.y
      const distanceSq = dx * dx + dy * dy
      const maxDistance = 118
      if (distanceSq >= maxDistance * maxDistance) continue

      const distance = Math.sqrt(distanceSq)
      if (distance < 132) {
        const alpha = (1 - distance / maxDistance) * 0.18
        ctx.strokeStyle = `rgba(99, 237, 255, ${alpha})`
        ctx.lineWidth = 1
        ctx.beginPath()
        ctx.moveTo(a.x, a.y)
        ctx.lineTo(b.x, b.y)
        ctx.stroke()
      }
    }
  }

  for (const particle of particles) {
    const glow = 0.3 + Math.sin(time * 0.002 + particle.pulse) * 0.18
    ctx.fillStyle = `rgba(142, 239, 255, ${glow})`
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
    ctx.fill()
  }

  ctx.globalCompositeOperation = 'source-over'
  if (active) animationId = requestAnimationFrame(draw)
}

function handlePointerMove(event: PointerEvent) {
  pointer = { x: event.clientX, y: event.clientY }
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
  window.addEventListener('pointermove', handlePointerMove)
  updateLoopState()
}

function stop() {
  pauseLoop()
  cancelAnimationFrame(resizeFrameId)
  resizeFrameId = 0
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('resize', scheduleResize)
  window.removeEventListener('pointermove', handlePointerMove)
  ctx = null
  particles = []
}

onMounted(setup)
onBeforeUnmount(stop)
</script>

<style scoped>
.particle-universe {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  opacity: 0.78;
  mix-blend-mode: screen;
}

@media (max-width: 1023px) {
  .particle-universe {
    display: none;
  }
}
</style>
