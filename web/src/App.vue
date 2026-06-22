<template>
  <div class="route-loading-bar" v-if="isRouteLoading"></div>
  <router-view />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isRouteLoading = ref(false)

// 路由懒加载指示器：chunk 加载期间显示顶部进度条
let resolveTimer: ReturnType<typeof setTimeout>
router.beforeResolve((_to, _from, next) => {
  clearTimeout(resolveTimer)
  isRouteLoading.value = true
  next()
})
router.afterEach(() => {
  resolveTimer = setTimeout(() => {
    isRouteLoading.value = false
  }, 150)
})
</script>

<style>
.route-loading-bar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #53e7ff, #a98bff, #53e7ff);
  background-size: 200% 100%;
  animation: route-loading-slide 0.8s linear infinite;
}

@keyframes route-loading-slide {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
