<template>
  <AppFrame>
    <!-- Announcement banner -->
    <div v-if="announcement" class="home-announcement">
      <div class="app-container">
        <p class="home-announcement__text">{{ announcement }}</p>
      </div>
    </div>

    <TerminalHero />

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
      <div class="app-container home-os">
        <div>
          <span class="app-eyebrow">Lab Operating System</span>
          <h2 class="app-title-lg">{{ siteSlogan || '把学习过程组织成可运行的工程系统' }}</h2>
          <p class="app-copy">
            {{ siteDescription || '从需求拆解到部署验证，训练链路围绕真实项目展开。每一次提交、评审和复盘都沉淀为可复用的工程经验。' }}
          </p>
          <div class="home-os__chips" aria-label="实验室工作模式">
            <span>task.ready</span>
            <span>commit.review</span>
            <span>deploy.verify</span>
          </div>
        </div>
        <CommandConsole
          title="recruit.flow"
          :commands="[
            'register()',
            'login()',
            'submitApplication()',
            'adminReview()',
            'joinCodeLab()'
          ]"
        />
        <GitBranchMap />
      </div>
    </AnimatedSection>

    <AnimatedSection>
      <div class="app-container">
        <div class="app-section-header">
          <div>
            <span class="app-eyebrow">Departments & Tracks</span>
            <h2 class="app-title-lg">部门与方向</h2>
          </div>
          <p class="app-copy">
            软件研发部设全栈开发、产品测试、运维三组，成果中心承接成果转化与对外合作，
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
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import MetricCard from '@/components/app/MetricCard.vue'
import DirectionCard from '@/components/app/DirectionCard.vue'
import ProjectCard from '@/components/app/ProjectCard.vue'
import ArticleCard from '@/components/app/ArticleCard.vue'
import MemberCard from '@/components/app/MemberCard.vue'
import RecruitCTA from '@/components/app/RecruitCTA.vue'
import StateView from '@/components/app/StateView.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import GitBranchMap from '@/components/app/GitBranchMap.vue'
import TerminalHero from '@/components/app/TerminalHero.vue'
import { getArticles, type ArticleItem } from '@/api/article'
import { getFeaturedProjects, type ProjectItem } from '@/api/project'
import { getSiteConfigMap } from '@/api/siteConfig'
import { fallbackText, parseList } from '@/utils/content'

const metrics = [
  { value: '3', label: '研发小组', caption: '软件研发部：全栈开发、产品测试、运维' },
  { value: '多个', label: '真实项目', caption: '以项目成果和工程训练驱动成长' },
  { value: '持续', label: '学习分享', caption: '通过文章和复盘沉淀实践经验' },
  { value: '开放', label: '招新通道', caption: '注册登录后可提交报名并查看审核状态' }
]

const directions = [
  {
    index: '01',
    title: '全栈开发',
    badge: '软件研发部',
    description: '负责前端与后端完整功能开发，覆盖 Java / Spring Boot、Vue / React、微信小程序与 AI / Agent 应用，训练从接口设计到功能交付的完整工程能力。',
    tags: ['Java 17', 'Spring Boot', 'Vue 3', 'AI 应用']
  },
  {
    index: '02',
    title: '产品测试',
    badge: '软件研发部',
    description: '负责需求拆解、原型设计、测试用例、提测验收与回归测试，以质量视角为项目交付把关。',
    tags: ['需求分析', '测试用例', '回归测试', '质量报告']
  },
  {
    index: '03',
    title: '运维与部署',
    badge: '软件研发部',
    description: '负责部署发布、CI/CD 流水线、服务器与监控、上线执行与回滚，补齐工程交付链路。',
    tags: ['Docker', 'Nginx', 'CI/CD', '监控']
  },
  {
    index: '04',
    title: '成果中心',
    badge: '成果中心',
    description: '统筹论文、专利、软件著作权、竞赛与企业合作，负责成果登记、归档与对外申报。',
    tags: ['论文', '专利', '软著', '竞赛']
  }
]

const memberRoles = [
  {
    initials: 'FS',
    role: '软件研发部',
    title: '全栈开发组',
    summary: '负责前后端完整功能开发与 AI 应用，从接口设计到功能交付，支撑项目从想法到可部署。',
    tags: ['Java', 'Vue', 'Agent']
  },
  {
    initials: 'QA',
    role: '软件研发部',
    title: '产品测试组',
    summary: '负责需求拆解、测试用例与质量报告，以质量视角保障项目交付。',
    tags: ['需求', '用例', '质量']
  },
  {
    initials: 'OPS',
    role: '软件研发部',
    title: '运维组',
    summary: '负责部署、CI/CD、监控与上线执行，保障项目稳定交付与回滚。',
    tags: ['Docker', 'CI/CD', '监控']
  }
]

// Site config from backend
const siteName = ref('')
const siteSlogan = ref('')
const siteDescription = ref('')
const announcement = ref('')
const githubUrl = ref('')

async function fetchSiteConfig() {
  try {
    const res = await getSiteConfigMap()
    const map = res.data || {}
    siteName.value = map.siteName || ''
    siteSlogan.value = map.siteSlogan || ''
    siteDescription.value = map.siteDescription || ''
    announcement.value = map.announcement || ''
    githubUrl.value = map.githubUrl || ''
  } catch {
    // Graceful fallback: config not available, keep hardcoded defaults
  }
}

// Article and project data
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
    const articleRes = await getArticles({ page: 1, pageSize: 3 })
    const data = articleRes.data
    // Support both PageResult and legacy array response
    if (data && 'records' in data) {
      latestArticles.value = data.records
    } else {
      latestArticles.value = (data as unknown as ArticleItem[]) || []
    }
  } catch (err: any) {
    articleError.value = err?.response?.data?.message || err?.message || '学习文章加载失败'
  } finally {
    articlesLoading.value = false
  }
}

onMounted(() => {
  fetchSiteConfig()
  fetchHomeData()
})
</script>

<style scoped>
.home-announcement {
  padding: 10px 0;
  background: linear-gradient(90deg, rgba(83, 231, 255, 0.08) 0%, rgba(83, 231, 255, 0.04) 50%, rgba(83, 231, 255, 0.08) 100%);
  border-bottom: 1px solid rgba(83, 231, 255, 0.12);
}

.home-announcement__text {
  text-align: center;
  color: rgba(216, 247, 255, 0.78);
  font-size: 13px;
  margin: 0;
  padding: 0 16px;
}

.section-actions {
  display: grid;
  justify-items: start;
  gap: 16px;
}

.home-os {
  display: grid;
  grid-template-columns: minmax(0, 0.72fr) minmax(300px, 0.62fr) minmax(320px, 0.8fr);
  gap: 18px;
  align-items: stretch;
}

.home-os > div:first-child {
  padding: 24px 0;
}

.home-os .app-copy {
  margin-top: 16px;
}

.home-os__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 9px;
  margin-top: 20px;
}

.home-os__chips span {
  padding: 7px 10px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 999px;
  color: rgba(216, 247, 255, 0.82);
  background: rgba(83, 231, 255, 0.06);
  box-shadow: inset 0 0 18px rgba(83, 231, 255, 0.035);
  font-family: var(--app-font-data);
  font-size: 12px;
}

@media (max-width: 1120px) {
  .home-os {
    grid-template-columns: 1fr;
  }
}
</style>
