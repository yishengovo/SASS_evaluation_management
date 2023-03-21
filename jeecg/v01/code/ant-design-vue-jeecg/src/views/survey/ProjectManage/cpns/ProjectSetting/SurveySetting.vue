<template>
  <div>
    <a-spin :spinning="confirmLoading">
      <div style="margin-top: 20px">
        <!-- 发送短信, 发送邮件按钮 -->
        <a-button type="primary" @click="sendMessage" style="margin-right: 20px;">发送短信</a-button>
        <a-button type="primary" @click="sendEmail">发送邮件</a-button>
      </div>
      <div class="btns" style="margin-top: 20px;">
        <a-button :loading="loading" type="primary" @click="nextStep">完成</a-button>
        <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
      </div>
    </a-spin>
  </div>
</template>
<script>
import { axios } from '@/utils/request'

export default {
  props: {
    projectType: {
      type: String,
      default: ''
    },
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      confirmLoading: false,
      url: {
        queryById: '/survey/surProject/queryById'
      }
    }
  },
  mounted() {},
  methods: {
    // 发送短信
    sendMessage() {
      this.$message.info('功能开发中！')
    },
    // 发送邮件
    sendEmail() {
      this.$message.info('功能开发中！')
    },
    nextStep() {
      let that = this
      that.loading = true
      if (this.projectType === '360度评估') {
        // 随机延迟1-2秒
        setTimeout(() => {
          that.$emit('nextStep360')
        }, 1000 + Math.floor(Math.random() * 2000))
      } else {
        setTimeout(() => {
          that.$emit('nextStep')
        }, 1000 + Math.floor(Math.random() * 2000))
      }
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style scoped>
.survey-select {
  display: flex;
  align-items: center;
  margin-top: 20px;
}
</style>
