/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:53
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 13:23:35
 * @FilePath: \survey\survey-user\src\stores\userSession.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import { acceptHMRUpdate, defineStore } from 'pinia'
import { ref } from 'vue'
import { getCookie, clearCookieByKey } from '/@src/utils/cookie'
export type UserData = Record<string, any> | null
export const useUserSession = defineStore('userSession', () => {
  // token will be synced with local storage
  // @see https://vueuse.org/core/usestorage/
  const token = ref(getCookie('hrtools_token'))
  const tenantId = ref(getCookie('hrtools_tanantId'))
  const user = ref<Partial<UserData>>()
  const loading = ref(true)
  function setUser(newUser: Partial<UserData>) {
    user.value = newUser
  }
  function setToken(newToken: string) {
    token.value = newToken
  }
  function setUserInfo(key: string, value: string) {
    user.value![key] = value
  }
  function setLoading(newLoading: boolean) {
    loading.value = newLoading
  }
  function setDomain(newDomain: string) {
    user.value!.domain = newDomain
  }
  function setTenantId(newTenantId: string) {
    tenantId.value = newTenantId
  }
  function logoutUser() {
    clearCookieByKey('hrtools_token')
    clearCookieByKey('hrtools_tanantId')
    token.value = ''
    user.value = undefined
  }

  return {
    user,
    token,
    tenantId,
    loading,
    setUserInfo,
    logoutUser,
    setDomain,
    setUser,
    setToken,
    setTenantId,
    setLoading,
  } as const
})

/**
 * Pinia supports Hot Module replacement so you can edit your stores and
 * interact with them directly in your app without reloading the page.
 *
 * @see https://pinia.esm.dev/cookbook/hot-module-replacement.html
 * @see https://vitejs.dev/guide/api-hmr.html
 */
if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserSession, import.meta.hot))
}
