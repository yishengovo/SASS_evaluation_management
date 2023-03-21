/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-11 16:02:37
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 13:26:58
 * @FilePath: \survey-user\src\plugins\session-check.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { definePlugin } from '/@src/app'
import { useUserSession } from '/@src/stores/userSession'
import { getUserInfo } from '/@src/api/auth'
import { useViewWrapper } from '/@src/stores/viewWrapper'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { getCookie } from '/@src/utils/cookie'
/**
 * Here we are setting up two router navigation guards
 * (note that we can have multiple guards in multiple plugins)
 *
 * We can add meta to pages either by declaring them manualy in the
 * routes declaration (see /@src/router.ts)
 * or by adding a <route> tag into .vue files (see /@src/pages/sidebar/dashboards.ts)
 *
 * <route lang="yaml">
 * meta:
 *   requiresAuth: true
 * </route>
 *
 * <script setup lang="ts">
 *  // TS script
 * </script>
 *
 * <template>
 *  // HTML content
 * </template>
 */
const domain = import.meta.env.VITE_API_BASE_Domain
export default definePlugin(async ({ router, api, pinia }) => {
  const userSession = useUserSession(pinia)
  const viewWrapper = useViewWrapper()
  // 1. Check token validity at app startup
  if (getCookie('hrtools_token')) {
    try {
      // Do api request call to retreive user profile.
      // Note that the api is provided with json-server
      const res = await getUserInfo()
      userSession.setUser({
        ...res.data.result.userInfo,
        name: res.data.result.tenantList[0].name,
        domain: res.data.result.tenantList[0].realmName,
      })
      viewWrapper.setPageTitle(res.data.result.tenantList[0].name)
    } catch (err: any) {
      Notice({
        notice_type: 'error',
        message: '获取用户信息失败,请重新登录',
      })
      userSession.logoutUser()
    }
  }

  router.beforeEach((to) => {
    if (to.path === '/auth/login' || to.path === '/auth/login2' || to.path === '/') {
      if (to.path === '/auth/login' && location.hostname !== domain) {
        return {
          name: '/auth/login2',
        }
      }
      if (localStorage.getItem('domainLogin') === 'yes' && to.path === '/auth/login') {
        return {
          name: '/auth/login2',
        }
      }
      if (
        to.path === '/' &&
        location.hostname !== domain &&
        !!getCookie('hrtools_token')
      ) {
        return {
          name: '/home/all-project',
        }
      }
      if (to.path === '/auth/login2' && !!getCookie('hrtools_token')) {
        return {
          name: '/home/all-project',
        }
      }
      return
    } else {
      if (!!getCookie('hrtools_token')) {
        return
      } else {
        if (to.path === '/auth/signup' || to.path === '/survey/survey-library') {
          return
        } else {
          return {
            name: '/auth/login',
            query: { redirect: to.fullPath },
          }
        }
      }
    }

    // if (to.meta.requiresAuth && !userSession.isLoggedIn) {
    //   // 2. If the page requires auth, check if user is logged in
    //   // if not, redirect to login page.
    //   return {
    //     name: '/auth/login',
    //     // save the location we were at to come back later
    //     query: { redirect: to.fullPath },
    //   }
    // }
  })
})
