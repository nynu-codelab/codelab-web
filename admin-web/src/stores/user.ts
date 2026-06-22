import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { login as loginApi, getMe, logout as logoutApi } from "@/api/auth";

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  role: string;
}

export const useUserStore = defineStore("user", () => {
  const token = ref<string>(localStorage.getItem("token") || "");
  const userInfo = ref<UserInfo | null>(null);
  /** 最近一次成功验证 token 的时间戳（用于后台路由守卫的周期性重验证） */
  const lastVerifiedAt = ref<number>(0);

  const isLoggedIn = computed(() => !!token.value);

  /** Token 重验证间隔（毫秒），超过此时间将重新调用 fetchMe 确认 token 有效性 */
  const REVERIFY_INTERVAL_MS = 5 * 60 * 1000; // 5 分钟

  function needsReverify(): boolean {
    return Date.now() - lastVerifiedAt.value > REVERIFY_INTERVAL_MS;
  }

  async function login(username: string, password: string) {
    const res = await loginApi({ username, password });
    token.value = res.data.token;
    localStorage.setItem("token", res.data.token);
    userInfo.value = res.data.user;
    lastVerifiedAt.value = Date.now();
  }

  async function fetchMe() {
    const res = await getMe();
    userInfo.value = res.data;
    lastVerifiedAt.value = Date.now();
  }

  async function logout() {
    try {
      await logoutApi();
    } catch {
      // 即使后端调用失败，前端也清理本地状态
    } finally {
      token.value = "";
      userInfo.value = null;
      lastVerifiedAt.value = 0;
      localStorage.removeItem("token");
    }
  }

  return {
    token,
    userInfo,
    lastVerifiedAt,
    isLoggedIn,
    needsReverify,
    login,
    fetchMe,
    logout,
  };
});
