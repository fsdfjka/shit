import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 登录态：token 与身份（type: 0 用户 / 1 平台管理员 / 2 商家 => JWT 与网关鉴权一致） */
export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('mall_token') ?? '')
  const type = ref<number>(Number(localStorage.getItem('mall_user_type') ?? -1))
  const username = ref<string>(localStorage.getItem('mall_username') ?? '')
  const nickname = ref<string>(localStorage.getItem('mall_nickname') ?? '')
  /** id 可能为雪花 ID（19 位），以字符串保存避免 JS 精度丢失 */
  const id = ref<string>(localStorage.getItem('mall_user_id') ?? '')

  function setLogin(data: { token: string; type: number; id: number | string; username: string; nickname?: string }) {
    token.value = data.token
    type.value = data.type
    username.value = data.username
    nickname.value = data.nickname ?? data.username
    id.value = String(data.id)
    localStorage.setItem('mall_token', data.token)
    localStorage.setItem('mall_user_type', String(data.type))
    localStorage.setItem('mall_username', data.username)
    localStorage.setItem('mall_nickname', data.nickname ?? data.username)
    localStorage.setItem('mall_user_id', String(data.id))
  }

  function logout() {
    token.value = ''
    type.value = -1
    username.value = ''
    nickname.value = ''
    id.value = ''
    localStorage.removeItem('mall_token')
    localStorage.removeItem('mall_user_type')
    localStorage.removeItem('mall_username')
    localStorage.removeItem('mall_nickname')
    localStorage.removeItem('mall_user_id')
  }

  return { token, type, username, nickname, id, setLogin, logout }
})
