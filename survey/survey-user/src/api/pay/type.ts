/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-30 15:08:38
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-30 18:51:59
 * @Description: file content
 */
export interface IGetHistoryOrder {
  pageSize: number
  pageNo: number
}
export interface Record {
  id: string
  productDescription: string
  amount: number
  succeeded: boolean
  userId: string
  outTradeNo: string
  integral: number
  createTime: string
}

export interface IGetHistoryOrderRusult {
  records: Record[]
  total: number
  size: number
  current: number
  orders: any[]
  optimizeCountSql: boolean
  searchCount: boolean
  countId?: any
  maxLimit?: any
  pages: number
}
