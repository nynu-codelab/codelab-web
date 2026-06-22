<template>
  <section ref="rootRef" class="terminal-hero">
    <div class="app-container terminal-hero__inner">
      <div class="terminal-hero__copy">
        <span class="app-eyebrow">NYNU Code Lab</span>
        <h1>南阳师范学院 Code Lab 实验室</h1>
        <p>
          专注项目实战、技术分享与工程能力培养，在真实开发中提升软件工程能力。
        </p>
        <div class="terminal-hero__actions">
          <AppButton label="立即报名" to="/recruit" size="lg" />
          <AppButton label="查看项目" to="/projects" variant="secondary" size="lg" />
          <AppButton label="阅读文章" to="/articles" variant="ghost" size="lg" />
        </div>
        <div class="terminal-hero__chips">
          <span><Cpu :size="15" /> Java / Vue / MySQL / Docker</span>
          <span><Activity :size="15" /> Real-world practice</span>
          <span><ShieldCheck :size="15" /> Review before deploy</span>
        </div>
      </div>

      <div ref="visualRef" class="terminal-hero__visual">
        <div class="terminal-hero__space" aria-hidden="true">
          <span class="terminal-hero__depth terminal-hero__depth--far"></span>
          <span class="terminal-hero__depth terminal-hero__depth--mid"></span>
          <span class="terminal-hero__depth terminal-hero__depth--near"></span>
          <span class="terminal-hero__axis terminal-hero__axis--x"></span>
          <span class="terminal-hero__axis terminal-hero__axis--y"></span>
        </div>
        <LabSpatialScene />
        <LabSignalField />
        <div class="terminal-hero__console glass-card">
          <div class="terminal-hero__lab-wall" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="terminal-hero__bar">
            <span></span>
            <span></span>
            <span></span>
            <strong>boot.sequence</strong>
          </div>
          <div class="terminal-hero__status-line">
            <span>kernel: codelab-os</span>
            <span>latency: local</span>
            <span>mode: practice</span>
          </div>
          <div class="terminal-hero__screen">
            <p
              v-for="(line, index) in commands"
              :key="line"
              class="terminal-hero__line"
              :style="{ '--line-index': String(index) }"
            >
              <span>$</span>
              <code>{{ line }}</code>
            </p>
          </div>
          <pre><code>const lab = createTeam({
  mode: 'learn-by-building',
  pipeline: ['design', 'code', 'review', 'deploy'],
  status: 'recruiting'
})</code></pre>
          <div class="terminal-hero__telemetry" aria-label="实验室运行状态">
            <span>
              <strong>branch</strong>
              <small>refine/pc-immersive</small>
            </span>
            <span>
              <strong>tests</strong>
              <small>ready</small>
            </span>
            <span>
              <strong>review</strong>
              <small>before deploy</small>
            </span>
          </div>
        </div>

        <div class="terminal-hero__side">
          <LabControlPanel />
          <div class="terminal-hero__pipeline glass-card">
            <div class="terminal-hero__panel-title">
              <Zap :size="16" />
              <span>Build pipeline</span>
            </div>
            <BuildPipeline />
          </div>
        </div>

        <div v-if="showFrameBudget" class="terminal-hero__budget" aria-hidden="true">
          <span>{{ fps }} fps</span>
          <span>{{ averageFrameMs }} ms</span>
          <span>{{ longFrameCount }} long</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { defineAsyncComponent, onBeforeUnmount, onMounted, ref } from 'vue'
import { Activity, Cpu, ShieldCheck, Zap } from '@lucide/vue'
import { gsap } from 'gsap'
import AppButton from './AppButton.vue'
import BuildPipeline from './BuildPipeline.vue'
import LabSignalField from './LabSignalField.vue'
import LabControlPanel from './LabControlPanel.vue'
import { useFrameBudget } from '@/composables/useFrameBudget'

const LabSpatialScene = defineAsyncComponent(() => import('./LabSpatialScene.vue'))

