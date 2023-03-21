/*
 * @Author: August-Rushme
 * @Date: 2022-11-26 15:08:23
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-28 16:57:07
 * @FilePath: \survey-user\src\api\excel\exportExcel.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */

import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'

const api = useApi()
enum ExcelApi {
  ExportExcel = '/client/userProject/exportAnswerSheet',
}

export const exportExcel = (data: {
  projectId: string
  surveyId: string
  userId?: string
  evaluatedId?: string
}): Promise<IDataType> => {
  return api.post(ExcelApi.ExportExcel, { ...data }, { responseType: 'blob' })
}
