import http from './http'

/** 报表接口（/api/report/dashboard；商家传 merchantId 为本店口径） */
export interface DailyTrend {
  day: string
  amount: number
  cnt: number
}

export interface FinanceFlow {
  biz: 'order_in' | 'refund_out' | 'withdrawal_out'
  ref: string
  amount: number
  createTime: string
}

export interface Dashboard {
  revenue: number
  incoming: number
  refundOut: number
  withdrawalOut: number
  outgoing: number
  merchantCount: number
  withdrawalCount: number
  dailyTrend: DailyTrend[]
  merchantStats?: { approved: number; pending: number; rejected: number } | null
  financeFlow: FinanceFlow[]
}

export const getDashboard = (merchantId?: number | string) =>
  http.get<never, Dashboard>('/report/dashboard', { params: { merchantId } })
