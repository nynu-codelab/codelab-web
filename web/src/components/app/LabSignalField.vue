<template>
  <div ref="fieldRef" class="lab-signal-field" aria-hidden="true">
    <canvas ref="canvasRef" class="lab-signal-field__canvas"></canvas>
    <span class="lab-signal-field__halo"></span>
    <span class="lab-signal-field__axis lab-signal-field__axis--x"></span>
    <span class="lab-signal-field__axis lab-signal-field__axis--y"></span>
    <span class="lab-signal-field__axis lab-signal-field__axis--z"></span>
    <span class="lab-signal-field__pulse lab-signal-field__pulse--a"></span>
    <span class="lab-signal-field__pulse lab-signal-field__pulse--b"></span>
    <span
      v-for="node in nodes"
      :key="node.name"
      class="lab-signal-field__node"
      :class="`lab-signal-field__node--${node.name}`"
      :style="{ left: `${node.x * 100}%`, top: `${node.y * 100}%` }"
    >
      <i></i>
      <small>{{ node.label }}</small>
    </span>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

type SignalNode = {
  name: string
  label: string
  x: number
  y: number
  phase: number
}

type SignalParticle = {
  from: number
  to: number
  progress: number
  speed: number
  size: number
}

type SignalQuality = {
  dprCap: number
  particleScale: number
  linkLimit: number
  drift: number
}

const nodes = [
  { name: 'api', label: 'API', x: 0.29, y: 0.18, phase: 0.2 },
  { name: 'web', label: 'WEB', x: 0.77, y: 0.34, phase: 1.8 },
  { name: 'data', label: 'DATA', x: 0.2, y: 0.66, phase: 2.9 },
  { name: 'ops', label: 'OPS', x: 0.66, y: 0.82, phase: 4.1 },
  { name: 'review', label: 'REVIEW', x: 0.48, y: 0.51, phase: 5.2 }
] satisfies SignalNode[]

const links = [
  [0, 4],
  [1, 4],
  [2, 4],
  [3, 4],
  [0, 1],
  [2, 3],
  [1, 3]
]

const qualityPresets: Record<'high' | 'medium' | 'low', SignalQuality> = {
  high: { dprCap: 1.5, particleScale: 1, linkLimit: links.length, drift: 4 },
  medium: { dprCap: 1.5, particleScale: 0.72, linkLimit: 6, drift: 3.2 },
  low: { dprCap: 1.25, particleScale: 0.48, linkLimit: 4, drift: 2.4 }
}

const fieldRef = ref<HTMLElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let resizeFrameId = 0
let resizeObserver: ResizeObserver | null = null
let intersectionObserver: IntersectionObserver | null = null
let width = 0
let height = 0
let dpr = 1
let active = false
let isInViewport = false
let pageVisible = true
let particles: SignalParticle[] = []
let pointer = { clientX: -9999, clientY: -9999, x: -9999, y: -9999, strength: 0 }
let lastFrameTime = 0
let quality = qualityPresets.medium
const nodeX = new Float32Array(nodes.length)
const nodeY = new Float32Array(nodes.length)

function shouldAnimate() {
  return window.innerWidth >= 1024 && !window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function resolveQuality(rect: DOMRect): SignalQuality {
  const area = rect.width * rect.height
  const deviceDpr = window.devicePixelRatio || 1

  if (area > 150000 && deviceDpr <= 1.5) return qualityPresets.high
  if (area > 95000) return qualityPresets.medium
  return qualityPresets.low
}

function resizeCanvas() {
  const canvas = canvasRef.value
  if (!canvas || !ctx) return

  const rect = canvas.getBoundingClientRect()
  quality = resolveQuality(rect)
  dpr = Math.min(window.devicePixelRatio || 1, quality.dprCap)
  width = Math.max(rect.width, 1)
  height = Math.max(rect.height, 1)
  canvas.width = Math.max(1, Math.floor(width * dpr))
  canvas.height = Math.max(1, Math.floor(height * dpr))
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)

  const particleMultiplier = Math.max(1, Math.round((width * height) / 62000 * quality.particleScale))
  particles = []
  const activeLinkCount = Math.min(quality.linkLimit, links.length)
  for (let linkIndex = 0; linkIndex < activeLinkCount; linkIndex += 1) {
    const [from, to] = links[linkIndex]
    for (let copy = 0; copy < particleMultiplier; copy += 1) {
      const index = particles.length
      particles.push({
        from,
        to,
        progress: (linkIndex * 0.17 + copy * 0.31) % 1,
        speed: 0.00012 + index * 0.000006,
        size: linkIndex % 3 === 0 ? 2.1 : 1.55
      })
    }
  }
}

function scheduleResize() {
  if (resizeFrameId) return
  resizeFrameId = requestAnimationFrame(() => {
    resizeFrameId = 0
    resizeCanvas()
  })
}

