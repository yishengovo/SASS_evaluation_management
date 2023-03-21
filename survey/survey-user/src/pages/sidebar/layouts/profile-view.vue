<!--
 * @Author: August-Rushme
 * @Date: 2022-11-02 14:21:09
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 18:53:50
 * @FilePath: \survey-user\src\pages\sidebar\layouts\profile-view.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
// import { useHead } from '@vueuse/head'

// import { useViewWrapper } from '/@src/stores/viewWrapper'

// const viewWrapper = useViewWrapper()
// viewWrapper.setPageTitle('个人信息')

// useHead({
//   title: '个人信息',
// })
import { useHead } from '@vueuse/head'
useHead({
  title: '云测 - 个人信息',
})
</script>

<template>
  <div class="page-content-inner">
    <ViewProfile />
  </div>
</template>
