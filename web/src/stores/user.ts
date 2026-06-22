import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMe, logout as logoutApi } from '@/api/auth'
import type { UserInfo, LoginParams, RegisterParams } from '@/api/auth'

/**
 * 用户状态管理。
 *
 * ## 安全注意事项
 *
 * Token 当前存储在 localStorage 中，同源任意脚本可读取——若发生 XSS 或供应链攻击，
 * Token 可能被窃取。缓解措施：
 * 1. Nginx 配置 CSP 头限制脚本来源（见 deploy/nginx/nginx.conf）
 * 2. 全站启用 HTTPS（见 deploy/nginx/nginx.conf）
 * 3. TODO: 迁移到 httpOnly Secure SameSite Cookie 方案（需前后端联合改造：
 *    后端 set-cookie httpOnly，前端移除手动 Token 管理，需同步添加 CSRF 保护）
 */
export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(
    (() => {
      const raw = localStorage.getItem('userInfo')
      return raw ? JSON.parse(raw) : null
    })()
  )

  const isLoggedIn = computed(() => !!token.value)

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUserInfo(info: UserInfo | null) {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  async function login(data: LoginParams) {
    const res = await loginApi(data)
    // 后端统一返回 Result<T> 包裹：{ code, message, data: { token, user } }
    // Axios 响应拦截器返回 response.data（即完整 Result 对象），需从 .data 中解包
    setToken(res.data.token)
    setUserInfo(res.data.user)
    return res
  }

  async function register(data: RegisterParams) {
    await registerApi(data)
  }

  async function fetchMe() {
    const res = await getMe()
    // 后端统一返回 Result<T> 包裹，需从 .data 中解包 UserInfo
    setUserInfo(res.data)
    return res.data
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 即使后端调用失败，前端也清理本地状态
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    fetchMe,
    logout
  }
})
