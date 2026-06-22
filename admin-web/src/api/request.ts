import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";
import { useUserStore } from "@/stores/user";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 15000,
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    const res = response.data;

    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message || "请求失败"));
    }

    return res;
  },
  async (error) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        // 同时清理 localStorage 和 Pinia store，避免路由守卫状态不一致导致死循环
        const userStore = useUserStore();
        await userStore.logout();
        router.push({ name: "Login" });
        ElMessage.error("登录已过期，请重新登录");
      } else if (data && data.message) {
        ElMessage.error(data.message);
      }
    } else {
      ElMessage.error("网络错误，请稍后重试");
    }

    return Promise.reject(error);
  }
);

export default request;
