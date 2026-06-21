<template>
  <section class="recruit-cta glass-card">
    <div>
      <span class="app-eyebrow">Recruit Pipeline</span>
      <h2 class="app-title-lg">从注册账号到审核状态，全流程在线完成</h2>
      <p>
        注册账号、登录系统、填写报名信息、提交报名、管理员审核、用户查看审核状态。
        这条链路保持当前真实业务闭环，视觉层升级后继续服务招新转化。
      </p>
    </div>
    <ol class="recruit-cta__steps">
      <li v-for="step in steps" :key="step">{{ step }}</li>
    </ol>
    <div class="recruit-cta__actions">
      <AppButton label="立即报名" to="/recruit" size="lg" />
      <AppButton label="查看我的报名" to="/my-application" variant="secondary" size="lg" />
    </div>
  </section>
</template>

<script setup lang="ts">
import AppButton from './AppButton.vue'

const steps = [
  '注册账号',
  '登录系统',
  '填写报名信息',
  '提交报名',
  '管理员审核',
  '查看审核状态'
]
</script>

<style scoped>
.recruit-cta {
  display: grid;
  grid-template-columns: minmax(0, 0.82fr) minmax(320px, 0.7fr);
  gap: 30px;
  align-items: center;
  padding: clamp(28px, 5vw, 46px);
}

.recruit-cta p {
  margin-top: 18px;
  color: var(--app-muted);
  font-size: 16px;
  line-height: 1.8;
}

.recruit-cta__steps {
  display: grid;
  gap: 12px;
  counter-reset: steps;
}

.recruit-cta__steps li {
  position: relative;
  padding: 13px 14px 13px 52px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: var(--app-radius-sm);
  color: var(--app-soft);
  background: rgba(255, 255, 255, 0.045);
  counter-increment: steps;
}

.recruit-cta__steps li::before {
  content: counter(steps, decimal-leading-zero);
  position: absolute;
  left: 14px;
  top: 13px;
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.recruit-cta__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  grid-column: 1 / -1;
}

@media (max-width: 820px) {
  .recruit-cta {
    grid-template-columns: 1fr;
  }
}
</style>
