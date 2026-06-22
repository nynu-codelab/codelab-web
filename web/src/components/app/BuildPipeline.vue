<template>
  <ol class="build-pipeline">
    <li v-for="(step, index) in normalizedSteps" :key="step.label">
      <span class="build-pipeline__node">{{ String(index + 1).padStart(2, '0') }}</span>
      <div>
        <strong>{{ step.label }}</strong>
        <small>{{ step.detail }}</small>
      </div>
    </li>
  </ol>
</template>

<script setup lang="ts">
type PipelineStep = {
  label: string
  detail: string
}

const props = withDefaults(
  defineProps<{
    steps?: PipelineStep[]
  }>(),
  {
    steps: () => [
      { label: 'DESIGN', detail: '需求拆解' },
      { label: 'CODE', detail: '工程实现' },
      { label: 'REVIEW', detail: '代码评审' },
      { label: 'DEPLOY', detail: '部署验证' }
    ]
  }
)

const normalizedSteps = props.steps
</script>

<style scoped>
.build-pipeline {
  position: relative;
  display: grid;
  gap: 12px;
}

.build-pipeline::before {
  content: "";
  position: absolute;
  left: 17px;
  top: 18px;
  bottom: 18px;
  width: 1px;
  background: linear-gradient(180deg, rgba(83, 231, 255, 0.18), rgba(47, 240, 182, 0.6), rgba(83, 231, 255, 0.18));
  box-shadow: 0 0 22px rgba(83, 231, 255, 0.24);
}

.build-pipeline li {
  position: relative;
  display: grid;
  grid-template-columns: 36px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 11px 12px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius-sm);
  background: rgba(255, 255, 255, 0.045);
}

.build-pipeline__node {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border: 1px solid rgba(83, 231, 255, 0.34);
  border-radius: 999px;
  color: var(--app-cyan);
  background: rgba(4, 10, 18, 0.86);
  box-shadow: 0 0 24px rgba(83, 231, 255, 0.15);
  font-family: var(--app-font-data);
  font-size: 11px;
}

.build-pipeline strong,
.build-pipeline small {
  display: block;
}

.build-pipeline strong {
  color: var(--app-text-strong);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.build-pipeline small {
  margin-top: 2px;
  color: var(--app-muted);
}
</style>
