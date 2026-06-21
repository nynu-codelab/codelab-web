<template>
  <AppFrame>
    <PageHero
      eyebrow="NYNU Code Lab"
      title="南阳师范学院 Code Lab 实验室"
      description="专注项目实战、技术分享与工程能力培养，在真实开发中提升软件工程能力。"
    >
      <template #actions>
        <AppButton label="立即报名" to="/recruit" size="lg" />
        <AppButton label="查看项目" to="/projects" variant="secondary" size="lg" />
        <AppButton label="阅读文章" to="/articles" variant="ghost" size="lg" />
      </template>

      <template #visual>
        <div class="hero-terminal glass-card">
          <div class="hero-terminal__bar">
            <span></span>
            <span></span>
            <span></span>
            <strong>code-lab.pipeline</strong>
          </div>
          <div class="hero-terminal__body">
            <p><span>$</span> init student-engineering-workflow</p>
            <p><span>></span> project_practice: active</p>
            <p><span>></span> tech_sharing: continuous</p>
            <p><span>></span> recruitment: open</p>
          </div>
          <div class="hero-terminal__orbit"></div>
        </div>
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container app-grid four">
        <MetricCard
          v-for="metric in metrics"
          :key="metric.label"
          :value="metric.value"
          :label="metric.label"
          :caption="metric.caption"
        />
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="app-section-header">
          <div>
            <span class="app-eyebrow">Technical Tracks</span>
            <h2 class="app-title-lg">围绕真实开发组织学习路径</h2>
          </div>
          <p class="app-copy">
            以 Java 后端、前端开发、微信小程序为核心方向，人工智能与数据库运维作为拓展方向，
            让同学在真实项目里建立工程化思维。
          </p>
        </div>
        <div class="app-grid three">
          <DirectionCard
            v-for="item in directions"
            :key="item.title"
            v-bind="item"
          />
        </div>
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="app-section-header">
          <div>
            <span class="app-eyebrow">Featured Projects</span>
            <h2 class="app-title-lg">项目成果</h2>
          </div>
          <div class="section-actions">
            <p class="app-copy">展示已发布的实验室项目成果，未伪造上线数据或成员数量。</p>
            <AppButton label="全部项目" to="/projects" variant="secondary" />
          </div>
        </div>

        <StateView
          v-if="projectsLoading"
          title="正在加载项目"
          message="正在读取已发布的精选项目数据。"
        />
        <StateView
          v-else-if="projectError"
          title="项目加载失败"
          :message="projectError"
        >
          <template #actions>
            <AppButton label="重新加载" variant="secondary" @click="fetchHomeData" />
          </template>
        </StateView>
        <StateView
          v-else-if="featuredProjects.length === 0"
          title="暂无精选项目"
          message="当前还没有配置精选项目，项目成果页可在后台发布后展示。"
        >
          <template #actions>
            <AppButton label="查看项目页" to="/projects" variant="secondary" />
          </template>
        </StateView>
        <div v-else class="app-grid three">
          <ProjectCard
            v-for="item in featuredProjects"
            :key="item.id"
            :title="item.title"
            :summary="fallbackText(item.summary, '项目成果简介待完善')"
            :type="item.projectType"
            :to="`/projects/${item.id}`"
            :cover-url="item.coverUrl"
            :tags="parseList(item.techStack).slice(0, 4)"
            :meta="item.featured ? '精选' : undefined"
          />
        </div>
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="app-section-header">
          <div>
            <span class="app-eyebrow">Learning Notes</span>
            <h2 class="app-title-lg">学习文章</h2>
          </div>
          <div class="section-actions">
            <p class="app-copy">沉淀项目复盘、技术笔记和工程实践经验。</p>
            <AppButton label="阅读全部" to="/articles" variant="secondary" />
          </div>
        </div>

        <StateView
          v-if="articlesLoading"
          title="正在加载文章"
          message="正在读取已发布的学习文章。"
        />
        <StateView
          v-else-if="articleError"
          title="文章加载失败"
          :message="articleError"
        />
        <StateView
          v-else-if="latestArticles.length === 0"
          title="暂无学习文章"
          message="文章发布后会在这里展示标题、标签、摘要和阅读入口。"
        />
        <div v-else class="app-grid three">
          <ArticleCard
            v-for="item in latestArticles"
            :key="item.id"
            :title="item.title"
            :summary="fallbackText(item.summary, '文章摘要待完善')"
            :category="item.category"
            :published-at="item.publishedAt || item.createTime"
            :tags="parseList(item.tags).slice(0, 4)"
            :to="`/articles/${item.id}`"
          />
        </div>
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="app-section-header">
          <div>
            <span class="app-eyebrow">Team Style</span>
            <h2 class="app-title-lg">年轻技术团队的协作方式</h2>
          </div>
          <p class="app-copy">
            成员展示模块后续对接真实数据前，正式页面只展示角色能力结构，不编造真实成员姓名或联系方式。
          </p>
        </div>
        <div class="app-grid three">
          <MemberCard
            v-for="item in memberRoles"
            :key="item.title"
            v-bind="item"
          />
        </div>
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <RecruitCTA />
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import AppButton from '@/components/app/AppButton.vue'
import PageHero from '@/components/app/PageHero.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import MetricCard from '@/components/app/MetricCard.vue'
import DirectionCard from '@/components/app/DirectionCard.vue'
import ProjectCard from '@/components/app/ProjectCard.vue'
import ArticleCard from '@/components/app/ArticleCard.vue'
import MemberCard from '@/components/app/MemberCard.vue'
import RecruitCTA from '@/components/app/RecruitCTA.vue'
import StateView from '@/components/app/StateView.vue'
import { getArticles, type ArticleItem } from '@/api/article'
import { getFeaturedProjects, type ProjectItem } from '@/api/project'
import { fallbackText, parseList } from '@/utils/content'

