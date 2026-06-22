<template>
  <div class="git-branch-map glass-card">
    <div class="git-branch-map__header">
      <GitBranch :size="18" />
      <strong>CodeLab workflow</strong>
    </div>
    <div class="git-branch-map__rail">
      <span v-for="node in nodes" :key="node.label" :class="node.className">
        <i></i>
        <strong>{{ node.label }}</strong>
        <small>{{ node.detail }}</small>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { GitBranch } from '@lucide/vue'

const nodes = [
  { label: 'issue', detail: '需求拆解', className: 'node-a' },
  { label: 'feature', detail: '功能实现', className: 'node-b' },
  { label: 'review', detail: '代码评审', className: 'node-c' },
  { label: 'release', detail: '部署验收', className: 'node-d' }
]
</script>

<style scoped>
.git-branch-map {
  padding: 22px;
}

.git-branch-map__header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 13px;
}

.git-branch-map__rail {
  position: relative;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 28px;
}

.git-branch-map__rail::before {
  content: "";
  position: absolute;
  left: 8%;
  right: 8%;
  top: 15px;
  height: 2px;
  background: linear-gradient(90deg, rgba(83, 231, 255, 0.1), rgba(47, 240, 182, 0.72), rgba(117, 167, 255, 0.18));
  box-shadow: 0 0 18px rgba(83, 231, 255, 0.24);
}

.git-branch-map__rail span {
  position: relative;
  display: grid;
  justify-items: center;
  gap: 7px;
  text-align: center;
}

.git-branch-map__rail i {
  width: 32px;
  height: 32px;
  border: 1px solid rgba(83, 231, 255, 0.42);
  border-radius: 999px;
  background:
    radial-gradient(circle, rgba(83, 231, 255, 0.62), rgba(83, 231, 255, 0.1) 58%, rgba(4, 10, 18, 0.94));
  box-shadow: 0 0 26px rgba(83, 231, 255, 0.25);
}

.git-branch-map__rail strong {
  color: var(--app-text-strong);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.git-branch-map__rail small {
  color: var(--app-muted);
  font-size: 12px;
}

.node-b,
.node-d {
  transform: translateY(28px);
}

@media (max-width: 720px) {
  .git-branch-map__rail {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    row-gap: 34px;
  }

  .git-branch-map__rail::before {
    display: none;
  }

  .node-b,
  .node-d {
    transform: none;
  }
}
</style>
