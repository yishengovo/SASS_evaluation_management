/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-29 20:23:05
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-10-30 13:16:27
 * @Description: file content
 */
/**
 * [setCookie 设置 cookie]
 * @author	 Leo
 * @param    {[type]}                 name   [变量名，必选]
 * @param    {[type]}                 value  [变量值，必选]
 * @param    {[type]}                 exdays [过期日期，可选]
 */
export function setCookie(name: string, value: string, exdays: number) {
  let cookie = name + '=' + value + ';'
  if (exdays) {
    const d = new Date()
    d.setTime(d.getTime() + exdays * 24 * 60 * 60 * 1000)
    const expires = 'expires=' + d.toUTCString()
    cookie = cookie + expires + ';path=/' + ';domain=.stalent.net'
  }
  document.cookie = cookie
}

/**
 * [getCookie 获取 cookie]
 * @author	 Leo
 * @param    {[type]}                 cname [变量名，必选]
 * @return   {[type]}                 [返回值：变量值（字符串）]
 */
export function getCookie(cname: string) {
  const name = cname + '='
  const cookie = document.cookie.split(';')
  for (let i = 0, len = cookie.length; i < len; i++) {
    const c = cookie[i].trim()
    if (c.indexOf(name) === 0) {
      return c.substring(name.length, c.length)
    }
  }
  return ''
}

/**
 * [getCookie 获取 cookie]
 * @author	 Leo
 * @param    {[type]}                 key[变量名，必选]
 */
export function clearCookieByKey(key: string) {
  setCookie(key, '', -1)
}
