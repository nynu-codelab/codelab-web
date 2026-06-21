<template>
  <section id="hero" class="preview-hero preview-container">
    <div class="preview-hero__copy">
      <span class="preview-hero__label">High-impact visual preview / 预览数据</span>
      <h1>南阳师范学院 Code Lab 实验室</h1>
      <p class="preview-hero__lead">
        专注项目实战、技术分享与工程能力培养，在真实开发中提升软件工程能力。
      </p>
      <p class="preview-hero__sub">
        从代码实践到项目落地，构建属于学生的技术成长社区。
      </p>
      <div class="preview-hero__actions">
        <GlowButton label="立即报名" to="/recruit" />
        <GlowButton label="查看项目" to="/projects" variant="secondary" />
        <GlowButton label="阅读文章" to="/articles" variant="ghost" />
      </div>
    </div>

    <div class="preview-hero__stage" aria-label="未来首页工程视觉预览">
      <div class="preview-terminal preview-card">
        <div class="preview-terminal__chrome">
          <span></span>
          <span></span>
          <span></span>
          <strong>codelab.pipeline</strong>
        </div>
        <div class="preview-terminal__body">
          <p><span>$</span> npm run build:ideas</p>
          <p><span>></span> routes: directions, projects, articles, recruit</p>
          <p><span>></span> motion: grid, glow, reveal, hover tilt</p>
          <p><span>></span> status: preview ready for review</p>
        </div>
      </div>

      <div class="preview-architecture">
        <div class="preview-architecture__node is-active">实战项目</div>
        <div class="preview-architecture__node">技术分享</div>
        <div class="preview-architecture__node">工程训练</div>
        <div class="preview-architecture__node">招新系统</div>
        <span class="preview-architecture__line"></span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import GlowButton from './GlowButton.vue'
</script>

<style scoped>
.preview-hero {
  display: grid;
  grid-template-columns: minmax(0, 0.94fr) minmax(380px, 0.7fr);
  gap: 44px;
  align-items: center;
  min-height: calc(100vh - 72px);
  padding: 82px 0 70px;
}

.preview-hero__label {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 8px 12px;
  border: 1px solid rgba(83, 231, 255, 0.28);
  border-radius: 999px;
  color: var(--preview-cyan);
  background: rgba(83, 231, 255, 0.07);
  font-family: var(--preview-font-data);
  font-size: 12px;
}

.preview-hero h1 {
  max-width: 820px;
  margin-top: 24px;
  color: #f8fcff;
  font-size: clamp(42px, 7vw, 88px);
  font-weight: 820;
  line-height: 0.98;
}

.preview-hero__lead {
  max-width: 760px;
  margin-top: 26px;
  color: #dcecff;
  font-size: clamp(18px, 2.2vw, 27px);
  line-height: 1.55;
}

.preview-hero__sub {
  max-width: 620px;
  margin-top: 14px;
  color: var(--preview-muted);
  font-size: 16px;
  line-height: 1.8;
}

.preview-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 32px;
}

.preview-hero__stage {
  position: relative;
  min-height: 540px;
}

.preview-terminal {
  position: absolute;
  inset: 24px 0 auto auto;
  width: min(100%, 520px);
  min-height: 322px;
  padding: 0;
  transform: rotate(1deg);
}

.preview-terminal__chrome {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(153, 217, 255, 0.14);
}

.preview-terminal__chrome span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: rgba(238, 247, 255, 0.28);
}

.preview-terminal__chrome span:first-child {
  background: var(--preview-danger);
}

.preview-terminal__chrome span:nth-child(2) {
  background: var(--preview-amber);
}

.preview-terminal__chrome span:nth-child(3) {
  background: var(--preview-teal);
}

.preview-terminal__chrome strong {
  margin-left: auto;
  color: var(--preview-muted);
  font-family: var(--preview-font-data);
  font-size: 12px;
  font-weight: 500;
}

.preview-terminal__body {
  display: grid;
  gap: 15px;
  padding: 26px 22px 28px;
  color: #dff8ff;
  font-family: var(--preview-font-data);
  font-size: 13px;
  line-height: 1.6;
}

.preview-terminal__body p {
  overflow-wrap: anywhere;
}

.preview-terminal__body span {
  color: var(--preview-teal);
}

.preview-architecture {
  position: absolute;
  left: 0;
  right: 32px;
  bottom: 28px;
  min-height: 256px;
  border: 1px solid rgba(153, 217, 255, 0.12);
  border-radius: var(--preview-radius-lg);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0.015)),
    rgba(3, 9, 16, 0.54);
  box-shadow: var(--preview-shadow);
  backdrop-filter: blur(18px);
}

.preview-architecture__node {
  position: absolute;
  display: grid;
  place-items: center;
  width: 128px;
  height: 54px;
  border: 1px solid rgba(153, 217, 255, 0.22);
  border-radius: 999px;
  color: #dff8ff;
  background: rgba(8, 18, 30, 0.84);
  font-size: 14px;
}

.preview-architecture__node.is-active {
  color: #031018;
  background: linear-gradient(135deg, var(--preview-cyan), var(--preview-teal));
  box-shadow: 0 0 34px rgba(83, 231, 255, 0.34);
}

.preview-architecture__node:nth-child(1) {
  left: 42px;
  top: 42px;
}

.preview-architecture__node:nth-child(2) {
  right: 44px;
  top: 54px;
}

.preview-architecture__node:nth-child(3) {
  left: 26%;
  bottom: 42px;
}

.preview-architecture__node:nth-child(4) {
  right: 18%;
  bottom: 48px;
}

.preview-architecture__line {
  position: absolute;
  inset: 74px 84px 76px;
  border: 1px solid rgba(83, 231, 255, 0.18);
  border-radius: 50%;
  animation: orbit-pulse 5s ease-in-out infinite;
}

@keyframes orbit-pulse {
  0%,
  100% {
    opacity: 0.28;
    transform: scale(0.98);
  }
  50% {
    opacity: 0.78;
    transform: scale(1.03);
  }
}

@media (max-width: 980px) {
  .preview-hero {
    grid-template-columns: 1fr;
    min-height: auto;
    padding-top: 64px;
  }

  .preview-hero__stage {
    min-height: 470px;
  }
}

@media (max-width: 560px) {
  .preview-hero {
    padding: 44px 0 48px;
  }

  .preview-hero h1 {
    line-height: 1.05;
  }

  .preview-hero__actions {
    display: grid;
  }

  .preview-hero__stage {
    min-height: 420px;
  }

  .preview-terminal {
    inset: 0 0 auto;
    transform: none;
  }

  .preview-architecture {
    right: 0;
    bottom: 0;
    min-height: 220px;
  }

  .preview-architecture__node {
    width: 112px;
    height: 48px;
    font-size: 13px;
  }

  .preview-architecture__node:nth-child(2),
  .preview-architecture__node:nth-child(4) {
    right: 20px;
  }

  .preview-architecture__node:nth-child(3) {
    left: 20px;
  }
}
</style>
