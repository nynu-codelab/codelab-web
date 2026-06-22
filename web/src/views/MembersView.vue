<template>
  <AppFrame>
    <PageHero
      eyebrow="Member System"
      title="核心成员"
      :description="heroDescription"
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
      <div v-if="loading" class="app-container">
        <p class="status-text">正在读取成员数据...</p>
      </div>

      <div v-else-if="error" class="app-container">
        <p class="status-text error">读取失败：{{ error }}</p>
      </div>

      <div v-else-if="members.length > 0" class="app-container app-grid three">
        <article
          v-for="item in members"
          :key="item.id"
          class="member-card glass-card is-hoverable"
        >
          <div class="member-card__avatar">
            <img
              v-if="item.avatarUrl"
              :src="item.avatarUrl"
              :alt="item.name"
              class="member-card__avatar-img"
            />
            <span v-else>{{ getInitials(item.name) }}</span>
          </div>
          <div>
            <span class="status-pill">{{ item.roleTitle || '成员' }}</span>
            <h3>{{ item.name }}</h3>
            <p>{{ item.bio || '暂无简介' }}</p>
          </div>
          <div class="pill-row">
            <span class="tech-pill" v-for="tag in parseTags(item.skills)" :key="tag">{{ tag }}</span>
          </div>
          <div v-if="item.githubUrl || item.blogUrl || item.email" class="member-links">
            <a v-if="item.githubUrl" :href="item.githubUrl" target="_blank" rel="noopener noreferrer" class="member-link" title="GitHub">
              GitHub
            </a>
            <a v-if="item.blogUrl" :href="item.blogUrl" target="_blank" rel="noopener noreferrer" class="member-link" title="博客">
              博客
            </a>
            <a v-if="item.email" :href="'mailto:' + item.email" class="member-link" title="邮箱">
              邮箱
            </a>
          </div>
        </article>
      </div>

      <!-- Fallback: ability structure when no members configured -->
      <div v-else class="app-container app-grid three">
        <MemberCard
          v-for="item in fallbackRoles"
          :key="item.title"
          v-bind="item"
        />
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import MemberCard from '@/components/app/MemberCard.vue'
import GitBranchMap from '@/components/app/GitBranchMap.vue'
import { getMembers, type MemberItem } from '@/api/member'

const members = ref<MemberItem[]>([])
const loading = ref(true)
const error = ref('')

const heroDescription = computed(() => {
  if (loading.value) return '正在加载成员数据...'
  if (members.value.length > 0) return '与优秀成员共同成长，以工程实践驱动能力提升。'
  return '成员展示模块后续将对接真实成员数据。当前正式页仅展示团队能力结构，不编造真实成员姓名、头像或联系方式。'
})

const fallbackRoles = [
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

function getInitials(name: string): string {
  if (!name) return '?'
  const trimmed = name.trim()
  const parts = trimmed.split(/\s+/)
  if (parts.length >= 2) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
  }
  return trimmed.slice(0, 2).toUpperCase()
}

function parseTags(skills: string): string[] {
  if (!skills) return []
  return skills.split(',').map((s) => s.trim()).filter(Boolean)
}

onMounted(async () => {
  try {
    const res = await getMembers()
    if (res.code === 200 && Array.isArray(res.data)) {
      members.value = res.data.sort((a, b) => a.sortOrder - b.sortOrder)
    }
  } catch (e: any) {
    error.value = e?.message || '加载成员数据失败，请稍后重试'
    console.error('[MembersView] fetch members error:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.member-card {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.member-card__avatar {
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border: 1px solid rgba(83, 231, 255, 0.38);
  border-radius: 18px;
  color: #041017;
  background: linear-gradient(135deg, var(--app-cyan), var(--app-teal));
  box-shadow: 0 0 34px rgba(83, 231, 255, 0.2);
  font-family: var(--app-font-data);
  font-weight: 840;
  overflow: hidden;
}

.member-card__avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-card h3 {
  margin: 14px 0 8px;
  color: var(--app-text-strong);
  font-size: 21px;
}

.member-card p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.75;
}

.member-links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 4px;
}

.member-link {
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 12px;
  text-decoration: none;
  transition: color 180ms ease;
}

.member-link:hover {
  color: var(--app-teal);
  text-decoration: underline;
}

.status-text {
  color: var(--app-muted);
  text-align: center;
  padding: 48px 0;
  font-size: 15px;
}

.status-text.error {
  color: var(--app-danger, #ef5350);
}
</style>
