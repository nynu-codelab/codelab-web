import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { login as loginApi, getMe } from "@/api/auth";

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  role: string;
}

export const useUserStore = defineStore("user", () => {
  const token = ref<string>(localStorage.getItem("token") || "");
  const userInfo = ref<UserInfo | null>(null);

  const isLoggedIn = computed(() => !!token.value);

  async function login(username: string, password: string) {
    const res = await loginApi({ username, password });
    token.value = res.data.token;
    localStorage.setItem("token", res.data.token);
    userInfo.value = res.data.user;
  }

  async function fetchMe() {
    const res = await getMe();
    userInfo.value = res.data;
  }

  function logout() {
    token.value = "";
    userInfo.value = null;
    localStorage.removeItem("token");
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    fetchMe,
    logout,
  };
});
