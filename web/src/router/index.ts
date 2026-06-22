import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/design-preview',
      name: 'designPreview',
      component: () => import('@/views/design/DesignPreviewView.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue')
    },
    {
      path: '/directions',
      name: 'directions',
      component: () => import('@/views/DirectionsView.vue')
    },
    {
      path: '/members',
      name: 'members',
      component: () => import('@/views/MembersView.vue')
    },
    {
      path: '/contact',
      name: 'contact',
      component: () => import('@/views/ContactView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('@/views/UserCenterView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserCenterView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/recruit',
      name: 'recruit',
      component: () => import('@/views/RecruitView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-application',
      name: 'myApplication',
      component: () => import('@/views/MyApplicationView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('@/views/articles/ArticleListView.vue')
    },
    {
      path: '/articles/:id',
      name: 'articleDetail',
      component: () => import('@/views/articles/ArticleDetailView.vue')
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('@/views/projects/ProjectListView.vue')
    },
    {
      path: '/projects/:id',
      name: 'projectDetail',
      component: () => import('@/views/projects/ProjectDetailView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: () => import('@/views/NotFoundView.vue')
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  // 已登录但尚未拉取用户信息 → 先验证 token 有效性
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.fetchMe()
    } catch {
      // token 无效或过期 → 清理状态并跳转登录
      userStore.logout()
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }
  }

  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
