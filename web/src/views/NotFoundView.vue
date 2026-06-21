<template>
  <AppFrame>
    <main class="not-found app-container narrow">
      <StateView
        title="页面不存在"
        message="你访问的路径暂时没有对应页面。可以返回首页，或继续查看项目成果和学习文章。"
      >
        <template #actions>
          <AppButton label="返回首页" to="/" />
          <AppButton label="查看项目" to="/projects" variant="secondary" />
        </template>
      </StateView>
    </main>
  </AppFrame>
</template>

<script setup lang="ts">
import AppFrame from '@/components/app/AppFrame.vue'
import AppButton from '@/components/app/AppButton.vue'
import StateView from '@/components/app/StateView.vue'
</script>

<style scoped>
.not-found {
  padding: 92px 0;
}
</style>
