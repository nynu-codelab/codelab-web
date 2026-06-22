<template>
  <AppFrame>
    <PageHero
      eyebrow="Member System"
      title="核心成员"
      description="成员展示模块后续将对接真实成员数据。当前正式页仅展示团队能力结构，不编造真实成员姓名、头像或联系方式。"
    >
      <template #actions>
        <AppButton label="招新报名" to="/recruit" />
        <AppButton label="了解实验室" to="/about" variant="secondary" />
      </template>
      <template #visual>
        <GitBranchMap />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container app-grid three">
        <MemberCard
          v-for="item in roles"
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
import MemberCard from '@/components/app/MemberCard.vue'
import GitBranchMap from '@/components/app/GitBranchMap.vue'

const roles = [
  {
    initials: 'BE',
    role: '后端能力',
    title: '服务端与数据建模',
    summary: '关注接口设计、权限控制、数据一致性和服务端构建验证。',
    tags: ['Java', 'API', 'Database']
  },
  {
    initials: 'FE',
    role: '前端能力',
    title: '界面工程与体验',
    summary: '关注组件化、状态管理、页面性能、响应式适配和交互质感。',
    tags: ['Vue', 'TypeScript', 'Motion']
  },
  {
    initials: 'OPS',
    role: '交付能力',
    title: '部署、验证与复盘',
    summary: '关注环境配置、Docker、Nginx、验收脚本和项目复盘。',
    tags: ['Docker', 'Nginx', 'QA']
  }
]
</script>
