import axios from 'axios'
import JSONbig from 'json-bigint'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

/** 大整数安全 JSON 解析：雪花 ID（19 位）超过 JS MAX_SAFE_INTEGER，
 *  默认 JSON.parse 会丢精度（如 2096571827303944193 -> ...4200），
 *  导致按 id 请求(审核/提现/购物车等)报"不存在"。storeAsString 将超长整数保留为字符串。 */
const jsonBig = JSONbig({ storeAsString: true })

/** Axios 实例：baseURL /api（vite 代理到网关 8090；也可由 VITE_API_BASE 指到网关直连） */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? '/api',
  timeout: 15000,
  transformResponse: [
    (data: unknown) => {
      if (typeof data !== 'string' || !data) return data
      try {
        return jsonBig.parse(data)
      } catch {
        return data
      }
    },
  ],
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
