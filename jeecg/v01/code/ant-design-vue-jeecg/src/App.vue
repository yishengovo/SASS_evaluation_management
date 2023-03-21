<!--
 * @Author: August Rush
 * @Date: 2023-01-09 09:50:56
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-16 17:05:12
 * @FilePath: \ant-design-vue-jeecg\src\App.vue
 * @Description: 
 * 
 * Copyright (c) 2023 by August Rush 864011713@qq.com, All Rights Reserved. 
-->
<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view />
    </div>
  </a-config-provider>
</template>
<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import enquireScreen from '@/utils/device'

export default {
  data() {
    return {
      locale: zhCN,
    }
  },
  created() {
    let that = this
    enquireScreen((deviceType) => {
      // tablet
      if (deviceType === 0) {
        that.$store.commit('TOGGLE_DEVICE', 'mobile')
        that.$store.dispatch('setSidebar', false)
      }
      // mobile
      else if (deviceType === 1) {
        that.$store.commit('TOGGLE_DEVICE', 'mobile')
        that.$store.dispatch('setSidebar', false)
      } else {
        that.$store.commit('TOGGLE_DEVICE', 'desktop')
        that.$store.dispatch('setSidebar', true)
      }
    })
  },
}
</script>
<style>
#app {
  height: 100%;
}
.sd-multipletext__item-title {
  height: 100% !important;
}

</style>
