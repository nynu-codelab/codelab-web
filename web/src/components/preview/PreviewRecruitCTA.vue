<template>
  <section id="recruit" class="recruit-cta preview-container">
    <div class="recruit-cta__content">
      <span class="preview-eyebrow">Recruit Flow</span>
      <h2>把招新流程做成清晰、可信、愿意点击的入口</h2>
      <p>
        预览版用流程卡展示完整报名链路：注册账号、登录系统、填写报名信息、提交报名、管理员审核、用户查看审核状态。
      </p>
      <GlowButton label="立即报名" to="/recruit" />
    </div>
    <div class="recruit-cta__steps">
      <div v-for="(step, index) in steps" :key="step" class="recruit-cta__step">
        <span>{{ String(index + 1).padStart(2, '0') }}</span>
        <strong>{{ step }}</strong>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import GlowButton from './GlowButton.vue'

const steps = ['注册账号', '登录系统', '填写报名信息', '提交报名', '管理员审核', '查看审核状态']
</script>

<style scoped>
.recruit-cta {
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(320px, 0.72fr);
  gap: 28px;
  align-items: stretch;
  padding: 36px;
  border: 1px solid rgba(83, 231, 255, 0.24);
  border-radius: var(--preview-radius-lg);
  background:
    radial-gradient(circle at 0% 0%, rgba(83, 231, 255, 0.24), transparent 34%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.025)),
    rgba(8, 14, 24, 0.78);
  box-shadow: var(--preview-shadow), var(--preview-glow);
  backdrop-filter: blur(24px);
}

.recruit-cta__content h2 {
  max-width: 680px;
  color: #f9fcff;
  font-size: clamp(30px, 4vw, 56px);
  line-height: 1.08;
}

.recruit-cta__content p {
  max-width: 650px;
  margin: 18px 0 26px;
  color: var(--preview-soft);
  font-size: 16px;
  line-height: 1.85;
}

.recruit-cta__steps {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.recruit-cta__step {
  min-height: 102px;
  padding: 16px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--preview-radius);
  background: rgba(2, 8, 14, 0.38);
}

.recruit-cta__step span {
  display: block;
  margin-bottom: 18px;
  color: var(--preview-cyan);
  font-family: var(--preview-font-data);
  font-size: 12px;
}

.recruit-cta__step strong {
  color: #f9fcff;
  font-size: 16px;
}

@media (max-width: 860px) {
  .recruit-cta {
    grid-template-columns: 1fr;
    padding: 24px;
  }
}

@media (max-width: 520px) {
  .recruit-cta__steps {
    grid-template-columns: 1fr;
  }
}
</style>
