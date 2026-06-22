<template>
  <aside class="lab-control glass-card">
    <div class="lab-control__header">
      <span></span>
      <strong>LAB CONTROL CENTER</strong>
    </div>
    <dl>
      <div v-for="item in rows" :key="item.label">
        <dt>{{ item.label }}</dt>
        <dd>{{ item.value }}</dd>
      </div>
    </dl>
  </aside>
</template>

<script setup lang="ts">
const rows = [
  { label: 'LAB STATUS', value: 'RECRUITING' },
  { label: 'FOCUS', value: 'PROJECT PRACTICE' },
  { label: 'STACK', value: 'JAVA / VUE / MYSQL / DOCKER' },
  { label: 'MODE', value: 'LEARN BY BUILDING' },
  { label: 'PIPELINE', value: 'DESIGN -> CODE -> REVIEW -> DEPLOY' }
]
</script>

<style scoped>
.lab-control {
  padding: 18px;
}

.lab-control__header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(153, 217, 255, 0.12);
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.lab-control__header span {
  width: 9px;
  height: 9px;
  border-radius: 999px;
  background: var(--app-success);
  box-shadow: 0 0 18px rgba(109, 242, 166, 0.65);
}

.lab-control dl {
  display: grid;
  gap: 11px;
  margin-top: 16px;
}

.lab-control div {
  display: grid;
  grid-template-columns: 104px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.lab-control dt {
  color: var(--app-muted);
  font-family: var(--app-font-data);
  font-size: 11px;
}

.lab-control dd {
  min-width: 0;
  overflow: hidden;
  color: var(--app-text-strong);
  font-family: var(--app-font-data);
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
