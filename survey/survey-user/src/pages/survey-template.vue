<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 15:17:34
 * @FilePath: \survey-user\src\pages\survey-template.vue
 * @Description: 我的项目

 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->

<route lang="yaml">
meta:
  requiresAuth: true
</route>
<script setup lang="ts">
const route = useRoute()
</script>

<template>
  <NavbarDropdownLayout :active="2">
    <template #sidebar>
      <Sideblock class="sideblock" theme="default">
        <!-- <template #header>
          <RouterLink to="/" class="sidebar-block-logo">
            <AnimatedLogo width="36px" />
          </RouterLink>
          <h3>Vuero</h3>
        </template> -->
        <template #links>
          <li>
            <RouterLink to="/survey-template/evaluation" class="single-link">
              <span class="icon">
                <i class="fas fa-calendar-check" aria-hidden="true"></i>
              </span>
              测评问卷模板
            </RouterLink>
          </li>

          <li>
            <RouterLink to="/survey-template/investigation" class="single-link">
              <span class="icon">
                <i
                  class="fas fa-file-signature"
                  aria-hidden="true"
                  style="padding-left: 5px"
                ></i>
              </span>
              调查问卷模板
              <!-- <span class="badge">3</span> -->
            </RouterLink>
          </li>
          <li>
            <RouterLink to="/survey-template/360survey" class="single-link">
              <span class="icon">
                <i class="lnir lnir-pie-chart" aria-hidden="true"></i>
              </span>
              360问卷模板
              <!-- <span class="badge">3</span> -->
            </RouterLink>
          </li>
        </template>
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
