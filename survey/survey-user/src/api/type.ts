/*
 * @Author: August-Rushme
 * @Date: 2022-09-26 15:12:02
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-28 10:25:14
 * @FilePath: \survey-user\src\api\type.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
// 统一接收的数据类型
interface data<T> {
  code: number
  message: string
  result: T
  success: boolean
  timestamp: number
}
export interface IDataType<T = any> {
  status: number
  data: data<T>
  headers: any
}
