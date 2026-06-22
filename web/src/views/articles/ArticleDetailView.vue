<template>
  <AppFrame>
    <main class="detail-page app-container narrow">
      <StateView
        v-if="loading"
        title="正在加载文章"
        message="正在读取文章详情。"
      />
      <StateView
        v-else-if="notFound"
        title="文章不存在或未发布"
        message="当前文章可能已下架，或链接参数不正确。"
      >
        <template #actions>
          <AppButton label="返回文章列表" to="/articles" />
        </template>
      </StateView>
      <StateView
        v-else-if="error"
        title="文章加载失败"
        :message="error"
      >
        <template #actions>
          <AppButton label="重新加载" variant="secondary" @click="fetchArticle" />
        </template>
      </StateView>

      <article v-else-if="article" class="detail-card glass-card">
        <div class="detail-card__meta">
          <span class="status-pill">{{ article.category || '技术文章' }}</span>
          <span>{{ formatDate(article.publishedAt || article.createTime) }}</span>
          <span>{{ article.viewCount }} 次阅读</span>
        </div>
        <h1>{{ article.title }}</h1>
        <p v-if="article.summary" class="detail-card__summary">{{ article.summary }}</p>
        <div class="pill-row" v-if="parseList(article.tags).length">
          <span class="tech-pill" v-for="tag in parseList(article.tags)" :key="tag">{{ tag }}</span>
        </div>
        <CommandConsole
          class="detail-console"
          title="markdown.reader"
          :framed="false"
          :commands="['loadPublishedArticle()', 'markdownIt({ html: false })', 'renderKnowledgeBase()']"
        />
        <div class="detail-card__divider"></div>
        <div class="app-markdown" v-html="renderedMarkdown"></div>
        <div class="detail-card__footer">
          <AppButton label="返回文章列表" to="/articles" variant="secondary" />
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
import { getArticle, type ArticleItem } from '@/api/article'
import { formatDate, parseList } from '@/utils/content'

const route = useRoute()
const article = ref<ArticleItem | null>(null)
const loading = ref(true)
const error = ref('')
const notFound = ref(false)

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

const renderedMarkdown = computed(() => {
  if (!article.value) return ''
  return md.render(article.value.contentMarkdown || '')
})

async function fetchArticle() {
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
    const res = await getArticle(id)
    article.value = res.data
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

onMounted(fetchArticle)
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

.detail-card .pill-row {
  margin-top: 22px;
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

@media (max-width: 720px) {
  .detail-card h1 {
    font-size: 34px;
  }
}
</style>
