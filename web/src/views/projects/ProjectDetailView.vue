<template>
  <AppFrame>
    <main class="detail-page app-container narrow">
      <StateView
        v-if="loading"
        title="正在加载项目"
        message="正在读取项目详情。"
      />
      <StateView
        v-else-if="notFound"
        title="项目不存在或未发布"
        message="当前项目可能已下架，或链接参数不正确。"
      >
        <template #actions>
          <AppButton label="返回项目列表" to="/projects" />
        </template>
      </StateView>
      <StateView
        v-else-if="error"
        title="项目加载失败"
        :message="error"
      >
        <template #actions>
          <AppButton label="重新加载" variant="secondary" @click="fetchProject" />
        </template>
      </StateView>

      <article v-else-if="project" class="detail-card glass-card">
        <div class="detail-card__meta">
          <span class="status-pill">{{ project.projectType || '项目实践' }}</span>
          <span>{{ formatDate(project.publishedAt || project.createTime) }}</span>
          <span>{{ project.viewCount }} 次浏览</span>
        </div>
        <h1>{{ project.title }}</h1>
        <p v-if="project.summary" class="detail-card__summary">{{ project.summary }}</p>

        <div class="project-facts">
          <div v-if="project.leaderName">
            <span>负责人</span>
            <strong>{{ project.leaderName }}</strong>
          </div>
          <div v-if="project.membersText">
            <span>参与成员</span>
            <strong>{{ project.membersText }}</strong>
          </div>
          <div v-if="project.techStack">
            <span>技术栈</span>
            <strong>{{ project.techStack }}</strong>
          </div>
        </div>

        <div class="pill-row" v-if="parseList(project.techStack).length">
          <span class="tech-pill" v-for="tag in parseList(project.techStack)" :key="tag">{{ tag }}</span>
        </div>

        <div class="project-links" v-if="project.repoUrl || project.demoUrl || project.documentUrl">
          <AppButton v-if="project.repoUrl" label="代码仓库" :href="project.repoUrl" target="_blank" variant="secondary" />
          <AppButton v-if="project.demoUrl" label="在线演示" :href="project.demoUrl" target="_blank" />
          <AppButton v-if="project.documentUrl" label="项目文档" :href="project.documentUrl" target="_blank" variant="ghost" />
        </div>

        <CommandConsole
          class="detail-console"
          title="project.runtime"
          :framed="false"
          :commands="['inspectProjectMetadata()', 'renderMarkdown(html=false)', 'preserveApiContract()']"
        />

        <div class="detail-card__divider"></div>
        <div class="app-markdown" v-html="renderedMarkdown"></div>
        <div class="detail-card__footer">
          <AppButton label="返回项目列表" to="/projects" variant="secondary" />
        </div>
      </article>
    </main>
  </AppFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import AppFrame from '@/components/app/AppFrame.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { getProject, type ProjectItem } from '@/api/project'
import { formatDate, parseList } from '@/utils/content'

const route = useRoute()
const project = ref<ProjectItem | null>(null)
const loading = ref(true)
const error = ref('')
const notFound = ref(false)

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

const renderedMarkdown = computed(() => {
  if (!project.value) return ''
  return md.render(project.value.descriptionMarkdown || '')
})

async function fetchProject() {
  const id = Number(route.params.id)
  if (!id) {
    notFound.value = true
    loading.value = false
    return
  }

  loading.value = true
  error.value = ''
  notFound.value = false
  try {
    const res = await getProject(id)
    project.value = res.data
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || ''
    if (msg.includes('不存在') || msg.includes('未发布')) {
      notFound.value = true
    } else {
      error.value = msg || '加载失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchProject)
</script>

<style scoped>
.detail-page {
  padding: 72px 0 92px;
}

.detail-card {
  padding: clamp(26px, 5vw, 52px);
}

.detail-card__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 22px;
  color: var(--app-muted);
  font-size: 13px;
}

.detail-card h1 {
  color: var(--app-text-strong);
  font-size: 58px;
  line-height: 1.12;
}

.detail-card__summary {
  margin-top: 18px;
  color: var(--app-soft);
  font-size: 17px;
  line-height: 1.8;
}

.project-facts {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 28px;
}

.project-facts div {
  padding: 16px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--app-radius-sm);
  background: rgba(255, 255, 255, 0.045);
}

.project-facts span {
  display: block;
  margin-bottom: 8px;
  color: var(--app-muted);
  font-size: 12px;
}

.project-facts strong {
  color: var(--app-text-strong);
  font-size: 14px;
  font-weight: 680;
}

.detail-card .pill-row,
.project-links {
  margin-top: 22px;
}

.project-links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-console {
  margin-top: 24px;
}

.detail-card__divider {
  height: 1px;
  margin: 34px 0;
  background: linear-gradient(90deg, transparent, rgba(153, 217, 255, 0.26), transparent);
}

.detail-card__footer {
  margin-top: 40px;
  padding-top: 26px;
  border-top: 1px solid rgba(153, 217, 255, 0.12);
}

@media (max-width: 760px) {
  .detail-card h1 {
    font-size: 34px;
  }

  .project-facts {
    grid-template-columns: 1fr;
  }
}
</style>
