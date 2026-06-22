<template>
  <div class="energy-flow" aria-hidden="true">
    <span class="energy-flow__beam energy-flow__beam--a"></span>
    <span class="energy-flow__beam energy-flow__beam--b"></span>
    <span class="energy-flow__beam energy-flow__beam--c"></span>
    <span class="energy-flow__ring"></span>
  </div>
</template>

<style scoped>
.energy-flow {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  opacity: 0.9;
}

.energy-flow__beam {
  position: absolute;
  left: 50%;
  width: 120vw;
  height: 1px;
  transform-origin: center;
  background:
    linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.1), rgba(47, 240, 182, 0.38), transparent);
  filter: drop-shadow(0 0 12px rgba(83, 231, 255, 0.3));
  animation: beam-flow 9s linear infinite;
}

.energy-flow__beam--a {
  top: 24%;
  transform: translateX(-50%) rotate(-10deg);
}

.energy-flow__beam--b {
  top: 58%;
  transform: translateX(-50%) rotate(8deg);
  animation-duration: 12s;
  animation-delay: -4s;
}

.energy-flow__beam--c {
  top: 76%;
  transform: translateX(-50%) rotate(-4deg);
  animation-duration: 15s;
  animation-delay: -7s;
}

.energy-flow__ring {
  position: absolute;
  right: 5vw;
  top: 14vh;
  width: 34vw;
  max-width: 520px;
  aspect-ratio: 1;
  border: 1px solid rgba(83, 231, 255, 0.16);
  border-radius: 50%;
  box-shadow:
    inset 0 0 42px rgba(83, 231, 255, 0.06),
    0 0 70px rgba(83, 231, 255, 0.08);
  animation: ring-rotate 22s linear infinite;
}

.energy-flow__ring::before,
.energy-flow__ring::after {
  content: "";
  position: absolute;
  inset: 10%;
  border: 1px dashed rgba(47, 240, 182, 0.22);
  border-radius: inherit;
}

.energy-flow__ring::after {
  inset: 24%;
  border-color: rgba(117, 167, 255, 0.18);
  animation: ring-rotate 12s linear infinite reverse;
}

@keyframes beam-flow {
  0% {
    background-position: -120vw 0;
    opacity: 0.14;
  }

  48% {
    opacity: 0.82;
  }

  100% {
    background-position: 120vw 0;
    opacity: 0.14;
  }
}

@keyframes ring-rotate {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1023px) {
  .energy-flow {
    opacity: 0.42;
  }

  .energy-flow__ring,
  .energy-flow__beam--c {
    display: none;
  }
}
</style>
