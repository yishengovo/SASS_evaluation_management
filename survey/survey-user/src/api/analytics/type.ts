/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-09 20:17:19
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 13:30:56
 * @FilePath: \survey-user\src\api\analytics\type.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */

export interface SurveyResult {
  answer: string
}
export interface SurveyList {
  id: string
  surName: string
  type: string
  surContent: string
  createTime: string
  jsonPreview: string
  createBy?: any
  createDate?: any
  updateBy?: any
  updateDate: string
  tenantId: string
  status?: any
  result?: any
  score?: any
  surveyResults: SurveyResult[]
}

export interface IAnalyticsResult {
  type: string
  surveyJson: string
  lastEditTime: string
  collectNum: number
  name: string
  surveyResults: SurveyResult[]
  surveyList: SurveyList[] | undefined
}

export interface SurveyResultById {
  projectId: string
  surveyId: string
}

export interface IGetCollect {
  id: string
  pageNum: number
  pageSize: number
}
