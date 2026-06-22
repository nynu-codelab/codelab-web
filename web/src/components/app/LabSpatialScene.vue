<template>
  <canvas ref="canvasRef" class="lab-spatial-scene" aria-hidden="true"></canvas>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import {
  AdditiveBlending,
  BufferAttribute,
  BufferGeometry,
  GridHelper,
  Group,
  Line,
  LineBasicMaterial,
  LineSegments,
  Mesh,
  MeshBasicMaterial,
  PerspectiveCamera,
  Scene,
  SphereGeometry,
  Vector3,
  WebGLRenderer,
  type BufferGeometry as ThreeBufferGeometry,
  type Material,
  type Object3D
} from 'three'

const canvasRef = ref<HTMLCanvasElement | null>(null)

type SpatialQuality = {
  dprCap: number
  ringSegments: number
  fieldDots: number
  sphereSegments: [number, number]
  antialias: boolean
}

let renderer: WebGLRenderer | null = null
let scene: Scene | null = null
let camera: PerspectiveCamera | null = null
let frameId = 0
let resizeFrameId = 0
let resizeObserver: ResizeObserver | null = null
let intersectionObserver: IntersectionObserver | null = null
let rootGroup: Group | null = null
let ringsGroup: Group | null = null
let lineGeometry: BufferGeometry | null = null
let nodes: Mesh[] = []
let packets: Array<{ mesh: Mesh; from: number; to: number; progress: number; speed: number }> = []
let pointer = { x: 0, y: 0 }
let lastFrameTime = 0
let isInViewport = false
let pageVisible = true
let loopRunning = false
let reducedMotion = false
let quality: SpatialQuality = {
  dprCap: 1.5,
  ringSegments: 104,
  fieldDots: 34,
  sphereSegments: [12, 8],
  antialias: true
}

const nodePositions = [
  new Vector3(-2.1, 0.72, -1.1),
  new Vector3(0.2, 1.12, -1.75),
  new Vector3(2.05, 0.28, -0.95),
  new Vector3(-1.52, -0.72, 0.18),
  new Vector3(0.7, -0.42, 0.72),
  new Vector3(2.12, -1.0, 0.2),
  new Vector3(-0.28, 0.02, 1.45)
]

const linkPairs = [
  [0, 1],
  [1, 2],
  [0, 3],
  [3, 4],
  [4, 5],
  [2, 5],
  [1, 6],
  [4, 6],
  [0, 6],
  [2, 6]
]

function resolveQuality(): SpatialQuality {
  const width = window.innerWidth
  const dpr = window.devicePixelRatio || 1

  if (width >= 1440 && dpr <= 1.5) {
    return {
      dprCap: 1.5,
      ringSegments: 128,
      fieldDots: 44,
      sphereSegments: [14, 10],
      antialias: true
    }
  }

  if (width >= 1180) {
    return {
      dprCap: 1.5,
      ringSegments: 96,
      fieldDots: 30,
      sphereSegments: [12, 8],
      antialias: true
    }
  }

  return {
    dprCap: 1.25,
    ringSegments: 72,
    fieldDots: 18,
    sphereSegments: [10, 6],
    antialias: false
  }
}

function canRender() {
  reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  return window.innerWidth >= 1024 && !reducedMotion
}

function createLineCircle(radius: number, segments: number, color: number, opacity: number) {
  const positions = new Float32Array((segments + 1) * 3)
  for (let index = 0; index <= segments; index += 1) {
    const angle = (index / segments) * Math.PI * 2
    const offset = index * 3
    positions[offset] = Math.cos(angle) * radius
    positions[offset + 1] = 0
    positions[offset + 2] = Math.sin(angle) * radius
  }
  const geometry = new BufferGeometry()
  geometry.setAttribute('position', new BufferAttribute(positions, 3))
  const material = new LineBasicMaterial({
    color,
    transparent: true,
    opacity,
    blending: AdditiveBlending
  })
  return new Line(geometry, material)
}

