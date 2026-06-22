<template>
  <AppFrame>
    <PageHero
      eyebrow="Contact"
      title="联系我们"
      description="当前站点未配置公开联系方式字段。为了避免伪造真实联系方式，本页提供可用的站内路径和后续配置说明。"
    >
      <template #actions>
        <AppButton label="招新报名" to="/recruit" />
        <AppButton label="查看我的报名" to="/my-application" variant="secondary" />
      </template>
      <template #visual>
        <CommandConsole
          title="contact.config"
          :commands="['readSiteConfig()', 'waitForRealContactInfo()', 'routeToRecruitForm()']"
        />
      </template>
    </PageHero>

    <AnimatedSection>
      <div class="app-container app-grid three">
        <article class="contact-card glass-card">
          <span class="app-eyebrow">Recruit</span>
          <h2>招新咨询</h2>
          <p>请先通过注册、登录、报名流程提交信息，管理员审核后可在我的报名中查看状态。</p>
          <AppButton label="进入报名" to="/recruit" variant="secondary" />
        </article>
        <article class="contact-card glass-card">
          <span class="app-eyebrow">Content</span>
          <h2>了解团队</h2>
          <p>可通过实验室介绍、技术方向、项目成果和学习文章了解当前建设方向。</p>
          <AppButton label="查看介绍" to="/about" variant="secondary" />
        </article>
        <article class="contact-card glass-card">
          <span class="app-eyebrow">Config</span>
          <h2>联系方式配置</h2>
          <p>后续站点配置模块接入后，可在后台维护真实联系方式、二维码和公开信息。</p>
          <AppButton label="查看后台入口" href="/admin/" variant="ghost" />
        </article>
      </div>
    </AnimatedSection>
  </AppFrame>
</template>

<script setup lang="ts">
import AppFrame from '@/components/app/AppFrame.vue'
import PageHero from '@/components/app/PageHero.vue'
import AppButton from '@/components/app/AppButton.vue'
import AnimatedSection from '@/components/app/AnimatedSection.vue'
import CommandConsole from '@/components/app/CommandConsole.vue'
</script>

<style scoped>
.contact-card {
  display: grid;
  align-content: start;
  gap: 16px;
  min-height: 290px;
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
</style>
