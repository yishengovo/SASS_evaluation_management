/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-06 18:52:07
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-22 11:17:22
 * @Description: file content
 */
import { Notice } from '/@src/components/base/au-notice/Notice'
export const keRequest = async (
  fn: any,
  message: string,
  errorMessage = '',
  needSuccess = true
) => {
  Notice({
    message: `${message}中,请稍等`,
    notice_type: 'default',
  })
  let allSuccess = true
  try {
    const resArray: any[] = await fn()
    if (!!resArray) {
      resArray.forEach((item) => {
        if (item.data.code !== 200 && item.data.code !== 0) {
          Notice({
            message:
              errorMessage === '' ? `接口路径${item.config.url}请求失败` : errorMessage,
            notice_type: 'error',
          })
          allSuccess = false
        }
      })
    }
    if (allSuccess && needSuccess) {
      Notice({
        message: `${message}成功`,
        notice_type: 'success',
      })
    }
    return resArray
  } catch (error) {
    return Notice({
      message: (error as Error).message,
      notice_type: 'error',
    })
  }
}
