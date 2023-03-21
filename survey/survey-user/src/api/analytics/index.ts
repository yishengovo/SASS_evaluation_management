/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-09 20:17:14
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 12:52:32
 * @FilePath: \survey-user\src\api\analytics\index.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import { IAnalyticsResult, SurveyResultById, IGetCollect } from './type'

const api = useApi()

enum analyticsApi {
  GetAnalytics = '/client/userProject/result/',
  GetCAnalylist = '/client/userProject/getSurveyData',
  GetSurAnalytics = '/client/userProject/getSurveyDataById',
  GetCollect = '/client/userProject/getAnalysis',
}

export const getAnalytics = (id: string): Promise<IDataType<IAnalyticsResult>> => {
  return api.get(analyticsApi.GetAnalytics + id)
}

export const getCAnalylist = (id: string): Promise<IDataType<any>> => {
  return api.get(analyticsApi.GetCAnalylist + '/' + id)
}

export const getSurAnalytics = (data: SurveyResultById): Promise<IDataType<any>> => {
  return api.post(analyticsApi.GetSurAnalytics, data)
}

export const getCollect = (data: IGetCollect): Promise<IDataType<any>> => {
  return api.post(analyticsApi.GetCollect, data)
}
