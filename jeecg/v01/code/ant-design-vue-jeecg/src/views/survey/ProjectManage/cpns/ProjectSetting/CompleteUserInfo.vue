<template>
  <div style="margin-top: 20px;">
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div>努力开发中...</div>
    <div class="btns" style="margin-top: 20px;">
      <a-button :loading="loading" type="primary" @click="nextStep">完成</a-button>
      <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      loading: false
    }
  },
  methods: {
    nextStep() {
      let that = this
      that.loading = true
      setTimeout(function() {
        that.$emit('nextStep360')
      }, 1500)
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style lang=""></style>
