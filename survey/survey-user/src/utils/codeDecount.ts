/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-23 18:33:20
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-10-23 18:59:44
 * @Description: file content
 */

interface refValue {
  value: any
}
export const codeDecount = (
  disable: refValue,
  disableTxt: refValue,
  timeOutNumber = 60
) => {
  let interval = null as any
  let time = timeOutNumber
  disable.value = true
  interval = setInterval(() => {
    time--
    disableTxt.value = `${time}秒后可重新获取`
    if (time <= 0) {
      time = timeOutNumber
      disable.value = false
      disableTxt.value = `发送验证码`
      clearInterval(interval)
    }
  }, 1000)
}
