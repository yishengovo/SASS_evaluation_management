/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:53
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-09-26 12:22:06
 * @FilePath: \survey\survey-user\types\window.d.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
declare interface Window {
  timer: number
  // dataLayer is injected via vite-plugin-radar
  dataLayer?: any[]
}