function createScene() {
  const canvas = canvasRef.value
  if (!canvas || !canRender()) return
  quality = resolveQuality()

  renderer = new WebGLRenderer({
    canvas,
    alpha: true,
    antialias: quality.antialias,
    powerPreference: 'high-performance'
  })
  renderer.setClearColor(0x000000, 0)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, quality.dprCap))

  scene = new Scene()
  camera = new PerspectiveCamera(42, 1, 0.1, 80)
  camera.position.set(0.2, 1.35, 6.2)
  camera.lookAt(0, 0, 0)

  rootGroup = new Group()
  rootGroup.rotation.x = -0.34
  rootGroup.rotation.y = -0.28
  scene.add(rootGroup)

  const grid = new GridHelper(6.5, 18, 0x53e7ff, 0x15374a)
  grid.position.y = -1.35
  const gridMaterial = grid.material as Material
  gridMaterial.transparent = true
  gridMaterial.opacity = 0.2
  rootGroup.add(grid)

  ringsGroup = new Group()
  const ringA = createLineCircle(2.8, quality.ringSegments, 0x53e7ff, 0.22)
  const ringB = createLineCircle(1.86, quality.ringSegments, 0x2ff0b6, 0.16)
  const ringC = createLineCircle(3.5, quality.ringSegments, 0xffd36a, 0.1)
  ringA.rotation.x = Math.PI / 2
  ringB.rotation.x = Math.PI / 2
  ringB.rotation.z = 0.42
  ringC.rotation.x = Math.PI / 2
  ringC.rotation.z = -0.22
  ringsGroup.add(ringA, ringB, ringC)
  rootGroup.add(ringsGroup)

  const nodeMaterial = new MeshBasicMaterial({
    color: 0x53e7ff,
    transparent: true,
    opacity: 0.88,
    blending: AdditiveBlending
  })
  const amberMaterial = new MeshBasicMaterial({
    color: 0xffd36a,
    transparent: true,
    opacity: 0.86,
    blending: AdditiveBlending
  })
  const nodeGeometry = new SphereGeometry(0.045, ...quality.sphereSegments)

  nodes = nodePositions.map((position, index) => {
    const node = new Mesh(nodeGeometry, index === 5 ? amberMaterial : nodeMaterial)
    node.position.copy(position)
    rootGroup!.add(node)
    return node
  })

  const linePositions = new Float32Array(linkPairs.length * 2 * 3)
  lineGeometry = new BufferGeometry()
  lineGeometry.setAttribute('position', new BufferAttribute(linePositions, 3))
  updateLineGeometry()

  const lineMaterial = new LineBasicMaterial({
    color: 0x8eefff,
    transparent: true,
    opacity: 0.28,
    blending: AdditiveBlending
  })
  const lines = new LineSegments(lineGeometry, lineMaterial)
  rootGroup.add(lines)

  const packetGeometry = new SphereGeometry(0.026, ...quality.sphereSegments)
  const packetMaterial = new MeshBasicMaterial({
    color: 0xffffff,
    transparent: true,
    opacity: 0.92,
    blending: AdditiveBlending
  })
  packets = linkPairs.map(([from, to], index) => {
    const packet = new Mesh(packetGeometry, packetMaterial)
    rootGroup!.add(packet)
    return {
      mesh: packet,
      from,
      to,
      progress: (index * 0.13) % 1,
      speed: 0.00016 + index * 0.00001
    }
  })

  const field = new Group()
  const fieldGeometrySmall = new SphereGeometry(0.012, 8, 6)
  const fieldGeometryLarge = new SphereGeometry(0.018, 8, 6)
  const fieldMaterialCyan = new MeshBasicMaterial({
    color: 0x53e7ff,
    transparent: true,
    opacity: 0.28,
    blending: AdditiveBlending
  })
  const fieldMaterialAmber = new MeshBasicMaterial({
    color: 0xffd36a,
    transparent: true,
    opacity: 0.24,
    blending: AdditiveBlending
  })
  for (let index = 0; index < quality.fieldDots; index += 1) {
    const dot = new Mesh(
      index % 3 === 0 ? fieldGeometryLarge : fieldGeometrySmall,
      index % 5 === 0 ? fieldMaterialAmber : fieldMaterialCyan
    )
    dot.position.set(
      (Math.random() - 0.5) * 6.4,
      (Math.random() - 0.5) * 2.6,
      (Math.random() - 0.5) * 3.8
    )
    field.add(dot)
  }
  rootGroup.add(field)

  resizeScene()
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
    intersectionObserver.observe(canvas)
  } else {
    isInViewport = true
  }
  pageVisible = !document.hidden
  document.addEventListener('visibilitychange', handleVisibilityChange)
  window.addEventListener('pointermove', handlePointerMove)
  updateLoopState()
}

function updateLineGeometry() {
  if (!lineGeometry) return
  const position = lineGeometry.getAttribute('position') as BufferAttribute
  linkPairs.forEach(([from, to], index) => {
    const start = nodes[from]?.position || nodePositions[from]
    const end = nodes[to]?.position || nodePositions[to]
    const offset = index * 6
    position.array[offset] = start.x
    position.array[offset + 1] = start.y
    position.array[offset + 2] = start.z
    position.array[offset + 3] = end.x
    position.array[offset + 4] = end.y
    position.array[offset + 5] = end.z
  })
  position.needsUpdate = true
}

function resizeScene() {
  const canvas = canvasRef.value
  if (!canvas || !renderer || !camera) return

  const rect = canvas.getBoundingClientRect()
  const width = Math.max(rect.width, 1)
  const height = Math.max(rect.height, 1)
  renderer.setSize(width, height, false)
  camera.aspect = width / height
  camera.updateProjectionMatrix()
}

