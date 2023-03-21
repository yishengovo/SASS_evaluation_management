/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-07 21:21:42
 * @FilePath: \survey-user\src\composable\useApi.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import axios, { AxiosInstance } from 'axios'
import { Notice } from '/@src/components/base/au-notice/Notice'
const baseURL = import.meta.env.VITE_WEB_BASE_URL
const port = import.meta.env.VITE_API_BASE_PORT
let api: AxiosInstance
import { getCookie, clearCookieByKey } from '/@src/utils/cookie'
// import { initDomainApi } from '/@src/api/auth'
export function createApi() {
  // Here we set the base URL for all requests made to the api
  api = axios.create({
    baseURL: 'http://' + location.hostname + ':' + port + '/jeecg-boot',
  })
  // We set an interceptor for each request to
  // include Bearer token to the request if user is logged in

  api.interceptors.request.use((config) => {
    // initDomainApi()
    config.headers = {
      ...config.headers,
      'X-Access-Token': getCookie('hrtools_token'),
      'tenant-id': getCookie('hrtools_tanantId'),
    }

    return config
  })
  api.interceptors.response.use(
    (data) => {
      if (data.data.code === 413 || data.data.code === 411) {
        location.href = baseURL + '/auth/login'
        Notice({
          notice_type: 'error',
          message: data.data.message,
        })
      } else if (data.data.code === 412) {
        location.href = 'http://' + location.host + '/auth/login2'
        Notice({
          notice_type: 'error',
          message: data.data.message,
        })
      }
      return data
    },
    (error: any) => {
      if (error.response.status === 401) {
        location.href = baseURL + '/auth/login'
        Notice({
          notice_type: 'error',
          message: '请求被服务器拒绝,请重新登录后重试',
        })
      }
      clearCookieByKey('hrtools_token')
      clearCookieByKey('hrtools_tanantId')
    }
  )

  return api
}

export function useApi() {
  if (!api) {
    createApi()
  }
  return api
}
