/*
 * @Author: August-Rushme
 * @Date: 2022-09-30 15:26:45
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-25 12:07:43
 * @FilePath: \survey-user\src\components\base\au-notice\Notice.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import { createVNode, render } from 'vue'
import AuNotice from './AuNotice.vue'
interface IOptions {
  notice_type?: 'success' | 'error' | 'warning' | 'default'
  message: string
  duration?: number
}
let mountNode: any = null
export const Notice = (options: IOptions) => {
  let duration = 2000

  if (options.duration) {
    duration = options.duration
  }

  // 确保只有一个loading
  if (mountNode) {
    document.body.removeChild(mountNode)
    mountNode = null
  }
  const app = createVNode(AuNotice, { ...options, loading: true, duration })

  //创建定时器，duration时间后将mountNode移除
  if (options.notice_type !== 'default' && options.notice_type !== undefined) {
    const timer = setTimeout(() => {
      document.body.removeChild(mountNode)
      mountNode = null
      clearTimeout(timer)
    }, duration)
  }

  mountNode = document.createElement('div')

  render(app, mountNode)
  document.body.append(mountNode)
}
