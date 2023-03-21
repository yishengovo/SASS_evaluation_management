<!--
 * @Author: August-Rushme
 * @Date: 2022-09-23 10:01:33
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 14:52:43
 * @FilePath: \survey-user\src\pages\survey-template\all-template.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'
import surTemplate from '/@src/components/surTemplate/surTemplate.vue'
useHead({
  title: '云测 - 测评问卷市场',
})
</script>

<template>
  <VCard style="text-align: center">
    <surTemplate type="测评"></surTemplate>
  </VCard>
</template>

<style lang="" scoped></style>