function scheduleResize() {
  if (resizeFrameId) return
  resizeFrameId = requestAnimationFrame(() => {
    resizeFrameId = 0
    resizeScene()
  })
}

function handlePointerMove(event: PointerEvent) {
  pointer = {
    x: (event.clientX / window.innerWidth - 0.5) * 2,
    y: (event.clientY / window.innerHeight - 0.5) * 2
  }
}

function handleVisibilityChange() {
  pageVisible = !document.hidden
  updateLoopState()
}

function shouldRunLoop() {
  return Boolean(renderer && scene && camera && rootGroup && isInViewport && pageVisible && canRender())
}

function startLoop() {
  if (loopRunning || !shouldRunLoop()) return

  loopRunning = true
  lastFrameTime = 0
  frameId = requestAnimationFrame(animate)
}

function pauseLoop() {
  if (!loopRunning) return

  loopRunning = false
  cancelAnimationFrame(frameId)
  frameId = 0
  lastFrameTime = 0
}

function updateLoopState() {
  if (shouldRunLoop()) {
    startLoop()
  } else {
    pauseLoop()
  }
}

function animate(time: number) {
  if (!renderer || !scene || !camera || !rootGroup || !loopRunning) return

  const deltaMs = lastFrameTime > 0 ? Math.min(time - lastFrameTime, 50) : 16.67
  lastFrameTime = time
  const t = time * 0.001
  const smoothing = Math.min(deltaMs / 280, 0.08)
  rootGroup.rotation.y += ((-0.28 + pointer.x * 0.16) - rootGroup.rotation.y) * smoothing
  rootGroup.rotation.x += ((-0.34 + pointer.y * 0.08) - rootGroup.rotation.x) * smoothing
  rootGroup.rotation.z = Math.sin(t * 0.22) * 0.025
  if (ringsGroup) {
    ringsGroup.rotation.y += deltaMs * 0.00006
    ringsGroup.rotation.z += deltaMs * 0.000025
  }

  nodes.forEach((node, index) => {
    const base = nodePositions[index]
    node.position.y = base.y + Math.sin(t * 1.2 + index * 0.82) * 0.045
    const scale = 1 + Math.sin(t * 2.4 + index) * 0.18
    node.scale.setScalar(scale)
  })
  updateLineGeometry()

  packets.forEach((packet) => {
    packet.progress += packet.speed * deltaMs
    if (packet.progress > 1) packet.progress = 0
    const start = nodes[packet.from].position
    const end = nodes[packet.to].position
    packet.mesh.position.lerpVectors(start, end, packet.progress)
  })

  renderer.render(scene, camera)
  if (loopRunning) frameId = requestAnimationFrame(animate)
}

function teardown() {
  pauseLoop()
  cancelAnimationFrame(resizeFrameId)
  resizeFrameId = 0
  window.removeEventListener('pointermove', handlePointerMove)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  resizeObserver?.disconnect()
  resizeObserver = null
  intersectionObserver?.disconnect()
  intersectionObserver = null

  const disposedGeometries = new WeakSet<ThreeBufferGeometry>()
  const disposedMaterials = new WeakSet<Material>()
  scene?.traverse((object) => {
    const disposable = object as Object3D & {
      geometry?: ThreeBufferGeometry
      material?: Material | Material[]
    }
    if (disposable.geometry && !disposedGeometries.has(disposable.geometry)) {
      disposable.geometry.dispose()
      disposedGeometries.add(disposable.geometry)
    }
    if (Array.isArray(disposable.material)) {
      disposable.material.forEach((material) => {
        if (!disposedMaterials.has(material)) {
          material.dispose()
          disposedMaterials.add(material)
        }
      })
    } else if (disposable.material && !disposedMaterials.has(disposable.material)) {
      disposable.material.dispose()
      disposedMaterials.add(disposable.material)
    }
  })
  renderer?.dispose()
  renderer?.forceContextLoss()
  renderer = null
  scene = null
  camera = null
  rootGroup = null
  ringsGroup = null
  lineGeometry = null
  nodes = []
  packets = []
  isInViewport = false
  pageVisible = true
}

onMounted(createScene)
onBeforeUnmount(teardown)
</script>

<style scoped>
.lab-spatial-scene {
  position: absolute;
  inset: -8% -8% -6% -8%;
  z-index: 0;
  width: 116%;
  height: 112%;
  pointer-events: none;
  opacity: 0.84;
  mix-blend-mode: screen;
}

@media (max-width: 1023px), (prefers-reduced-motion: reduce) {
  .lab-spatial-scene {
    display: none;
  }
}
</style>
