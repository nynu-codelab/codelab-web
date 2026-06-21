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
        <div v-else class="app-grid three">
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
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import ProjectCard from '@/components/app/ProjectCard.vue'
import StateView from '@/components/app/StateView.vue'
import { getProjects, type ProjectItem } from '@/api/project'
import { fallbackText, parseList } from '@/utils/content'

const projects = ref<ProjectItem[]>([])
const loading = ref(true)
const error = ref('')

async function fetchProjects() {
  loading.value = true
  error.value = ''
  try {
    const res = await getProjects()
    projects.value = res.data || []
  } catch (err: any) {
    error.value = err?.response?.data?.message || err?.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(fetchProjects)
</script>