const rootRef = ref<HTMLElement | null>(null)
const visualRef = ref<HTMLElement | null>(null)
const showFrameBudget = import.meta.env.DEV
const {
  fps,
  averageFrameMs,
  longFrameCount,
  start: startFrameBudget,
  stop: stopFrameBudget
} = useFrameBudget()
const commands = [
  'boot --lab nynu-codelab',
  'connect --team student-engineers',
  'scan --signals api web data ops',
  'build --stack java vue mysql docker',
  'deploy --mode real-world-practice',
  'status recruiting'
]

let gsapContext: ReturnType<typeof gsap.context> | null = null
let frameBudgetObserver: IntersectionObserver | null = null
let frameBudgetInViewport = false
let tiltFrameId = 0
let visualRect: DOMRect | null = null
const tilt = {
  currentX: 0,
  currentY: 0,
  targetX: 0,
  targetY: 0,
  pointerX: 50,
  pointerY: 45
}

function isReducedMotion() {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function canUseMotion() {
  return window.innerWidth >= 1024 && !isReducedMotion()
}

function updateFrameBudgetState() {
  if (!showFrameBudget) return

  if (frameBudgetInViewport && !document.hidden && canUseMotion()) {
    startFrameBudget()
  } else {
    stopFrameBudget()
  }
}

function handleBudgetVisibilityChange() {
  updateFrameBudgetState()
}

function applyVisualTilt() {
  tiltFrameId = 0
  const visual = visualRef.value
  if (!visual) return

  tilt.currentX += (tilt.targetX - tilt.currentX) * 0.18
  tilt.currentY += (tilt.targetY - tilt.currentY) * 0.18

  visual.style.setProperty('--hero-tilt-x', `${tilt.currentX.toFixed(2)}deg`)
  visual.style.setProperty('--hero-tilt-y', `${tilt.currentY.toFixed(2)}deg`)
  visual.style.setProperty('--hero-pointer-x', `${tilt.pointerX.toFixed(2)}%`)
  visual.style.setProperty('--hero-pointer-y', `${tilt.pointerY.toFixed(2)}%`)

  if (Math.abs(tilt.targetX - tilt.currentX) > 0.03 || Math.abs(tilt.targetY - tilt.currentY) > 0.03) {
    tiltFrameId = requestAnimationFrame(applyVisualTilt)
  }
}

function scheduleVisualTilt() {
  if (tiltFrameId) return
  tiltFrameId = requestAnimationFrame(applyVisualTilt)
}

function handleVisualMove(event: PointerEvent) {
  const visual = visualRef.value
  if (!visual || !canUseMotion()) return

  visualRect = visualRect ?? visual.getBoundingClientRect()
  const x = (event.clientX - visualRect.left) / visualRect.width
  const y = (event.clientY - visualRect.top) / visualRect.height

  tilt.targetX = (0.5 - y) * 7
  tilt.targetY = (x - 0.5) * 9
  tilt.pointerX = x * 100
  tilt.pointerY = y * 100
  scheduleVisualTilt()
}

function refreshVisualRect() {
  visualRect = visualRef.value?.getBoundingClientRect() ?? null
}

function resetVisualTilt() {
  tilt.targetX = 0
  tilt.targetY = 0
  visualRect = null
  scheduleVisualTilt()
}

onMounted(() => {
  const visual = visualRef.value
  if (visual) {
    visual.addEventListener('pointerenter', refreshVisualRect)
    visual.addEventListener('pointermove', handleVisualMove)
    visual.addEventListener('pointerleave', resetVisualTilt)
  }

  if (showFrameBudget && rootRef.value) {
    if ('IntersectionObserver' in window) {
      frameBudgetObserver = new IntersectionObserver(
        ([entry]) => {
          frameBudgetInViewport = Boolean(entry?.isIntersecting)
          updateFrameBudgetState()
        },
        { rootMargin: '120px 0px', threshold: 0.05 }
      )
      frameBudgetObserver.observe(rootRef.value)
    } else {
      frameBudgetInViewport = true
    }
    document.addEventListener('visibilitychange', handleBudgetVisibilityChange)
    updateFrameBudgetState()
  }

  if (!rootRef.value || isReducedMotion()) return

  gsapContext = gsap.context(() => {
    gsap.fromTo(
      '.terminal-hero__line, .terminal-hero__side > *, .terminal-hero__chips span, .terminal-hero__telemetry span',
      { y: 18, opacity: 0 },
      { y: 0, opacity: 1, duration: 0.76, stagger: 0.07, ease: 'power3.out', delay: 0.14 }
    )

    gsap.fromTo(
      '.terminal-hero__console',
      { rotateY: -8, rotateX: 5, y: 18, opacity: 0 },
      { rotateY: -4, rotateX: 2, y: 0, opacity: 1, duration: 0.9, ease: 'power3.out' }
    )
  }, rootRef.value)
})

onBeforeUnmount(() => {
  const visual = visualRef.value
  visual?.removeEventListener('pointerenter', refreshVisualRect)
  visual?.removeEventListener('pointermove', handleVisualMove)
  visual?.removeEventListener('pointerleave', resetVisualTilt)
  cancelAnimationFrame(tiltFrameId)
  frameBudgetObserver?.disconnect()
  frameBudgetObserver = null
  document.removeEventListener('visibilitychange', handleBudgetVisibilityChange)
  stopFrameBudget()
  gsapContext?.revert()
  gsapContext = null
})
</script>

<style scoped>
.terminal-hero {
  position: relative;
  padding: 88px 0 70px;
}

.terminal-hero::before {
  content: "";
  position: absolute;
  inset: 8% 0 auto;
  height: 520px;
  pointer-events: none;
  background:
    radial-gradient(circle at 18% 28%, rgba(83, 231, 255, 0.2), transparent 30%),
    radial-gradient(circle at 76% 22%, rgba(47, 240, 182, 0.14), transparent 32%);
  filter: blur(18px);
}

.terminal-hero__inner {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 0.82fr) minmax(540px, 1fr);
  gap: 54px;
  align-items: center;
  min-height: min(760px, calc(100vh - 92px));
}

