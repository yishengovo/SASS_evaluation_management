<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-04 20:09:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-12 13:09:16
 * @FilePath: \survey-user\src\layouts\mobile-subsidebars\DashboardsMobileSubsidebar.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<template>
  <div class="mobile-subsidebar">
    <div class="inner">
      <div class="sidebar-title">
        <h3>问卷市场</h3>
      </div>
      <ul class="submenu">
        <li>
          <RouterLink to="/survey-template/evaluation">测评问卷模板</RouterLink>
        </li>
        <li>
          <RouterLink to="/survey-template/investigation">调查问卷模板</RouterLink>
        </li>
        <li>
          <RouterLink to="/survey-template/360survey">360度问卷模板</RouterLink>
        </li>
      </ul>
      <!-- <ul class="submenu" data-simplebar>
        <VCollapseLinks>
          <template #header>
            Personal
            <i aria-hidden="true" class="iconify" data-icon="feather:chevron-right" />
          </template>

          <RouterLink to="app" class="is-submenu">
            <i aria-hidden="true" class="lnil lnil-home"></i>
            <span>Home</span>
          </RouterLink>
        </VCollapseLinks>
      </ul> -->
    </div>
  </div>
</template>
