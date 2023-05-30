/*
 * @Author: August-Rushme
 * @Date: 2022-09-28 11:49:34
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-12-03 19:51:25
 * @FilePath: \survey-user\src\api\createProject\type.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */

import type { LocationQueryValue } from 'vue-router'
import { WizardType } from '/@src/models/wizard'

interface Questions {
  name: string
  names: string[]
  choices: string[]
  type?: string
}

export interface AddPorject {
  name: string
  content?: string
  type: WizardType
  jsonPreview: string
  id?: string | LocationQueryValue[] | null
  question: Questions[]
}

export interface ProjectInfo {
  project: Project
  survey: Survey
  surveyList: Survey[]
  url: string
}

export interface TemplateInfo {
  name: string
  content?: string
  type: WizardType
  id: string | LocationQueryValue[] | null
  jsonPreview: string | undefined
}

export interface Survey {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  type?: any
  name: string
  content: string
  jsonPreview: string
  srcId?: any
  reportLink: string
  tenantId: string
}

export interface QuestionResult {
  records: Question[]
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

export interface Question {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  surveyUid: string
  content: string
  typeId: string
  dimensionId?: any
  required?: any
  tenantId?: any
}

export interface IChoices {
  id: string
  surveyUid: string
  questionId: string
  choice: string
  basicScore: string
}

export interface ISaveScore {
  id: string
  projectId: string
  score: string[]
}

export interface IEvaluationProject {
  id?: string
  name: string
  content?: string
  type: string
  survey: string[]
}

// export interface IEvaluationProjectResult {
//   id: string
//   createBy: string
//   createTime: string
//   updateBy: string
//   updateTime: string
//   sysOrgCode?: any
//   fid?: any
//   projectName: string
//   content: string
//   leader?: any
//   isPublish: boolean
//   selectNumber: number
//   evaluationType?: any
//   collectNumber: number
//   type: WizardType
//   surveyCurrentId?: any
//   surveySrcId?: any
//   rowKeys?: any
//   userRowKeys?: any
//   orgUid?: any
//   tenantUid?: any
//   surveyId?: any
//   tenantId: string
//   isDel: boolean
// }
export interface ISurveyPagenation {
  pageNum: number
  pageSize: number
  type: string
}

export interface ISurveyTemplateResult {
  records: SurveyTemplate[]
  total: number
  size: number
  current: number
  orders: any[]
  optimizeCountSql: boolean
  searchCount: boolean
  countId: any
  maxLimit: any
  pages: number
}
export interface SurveyTemplate {
  id: string
  fid: string
  surName: string
  type: string
  surContent: string
  isShare?: any
  filterModel?: any
  createTime: string
  completed?: any
  surStatus?: any
  collectionAmount?: any
  amounted?: any
  targetUser?: any
  qualityControl?: any
  empUid?: any
  userUid?: any
  surResult?: any
  jsonContent?: any
  jsonPreview: string
  jsonPublish?: any
  publishTime?: any
  surStartDate?: any
  surEndDate?: any
  isUnlocked?: any
  isOnline?: any
  voteType?: any
  orgUid?: any
  tenantUid?: any
  enableDate?: any
  disableDate?: any
  dr?: any
  ts?: any
  createBy: string
  createName?: any
  createDate?: any
  updateBy?: any
  updateName?: any
  updateDate?: any
  tenantId: string
  status?: number
}

export interface ISurvey {
  id: string
  fid: string
  surName: string
  type: string
  surContent?: any
  isShare?: any
  filterModel?: any
  createTime: string
  completed?: any
  surStatus?: any
  collectionAmount?: any
  amounted?: any
  targetUser?: any
  qualityControl?: any
  empUid?: any
  userUid?: any
  surResult?: any
  jsonContent?: any
  jsonPreview?: any
  jsonPublish?: any
  publishTime?: any
  surStartDate?: any
  surEndDate?: any
  isUnlocked?: any
  isOnline?: any
  voteType?: any
  orgUid?: any
  tenantUid?: any
  enableDate?: any
  disableDate?: any
  dr?: any
  ts?: any
  createBy?: any
  createName?: any
  createDate?: any
  updateBy?: any
  updateName?: any
  updateDate?: any
  tenantId: string
  status?: number
  num?: number
}

export interface IEvaluationProjectResult {
  project: Project
  surveyList: SurveyList[]
}

export interface SurveyList {
  id: string
  fid: string
  surName: string
  type: string
  surContent?: any
  isShare?: any
  filterModel?: any
  createTime: string
  completed?: any
  surStatus?: any
  collectionAmount?: any
  amounted?: any
  targetUser?: any
  qualityControl?: any
  empUid?: any
  userUid?: any
  surResult?: any
  jsonContent?: any
  jsonPreview?: any
  jsonPublish?: any
  publishTime?: any
  surStartDate?: any
  surEndDate?: any
  isUnlocked?: any
  isOnline?: any
  voteType?: any
  orgUid?: any
  tenantUid?: any
  enableDate?: any
  disableDate?: any
  dr?: any
  ts?: any
  createBy?: any
  createName?: any
  createDate?: any
  updateBy?: any
  updateName?: any
  updateDate?: any
  tenantId: string
  status: number
  num?: number
}

interface Project {
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
  isAddUser?: boolean
  collectNumber: number
  type: WizardType
  surveyCurrentId?: any
  surveySrcId?: any
  rowKeys?: any
  userRowKeys?: any
  orgUid?: any
  tenantUid?: any
  surveyId?: any
  tenantId: string
  isDel: boolean
}

export interface IEvaluationPersonResult {
  id: string
  createBy: string
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode: string
  name: string
  gender?: any
  age?: any
  phone: string
  projectId: string
  type: number
  tenantUid?: any
  tenantId: string
  status: number
  isGenerate: number
}

export interface IQueryProjectResult {
  user: IEvaluationPersonResult
  surveyList: SurveyList[]
  evaluator: any
}
