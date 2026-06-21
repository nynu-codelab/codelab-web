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
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
