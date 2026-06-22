<template>
  <div
    class="preview-page"
    @pointermove="handlePointerMove"
  >
    <PreviewAnimatedBackground />
    <ParticleUniverse />
    <CodeRainCanvas />
    <EnergyFlowBackground />
    <div class="preview-shell">
      <PreviewHeader />
      <main>
        <PreviewHero />

        <AnimatedSection>
          <div class="preview-container">
            <div class="preview-section-header">
              <div>
                <span class="preview-eyebrow">Preview Metrics</span>
                <h2 class="preview-section-title">不用虚高数字，先展示真实可信的成长信号</h2>
              </div>
              <p class="preview-section-copy">
                以下均为预览表达，不代表当前真实运营统计。视觉上强调方向、项目、内容和招新状态。
              </p>
            </div>
            <div class="preview-grid five">
              <PreviewMetricCard
                v-for="metric in metrics"
                :key="metric.label"
                v-bind="metric"
              />
            </div>
          </div>
        </AnimatedSection>

        <div class="preview-divider"></div>

        <AnimatedSection>
          <div id="directions" class="preview-container">
            <div class="preview-section-header">
              <div>
                <span class="preview-eyebrow">Tech Directions</span>
                <h2 class="preview-section-title">面向项目实战组织技术方向，而不是堆砌课程名</h2>
              </div>
              <p class="preview-section-copy">
                卡片采用玻璃质感、轻量 3D hover 和技术栈标签，后续可接入真实方向接口。
              </p>
            </div>
            <div class="preview-grid five">
              <PreviewDirectionCard
                v-for="direction in directions"
                :key="direction.title"
                v-bind="direction"
              />
            </div>
          </div>
        </AnimatedSection>

        <AnimatedSection>
          <div id="projects" class="preview-container">
            <div class="preview-section-header">
              <div>
                <span class="preview-eyebrow">Featured Projects</span>
                <h2 class="preview-section-title">项目卡片更像产品成果，而不是普通列表</h2>
              </div>
              <p class="preview-section-copy">
                使用中性预览文案，不编造上线数据；视觉重点放在工程化、协作和作品沉淀。
              </p>
            </div>
            <div class="preview-grid three">
              <PreviewProjectCard
                v-for="project in projects"
                :key="project.title"
                v-bind="project"
              />
            </div>
          </div>
        </AnimatedSection>

        <AnimatedSection>
          <div id="articles" class="preview-container">
            <div class="preview-section-header">
              <div>
                <span class="preview-eyebrow">Learning Articles</span>
                <h2 class="preview-section-title">把学习文章做成值得阅读的技术内容入口</h2>
              </div>
              <p class="preview-section-copy">
                保留标题、标签、摘要的清晰层级，后续接入 Markdown 内容和分类筛选。
              </p>
            </div>
            <div class="preview-grid three">
              <PreviewArticleCard
                v-for="article in articles"
                :key="article.title"
                v-bind="article"
              />
            </div>
          </div>
        </AnimatedSection>

        <AnimatedSection>
          <PreviewRecruitCTA />
        </AnimatedSection>
      </main>
      <PreviewFooter />
    </div>
  </div>
</template>

<script setup lang="ts">
import AnimatedSection from '@/components/preview/AnimatedSection.vue'
import PreviewAnimatedBackground from '@/components/preview/PreviewAnimatedBackground.vue'
import PreviewArticleCard from '@/components/preview/PreviewArticleCard.vue'
import PreviewDirectionCard from '@/components/preview/PreviewDirectionCard.vue'
import PreviewFooter from '@/components/preview/PreviewFooter.vue'
import PreviewHeader from '@/components/preview/PreviewHeader.vue'
import PreviewHero from '@/components/preview/PreviewHero.vue'
import PreviewMetricCard from '@/components/preview/PreviewMetricCard.vue'
import PreviewProjectCard from '@/components/preview/PreviewProjectCard.vue'
import PreviewRecruitCTA from '@/components/preview/PreviewRecruitCTA.vue'
import CodeRainCanvas from '@/components/app/CodeRainCanvas.vue'
import EnergyFlowBackground from '@/components/app/EnergyFlowBackground.vue'
import ParticleUniverse from '@/components/app/ParticleUniverse.vue'

