<template>
  <AppFrame>
    <PageHero
      eyebrow="Project Gallery"
      title="项目成果"
      description="展示实验室已发布的项目成果与工程化实践记录。这里不伪造上线数据，只呈现后台发布的真实内容。"
    >
      <template #actions>
        <AppButton label="立即报名" to="/recruit" />
        <AppButton label="阅读文章" to="/articles" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="project.showcase"
          :commands="['fetchPublishedProjects()', 'scanTechStack()', 'openArchitectureNotes()']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container">
        <StateView
          v-if="loading"
          title="正在加载项目"
          message="正在读取已发布的项目成果。"
        />
        <StateView
          v-else-if="error"
          title="项目加载失败"
          :message="error"
        >
          <template #actions>
            <AppButton label="重新加载" variant="secondary" @click="fetchProjects" />
          </template>
        </StateView>
        <StateView
          v-else-if="projects.length === 0"
          title="暂无项目成果"
          message="后台发布项目后，这里会展示项目封面、技术栈、摘要和详情入口。"
        />
        <template v-else>
          <div class="app-grid three">
            <ProjectCard
              v-for="item in projects"
              :key="item.id"
              :title="item.title"
              :summary="fallbackText(item.summary, '项目成果简介待完善')"
              :type="item.projectType"
              :to="`/projects/${item.id}`"
              :cover-url="item.coverUrl"
              :tags="parseList(item.techStack).slice(0, 5)"
              :meta="item.leaderName ? `负责人：${item.leaderName}` : undefined"
            />
          </div>

          <div class="list-pagination">
            <p class="list-pagination__info">
              第 {{ page }} 页，共 {{ totalPages }} 页（{{ total }} 个项目）
            </p>
            <div class="list-pagination__actions">
              <AppButton
                label="上一页"
                variant="secondary"
                :disabled="page <= 1"
                @click="goToPage(page - 1)"
              />
              <AppButton
                label="下一页"
                variant="secondary"
                :disabled="page >= totalPages"
                @click="goToPage(page + 1)"
              />
            </div>
          </div>
        </template>
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import ProjectCard from '@/components/app/ProjectCard.vue'
import StateView from '@/components/app/StateView.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { getProjects, type ProjectItem } from '@/api/project'
import { fallbackText, parseList } from '@/utils/content'

const projects = ref<ProjectItem[]>([])
const loading = ref(true)
const error = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

function goToPage(p: number) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchProjects()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function fetchProjects() {
  loading.value = true
  error.value = ''
  try {
    const res = await getProjects({ page: page.value, pageSize: pageSize.value })
    const data = res.data
    // Support both PageResult and legacy array response
    if (data && 'records' in data) {
      projects.value = data.records
      total.value = data.total
    } else {
      projects.value = (data as unknown as ProjectItem[]) || []
      total.value = projects.value.length
    }
  } catch (err: any) {
    error.value = err?.response?.data?.message || err?.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(fetchProjects)
</script>

<style scoped>
.list-pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(153, 217, 255, 0.08);
}

.list-pagination__info {
  color: rgba(216, 247, 255, 0.58);
  font-size: 13px;
  margin: 0;
}

.list-pagination__actions {
  display: flex;
  gap: 10px;
}
</style>
