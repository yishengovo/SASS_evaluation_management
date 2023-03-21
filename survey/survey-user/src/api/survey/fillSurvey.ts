/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-08 16:27:31
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-12 13:13:43
 * @FilePath: \survey-user\src\api\survey\fillSurvey.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { IDataType } from '../type'
import { ISaveAnswer, IUserList } from './type'
import { useApi } from '/@src/composable/useApi'

const api = useApi()

enum fillSurveyApi {
  SaveAnswer = '/client/userProject/saveAnswer',
  GetUsers = '/client/userProject/getUsers',
}

export const saveAnswer = (data: ISaveAnswer): Promise<IDataType<any>> => {
  return api.post(fillSurveyApi.SaveAnswer, data)
}

export const getUsers = (data: {
  id: string
  pageNum: number
  pageSize: number
}): Promise<IDataType<IUserList>> => {
  return api.post(fillSurveyApi.GetUsers, data)
}
