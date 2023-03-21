/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-15 15:06:18
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-15 15:18:47
 * @Description: file content
 */

export interface recycleParams {
  pageNum: number
  pageSize: number
}
export interface Record {
  id: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  sysOrgCode?: any
  fid?: any
  projectName: string
  content: string
  leader?: any
  isPublish: boolean
  selectNumber: number
  evaluationType?: any
  collectNumber: number
  type: string
  surveyCurrentId: string
  surveySrcId?: any
  rowKeys?: any
  userRowKeys: string
  orgUid?: any
  tenantUid?: any
  surveyId?: any
  tenantId: string
  isDel: boolean
}

export interface recycleData {
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
