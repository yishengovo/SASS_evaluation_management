<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-04 20:09:25
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-10-13 20:53:45
 * @FilePath: \survey-user\src\components\base\layouts\VPageContentWrapper.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
// 计算侧边栏高度
// 获取浏览器视口宽度

const innerWitdh = window.innerWidth
const sidebarWidth = 280 / (innerWitdh / 100)
const contentWidth = 100 - sidebarWidth + 'vw'
</script>
<template>
  <div
    class="page-content-wrapper"
    :style="{
      width: '100%',
      maxWidth: contentWidth,
      marginLeft: sidebarWidth + 'vw',
    }"
  >
    <slot></slot>
  </div>
</template>

<style lang="scss">
/* ==========================================================================
3. Page Content
========================================================================== */

.page-content-wrapper {
  // max-width: calc(100vw - 10px);
  // transform: translateX(22.3%);

  .page-content {
    padding: 0 40px;

    &.is-relative {
      position: relative;
    }

    &.kanban-content {
      padding: 0 20px;
    }

    &.chat-content {
      padding: 0 40px;
    }

    &.card-content {
      padding: 0 20px;
    }

    &.waterfall-content {
      overflow: hidden;
    }

    &.projects-content {
      padding: 0 40px;
    }
  }
}

// Enable this setting if you want the dashboard to be larger
@media only screen and (min-width: 1408px) {
  .page-content-wrapper {
    // max-width: 1380px;
  }
}
@media only screen and (max-width: 1026px) {
  .page-content-wrapper {
    max-width: 98vw !important;

    margin: 0 auto !important;
  }
}

@media only screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: portrait) {
  .page-content-wrapper {
    .page-content {
      padding: 0;
    }
  }
}
</style>
