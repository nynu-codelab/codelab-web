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
