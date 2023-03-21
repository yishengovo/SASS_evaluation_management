<!--
 * @Author: August-Rushme
 * @Date: 2022-09-30 14:14:00
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-10-27 15:43:48
 * @FilePath: \survey-user\src\components\base\au-notice\AuNotice.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
interface IOptions {
  loading: boolean
  notice_type?: 'success' | 'error' | 'warning' | 'default'
  message: string
  duration?: number
}

const props = withDefaults(defineProps<IOptions>(), {
  loading: true,
  notice_type: 'default',
  message: '',
  duration: 1500,
})
console.log(props.notice_type)
</script>

<template>
  <Transition appear>
    <div>
      <div v-if="props.loading" :class="`au-loading ${props.notice_type}`">
        <div class="content">
          <div v-if="props.notice_type === 'default'" class="loading"></div>
          <div v-if="props.notice_type === 'success'" class="success-text icon-content">
            <i class="fas fa-check-circle" aria-hidden="true"></i>
          </div>
          <div v-if="props.notice_type === 'warning'" class="warning-text icon-content">
            <i class="fas fa-info-circle" aria-hidden="true"></i>
          </div>

          <div v-if="props.notice_type === 'error'" class="danger-text icon-content">
            <i class="fas fa-times-circle" aria-hidden="true"></i>
          </div>
          <div class="saving-text">
            <div>{{ props.message }}</div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style lang="scss" scoped>
.au-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;

  margin: 0 auto;
  z-index: 99999 !important;
  .content {
    position: absolute;
    top: 8px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 16px;

    background: #fff;
    border-radius: 2px;
    box-shadow: 0 3px 6px -4px #0000001f, 0 6px 16px #00000014, 0 9px 28px 8px #0000000d;
    pointer-events: all;
    .loading {
      width: 20px;
      height: 20px;
      border: 2px solid #41b983;
      border-top-color: transparent;
      border-radius: 100%;

      animation: circle infinite 0.75s linear;
    }
    .icon-content {
      padding-top: 1px;
    }
    .saving-text {
      margin-left: 6px;
      font-size: 14px;
      font-variant: tabular-nums;
      color: #000000d9;
    }
  }
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
