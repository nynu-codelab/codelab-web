<template>
  <AppFrame>
    <PageHero
      eyebrow="Technical Tracks"
      title="技术方向"
      :description="heroDescription"
    >
      <template #actions>
        <AppButton label="加入方向学习" to="/recruit" />
        <AppButton label="查看项目成果" to="/projects" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="tracks.matrix"
          :commands="consoleCommands"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div v-if="loading" class="app-container">
        <p class="status-text">正在装载方向矩阵...</p>
      </div>

      <div v-else-if="error" class="app-container">
        <p class="status-text error">装载失败：{{ error }}</p>
      </div>

      <div v-else-if="directions.length === 0" class="app-container">
        <p class="status-text">技术方向数据暂未配置，请关注后续更新。</p>
      </div>

      <div v-else class="app-container app-grid three">
        <DirectionCard
          v-for="(item, idx) in directions"
          :key="item.id"
          :index="String(idx + 1).padStart(2, '0')"
          :title="item.name"
          :description="item.summary"
          :badge="item.status === 1 ? '核心方向' : '拓展方向'"
          :tags="parseTags(item.tags)"
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
import DirectionCard from '@/components/app/DirectionCard.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { getDirections, type DirectionItem } from '@/api/direction'

const directions = ref<DirectionItem[]>([])
const loading = ref(true)
const error = ref('')

const heroDescription = computed(() => {
  if (loading.value) return '正在加载方向数据...'
  if (directions.value.length === 0) return '技术方向数据暂未配置，请关注后续更新。'
  return '以核心方向建立软件工程基础，以拓展方向打开技术视野。每个方向都服务于真实项目训练。'
})

const consoleCommands = computed(() => {
  if (directions.value.length === 0) return ['awaitingDirections()']
  return directions.value.map((d) => `selectTrack(${d.code || d.name})`)
})

function parseTags(tags: string): string[] {
  if (!tags) return []
  return tags.split(',').map((t) => t.trim()).filter(Boolean)
}

onMounted(async () => {
  try {
    const res = await getDirections()
    if (res.code === 0 && Array.isArray(res.data)) {
      directions.value = res.data.sort((a, b) => a.sortOrder - b.sortOrder)
    }
  } catch (e: any) {
    error.value = e?.message || '加载方向数据失败，请稍后重试'
    console.error('[DirectionsView] fetch directions error:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
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
