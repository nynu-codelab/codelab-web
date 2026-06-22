<template>
  <div class="app-bg" aria-hidden="true">
    <ParticleUniverse />
    <CodeRainCanvas />
    <EnergyFlowBackground />
    <div class="app-bg__grid"></div>
    <div class="app-bg__scanline"></div>
    <div class="app-bg__orb app-bg__orb--cyan"></div>
    <div class="app-bg__orb app-bg__orb--violet"></div>
    <div class="app-bg__cursor"></div>
    <div class="app-bg__noise"></div>
  </div>
</template>

<script setup lang="ts">
import CodeRainCanvas from './CodeRainCanvas.vue'
import EnergyFlowBackground from './EnergyFlowBackground.vue'
import ParticleUniverse from './ParticleUniverse.vue'
</script>

<style scoped>
.app-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.app-bg__grid {
  position: absolute;
  inset: -20%;
  background-image:
    linear-gradient(rgba(153, 217, 255, 0.09) 1px, transparent 1px),
    linear-gradient(90deg, rgba(153, 217, 255, 0.08) 1px, transparent 1px);
  background-size: 58px 58px;
  mask-image: radial-gradient(circle at center, black, transparent 72%);
  opacity: 0.52;
  transform: perspective(900px) rotateX(62deg) translateY(-18%);
  animation: grid-drift 18s linear infinite;
}

.app-bg__scanline {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent, rgba(83, 231, 255, 0.08), transparent);
  opacity: 0.42;
  transform: translateY(-100%);
  animation: scanline 8s ease-in-out infinite;
}

.app-bg__orb {
  position: absolute;
  width: 44vw;
  max-width: 720px;
  aspect-ratio: 1;
  border-radius: 999px;
  filter: blur(44px);
  opacity: 0.17;
  animation: orb-float 16s ease-in-out infinite alternate;
}

.app-bg__orb--cyan {
  top: -16%;
  left: -12%;
  background: var(--app-cyan);
}

.app-bg__orb--violet {
  right: -14%;
  bottom: 10%;
  background: var(--app-violet);
  animation-delay: -5s;
}

.app-bg__cursor {
  position: absolute;
  left: var(--app-pointer-x, 50%);
  top: var(--app-pointer-y, 35%);
  width: 34vw;
  max-width: 520px;
  aspect-ratio: 1;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(83, 231, 255, 0.16), transparent 62%);
  transform: translate(-50%, -50%);
  transition:
    left 180ms ease,
    top 180ms ease;
}

.app-bg__noise {
  position: absolute;
  inset: 0;
  opacity: 0.1;
  background-image:
    linear-gradient(30deg, rgba(255, 255, 255, 0.08) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.08) 87.5%, rgba(255, 255, 255, 0.08)),
    linear-gradient(150deg, rgba(255, 255, 255, 0.08) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.08) 87.5%, rgba(255, 255, 255, 0.08));
  background-size: 9px 9px;
  mix-blend-mode: soft-light;
}

.app-bg::after {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(90deg, rgba(83, 231, 255, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(83, 231, 255, 0.04) 1px, transparent 1px);
  background-size: 180px 180px;
  mask-image: radial-gradient(circle at 50% 22%, black, transparent 70%);
  opacity: 0.38;
}

@media (max-width: 720px) {
  .app-bg__grid {
    opacity: 0.26;
    animation-duration: 26s;
  }

  .app-bg__cursor {
    display: none;
  }
}
</style>
