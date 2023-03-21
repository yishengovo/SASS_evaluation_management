/*
 * @Author: August-Rushme
 * @Date: 2022-09-26 11:38:31
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-28 11:35:01
 * @FilePath: \survey-user\src\utils\util.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
// 解决遮罩层滚动穿透问题，分别在遮罩层弹出后和关闭前调用
let _scrollTop = 0
export function afterOpen() {
  // dialog 显示时调用
  if (document.scrollingElement?.scrollTop) {
    _scrollTop = document.scrollingElement?.scrollTop
  }

  document.body.style.position = 'fixed'
  document.body.style.top = -_scrollTop + 'px'
}
export function afterClose() {
  // dialog 关闭时调用
  document.body.style.position = ''
  document.body.style.top = ''
  // 使 scrollTop 恢复原状
  if (document.scrollingElement?.scrollTop) {
    document.scrollingElement.scrollTop = _scrollTop
  }
}

// 处理文件流并下载
export const handleFileDownload = (
  blobParts: BlobPart[],
  options: BlobPropertyBag,
  name: string
) => {
  const blob = new Blob(blobParts, options)
  const fileName = decodeURI(name)
  const downloadElement = document.createElement('a')
  const href = window.URL.createObjectURL(blob) //创建下载的链接
  downloadElement.href = href
  downloadElement.download = fileName //下载后文件名
  document.body.appendChild(downloadElement)
  downloadElement.click() //点击下载
  document.body.removeChild(downloadElement) //下载完成移除元素
  window.URL.revokeObjectURL(href)
}
