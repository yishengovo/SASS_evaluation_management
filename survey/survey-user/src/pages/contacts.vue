<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 14:33:02
 * @FilePath: \survey-user\src\pages\contacts.vue
 * @Description: 我的项目

 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->

<script setup lang="ts">
import creatGroup from '/@src/components/contactGroup/index.vue'
const route = useRoute()
</script>

<template>
  <NavbarDropdownLayout :active="3">
    <template #sidebar>
      <Sideblock class="sideblock" theme="default">
        <template #links> <creatGroup></creatGroup></template>
      </Sideblock>
    </template>
    <!-- Content Wrapper -->
    <RouterView v-slot="{ Component }">
      <Transition name="fade-fast" mode="out-in">
        <component :is="Component" :key="route.fullPath" />
      </Transition>
    </RouterView>
  </NavbarDropdownLayout>
</template>
<style lang="scss" scoped>
@media only screen and (max-width: 1026px) {
  .sideblock {
    // max-width: 1380px;
    display: none;
  }
}

.sideblock {
  .single-link {
    font-size: 14px;
  }
}
</style>
