/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:53
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-08 13:08:22
 * @FilePath: \survey\survey-user\src\stores\userSession.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import { acceptHMRUpdate, defineStore } from 'pinia'
import { ref } from 'vue'
import { IGroupList } from '/@src/components/contactGroup/type'
export const useGroupSession = defineStore('groupSession', () => {
  const id = ref('')
  const name = ref('我的联系人')
  const curentActiveIndex = ref(0)
  const count = ref(0)
  const totolCout = ref(0)
  const groupList = ref([
    {
      id: '0',
      name: '',
      createTime: '',
      tenantId: 0,
      count: 0,
    },
  ])
  function setId(pId: string) {
    id.value = pId
  }
  function setGroupActiveIndex(pcurentActiveIndex: number) {
    curentActiveIndex.value = pcurentActiveIndex
  }
  function setName(pName: string) {
    name.value = pName
  }
  function setCount(pCount: number) {
    count.value = pCount
  }
  function setGroupList(pGroupList: IGroupList[]) {
    groupList.value = pGroupList
  }
  function setTotalCount(pCount: number) {
    totolCout.value = pCount
  }
  return {
    id,
    name,
    count,
    groupList,
    totolCout,
    curentActiveIndex,
    setGroupActiveIndex,
    setGroupList,
    setTotalCount,
    setId,
    setName,
    setCount,
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
  import.meta.hot.accept(acceptHMRUpdate(useGroupSession, import.meta.hot))
}
