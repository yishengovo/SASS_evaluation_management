<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-07 14:38:03
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-30 15:18:46
 * @FilePath: \survey-user\src\pages\home\sidebar\all-project\project-code.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import QrcodeVue from 'qrcode.vue'
import Clipboard from 'clipboard'

import { Notice } from '/@src/components/base/au-notice/Notice'
export interface QrcodeConfig {
  value: string
  size: number
}

const props = withDefaults(defineProps<QrcodeConfig>(), {
  value: '',
  size: 300,
})

const copyLink = async () => {
  let clipboard = new Clipboard('.copyBtn')
  clipboard
    .on('success', () => {
      clipboard.destroy() // 销毁上一次的复制内容
      clipboard = new Clipboard('.copyBtn')

      Notice({
        notice_type: 'success',
        message: '复制成功！',
      })
    })
    .on('error', () => {
      Notice({
        notice_type: 'error',
        message: '复制失败请稍后再试',
      })
    })
}
</script>

<template>
  <div class="qr-code">
    <div class="qrcode-wrapper">
      <div class="content">
        <div class="qrcode-title">
          <i class="lnir lnir-mobile-alt-1 success-text" aria-hidden="true"></i>
          <span class="text"> 使用其他设备扫描二维码使用此 问卷</span>
        </div>
        <qrcode-vue
          class="qrcode-code"
          :value="props.value"
          :size="props.size"
          level="H"
        />
        <div class="link-survey">
          <div id="link" class="link ellipsis">{{ value }}</div>
          <VButton
            color="primary"
            class="copyBtn"
            :data-clipboard-text="value"
            @click="copyLink"
            >复制链接</VButton
          >
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.ellipsis {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.qr-code {
  .content {
    width: 300px;
    margin: 0 auto;
    .qrcode-title {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 10px;
      .text {
        margin-left: 4px;
      }
    }
    .link-survey {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;
      .link {
        margin-right: 3px;
      }
      .copyBtn {
        width: 80px;
        height: 26px;
      }
    }
  }
}
</style>
