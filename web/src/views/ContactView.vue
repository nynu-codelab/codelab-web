<template>
  <AppFrame>
    <PageHero
      eyebrow="Contact"
      title="联系我们"
      :description="heroDescription"
    >
      <template #actions>
        <AppButton label="招新报名" to="/recruit" />
        <AppButton label="查看我的报名" to="/my-application" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="contact.config"
          :commands="['readSiteConfig()', 'resolveContactInfo()', 'routeToRecruitForm()']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div v-if="loading" class="app-container">
        <p class="status-text">正在加载联系方式...</p>
      </div>

      <div v-else-if="error" class="app-container">
        <p class="status-text error">加载失败：{{ error }}</p>
      </div>

      <template v-else>
        <!-- Lab Address -->
        <div class="app-container">
          <article class="contact-card glass-card">
            <span class="app-eyebrow">Address</span>
            <h2>实验室地址</h2>
            <p v-if="contactAddress" class="contact-value">{{ contactAddress }}</p>
            <p v-else class="contact-empty">地址暂未配置</p>
          </article>
        </div>

        <!-- Contact Info Grid -->
        <div class="app-container app-grid three">
          <article class="contact-card glass-card">
            <span class="app-eyebrow">Email</span>
            <h2>电子邮箱</h2>
            <template v-if="contactEmail">
              <p class="contact-value">{{ contactEmail }}</p>
              <button class="copy-btn" @click="copyToClipboard(contactEmail, 'email')">
                <span class="copy-icon">{{ copiedTarget === 'email' ? '✓' : '⎘' }}</span>
                {{ copiedTarget === 'email' ? '已复制' : '复制邮箱' }}
              </button>
            </template>
            <p v-else class="contact-empty">邮箱暂未配置</p>
          </article>

          <article class="contact-card glass-card">
            <span class="app-eyebrow">Phone</span>
            <h2>联系电话</h2>
            <template v-if="contactPhone">
              <p class="contact-value">{{ contactPhone }}</p>
              <button class="copy-btn" @click="copyToClipboard(contactPhone, 'phone')">
                <span class="copy-icon">{{ copiedTarget === 'phone' ? '✓' : '⎘' }}</span>
                {{ copiedTarget === 'phone' ? '已复制' : '复制电话' }}
              </button>
            </template>
            <p v-else class="contact-empty">电话暂未配置</p>
          </article>

          <article class="contact-card glass-card">
            <span class="app-eyebrow">GitHub</span>
            <h2>代码仓库</h2>
            <template v-if="githubUrl">
              <p class="contact-value">
                <a :href="githubUrl" target="_blank" rel="noopener noreferrer" class="external-link">{{ githubUrl }}</a>
              </p>
            </template>
            <p v-else class="contact-empty">GitHub 暂未配置</p>
          </article>
        </div>

        <!-- QR Code & Join Method -->
        <div class="app-container app-grid two">
          <article class="contact-card glass-card">
            <span class="app-eyebrow">QR Code</span>
            <h2>招新二维码</h2>
            <div v-if="contactQrcodeUrl" class="qrcode-wrap">
              <img :src="contactQrcodeUrl" alt="招新二维码" class="qrcode-img" />
            </div>
            <p v-else class="contact-empty">招新二维码暂未配置</p>
          </article>

          <article class="contact-card glass-card">
            <span class="app-eyebrow">Join</span>
            <h2>加入方式</h2>
            <p>请通过注册、登录、报名流程提交信息，管理员审核后可在「我的报名」中查看状态。加入前可通过技术方向和项目成果了解团队建设内容。</p>
            <AppButton label="进入报名" to="/recruit" variant="secondary" />
          </article>
        </div>

        <!-- Project Cooperation -->
        <div class="app-container">
          <article class="contact-card glass-card">
            <span class="app-eyebrow">Cooperation</span>
            <h2>项目合作</h2>
            <p>如果你有项目合作意向、技术咨询或资源对接需求，欢迎通过上方邮箱或 GitHub 联系我们。我们关注工程能力的实践和知识沉淀，期待与志同道合的伙伴一起创造价值。</p>
          </article>
        </div>
      </template>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
import { getSiteConfigMap } from '@/api/siteConfig'

const loading = ref(true)
const error = ref('')

const contactEmail = ref('')
const contactPhone = ref('')
const contactAddress = ref('')
const contactQrcodeUrl = ref('')
const githubUrl = ref('')

const copiedTarget = ref<string | null>(null)

const hasAnyConfig = computed(() => {
  return contactEmail.value || contactPhone.value || contactAddress.value || contactQrcodeUrl.value || githubUrl.value
})

const heroDescription = computed(() => {
  if (loading.value) return '正在加载联系方式...'
  if (hasAnyConfig.value) return '欢迎通过以下方式联系实验室，期待与你交流。'
  return '当前站点未配置公开联系方式字段。为了避免伪造真实联系方式，本页提供可用的站内路径和后续配置说明。'
})

onMounted(async () => {
  try {
    const res = await getSiteConfigMap()
    if (res.code === 200 && res.data) {
      contactEmail.value = res.data.contactEmail || ''
      contactPhone.value = res.data.contactPhone || ''
      contactAddress.value = res.data.contactAddress || ''
      contactQrcodeUrl.value = res.data.contactQrcodeUrl || ''
      githubUrl.value = res.data.githubUrl || ''
    }
  } catch (e: any) {
    error.value = e?.message || '加载联系方式失败，请稍后重试'
    console.error('[ContactView] fetch site config error:', e)
  } finally {
    loading.value = false
  }
})

async function copyToClipboard(text: string, target: string) {
  try {
    await navigator.clipboard.writeText(text)
    copiedTarget.value = target
    setTimeout(() => {
      if (copiedTarget.value === target) {
        copiedTarget.value = null
      }
    }, 2000)
  } catch (e) {
    console.error('[ContactView] copy failed:', e)
  }
}
</script>

<style scoped>
.contact-card {
  display: grid;
  align-content: start;
  gap: 16px;
  min-height: 220px;
  padding: 28px;
}

.contact-card h2 {
  color: var(--app-text-strong);
  font-size: 24px;
}

.contact-card p {
  color: var(--app-muted);
  line-height: 1.8;
}

.contact-value {
  color: var(--app-text-strong) !important;
  font-family: var(--app-font-data);
  font-size: 15px;
  word-break: break-all;
}

.contact-empty {
  color: var(--app-muted) !important;
  font-style: italic;
  opacity: 0.7;
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

.copy-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  border: 1px solid rgba(83, 231, 255, 0.28);
  border-radius: 8px;
  background: rgba(83, 231, 255, 0.06);
  color: var(--app-cyan);
  font-family: var(--app-font-data);
  font-size: 13px;
  cursor: pointer;
  transition: all 220ms ease;
  align-self: start;
}

.copy-btn:hover {
  background: rgba(83, 231, 255, 0.14);
  border-color: rgba(83, 231, 255, 0.48);
}

.copy-icon {
  font-size: 14px;
}

.qrcode-wrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 8px 0;
}

.qrcode-img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 12px;
  border: 1px solid rgba(83, 231, 255, 0.18);
  background: rgba(4, 16, 23, 0.6);
  object-fit: contain;
}

.external-link {
  color: var(--app-cyan);
  text-decoration: none;
  transition: color 180ms ease;
}

.external-link:hover {
  color: var(--app-teal);
  text-decoration: underline;
}

.app-grid.two {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

@media (max-width: 768px) {
  .app-grid.two {
    grid-template-columns: 1fr;
  }
}
</style>
