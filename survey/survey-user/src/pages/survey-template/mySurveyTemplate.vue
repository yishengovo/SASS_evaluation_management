<script setup lang="ts">
import { useHead } from '@vueuse/head'
import surTemplate from '/@src/components/surTemplate/surTemplate.vue'
useHead({
  title: '云测 - 我的问卷模板',
})
</script>

<template>
  <VCard style="text-align: center">
    <surTemplate type="我的"></surTemplate>
  </VCard>
</template>

<style lang="" scoped></style>