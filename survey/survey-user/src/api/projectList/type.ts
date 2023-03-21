/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-09-28 16:39:39
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-10-16 21:10:03
 * @Description: file content
 */
// 参数类型
import type { WizardData, WizardType } from '/@src/models/wizard'
export interface ProjectList {
  id: string
  pageNum: number
  pageSize: number
  type?: WizardData
  oder?: boolean
  isPublish?: boolean
  name?: string
}
export interface PublishProject {
  id: string
  isPublish: boolean
}

export interface ChangeProjectName {
  id: string
  name: string
  content: string
}

export interface GetCollection {
  id: string
  pageSize: number
  pageNum: number
}

export interface CopyProject {
  id: string
  projectName: string
  tenantId: string
}
// 返回数据类型
export interface PaperListRecord {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime: string
  sysOrgCode?: any
  projectName: string
  content: string
  leader?: any
  type: WizardType
  isPublish: boolean
  evaluationType?: any
  surveyCurrentId: string
  surveySrcId?: any
  selectNumber: number
  collectNumber: number
  rowKeys?: any
  userRowKeys?: any
  tenantId: string
}

export interface PaperListData {
  records: PaperListRecord[]
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

export type DeleteData = string

export type PublishData = string

export interface User {
  name?: any
  gender?: any
  age?: any
  phone?: any
  result: string
  score: number
}

export interface Users {
  total: number
  size: number
  user: User[]
}

export interface CollectionData {
  id: string
  surveyId: string
  selectNumber: number
  collectNumber: number
  rate: string
  userPage: Users
}