.terminal-hero__copy {
  min-width: 0;
}

.terminal-hero h1 {
  max-width: 880px;
  color: var(--app-text-strong);
  font-size: 96px;
  font-weight: 880;
  line-height: 0.96;
  text-wrap: balance;
  text-shadow: 0 0 46px rgba(83, 231, 255, 0.18);
}

.terminal-hero__copy p {
  max-width: 720px;
  margin-top: 24px;
  color: var(--app-soft);
  font-size: 22px;
  line-height: 1.78;
}

.terminal-hero__actions,
.terminal-hero__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.terminal-hero__actions {
  margin-top: 34px;
}

.terminal-hero__chips {
  margin-top: 28px;
}

.terminal-hero__chips span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 34px;
  padding: 8px 12px;
  border: 1px solid rgba(153, 217, 255, 0.18);
  border-radius: 999px;
  color: var(--app-soft);
  background: rgba(255, 255, 255, 0.055);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.terminal-hero__visual {
  --hero-tilt-x: 0deg;
  --hero-tilt-y: 0deg;
  --hero-pointer-x: 50%;
  --hero-pointer-y: 45%;
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(250px, 0.72fr);
  gap: 18px;
  align-items: stretch;
  perspective: 1400px;
  transform: rotateX(var(--hero-tilt-x)) rotateY(var(--hero-tilt-y));
  transform-style: preserve-3d;
  transition: transform 260ms cubic-bezier(0.16, 1, 0.3, 1);
}

.terminal-hero__space {
  position: absolute;
  inset: -12% -10% -16%;
  pointer-events: none;
  transform-style: preserve-3d;
}

.terminal-hero__depth,
.terminal-hero__axis {
  position: absolute;
  pointer-events: none;
}

.terminal-hero__depth {
  left: 3%;
  right: 0;
  border: 1px solid rgba(153, 217, 255, 0.11);
  border-radius: 28px;
  background:
    linear-gradient(rgba(83, 231, 255, 0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.045) 1px, transparent 1px),
    radial-gradient(circle at var(--hero-pointer-x) var(--hero-pointer-y), rgba(83, 231, 255, 0.16), transparent 34%);
  background-size: 36px 36px, 36px 36px, auto;
  mask-image: linear-gradient(90deg, transparent, black 14%, black 80%, transparent);
}

