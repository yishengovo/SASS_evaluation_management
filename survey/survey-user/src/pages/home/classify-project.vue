<!--
 * @Author: August-Rushme
 * @Date: 2022-09-22 14:10:40
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 18:43:14
 * @FilePath: \survey-user\src\pages\home\classify-project.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'

// import SurveyLibrary from '/@src/components/survey/library/SurveyLibrary.vue'

useHead({
  title: '云测 - 项目分类',
})
// const router = useRouter()

// const test = () => {
//   router.push('/survey/survey-library')
// }
</script>

<template>
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
