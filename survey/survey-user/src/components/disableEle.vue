<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-24 22:30:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 13:53:12
 * @Description: file content
-->
<script lang="ts" setup>
import { Notice } from '/@src/components/base/au-notice/Notice'
type propsEle = {
  disable: boolean
  message?: string
}
const props = withDefaults(defineProps<propsEle>(), {
  disable: false,
  message: '问卷已发布,目前不可编辑',
})
const tip = () => {
  props.disable &&
    Notice({
      notice_type: 'warning',
      message: props.message,
    })
}
</script>
<template>
  <div :class="disable && 'disable'" style="width: 100%" @click="tip">
    <div :class="disable && 'contanier'">
      <slot></slot>
    </div>
  </div>
</template>
<style scoped lang="scss">
.disable {
  cursor: not-allowed;
  .contanier {
    pointer-events: none;
  }
}
</style>
