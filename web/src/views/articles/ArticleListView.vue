<template>
  <AppFrame>
    <PageHero
      eyebrow="Learning Articles"
      title="学习文章"
      description="技术分享、学习笔记与项目复盘在这里沉淀。每篇文章都面向真实开发经验，不做空洞展示。"
    >
      <template #actions>
        <AppButton label="返回首页" to="/" variant="secondary" />
        <AppButton label="招新报名" to="/recruit" />
      </template>
      <template #visual>
        <CommandConsole
          title="knowledge.base"
          :commands="['indexMarkdownNotes()', 'renderCodeBlocks(html=false)', 'publishLearningLog()']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container">
        <StateView
          v-if="loading"
          title="正在加载文章"
          message="正在读取已发布的学习文章。"
        />
        <StateView
          v-else-if="error"
          title="文章加载失败"
          :message="error"
        >
          <template #actions>
            <AppButton label="重新加载" variant="secondary" @click="fetchArticles" />
          </template>
        </StateView>
        <StateView
          v-else-if="articles.length === 0"
          title="暂无文章"
          message="后台发布文章后，这里会展示文章标题、标签、摘要和阅读入口。"
        />
        <template v-else>
          <div class="app-grid three">
            <ArticleCard
              v-for="item in articles"
              :key="item.id"
              :title="item.title"
              :summary="fallbackText(item.summary, '文章摘要待完善')"
              :category="item.category"
              :published-at="item.publishedAt || item.createTime"
              :tags="parseList(item.tags).slice(0, 5)"
              :to="`/articles/${item.id}`"
            />
          </div>

          <div class="list-pagination">
            <p class="list-pagination__info">
              第 {{ page }} 页，共 {{ totalPages }} 页（{{ total }} 篇）
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
import ArticleCard from '@/components/app/ArticleCard.vue'
import StateView from '@/components/app/StateView.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { getArticles, type ArticleItem } from '@/api/article'
import { fallbackText, parseList } from '@/utils/content'

const articles = ref<ArticleItem[]>([])
const loading = ref(true)
const error = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

function goToPage(p: number) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function fetchArticles() {
  loading.value = true
  error.value = ''
  try {
    const res = await getArticles({ page: page.value, pageSize: pageSize.value })
    const data = res.data
    // Support both PageResult and legacy array response
    if (data && 'records' in data) {
      articles.value = data.records
      total.value = data.total
    } else {
      articles.value = (data as unknown as ArticleItem[]) || []
      total.value = articles.value.length
    }
  } catch (err: any) {
    error.value = err?.response?.data?.message || err?.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(fetchArticles)
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
