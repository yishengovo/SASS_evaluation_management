<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-09-23 11:21:25
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 21:30:17
 * @Description: file content
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'

// import { useViewWrapper } from '/@src/stores/viewWrapper'
// import { useUserSession } from '/@src/stores/userSession'
// const userSession = useUserSession()
// const viewWrapper = useViewWrapper()
// viewWrapper.setPageTitle(userSession.user?.user)

// useHead({
//   title: `${userSession.user?.user}- Navbar - Vuero`,
// })
useHead({
  title: `云测 - 消息通知`,
})
</script>

<template>
  <div class="page-content-inner">
    <NotificationPage />
  </div>
</template>
