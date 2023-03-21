/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-21 16:09:11
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-21 16:12:35
 * @Description: file content
 */
export const getTimeArray = (maxNumber: number) => {
  const arr = []
  for (let i = 0; i < maxNumber; i++) {
    if (i <= 10) {
      arr.push((i + '').padStart(2, '0'))
    } else {
      arr.push(i + '')
    }
  }
  return arr
}
