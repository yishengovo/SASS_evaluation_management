/*
 * @Author: August Rush
 * @Date: 2023-01-12 20:38:43
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-12 20:53:39
 * @FilePath: \survey-user\src\api\survey\report.ts
 * @Description:
 *
 * Copyright (c) 2023 by August Rush 864011713@qq.com, All Rights Reserved.
 */
import { IDataType } from '../type'

import { useApi } from '/@src/composable/useApi'

const api = useApi()
enum ReportApi {
  GeneratorReport = '/client/userProject/generateReport',
}
/**
 * @description:  生成测评报告
 * @return {*}
 */
export const generatorReport = (
  projectId: string,
  userId: string
): Promise<IDataType<any>> => {
  return api.post(ReportApi.GeneratorReport, { projectId, userId })
}
