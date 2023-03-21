/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-25 18:28:41
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-24 15:00:24
 * @Description: file content
 */
import { Notice } from '/@src/components/base/au-notice/Notice'
export const checkPhone = (phone: string): boolean => {
  const reg = /^(?:(?:\+|00)86)?1[3-9]\d{9}$/
  if (!reg.test(phone)) {
    Notice({
      notice_type: 'error',
      message: '请输入正确的电话号码',
    })
    return false
  }
  return true
}
export const checkEmail = (email: string): boolean => {
  const reg = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
  if (!reg.test(email)) {
    Notice({
      notice_type: 'error',
      message: '请输入正确的邮箱',
    })
    return false
  }
  return true
}
export const checkCode = (code: string): boolean => {
  if (code.length !== 6) {
    Notice({
      notice_type: 'error',
      message: '验证码应为6位,请输入正确的验证码',
    })
    return false
  }
  return true
}
export const checkPwd = (pwd: string, checkPwd: string): boolean => {
  if (pwd !== checkPwd) {
    Notice({
      notice_type: 'error',
      message: '密码不一致',
    })
    return false
  }
  if (pwd.length < 8) {
    Notice({
      notice_type: 'error',
      message: '密码至少为8位',
    })
    return false
  }
  return true
}