.terminal-hero__depth--far {
  top: 0;
  height: 74%;
  transform: translateZ(-120px) rotateX(68deg) translateY(-28%);
  opacity: 0.38;
}

.terminal-hero__depth--mid {
  top: 18%;
  height: 62%;
  transform: translateZ(-46px) rotateX(58deg) translateY(-12%);
  opacity: 0.52;
}

.terminal-hero__depth--near {
  bottom: -8%;
  height: 48%;
  transform: translateZ(28px) rotateX(64deg) translateY(10%);
  opacity: 0.62;
}

.terminal-hero__axis {
  z-index: 1;
  background: linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.42), transparent);
  filter: drop-shadow(0 0 12px rgba(83, 231, 255, 0.26));
}

.terminal-hero__axis--x {
  left: 4%;
  right: 8%;
  bottom: 14%;
  height: 1px;
  transform: translateZ(80px) rotate(-8deg);
}

.terminal-hero__axis--y {
  top: 8%;
  bottom: 3%;
  right: 18%;
  width: 1px;
  transform: translateZ(80px) rotate(18deg);
}

.terminal-hero__console {
  position: relative;
  min-height: 520px;
  padding: 22px;
  transform: translateZ(52px) rotateY(-4deg) rotateX(2deg);
  box-shadow:
    0 36px 110px rgba(0, 0, 0, 0.5),
    0 0 64px rgba(83, 231, 255, 0.12);
}

.terminal-hero__console::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background:
    linear-gradient(180deg, transparent 0%, rgba(83, 231, 255, 0.055) 50%, transparent 100%),
    linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px);
  background-size: 100% 100%, 100% 8px;
  opacity: 0.58;
  mix-blend-mode: screen;
}

.terminal-hero__console::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  border-radius: inherit;
  background: radial-gradient(420px circle at var(--hero-pointer-x) var(--hero-pointer-y), rgba(83, 231, 255, 0.2), transparent 52%);
  opacity: 0;
  transition: opacity 220ms ease;
}

.terminal-hero__visual:hover .terminal-hero__console::after {
  opacity: 0.82;
}

.terminal-hero__lab-wall {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  border-radius: inherit;
}

.terminal-hero__lab-wall span {
  position: absolute;
  inset: 18px;
  border: 1px solid rgba(153, 217, 255, 0.1);
  border-radius: 18px;
  transform: translateZ(-20px);
}

.terminal-hero__lab-wall span:nth-child(2) {
  inset: 56px 28px 104px;
  border-color: rgba(47, 240, 182, 0.12);
}

.terminal-hero__lab-wall span:nth-child(3) {
  inset: auto 20px 24px 20px;
  height: 1px;
  border: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 211, 106, 0.28), transparent);
}

.terminal-hero__bar {
  display: grid;
  grid-template-columns: 10px 10px 10px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(153, 217, 255, 0.12);
}

.terminal-hero__bar span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--app-danger);
}

.terminal-hero__bar span:nth-child(2) {
  background: var(--app-amber);
}

.terminal-hero__bar span:nth-child(3) {
  background: var(--app-success);
}

