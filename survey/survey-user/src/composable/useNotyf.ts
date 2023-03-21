/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-10-03 13:25:24
 * @FilePath: \survey-user\src\composable\useNotyf.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */

// notyf is injected by the plugin in /src/plugins/notyf.ts
export const useNotyf = () => {
  return inject<any>('notyf')
}
