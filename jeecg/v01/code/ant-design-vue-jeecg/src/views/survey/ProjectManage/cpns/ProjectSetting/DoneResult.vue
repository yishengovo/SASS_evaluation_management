<template>
  <div>
    <result title="您已完成项目所有设置" :is-success="true" description="快前往发布问卷吧">
      <!-- <div class="information">
        <a-row>
          <a-col :sm="8" :xs="24">付款账户：</a-col>
          <a-col :sm="16" :xs="24">ant-design@alipay.com</a-col>
        </a-row>
      </div> -->
      <div slot="action">
        <a-button type="primary" @click="finish">重新设置项目</a-button>
      </div>
    </result>
  </div>
</template>
<script>
import Result from '@/views/result/Result'
export default {
  components: {
    Result
  },
  data() {
    return {}
  },
  methods: {
    finish() {
      this.$emit('finish')
    }
  }
}
</script>
<style lang=""></style>
