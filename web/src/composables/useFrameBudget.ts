import { onBeforeUnmount, ref } from 'vue'

type FrameBudgetOptions = {
  longFrameMs?: number
  sampleSize?: number
  updateEveryMs?: number
}

export function useFrameBudget(options: FrameBudgetOptions = {}) {
  const fps = ref(0)
  const averageFrameMs = ref(0)
  const longFrameCount = ref(0)
  const running = ref(false)

  const longFrameMs = options.longFrameMs ?? 24
  const sampleSize = options.sampleSize ?? 90
  const updateEveryMs = options.updateEveryMs ?? 500

  let frameId = 0
  let lastTime = 0
  let updateElapsed = 0
  let updateFrames = 0
  const samples: number[] = []

  function tick(time: number) {
    if (!running.value) return

    if (lastTime > 0) {
      const delta = time - lastTime
      samples.push(delta)
      if (samples.length > sampleSize) samples.shift()

      updateElapsed += delta
      updateFrames += 1
      if (delta > longFrameMs) longFrameCount.value += 1

      if (updateElapsed >= updateEveryMs) {
        const total = samples.reduce((sum, value) => sum + value, 0)
        averageFrameMs.value = samples.length ? Number((total / samples.length).toFixed(2)) : 0
        fps.value = Math.round((updateFrames * 1000) / updateElapsed)
        updateElapsed = 0
        updateFrames = 0
      }
    }

    lastTime = time
    frameId = requestAnimationFrame(tick)
  }

  function start() {
    if (!import.meta.env.DEV || running.value) return

    running.value = true
    lastTime = 0
    updateElapsed = 0
    updateFrames = 0
    frameId = requestAnimationFrame(tick)
  }

  function stop() {
    if (!running.value) return

    running.value = false
    cancelAnimationFrame(frameId)
    frameId = 0
    lastTime = 0
  }

  function reset() {
    fps.value = 0
    averageFrameMs.value = 0
    longFrameCount.value = 0
    samples.length = 0
    updateElapsed = 0
    updateFrames = 0
  }

  onBeforeUnmount(stop)

  return {
    fps,
    averageFrameMs,
    longFrameCount,
    running,
    start,
    stop,
    reset
  }
}
