/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-23 11:35:00
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-30 17:29:37
 * @Description: file content
 */
export interface IEditControl {
  projectId: string
  timeEnable: boolean
  startTime: string | null
  endTime: string | null
  dayStartTime: string | null
  dayEndTime: string | null
  passwordEnable: boolean
  password: string
  phoneCaptchaEnable: boolean
}
