<template>
  <AppFrame>
    <PageHero
      eyebrow="About Code Lab"
      title="从代码实践到项目落地，构建学生技术成长社区"
      description="南阳师范学院 Code Lab 实验室是一个自建展示站与招新管理系统承载的学生技术团队，重点关注项目实战、技术分享和工程能力培养。"
    >
      <template #actions>
        <AppButton label="立即报名" to="/recruit" />
        <AppButton label="查看项目" to="/projects" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="lab.manifesto"
          :commands="['defineMission(projectPractice)', 'shareKnowledge()', 'reviewAndDeploy()']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container app-grid three">
        <article class="glass-card about-card" v-for="item in values" :key="item.title">
          <span class="app-eyebrow">{{ item.kicker }}</span>
          <h2>{{ item.title }}</h2>
          <p>{{ item.text }}</p>
        </article>
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="about-system glass-card">
          <div>
            <span class="app-eyebrow">Engineering Culture</span>
            <h2 class="app-title-lg">把一次次练习推进成可交付的项目</h2>
          </div>
          <div class="about-system__grid">
            <div v-for="item in systems" :key="item.title">
              <span>{{ item.index }}</span>
              <strong>{{ item.title }}</strong>
              <p>{{ item.text }}</p>
            </div>
          </div>
        </div>
        <GitBranchMap class="about-branch" />
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import GitBranchMap from '@/components/app/GitBranchMap.vue'

const values = [
  {
    kicker: 'Project First',
    title: '项目实战',
    text: '围绕真实页面、接口、权限、数据和部署链路组织训练，让学习结果能够被运行和验证。'
  },
  {
    kicker: 'Share More',
    title: '技术分享',
    text: '通过文章、复盘和代码评审沉淀知识，帮助新成员更快理解工程实践。'
  },
  {
    kicker: 'Build Systems',
    title: '工程能力',
    text: '重视需求拆解、组件抽象、接口契约、环境配置和上线前验收。'
  }
]

const systems = [
  { index: '01', title: '需求到任务', text: '把招新、内容展示和后台管理拆成可交付的功能单元。' },
  { index: '02', title: '代码到构建', text: '前台、后台、后端分别通过构建验证，确保基础质量。' },
  { index: '03', title: '接口到体验', text: '业务接口保持稳定，视觉升级服务于实际流程。' },
  { index: '04', title: '复盘到成长', text: '项目交付后用文章与记录沉淀可复用经验。' }
]
</script>

<style scoped>
.about-card {
  padding: 28px;
}

.about-card h2 {
  margin-bottom: 12px;
  color: var(--app-text-strong);
  font-size: 26px;
}

.about-card p {
  color: var(--app-muted);
  line-height: 1.8;
}

.about-system {
  display: grid;
  grid-template-columns: minmax(0, 0.72fr) minmax(360px, 1fr);
  gap: 32px;
  padding: clamp(28px, 5vw, 46px);
}

.about-branch {
  margin-top: 18px;
}

.about-system__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.about-system__grid div {
  padding: 18px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius-sm);
  background: rgba(255, 255, 255, 0.045);
}

.about-system__grid span {
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.about-system__grid strong {
  display: block;
  margin: 8px 0;
  color: var(--app-text-strong);
}

.about-system__grid p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.7;
}

@media (max-width: 860px) {
  .about-system {
    grid-template-columns: 1fr;
  }

  .about-system__grid {
    grid-template-columns: 1fr;
  }
}
</style>
