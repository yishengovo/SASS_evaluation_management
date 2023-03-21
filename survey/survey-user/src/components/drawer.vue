<script lang="ts" setup>
type propsEle = {
  modelValue: boolean
  title?: string
  closable?: boolean
  mask?: boolean
  maskClosable?: boolean
  width?: string
}
const props = withDefaults(defineProps<propsEle>(), {
  modelValue: false,
  title: '标题',
  closable: true,
  mask: true,
  maskClosable: true,
  width: '400px',
})
const emit = defineEmits(['update:modelValue', 'drawerColse'])
const maskClass = computed(() => {
  return {
    'mask-show': props.mask && props.modelValue,
    'mask-hide': !(props.mask && props.modelValue),
  }
})
const mainClass = computed(() => {
  return {
    'main-show': props.modelValue,
    'main-hide': !props.modelValue,
  }
})
const mainStyle = computed(() => {
  return {
    width: props.width,
    right: props.modelValue ? '0' : `-${props.width}`,
    borderLeft: props.mask ? 'none' : '1px solid #eee',
  }
})
const closeByMask = () => {
  props.maskClosable && emit('update:modelValue', false)
  props.maskClosable && emit('drawerColse')
}
const closeByButton = () => {
  emit('update:modelValue', false)
  props.maskClosable && emit('drawerColse')
}
</script>
<template>
  <div class="drawer">
    <div :class="maskClass" @click="closeByMask"></div>
    <div :class="mainClass" :style="mainStyle" class="main">
      <div class="drawer-head">
        <span>{{ title }}</span>
        <span v-show="closable" class="close-btn" @click="closeByButton">X</span>
      </div>
      <div class="drawer-body">
        <slot />
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.drawer {
  /* 遮罩 */
  .mask-show {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 999;
    background-color: rgba(0, 0, 0, 0.5);
    opacity: 1;
    transition: opacity 0.5s;
  }
  .mask-hide {
    opacity: 0;
    transition: opacity 0.5s;
  }

  /* 滑块 */
  .main {
    position: fixed;
    z-index: 9999;
    top: 0;
    height: 100%;

    background: #fff;
    transition: all 0.5s;
  }
  .main-show {
    opacity: 1;
  }
  .main-hide {
    opacity: 0;
  }

  /* 某个元素内部显示 */
  .inner {
    position: absolute;
  }

  /* 其他样式 */
  .drawer-head {
    display: flex;
    justify-content: space-between;
    height: 45px;
    line-height: 45px;
    padding: 0 15px;
    font-size: 14px;
    font-weight: bold;
    border-bottom: 1px solid #eee;
    .close-btn {
      display: inline-block;
      cursor: pointer;
      height: 100%;
      padding-left: 20px;
    }
  }
  .drawer-body {
    font-size: 14px;
    padding: 15px;
  }
}
</style>
