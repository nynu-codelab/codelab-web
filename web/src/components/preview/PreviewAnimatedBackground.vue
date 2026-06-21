<template>
  <div class="preview-bg" aria-hidden="true">
    <div class="preview-bg__grid"></div>
    <div class="preview-bg__scanline"></div>
    <div class="preview-bg__orb preview-bg__orb--cyan"></div>
    <div class="preview-bg__orb preview-bg__orb--violet"></div>
    <div class="preview-bg__cursor"></div>
    <div class="preview-bg__noise"></div>
  </div>
</template>

<style scoped>
.preview-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.preview-bg__grid {
  position: absolute;
  inset: -20%;
  background-image:
    linear-gradient(rgba(153, 217, 255, 0.09) 1px, transparent 1px),
    linear-gradient(90deg, rgba(153, 217, 255, 0.08) 1px, transparent 1px);
  background-size: 58px 58px;
  mask-image: radial-gradient(circle at center, black, transparent 72%);
  opacity: 0.42;
  transform: perspective(900px) rotateX(62deg) translateY(-18%);
  animation: grid-drift 18s linear infinite;
}

.preview-bg__scanline {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    transparent,
    rgba(83, 231, 255, 0.08),
    transparent
  );
  opacity: 0.45;
  transform: translateY(-100%);
  animation: scanline 8s ease-in-out infinite;
}

.preview-bg__orb {
  position: absolute;
  width: 44vw;
  max-width: 720px;
  aspect-ratio: 1;
  border-radius: 999px;
  filter: blur(44px);
  opacity: 0.18;
  animation: orb-float 16s ease-in-out infinite alternate;
}

.preview-bg__orb--cyan {
  top: -16%;
  left: -12%;
  background: var(--preview-cyan);
}

.preview-bg__orb--violet {
  right: -14%;
  bottom: 10%;
  background: var(--preview-violet);
  animation-delay: -5s;
}

.preview-bg__cursor {
  position: absolute;
  left: var(--preview-pointer-x, 50%);
  top: var(--preview-pointer-y, 35%);
  width: 34vw;
  max-width: 520px;
  aspect-ratio: 1;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(83, 231, 255, 0.18), transparent 62%);
  transform: translate(-50%, -50%);
  transition:
    left 180ms ease,
    top 180ms ease;
}

.preview-bg__noise {
  position: absolute;
  inset: 0;
  opacity: 0.12;
  background-image:
    linear-gradient(30deg, rgba(255, 255, 255, 0.08) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.08) 87.5%, rgba(255, 255, 255, 0.08)),
    linear-gradient(150deg, rgba(255, 255, 255, 0.08) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.08) 87.5%, rgba(255, 255, 255, 0.08));
  background-size: 9px 9px;
  mix-blend-mode: soft-light;
}

@keyframes grid-drift {
  from {
    background-position: 0 0;
  }
  to {
    background-position: 0 58px;
  }
}

@keyframes scanline {
  0%,
  68% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100%);
  }
}

@keyframes orb-float {
  from {
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    transform: translate3d(10%, 7%, 0) scale(1.08);
  }
}

@media (max-width: 720px) {
  .preview-bg__grid {
    opacity: 0.28;
    animation-duration: 26s;
  }

  .preview-bg__cursor {
    display: none;
  }
}
</style>
