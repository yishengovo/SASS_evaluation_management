/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-08 16:27:31
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 16:48:26
 * @FilePath: \survey-user\src\api\survey\fillSurvey.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { IDataType } from '../type'
import { IGetTemplate, ITemplateResult } from './type'
import { useApi } from '/@src/composable/useApi'

const api = useApi()

enum surTemplateApi {
  GetTemplate = '/client/userProject/getSurveyTemplate',
  GetMyTemplate = '/client/userProject/getExclusiveSurveyTemplate',
  BuyTemplate = '/client/userProject/purchaseByPoint',
  EditTemplate = '/client/userProject/editMyTemplateById',
  PushTemplate = '/client/userProject/pushMyTemplate'
}

export const getTemplateApi = (
  data: IGetTemplate
): Promise<IDataType<ITemplateResult>> => {
  if(data.type === '我的') {
    return api.post(surTemplateApi.GetMyTemplate, data)
  } else {
    return api.post(surTemplateApi.GetTemplate, data)
  }
}

export const buyTemplateApi = (data: {}) => {
  return api.post(surTemplateApi.BuyTemplate, data)
}

export const editTemplateApi = (surveyId: string) => {
  return api.post(surTemplateApi.EditTemplate, surveyId)
}

export const PushTemplateApi = (surveyId: string) => {
  return api.post(surTemplateApi.PushTemplate, surveyId)
}

