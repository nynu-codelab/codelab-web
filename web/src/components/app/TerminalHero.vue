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

      <div class="terminal-hero__visual">
        <div class="terminal-hero__console glass-card">
          <div class="terminal-hero__bar">
            <span></span>
            <span></span>
            <span></span>
            <strong>boot.sequence</strong>
          </div>
          <div class="terminal-hero__screen">
            <p v-for="line in commands" :key="line" class="terminal-hero__line">
              <span>$</span>
              <code>{{ line }}</code>
            </p>
          </div>
          <pre><code>const lab = createTeam({
  mode: 'learn-by-building',
  pipeline: ['design', 'code', 'review', 'deploy'],
  status: 'recruiting'
})</code></pre>
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
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Activity, Cpu, ShieldCheck, Zap } from '@lucide/vue'
import { gsap } from 'gsap'
import AppButton from './AppButton.vue'
import BuildPipeline from './BuildPipeline.vue'
import LabControlPanel from './LabControlPanel.vue'

const rootRef = ref<HTMLElement | null>(null)
const commands = [
  'boot --lab nynu-codelab',
  'connect --team student-engineers',
  'build --stack java vue mysql docker',
  'deploy --mode real-world-practice',
  'status recruiting'
]

onMounted(() => {
  if (!rootRef.value || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return

  gsap.fromTo(
    rootRef.value.querySelectorAll('.terminal-hero__line, .terminal-hero__side > *, .terminal-hero__chips span'),
    { y: 18, opacity: 0 },
    { y: 0, opacity: 1, duration: 0.76, stagger: 0.08, ease: 'power3.out', delay: 0.14 }
  )
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
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(250px, 0.72fr);
  gap: 18px;
  align-items: stretch;
  perspective: 1200px;
}

.terminal-hero__console {
  min-height: 520px;
  padding: 22px;
  transform: rotateY(-4deg) rotateX(2deg);
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

.terminal-hero__screen {
  display: grid;
  gap: 14px;
  padding-top: 26px;
}

.terminal-hero__line {
  display: flex;
  min-width: 0;
  gap: 12px;
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 14px;
}

.terminal-hero__line span {
  color: var(--app-cyan);
}

.terminal-hero__line code {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

.terminal-hero__side {
  display: grid;
  gap: 18px;
  align-content: stretch;
  transform: translateY(38px);
}

.terminal-hero__pipeline {
  padding: 18px;
}

.terminal-hero__panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  color: var(--app-cyan);
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

  .terminal-hero__side {
    grid-template-columns: 1fr;
  }
}
</style>
