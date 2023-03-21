<!--
 * @Author: Augut Rush
 * @Date: 2022-09-21 14:40:24
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-07 18:08:04
 * @FilePath: \survey-user\src\pages\contacts\index.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713@qq.com, All Rights Reserved.
-->
<route lang="yaml">
meta:
  requiresAuth: true
</route>
<script setup lang="ts">
import { useHead } from '@vueuse/head'

// import { useViewWrapper } from '/@src/stores/viewWrapper'
// const viewWrapper = useViewWrapper()

useHead({
  title: '联系人 - Contacts',
})
</script>
<template>
  <!-- Content Wrapper -->
  <div class="page-content-inner">
    <VCard style="text-align: center">
      <div class="empty">
        <img src="/@src/assets/project/empty.svg" alt="" />
        <div class="desc">暂未开放</div>
      </div></VCard
    >
  </div>
</template>

<style lang="" scoped></style>
