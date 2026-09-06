import http from './http'

/** 认证接口（对应 mall-auth-service，经网关 /api/auth/**） */
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  type: number
  id: number
  username: string
  nickname?: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname?: string
  phone?: string
}

export interface MerchantApplyRequest {
  username: string
  password: string
  merchantName: string
  phone: string
  contact?: string
  shopName?: string
}

export const login = (data: LoginRequest) => http.post<never, LoginResponse>('/auth/login', data)

export const register = (data: RegisterRequest) => http.post('/auth/register', data)

export const merchantApply = (data: MerchantApplyRequest) =>
  http.post('/auth/register/merchant', data)
