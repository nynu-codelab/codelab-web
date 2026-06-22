import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMe, logout as logoutApi } from '@/api/auth'
import type { UserInfo, LoginParams, RegisterParams } from '@/api/auth'

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
    setToken(res.token)
    setUserInfo(res.user)
    return res
  }

  async function register(data: RegisterParams) {
    await registerApi(data)
  }

  async function fetchMe() {
    const user = await getMe()
    setUserInfo(user)
    return user
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
