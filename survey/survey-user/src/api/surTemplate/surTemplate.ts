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
}

export const getTemplateApi = (
  data: IGetTemplate
): Promise<IDataType<ITemplateResult>> => {
  return api.post(surTemplateApi.GetTemplate, data)
}
