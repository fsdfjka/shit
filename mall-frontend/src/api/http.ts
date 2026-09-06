import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

/** Axios 实例：baseURL /api（vite 代理到网关 8090；也可由 VITE_API_BASE 指到网关直连） */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? '/api',
  timeout: 15000,
})

// 请求拦截器：携带 token
http.interceptors.request.use((config) => {
  const user = useUserStore()
  if (user.token) {
    config.headers.Authorization = `Bearer ${user.token}`
  }
  return config
})

// 响应拦截器：统一 Result{code,message,data}，401 跳登录
http.interceptors.response.use(
  (resp) => {
    const res = resp.data
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code === 200) return res.data
      ElMessage.error(res.message ?? '请求失败')
      return Promise.reject(new Error(res.message ?? '请求失败'))
    }
    return res
  },
  (err) => {
    const status = err.response?.status
    if (status === 401) {
      useUserStore().logout()
      router.push({ name: 'login' })
    }
    ElMessage.error(err.response?.data?.message ?? '网络异常，请稍后重试')
    return Promise.reject(err)
  },
)

export default http
