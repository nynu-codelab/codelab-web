import axios, { type AxiosError } from 'axios'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error: AxiosError<{ message?: string }>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const currentRoute = router.currentRoute.value
      // 仅在当前不是登录页时跳转，避免死循环；使用 router.push 而非 window.location.href
      if (currentRoute.name !== 'login') {
        router.push({ name: 'login', query: { redirect: currentRoute.fullPath } })
      }
    }

    const message =
      error.response?.data?.message || error.message || '请求失败，请稍后重试'
    // console.error 在 production build 中被 esbuild drop 移除（见 vite.config.ts）
    console.error('[API Error]', message)

    return Promise.reject(error)
  }
)

export default request
