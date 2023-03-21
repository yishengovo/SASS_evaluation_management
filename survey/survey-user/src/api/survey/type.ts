/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-08 16:28:22
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-03 19:00:57
 * @FilePath: \survey-user\src\api\survey\type.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { SurveyList } from '../createProject/type'
export interface ISaveAnswer {
  answer: string
  projectId: string
  result: Record<string, string>
  user?: User
  evaluator?: string
  type: string
  status: number
  surveyId?: string
}

// 360项目相关
export interface IPersonList {
  id: string
  names: string[]
}

export interface IEvaluationPerson {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  projectId: string
  gender?: any
  name?: string
  age?: any
  phone?: any
  type: number
  tenantId?: any
}

export interface IEvaluation {
  id: string
  createTime: string
  updateTime: string
  userId: string
  userName: string
  type: number
  surveyId: string
  projectId: string
  evaluatedList: EvaluatedList[]
  superiorList?: any
  colleagueList?: any
  subordinateList?: any
}

export interface EvaluatedList {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  projectId: string
  userId: string
  userName: string
  evaluatorId: string
  evaluatorName: string
  evaluatorLevel?: any
  tenantId?: any
}

export interface ISetEvaluationData {
  id?: string
  projectId: string
  evaluator: string
  superior: string[]
  colleague: string[]
  subordinate: string[]
}

export interface ISetWeight {
  id: string
  type: number
  self: string
  superior: string
  colleague: string
  subordinate: string
}

export interface evaluateInfo {
  user: User
  evaluator: Evaluator[]
  surveyList: SurveyList[]
}

export interface Evaluator {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  projectId: string
  userId: string
  userName: string
  evaluatorId: string
  evaluatorName: string
  evaluatorLevel: number
  tenantId: string
}

export interface User {
  id: string
  createBy?: any
  createTime: string
  updateBy?: any
  updateTime?: any
  sysOrgCode?: any
  name: string
  gender?: any
  age?: any
  phone: string
  projectId: string
  type: number
  tenantUid?: any
  tenantId: string
  isGenerate: number
  status: number
}

export interface IGetPhoneCode {
  projectId: string
  phone: string
}

export interface IphoneLogin {
  projectId: string
  password?: string
  phone?: string
  code?: string
}

export interface IUserList {
  records: User[]
  total: number
  size: number
}
