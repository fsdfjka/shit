import http from './http'

/** 购物车接口（/api/portal/carts，登录用户） */
export interface CartItem {
  id: number
  userId: number
  skuId: number
  productId: number
  count: number
  checked: number
  specJson?: string
  price?: number
  stock?: number
  skuStatus?: number
  productTitle?: string
  mainImg?: string
}

export const getCart = () => http.get<never, CartItem[]>('/portal/carts')
export const addToCart = (skuId: number, count: number) =>
  http.post('/portal/carts', { skuId, count })
export const mergeCart = (items: { skuId: number; count: number }[]) =>
  http.post('/portal/carts/merge', { items })
export const updateCartCount = (skuId: number, count: number) =>
  http.put(`/portal/carts/${skuId}/count`, null, { params: { count } })
export const updateCartChecked = (skuId: number, checked: number) =>
  http.put(`/portal/carts/${skuId}/checked`, null, { params: { checked } })
export const removeCart = (skuId: number) => http.delete(`/portal/carts/${skuId}`)
