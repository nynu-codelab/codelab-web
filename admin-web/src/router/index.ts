import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/LoginView.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/",
    component: () => import("@/layout/AdminLayout.vue"),
    meta: { requiresAuth: true },
    children: [
      {
        path: "",
        redirect: "/dashboard",
      },
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/DashboardView.vue"),
        meta: { title: "数据概览", requiresAuth: true },
      },
      {
        path: "users",
        name: "Users",
        component: () => import("@/views/UsersView.vue"),
        meta: { title: "用户管理", requiresAuth: true },
      },
      {
        path: "recruit",
        name: "Recruit",
        component: () => import("@/views/RecruitView.vue"),
        meta: { title: "报名管理", requiresAuth: true },
      },
      {
        path: "articles",
        name: "Articles",
        component: () => import("@/views/articles/ArticlesView.vue"),
        meta: { title: "文章管理", requiresAuth: true },
      },
      {
        path: "projects",
        name: "Projects",
        component: () => import("@/views/projects/ProjectsView.vue"),
        meta: { title: "项目管理", requiresAuth: true },
      },
      {
        path: "members",
        name: "Members",
        component: () => import("@/views/MembersView.vue"),
        meta: { title: "成员管理", requiresAuth: true },
      },
      {
        path: "directions",
        name: "Directions",
        component: () => import("@/views/DirectionsView.vue"),
        meta: { title: "方向管理", requiresAuth: true },
      },
      {
        path: "site",
        name: "Site",
        component: () => import("@/views/SiteView.vue"),
        meta: { title: "站点配置", requiresAuth: true },
      },
      {
        path: "upload",
        name: "Upload",
        component: () => import("@/views/UploadView.vue"),
        meta: { title: "文件上传", requiresAuth: true },
      },
      {
        path: ":pathMatch(.*)*",
        name: "AdminNotFound",
        component: () => import("@/views/NotFoundView.vue"),
        meta: { title: "页面不存在", requiresAuth: true },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore();

  // 已登录但尚未拉取用户信息 → 先获取用户信息
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.fetchMe();
    } catch {
      // fetchMe 失败（如 token 已过期）→ 清理状态并跳转登录
      await userStore.logout();
      next({ name: "Login", query: { redirect: to.fullPath } });
      return;
    }
  }

  // 已登录且已有用户信息 → 周期性重验证 token 是否仍然有效
  if (userStore.token && userStore.userInfo && userStore.needsReverify()) {
    try {
      await userStore.fetchMe();
    } catch {
      await userStore.logout();
      next({ name: "Login", query: { redirect: to.fullPath } });
      return;
    }
  }

  // 需要认证但未登录 → 跳转登录
  if (to.meta.requiresAuth !== false && !userStore.token) {
    next({ name: "Login", query: { redirect: to.fullPath } });
    return;
  }

  // 已登录但非管理员 → 禁止访问（防御越权）
  if (
    to.meta.requiresAuth !== false &&
    userStore.userInfo &&
    userStore.userInfo.role !== "ADMIN"
  ) {
    // 登出并提示
    await userStore.logout();
    next({ name: "Login", query: { redirect: to.fullPath } });
    return;
  }

  // 已登录访问登录页 → 重定向到首页
  if (to.name === "Login" && userStore.token) {
    next({ path: "/dashboard" });
    return;
  }

  next();
});

export default router;
