<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 21:14:30
 * @FilePath: \survey-user\src\pages\home.vue
 * @Description: 我的项目

 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->

<route lang="yaml">
meta:
  requiresAuth: true
</route>
<script setup lang="ts">
const route = useRoute()
const createProject = () => {
  localStorage.setItem('isEdit', 'false')
}
</script>

<template>
  <NavbarDropdownLayout>
    <template #sidebar>
      <Sideblock class="sideblock" theme="default">
        <!-- <template #header>
        <RouterLink to="/" class="sidebar-block-logo">
          <AnimatedLogo width="36px" />
        </RouterLink>
        <h3>Vuero</h3>
      </template> -->
        <template #links>
          <RouterLink to="/wizard-v1" @click="createProject">
            <li class="create-project">
              <VButton
                class="create-btn"
                icon="feather:plus"
                color="primary"
                style="width: 200px; font-size: 16px"
              >
                <span> 创建项目</span>
              </VButton>
            </li>
          </RouterLink>
          <li>
            <RouterLink to="/home/all-project" class="single-link">
              <span class="icon">
                <i class="iconify" data-icon="feather:package"></i>
              </span>
              全部项目
            </RouterLink>
          </li>
          <li>
            <RouterLink to="/home/collection-project" class="single-link">
              <span class="icon">
                <i class="iconify" data-icon="feather:heart" aria-hidden="true"></i>
              </span>
              收藏项目
            </RouterLink>
          </li>
          <!-- <li>
            <RouterLink to="/home/classify-project" class="single-link">
              <span class="icon">
                <i class="iconify" data-icon="feather:package" aria-hidden="true"></i>
              </span>
              项目分类
              <span class="badge">3</span>
            </RouterLink>
          </li> -->
          <li>
            <RouterLink to="/home/recycle-bin" class="single-link">
              <span class="icon">
                <i class="iconify" data-icon="feather:trash-2" aria-hidden="true"></i>
              </span>
              回收站
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
  .create-project {
    display: flex;
    justify-content: center;
    .create-btn {
      // padding-right: 60px;
      margin-bottom: 1.875rem;
    }
  }
  .single-link {
    font-size: 14px !important;
  }
  .sidebar-muen {
    display: flex;
    flex-direction: column;
    align-items: center;
    .item {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 95%;
      height: 32px;
      margin-top: 20px;
      font-size: 14px;
      .iconify {
        font-size: 14px;
        margin-right: 8px;
      }
      span {
        font-size: 14px;
      }
      &.active {
        background-color: #efefef;
      }
      &:hover {
        cursor: pointer;
      }
      &:nth-child(4n) {
        margin-left: -15px;
      }
    }
  }
}
</style>
