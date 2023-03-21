export const debounce = (fn: any, time: number) => {
  // 利用闭包避免全局污染
  let timeout: any = null
  return function (this: any, ...args: any[]) {
    if (timeout) {
      // 在定时期间，那么清除原来计时器 重新计时(核心)
      clearTimeout(timeout)
    }
    // 设置定时器
    timeout = setTimeout(() => {
      // 执行函数
      // eslint-disable-next-line prefer-rest-params
      fn.apply(this, ...args)
    }, time)
  }
}
