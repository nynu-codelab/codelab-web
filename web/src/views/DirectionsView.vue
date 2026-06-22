<template>
  <AppFrame>
    <PageHero
      eyebrow="Technical Tracks"
      title="技术方向"
      description="以核心方向建立软件工程基础，以拓展方向打开技术视野。每个方向都服务于真实项目训练。"
    >
      <template #actions>
        <AppButton label="加入方向学习" to="/recruit" />
        <AppButton label="查看项目成果" to="/projects" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="tracks.matrix"
          :commands="['selectTrack(javaBackend)', 'selectTrack(frontend)', 'selectTrack(miniprogram)', 'extend(aiAndOps)']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container app-grid three">
        <DirectionCard
          v-for="item in directions"
          :key="item.title"
          v-bind="item"
        />
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import DirectionCard from '@/components/app/DirectionCard.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'

const directions = [
  {
    index: '01',
    title: 'Java 后端',
    badge: '核心方向',
    description: '学习服务端分层、接口设计、认证鉴权、数据库建模和上线前验证。',
    tags: ['Java 17', 'Spring Boot', 'MyBatis-Plus', 'Sa-Token']
  },
  {
    index: '02',
    title: '前端开发',
    badge: '核心方向',
    description: '学习 Vue 3、TypeScript、路由、状态管理、组件化和响应式体验。',
    tags: ['Vue 3', 'TypeScript', 'Vite', 'Pinia']
  },
  {
    index: '03',
    title: '微信小程序',
    badge: '核心方向',
    description: '面向移动端场景训练登录态、接口联调、页面组织和发布流程。',
    tags: ['小程序', '移动端', '接口联调', '体验优化']
  },
  {
    index: '04',
    title: '人工智能',
    badge: '拓展方向',
    description: '探索智能化应用原型、数据处理和模型能力接入。',
    tags: ['AI 应用', 'Prompt', '数据处理']
  },
  {
    index: '05',
    title: '数据库与运维',
    badge: '拓展方向',
    description: '理解 MySQL、Docker、Nginx、环境变量和部署验证。',
    tags: ['MySQL', 'Docker', 'Nginx', 'CI 思维']
  }
]
</script>
