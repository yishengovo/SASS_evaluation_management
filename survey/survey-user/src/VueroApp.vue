<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:51
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 13:20:19
 * @FilePath: \survey\survey-user\src\VueroApp.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { initDarkmode } from '/@src/stores/darkmode'
// setUpUSerInfo()
// This is the global app setup function

/**
 * Initialize the darkmode watcher
 *
 * @see /@src/stores/darkmode
 */
initDarkmode()
// 监听滚动条
</script>

<template>
  <div>
    <Suspense>
      <RouterView v-slot="{ Component }">
        <Transition name="fade-slow" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </Suspense>
    <ClientOnly>
      <VReloadPrompt app-name="Vuero" />
    </ClientOnly>
  </div>
</template>
