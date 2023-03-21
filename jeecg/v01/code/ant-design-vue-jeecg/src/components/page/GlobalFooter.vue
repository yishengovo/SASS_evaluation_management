<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-04 20:09:21
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-12-09 10:39:02
 * @FilePath: \ant-design-vue-jeecg\src\components\page\GlobalFooter.vue
 * @Description: 
 * 
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved. 
-->
<template>
  <div class="footer">
    <!-- <div class="links">
      <a href="http://www.jeecg.com" target="_blank">HRTools 首页</a>
      <a href="https://github.com/zhangdaiscott/jeecg-boot" target="_blank">
        <a-icon type="github" />
      </a>
      <a href="https://ant.design/">Ant Design</a>
      <a href="https://vuecomponent.github.io/ant-design-vue/docs/vue/introduce-cn/">Vue Antd</a>
    </div> -->
    <div class="copyright">
      Copyright
      <a-icon type="copyright" />
      2022 <span>厦门朗新天霁软件技术有限公司 出品</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LayoutFooter'
}
</script>

<style lang="less" scoped>
.footer {
  padding: 0 16px;
  margin: 48px 0 24px;
  text-align: center;

  .links {
    margin-bottom: 8px;

    a {
      color: rgba(0, 0, 0, 0.45);

      &:hover {
        color: rgba(0, 0, 0, 0.65);
      }

      &:not(:last-child) {
        margin-right: 40px;
      }
    }
  }
  .copyright {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
  }
}
</style>
