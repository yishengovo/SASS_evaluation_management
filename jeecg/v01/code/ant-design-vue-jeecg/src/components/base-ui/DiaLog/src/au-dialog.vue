<template>
  <div class="au-dialog">
    <div class="m-dialog-mask"></div>
    <div class="m-modal">
      <div class="m-modal-content">
        <svg
          @click="onClose"
          class="u-close"
          viewBox="64 64 896 896"
          data-icon="close"
          aria-hidden="true"
          focusable="false"
        >
          <path
            d="M563.8 512l262.5-312.9c4.4-5.2.7-13.1-6.1-13.1h-79.8c-4.7 0-9.2 2.1-12.3 5.7L511.6 449.8 295.1 191.7c-3-3.6-7.5-5.7-12.3-5.7H203c-6.8 0-10.5 7.9-6.1 13.1L459.4 512 196.9 824.9A7.95 7.95 0 0 0 203 838h79.8c4.7 0 9.2-2.1 12.3-5.7l216.5-258.1 216.5 258.1c3 3.6 7.5 5.7 12.3 5.7h79.8c6.8 0 10.5-7.9 6.1-13.1L563.8 512z"
          ></path>
        </svg>
        <div class="m-modal-header">
          <div class="u-head">{{ title }}</div>
        </div>

        <div class="m-modal-body">
          <slot></slot>
        </div>

        <div class="m-modal-footer" v-show="footer">
          <button class="u-cancel" @click="onCancel">{{ cancelText }}</button>
          <button class="u-confirm" @click="onConfirm">{{ okText }}</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { afterOpen, afterClose } from '@/utils/util'
export default {
  name: 'Dialog',
  props: {
    title: {
      // 标题
      type: String,
      default: '提示'
    },
    content: {
      // 内容
      type: String,
      default: ''
    },
    width: {
      // 宽度，默认640
      type: Number,
      default: 640
    },
    cancelText: {
      // 取消按钮文字
      type: String,
      default: '取消'
    },
    okText: {
      // 确认按钮文字
      type: String,
      default: '确定'
    },
    footer: {
      // 是否显示底部按钮，默认显示
      type: Boolean,
      default: true
    }
  },
  mounted() {
    afterOpen()
  },
  methods: {
    onClose() {
      afterClose()
      this.$emit('close')
    },
    onCancel() {
      this.$emit('cancel')
    },
    onConfirm() {
      this.$emit('ok')
    }
  }
}
</script>
<style lang="less" scoped>
.m-dialog-mask {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1000;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.65);
}
.m-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  overflow: auto;
  outline: 0;

  .m-modal-content {
    // position: relative;
    height: 100vh;
    background: #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    .u-close {
      width: 16px;
      height: 16px;
      position: absolute;
      top: 19px;
      right: 24px;
      fill: rgba(0, 0, 0, 0.45);
      font-size: 18px;
      line-height: 22px;
      cursor: pointer;
      transition: color 0.3s;
      &:hover {
        fill: rgba(0, 0, 0, 0.75);
      }
    }
    .m-modal-header {
      height: 52px;
      padding: 16px 24px;
      color: rgba(0, 0, 0, 0.65);
      border-radius: 4px 4px 0 0;
      border-bottom: 1px solid #e8e8e8;
      .u-head {
        margin: 0;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 500;
        font-size: 16px;
        line-height: 22px;
        word-wrap: break-word;
      }
    }
    .m-modal-body {
      padding: 24px;
      font-size: 14px;
      line-height: 1.5;
      word-wrap: break-word;
      height: calc(100% - 55px - 55px);
      overflow: auto;
    }
    .m-modal-footer {
      padding: 10px 16px;
      text-align: right;
      border-top: 1px solid #e8e8e8;
      .u-cancel {
        height: 32px;
        line-height: 32px;
        padding: 0 15px;
        font-size: 16px;
        border-radius: 4px;
        color: rgba(0, 0, 0, 0.65);
        background: #fff;
        border: 1px solid #d9d9d9;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
        &:hover {
          color: #40a9ff;
          border-color: #40a9ff;
        }
        &:focus {
          color: #096dd9;
          border-color: #096dd9;
        }
      }
      .u-confirm {
        margin-left: 8px;
        height: 32px;
        line-height: 32px;
        padding: 0 15px;
        font-size: 16px;
        border-radius: 4px;
        background: #1890ff;
        border: 1px solid #1890ff;
        color: #fff;
        transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
        cursor: pointer;
        &:hover {
          color: #fff;
          background: #40a9ff;
          border-color: #40a9ff;
        }
        &:focus {
          background: #096dd9;
          border-color: #096dd9;
        }
      }
    }
  }
}
</style>