function updateNodeCoordinates(time: number) {
  for (let index = 0; index < nodes.length; index += 1) {
    const node = nodes[index]
    nodeX[index] = node.x * width + Math.sin(time * 0.0012 + node.phase) * quality.drift
    nodeY[index] = node.y * height + Math.cos(time * 0.001 + node.phase) * quality.drift
  }
}

function updatePointerFromClient() {
  const canvas = canvasRef.value
  if (!canvas || pointer.clientX < -1000) return

  const rect = canvas.getBoundingClientRect()
  pointer.x = pointer.clientX - rect.left
  pointer.y = pointer.clientY - rect.top
}

function drawNetwork(time: number) {
  if (!ctx || !active) return

  const deltaMs = lastFrameTime > 0 ? Math.min(time - lastFrameTime, 50) : 16.67
  lastFrameTime = time
  updatePointerFromClient()
  updateNodeCoordinates(time)

  ctx.clearRect(0, 0, width, height)
  ctx.globalCompositeOperation = 'lighter'

  const activeLinkCount = Math.min(quality.linkLimit, links.length)
  for (let index = 0; index < activeLinkCount; index += 1) {
    const [from, to] = links[index]
    const alpha = 0.16 + Math.sin(time * 0.001 + index) * 0.05
    ctx!.strokeStyle = `rgba(120, 234, 255, ${alpha + 0.03})`
    ctx!.lineWidth = 1
    ctx!.beginPath()
    ctx!.moveTo(nodeX[from], nodeY[from])
    ctx!.lineTo(nodeX[to], nodeY[to])
    ctx!.stroke()
  }

  for (const particle of particles) {
    particle.progress += particle.speed * deltaMs
    if (particle.progress > 1) particle.progress = 0

    let x = nodeX[particle.from] + (nodeX[particle.to] - nodeX[particle.from]) * particle.progress
    let y = nodeY[particle.from] + (nodeY[particle.to] - nodeY[particle.from]) * particle.progress
    const dxPointer = x - pointer.x
    const dyPointer = y - pointer.y
    const pointerDistance = Math.hypot(dxPointer, dyPointer)

    if (pointerDistance < 120) {
      const force = (120 - pointerDistance) / 120
      x += (dxPointer / Math.max(pointerDistance, 1)) * force * 18
      y += (dyPointer / Math.max(pointerDistance, 1)) * force * 18
      pointer.strength = Math.max(pointer.strength, force)
    }

    const glow = 0.48 + Math.sin(time * 0.004 + particle.progress * 8) * 0.16
    ctx.fillStyle = `rgba(142, 239, 255, ${glow})`
    ctx.beginPath()
    ctx.arc(x, y, particle.size, 0, Math.PI * 2)
    ctx.fill()
  }

  for (let index = 0; index < nodes.length; index += 1) {
    const ring = 10 + Math.sin(time * 0.002 + index) * 3
    ctx!.strokeStyle = index === 3 ? 'rgba(255, 211, 106, 0.42)' : 'rgba(83, 231, 255, 0.48)'
    ctx!.lineWidth = 1
    ctx!.beginPath()
    ctx!.arc(nodeX[index], nodeY[index], ring, 0, Math.PI * 2)
    ctx!.stroke()
  }

  if (pointer.strength > 0.04) {
    ctx.strokeStyle = `rgba(83, 231, 255, ${pointer.strength * 0.36})`
    ctx.lineWidth = 1
    ctx.beginPath()
    ctx.arc(pointer.x, pointer.y, 80 + pointer.strength * 42, 0, Math.PI * 2)
    ctx.stroke()
    pointer.strength *= 0.92
  }

  ctx.globalCompositeOperation = 'source-over'
  if (active) animationId = requestAnimationFrame(drawNetwork)
}

function handlePointerMove(event: PointerEvent) {
  pointer.clientX = event.clientX
  pointer.clientY = event.clientY
}

function shouldRunLoop() {
  return Boolean(ctx && shouldAnimate() && isInViewport && pageVisible)
}

function startLoop() {
  if (active || !shouldRunLoop()) return

  active = true
  lastFrameTime = 0
  animationId = requestAnimationFrame(drawNetwork)
}

function pauseLoop() {
  if (!active) return

  active = false
  cancelAnimationFrame(animationId)
  animationId = 0
  lastFrameTime = 0
}

function updateLoopState() {
  if (shouldRunLoop()) {
    startLoop()
  } else {
    pauseLoop()
  }
}

function handleVisibilityChange() {
  pageVisible = !document.hidden
  updateLoopState()
}

function setup() {
  const canvas = canvasRef.value
  if (!canvas || !shouldAnimate()) return

  ctx = canvas.getContext('2d')
  if (!ctx) return

  resizeCanvas()
  resizeObserver = new ResizeObserver(scheduleResize)
  resizeObserver.observe(canvas)
  if ('IntersectionObserver' in window) {
    intersectionObserver = new IntersectionObserver(
      ([entry]) => {
        isInViewport = Boolean(entry?.isIntersecting)
        updateLoopState()
      },
      { rootMargin: '120px 0px', threshold: 0.05 }
    )
    if (fieldRef.value) intersectionObserver.observe(fieldRef.value)
  } else {
    isInViewport = true
  }
  pageVisible = !document.hidden
  document.addEventListener('visibilitychange', handleVisibilityChange)
  window.addEventListener('pointermove', handlePointerMove)
  updateLoopState()
}