.terminal-hero__bar strong,
.terminal-hero__panel-title {
  color: var(--app-muted);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.terminal-hero__bar strong {
  justify-self: end;
}

.terminal-hero__status-line {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  padding: 14px 0 2px;
  color: rgba(199, 215, 232, 0.72);
  font-family: var(--app-font-data);
  font-size: 11px;
}

.terminal-hero__status-line span {
  overflow: hidden;
  padding: 7px 8px;
  border: 1px solid rgba(153, 217, 255, 0.12);
  border-radius: var(--app-radius-sm);
  background: rgba(255, 255, 255, 0.035);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.terminal-hero__screen {
  display: grid;
  gap: 14px;
  padding-top: 26px;
}

.terminal-hero__line {
  --line-index: 0;
  display: flex;
  min-width: 0;
  gap: 12px;
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 14px;
  opacity: 0;
  transform: translateY(12px);
  animation: terminal-line-in 640ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
  animation-delay: calc(var(--line-index) * 110ms + 120ms);
}

.terminal-hero__line span {
  color: var(--app-cyan);
}

.terminal-hero__line code {
  position: relative;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.terminal-hero__line:last-child code::after {
  content: "";
  display: inline-block;
  width: 8px;
  height: 1.15em;
  margin-left: 8px;
  vertical-align: -0.18em;
  background: var(--app-amber);
  box-shadow: 0 0 16px rgba(255, 211, 106, 0.62);
  animation: terminal-cursor-blink 1s steps(2, start) infinite;
}

.terminal-hero pre {
  margin-top: 34px;
  padding: 20px;
  overflow: hidden;
  border: 1px solid rgba(153, 217, 255, 0.13);
  border-radius: var(--app-radius-sm);
  color: #d8f7ff;
  background:
    linear-gradient(180deg, rgba(83, 231, 255, 0.08), transparent),
    rgba(2, 8, 15, 0.72);
  box-shadow: inset 0 0 38px rgba(83, 231, 255, 0.05);
  font-family: var(--app-font-data);
  font-size: 13px;
  line-height: 1.7;
}

.terminal-hero__telemetry {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 18px;
}

.terminal-hero__telemetry span {
  display: grid;
  gap: 5px;
  min-width: 0;
  padding: 11px;
  border: 1px solid rgba(153, 217, 255, 0.12);
  border-radius: var(--app-radius-sm);
  background:
    linear-gradient(135deg, rgba(83, 231, 255, 0.07), transparent 62%),
    rgba(255, 255, 255, 0.035);
}

.terminal-hero__telemetry strong,
.terminal-hero__telemetry small {
  overflow: hidden;
  font-family: var(--app-font-data);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.terminal-hero__telemetry strong {
  color: var(--app-cyan);
  font-size: 11px;
}

.terminal-hero__telemetry small {
  color: rgba(199, 215, 232, 0.72);
  font-size: 10px;
}

.terminal-hero__side {
  display: grid;
  gap: 18px;
  align-content: stretch;
  transform: translateY(38px) translateZ(90px);
}

.terminal-hero__pipeline {
  padding: 18px;
}

.terminal-hero__budget {
  position: absolute;
  right: 12px;
  bottom: 12px;
  z-index: 6;
  display: flex;
  gap: 7px;
  padding: 7px 9px;
  border: 1px solid rgba(83, 231, 255, 0.22);
  border-radius: 999px;
  color: rgba(216, 247, 255, 0.78);
  background: rgba(2, 8, 15, 0.62);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.24);
  font-family: var(--app-font-data);
  font-size: 10px;
  line-height: 1;
  pointer-events: none;
}

.terminal-hero__budget span {
  white-space: nowrap;
}

.terminal-hero__panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  color: var(--app-cyan);
}

@keyframes terminal-line-in {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes terminal-cursor-blink {
  50% {
    opacity: 0;
  }
}

@media (max-width: 1180px) {
  .terminal-hero__inner,
  .terminal-hero__visual {
    grid-template-columns: 1fr;
  }

  .terminal-hero__side {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    transform: none;
  }

  .terminal-hero h1 {
    font-size: 72px;
  }
}

@media (max-width: 720px) {
  .terminal-hero {
    padding: 64px 0 42px;
  }

  .terminal-hero__inner {
    min-height: auto;
  }

  .terminal-hero h1 {
    font-size: 42px;
  }

  .terminal-hero__copy p {
    font-size: 17px;
  }

  .terminal-hero__console {
    min-height: 420px;
    transform: none;
  }

  .terminal-hero__status-line {
    grid-template-columns: 1fr;
  }

  .terminal-hero__side {
    grid-template-columns: 1fr;
  }

  .terminal-hero__budget {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .terminal-hero__line {
    opacity: 1;
    transform: none;
    animation: none;
  }

  .terminal-hero__line:last-child code::after {
    animation: none;
  }
}
</style>
