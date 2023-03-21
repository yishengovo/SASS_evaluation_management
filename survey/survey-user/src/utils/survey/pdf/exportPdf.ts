/*
 * @Author: August-Rushme
 * @Date: 2022-11-23 10:37:35
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-30 18:37:47
 * @FilePath: \survey-user\src\utils\survey\pdf\exportPdf.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */

import * as SurveyPDF from 'survey-pdf'

import { Notice } from '/@src/components/base/au-notice/Notice'
import { useApi } from '/@src/composable/useApi'
const api = useApi()

api.get(import.meta.env.VITE_API_BASE_OSS_FONT).then((res) => {
  const myFont = res.data
  SurveyPDF.DocController.addFont('myRoboto', myFont, 'normal')
  SurveyPDF.DocController.addFont('myRoboto', myFont, 'bold')
  SurveyPDF.DocController.addFont('myRoboto', myFont, 'italic')
  SurveyPDF.DocController.addFont('myRoboto', myFont, 'bolditalic')
})

const exportToPdfOptions = {
  haveCommercialLicense: true,
  fontSize: 12,
  fontName: 'myRoboto',
}
/**
 * @description: 保存为PDF
 * @param {any} surveyData 问卷的答案
 * @param {string} surveyJson 问卷的json
 * @param {string} fileName 保存的文件名字
 * @param {any} exportOptions 导出的选项配置
 * @return {*}
 */
export const savePdf = async (
  surveyData: any,
  surveyJson: any,
  fileName: string,
  exportOptions: any = exportToPdfOptions
) => {
  const surveyPdf = new SurveyPDF.SurveyPDF(surveyJson, exportOptions)
  // surveyPdf.mode = 'display'
  surveyPdf.data = surveyData
  await surveyPdf.save(fileName).then((res: any) => {
    console.log(res)

    Notice({ notice_type: 'success', message: '下载成功' })
  })
}