function stop() {
  pauseLoop()
  cancelAnimationFrame(resizeFrameId)
  resizeFrameId = 0
  resizeObserver?.disconnect()
  resizeObserver = null
  intersectionObserver?.disconnect()
  intersectionObserver = null
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('pointermove', handlePointerMove)
  ctx = null
  particles = []
  isInViewport = false
}

onMounted(setup)
onBeforeUnmount(stop)
</script>

<style scoped>
.lab-signal-field {
  position: absolute;
  inset: -12% -8% -8% auto;
  width: min(520px, 48vw);
  aspect-ratio: 1;
  pointer-events: none;
  opacity: 0.82;
  transform: translate3d(0, 0, 0) rotateX(58deg) rotateZ(-18deg);
  transform-style: preserve-3d;
}

.lab-signal-field__halo,
.lab-signal-field__axis,
.lab-signal-field__node,
.lab-signal-field__pulse,
.lab-signal-field__canvas {
  position: absolute;
}

.lab-signal-field__canvas {
  inset: 8%;
  z-index: 2;
  width: 84%;
  height: 84%;
  opacity: 0.9;
  transform: translateZ(28px);
}

.lab-signal-field__halo {
  inset: 16%;
  border: 1px solid rgba(83, 231, 255, 0.18);
  border-radius: 50%;
  background:
    radial-gradient(circle, rgba(83, 231, 255, 0.14), transparent 54%),
    conic-gradient(from 180deg, transparent, rgba(83, 231, 255, 0.28), transparent, rgba(47, 240, 182, 0.22), transparent);
  box-shadow:
    inset 0 0 46px rgba(83, 231, 255, 0.06),
    0 0 70px rgba(83, 231, 255, 0.08);
  animation: lab-field-rotate 24s linear infinite;
  transform: translateZ(0);
}

.lab-signal-field__halo::before,
.lab-signal-field__halo::after {
  content: "";
  position: absolute;
  border-radius: inherit;
}

.lab-signal-field__halo::before {
  inset: 14%;
  border: 1px dashed rgba(153, 217, 255, 0.2);
}

.lab-signal-field__halo::after {
  inset: 34%;
  border: 1px solid rgba(255, 211, 106, 0.18);
  box-shadow: 0 0 24px rgba(255, 211, 106, 0.1);
}

.lab-signal-field__axis {
  left: 22%;
  right: 14%;
  top: 50%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.34), transparent);
  transform-origin: center;
}

.lab-signal-field__axis--y {
  transform: rotate(62deg);
}

.lab-signal-field__axis--z {
  transform: rotate(-28deg);
  opacity: 0.72;
}

.lab-signal-field__pulse {
  inset: 28%;
  border: 1px solid rgba(83, 231, 255, 0.18);
  border-radius: 50%;
  transform: translateZ(18px) scale(0.76);
  opacity: 0;
  animation: lab-field-pulse 4.8s cubic-bezier(0.16, 1, 0.3, 1) infinite;
}

.lab-signal-field__pulse--b {
  inset: 18%;
  border-color: rgba(255, 211, 106, 0.16);
  animation-delay: -2.2s;
}

.lab-signal-field__node {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 6px 8px;
  border: 1px solid rgba(153, 217, 255, 0.18);
  border-radius: 999px;
  color: rgba(216, 247, 255, 0.82);
  background: rgba(4, 10, 18, 0.68);
  box-shadow: 0 0 26px rgba(83, 231, 255, 0.08);
  font-family: var(--app-font-data);
  font-size: 10px;
  transform: translate3d(-50%, -50%, 46px) rotateZ(18deg) rotateX(-58deg);
}

.lab-signal-field__node i {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: var(--app-cyan);
  box-shadow: 0 0 16px rgba(83, 231, 255, 0.72);
}

.lab-signal-field__node--review i,
.lab-signal-field__node--ops i {
  background: var(--app-amber);
  box-shadow: 0 0 16px rgba(255, 211, 106, 0.62);
}

@keyframes lab-field-rotate {
  to {
    transform: rotate(360deg);
  }
}

@keyframes lab-field-pulse {
  0% {
    opacity: 0;
    transform: translateZ(18px) scale(0.72);
  }

  24% {
    opacity: 0.48;
  }

  100% {
    opacity: 0;
    transform: translateZ(18px) scale(1.24);
  }
}

@media (prefers-reduced-motion: reduce) {
  .lab-signal-field__halo,
  .lab-signal-field__pulse {
    animation: none;
  }

  .lab-signal-field__canvas {
    display: none;
  }
}

@media (max-width: 1023px) {
  .lab-signal-field {
    display: none;
  }
}
</style>
