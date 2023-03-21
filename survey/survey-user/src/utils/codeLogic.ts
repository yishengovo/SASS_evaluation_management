/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-22 10:58:47
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-22 11:13:15
 * @Description: file content
 */
import { Notice } from '/@src/components/base/au-notice/Notice'
export const codeLogic = (res: any) => {
  if (res.data.code === 0 || res.data.code === 200) {
    if (res.data.success) {
      Notice({
        message: '验证码发送成功',
        notice_type: 'success',
      })
    } else {
      Notice({
        message: `${res.data.message},请勿重复发送`,
        notice_type: 'warning',
      })
    }
  } else {
    Notice({
      message: res.data.message,
      notice_type: 'error',
    })
  }
}