const metrics = [
  {
    value: '3',
    label: '核心方向',
    description: 'Java 后端、前端开发、微信小程序作为当前主要培养方向。'
  },
  {
    value: '多项',
    label: '真实项目',
    description: '以项目实战训练工程协作、接口设计、上线意识与复盘能力。'
  },
  {
    value: '持续',
    label: '学习分享',
    description: '通过 Markdown 文章沉淀学习笔记、技术分享与项目复盘。'
  },
  {
    value: '进行中',
    label: '招新状态',
    description: '注册登录后填写报名信息，管理员审核，用户查看进度。'
  },
  {
    value: '预览',
    label: '视觉版本',
    description: '本页为高冲击视觉方向预览，不代表全站已完成重构。'
  }
]

const directions = [
  {
    code: 'CORE / 01',
    title: 'Java 后端',
    description: '围绕 Spring Boot、接口设计、权限控制和数据库建模，训练可维护的服务端工程能力。',
    tags: ['Java 17', 'Spring Boot', 'MyBatis-Plus']
  },
  {
    code: 'CORE / 02',
    title: '前端开发',
    description: '从 Vue 3、TypeScript 到响应式体验，建立把产品想法变成可用界面的能力。',
    tags: ['Vue 3', 'TypeScript', 'Vite']
  },
  {
    code: 'CORE / 03',
    title: '微信小程序',
    description: '面向校园服务与轻量产品场景，训练移动端交互、接口联调和发布流程。',
    tags: ['小程序', 'API 联调', '移动端']
  },
  {
    code: 'EXT / 04',
    title: '人工智能',
    description: '作为拓展方向，探索模型调用、智能应用原型和 AI 辅助工程实践。',
    tags: ['AI 应用', '模型接口', '原型验证'],
    badge: '拓展方向'
  },
  {
    code: 'EXT / 05',
    title: '数据库与运维',
    description: '作为拓展方向，补齐数据设计、Docker 部署、日志排查与上线安全意识。',
    tags: ['MySQL', 'Docker', 'Nginx'],
    badge: '拓展方向'
  }
]

const projects = [
  {
    code: 'PROJECT / PREVIEW',
    type: '实验室项目成果展示',
    title: '从需求到上线的项目成果页',
    description: '展示项目背景、技术栈、工程亮点和复盘摘要，让作品看起来像认真完成的产品。',
    tags: ['工程化', '作品展示', '复盘']
  },
  {
    code: 'TRAINING / PREVIEW',
    type: '项目实战训练',
    title: '团队协作开发实践',
    description: '用真实分工、接口联调、前后端协作和验收脚本，训练学生从写代码到交付。',
    tags: ['协作', '接口联调', '验收']
  },
  {
    code: 'SYSTEM / PREVIEW',
    type: '工程化开发实践',
    title: '招新与内容管理系统',
    description: '围绕注册登录、报名审核、文章发布和项目展示建立完整闭环。',
    tags: ['认证', 'CMS', '招新']
  }
]

const articles = [
  {
    category: '技术分享',
    readTime: '预览',
    title: '如何把一个课堂项目整理成可展示作品',
    description: '从需求描述、技术选型、截图结构到复盘写法，帮助新成员建立作品表达能力。',
    tags: ['项目复盘', '表达', '工程实践']
  },
  {
    category: '学习笔记',
    readTime: '预览',
    title: 'Vue 3 页面开发中的组件拆分思路',
    description: '用清晰的组件边界组织页面结构，让后续迭代和接口接入更稳。',
    tags: ['Vue 3', '组件', '前端']
  },
  {
    category: '路线规划',
    readTime: '预览',
    title: '加入 Code Lab 后如何进入项目实战',
    description: '从基础补齐、任务拆解到团队协作，说明新同学的成长路径。',
    tags: ['招新', '成长路径', '协作']
  }
]

function handlePointerMove(event: PointerEvent) {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    return
  }

  document.documentElement.style.setProperty('--preview-pointer-x', `${event.clientX}px`)
  document.documentElement.style.setProperty('--preview-pointer-y', `${event.clientY}px`)
}
</script>
