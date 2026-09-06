import http from './http'

/** 用户域接口：个人中心（/api/user）+ 管理员员工管理（/api/admin/users） */
export interface Profile {
  id: number
  username: string
  nickname?: string
  avatar?: string
  phone?: string
  email?: string
  role: number
  status: number
}

export interface Address {
  id?: number
  userId?: number
  receiver: string
  phone: string
  province?: string
  city?: string
  district?: string
  detail: string
  isDefault: number
}

export interface EmployeeRow {
  id: number
  username: string
  nickname?: string
  phone?: string
  status: number
  createTime: string
}

export const getProfile = () => http.get<never, Profile>('/user/profile')
export const updateProfile = (patch: Partial<Profile>) => http.put('/user/profile', patch)
export const getAddresses = () => http.get<never, Address[]>('/user/addresses')
export const saveAddress = (addr: Address) =>
  addr.id ? http.put(`/user/addresses/${addr.id}`, addr) : http.post('/user/addresses', addr)
export const deleteAddress = (id: number) => http.delete(`/user/addresses/${id}`)

export const getEmployees = (page = 1, size = 10) =>
  http.get<never, { records: EmployeeRow[]; total: number; current: number; size: number }>('/admin/users', {
    params: { page, size },
  })
export const createEmployee = (data: { username: string; password: string; nickname?: string; phone?: string }) =>
  http.post('/admin/users', data)
export const updateEmployeeStatus = (id: number, status: number) =>
  http.put(`/admin/users/${id}/status`, null, { params: { status } })
