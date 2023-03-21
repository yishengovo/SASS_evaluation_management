<!--
 * @Author: August-Rushme
 * @Date: 2022-11-02 14:21:09
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 22:01:07
 * @FilePath: \survey-user\src\pages\sidebar\layouts\profile-edit\experience.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'
import { useViewWrapper } from '/@src/stores/viewWrapper'

const viewWrapper = useViewWrapper()
viewWrapper.setPageTitle('Profile Edit 2')

useHead({
  title: '云测 - 编辑',
})
</script>

<template>
  <EditProfileExperience />
</template>
