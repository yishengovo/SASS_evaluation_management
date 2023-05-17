<!--
 * @Author: Augut Rush
 * @Date: 2022-09-21 14:37:31
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 10:51:20
 * @FilePath: \survey-user\src\pages\survey-template\index.vue
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
  title: '问卷市场 - Survey template',
})
</script>

<template>
  <div>
    <VCard>
      <div class="empty">
        <img src="/@src/assets/project/empty.svg" alt="" />
        <div class="desc">暂未开放</div>
      </div>
    </VCard>
  </div>
</template>

<!-- <style lang="" scoped>

</style> -->
