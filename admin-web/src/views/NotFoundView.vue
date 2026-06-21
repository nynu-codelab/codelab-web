<template>
  <div class="admin-page">
    <section class="admin-empty-panel">
      <span class="admin-page-kicker">404</span>
      <h3>后台页面不存在</h3>
      <p>请从左侧菜单进入数据概览、报名管理、文章管理或项目管理。</p>
      <router-link to="/dashboard">返回数据概览</router-link>
    </section>
  </div>
</template>

<style scoped>
a {
  color: var(--admin-cyan);
  font-weight: 720;
}
</style>
