import http from './http'

/** 支付接口（/api/pay；支付单创建、Mock 回调、状态查询、退款）+ 管理员退款记录 */
export interface RefundRecord {
  id: number
  refundNo: string
  payNo: string
  orderNo: string
  amount: number
  reason?: string
  status: number
  handleTime?: string
  createTime: string
}

export const createPay = (orderNo: string) => http.post<never, string>('/pay/create', { orderNo })

/** 演示用：模拟渠道回调（真实沙箱由支付宝异步通知） */
export const mockPayCallback = (payNo: string, tradeNo: string, amount: number) =>
  http.post('/pay/mock/callback', { payNo, tradeNo, amount })

export const getPayStatus = (orderNo: string) => http.get<never, boolean>(`/pay/status/${orderNo}`)

export const requestRefund = (orderNo: string, reason: string) =>
  http.post<never, string>('/pay/refund', { orderNo, reason })

export const getRefunds = (status?: number, page = 1, size = 10) =>
  http.get<never, { records: RefundRecord[]; total: number; current: number; size: number }>('/admin/refunds', {
    params: { status, page, size },
  })