const metrics = [
  { value: '3', label: '核心方向', caption: 'Java 后端、前端开发、微信小程序' },
  { value: '多个', label: '真实项目', caption: '以项目成果和工程训练驱动成长' },
  { value: '持续', label: '学习分享', caption: '通过文章和复盘沉淀实践经验' },
  { value: '开放', label: '招新通道', caption: '注册登录后可提交报名并查看审核状态' }
]

const directions = [
  {
    index: '01',
    title: 'Java 后端',
    badge: '核心方向',
    description: '围绕 Spring Boot、接口设计、权限认证、数据库建模和部署链路进行项目实战。',
    tags: ['Java 17', 'Spring Boot', 'MyBatis-Plus', 'JWT']
  },
  {
    index: '02',
    title: '前端开发',
    badge: '核心方向',
    description: '从 Vue 3、TypeScript、组件化、状态管理到可访问的交互体验，面向真实产品构建页面。',
    tags: ['Vue 3', 'TypeScript', 'Vite', 'Pinia']
  },
  {
    index: '03',
    title: '微信小程序',
    badge: '核心方向',
    description: '面向移动端场景完成界面、接口、登录态和发布链路的完整训练。',
    tags: ['小程序', '移动端', '接口联调', '发布流程']
  },
  {
    index: '04',
    title: '人工智能',
    badge: '拓展方向',
    description: '以应用实践为目标，探索数据处理、模型调用与智能化功能原型。',
    tags: ['AI 应用', '数据处理', '原型验证']
  },
  {
    index: '05',
    title: '数据库与运维',
    badge: '拓展方向',
    description: '理解 MySQL、Docker、Nginx、环境变量和上线前验证，补齐工程交付能力。',
    tags: ['MySQL', 'Docker', 'Nginx', '部署']
  }
]

const memberRoles = [
  {
    initials: 'BE',
    role: '能力结构',
    title: '后端与接口协作',
    summary: '负责服务端设计、权限边界、数据模型和接口稳定性，支撑项目从功能到可部署。',
    tags: ['API', 'Auth', 'Database']
  },
  {
    initials: 'FE',
    role: '能力结构',
    title: '前端与体验实现',
    summary: '负责页面工程、组件抽象、交互状态和响应式适配，让项目具备完整展示与使用体验。',
    tags: ['UI', 'State', 'Responsive']
  },
  {
    initials: 'PM',
    role: '能力结构',
    title: '项目推进与复盘',
    summary: '围绕需求拆解、任务推进、代码评审和项目复盘建立团队协作节奏。',
    tags: ['Planning', 'Review', 'Delivery']
  }
]

const featuredProjects = ref<ProjectItem[]>([])
const latestArticles = ref<ArticleItem[]>([])
const projectsLoading = ref(true)
const articlesLoading = ref(true)
const projectError = ref('')
const articleError = ref('')

async function fetchHomeData() {
  projectsLoading.value = true
  articlesLoading.value = true
  projectError.value = ''
  articleError.value = ''

  try {
    const projectRes = await getFeaturedProjects()
    featuredProjects.value = (projectRes.data || []).slice(0, 3)
  } catch (err: any) {
    projectError.value = err?.response?.data?.message || err?.message || '精选项目加载失败'
  } finally {
    projectsLoading.value = false
  }

  try {
    const articleRes = await getArticles()
    latestArticles.value = (articleRes.data || []).slice(0, 3)
  } catch (err: any) {
    articleError.value = err?.response?.data?.message || err?.message || '学习文章加载失败'
  } finally {
    articlesLoading.value = false
  }
}

onMounted(fetchHomeData)
</script>

<style scoped>
.hero-terminal {
  position: relative;
  min-height: 360px;
  padding: 22px;
}

.hero-terminal__bar {
  display: grid;
  grid-template-columns: 10px 10px 10px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(153, 217, 255, 0.12);
}

.hero-terminal__bar span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--app-danger);
}

.hero-terminal__bar span:nth-child(2) {
  background: var(--app-amber);
}

.hero-terminal__bar span:nth-child(3) {
  background: var(--app-success);
}

.hero-terminal__bar strong {
  justify-self: end;
  color: var(--app-muted);
  font-family: var(--app-font-data);
  font-size: 12px;
}

.hero-terminal__body {
  display: grid;
  gap: 16px;
  padding-top: 28px;
  color: var(--app-soft);
  font-family: var(--app-font-data);
  font-size: 14px;
}

.hero-terminal__body p {
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.hero-terminal__body span {
  margin-right: 12px;
  color: var(--app-cyan);
}

.hero-terminal__orbit {
  position: absolute;
  right: 28px;
  bottom: 24px;
  width: 132px;
  height: 132px;
  border: 1px solid rgba(83, 231, 255, 0.28);
  border-radius: 999px;
  background:
    radial-gradient(circle, rgba(83, 231, 255, 0.34), transparent 44%),
    conic-gradient(from 90deg, transparent, rgba(83, 231, 255, 0.64), transparent);
  filter: drop-shadow(0 0 32px rgba(83, 231, 255, 0.28));
  animation: orb-float 7s ease-in-out infinite;
}

.section-actions {
  display: grid;
  justify-items: start;
  gap: 16px;
}

@media (max-width: 640px) {
  .hero-terminal {
    min-height: 300px;
  }

  .hero-terminal__body {
    font-size: 12px;
  }

  .hero-terminal__orbit {
    width: 96px;
    height: 96px;
  }
}
</style>
