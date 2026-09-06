import http from './http'

/** 订单接口：用户（/api/order）、商家（/api/merchant/orders）、管理员（/api/admin/orders） */
export interface OrderItem {
  skuId: number
  productId: number
  title: string
  specJson: string
  price: number
  count: number
  amount: number
}

export interface OrderVO {
  id: number
  orderNo: string
  merchantId: number
  totalAmount: number
  payAmount: number
  freak?: number
  status: number
  payTime?: string
  cancelTime?: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  logisticsCompany?: string
  trackingNo?: string
  sendTime?: string
  receiveTime?: string
  createTime: string
  items: OrderItem[]
}

interface Page<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface CreateOrderPayload {
  reqId: string
  items: { skuId: number; count: number }[]
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  fromCart?: boolean
}

export const STATUS_TEXT: Record<number, string> = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已收货',
  4: '已取消',
  5: '退款中',
  6: '已退款',
}

// 用户侧
export const createOrder = (payload: CreateOrderPayload) =>
  http.post<never, string[]>('/order/create', payload)
export const getMyOrders = (status?: number, page = 1, size = 10) =>
  http.get<never, Page<OrderVO>>('/order/list', { params: { status, page, size } })
export const cancelOrder = (orderNo: string) => http.put(`/order/${orderNo}/cancel`)
export const receiveOrder = (orderNo: string) => http.put(`/order/${orderNo}/receive`)

// 商家侧
export const getMerchantOrders = (status?: number, page = 1, size = 10) =>
  http.get<never, Page<OrderVO>>('/merchant/orders', { params: { status, page, size } })
export const shipOrder = (orderNo: string, logisticsCompany: string, trackingNo: string) =>
  http.put(`/merchant/orders/${orderNo}/ship`, { logisticsCompany, trackingNo })

// 管理员侧
export const getAdminOrders = (status?: number, page = 1, size = 10) =>
  http.get<never, Page<OrderVO>>('/admin/orders', { params: { status, page, size } })
export const adminShipOrder = (orderNo: string, logisticsCompany: string, trackingNo: string) =>
  http.put(`/admin/orders/${orderNo}/ship`, { logisticsCompany, trackingNo })
