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

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore();

  if (to.meta.requiresAuth !== false && !userStore.token) {
    next({ name: "Login", query: { redirect: to.fullPath } });
  } else if (to.name === "Login" && userStore.token) {
    next({ path: "/dashboard" });
  } else {
    next();
  }
});

export default router;
