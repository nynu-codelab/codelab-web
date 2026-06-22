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
let particles: Particle[] = []
let width = 0
let height = 0
let dpr = 1
let pointer = { x: -9999, y: -9999 }
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

  const count = Math.min(118, Math.max(74, Math.floor(width / 16)))
  particles = Array.from({ length: count }, () => ({
    x: Math.random() * width,
    y: Math.random() * height,
    vx: (Math.random() - 0.5) * 0.24,
    vy: (Math.random() - 0.5) * 0.2,
    size: Math.random() * 1.9 + 0.7,
    pulse: Math.random() * Math.PI * 2
  }))
}

function draw(time: number) {
  if (!ctx || !active) return

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
      const distance = Math.hypot(a.x - b.x, a.y - b.y)
      if (distance < 132) {
        const alpha = (1 - distance / 132) * 0.22
        const gradient = ctx.createLinearGradient(a.x, a.y, b.x, b.y)
        gradient.addColorStop(0, `rgba(83, 231, 255, ${alpha})`)
        gradient.addColorStop(1, `rgba(47, 240, 182, ${alpha * 0.72})`)
        ctx.strokeStyle = gradient
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
  animationId = requestAnimationFrame(draw)
}

function handlePointerMove(event: PointerEvent) {
  pointer = { x: event.clientX, y: event.clientY }
}

function start() {
  const canvas = canvasRef.value
  if (!canvas || !shouldAnimate()) return

  ctx = canvas.getContext('2d')
  if (!ctx) return

  active = true
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  window.addEventListener('pointermove', handlePointerMove)
  animationId = requestAnimationFrame(draw)
}

function stop() {
  active = false
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resizeCanvas)
  window.removeEventListener('pointermove', handlePointerMove)
}

onMounted(start)
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
