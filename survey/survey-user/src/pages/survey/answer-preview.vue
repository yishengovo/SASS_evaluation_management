<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-11 17:07:24
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-16 15:23:33
 * @FilePath: \survey-user\src\pages\survey\answer-preview.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import * as Survey from 'survey-jquery'
import $ from 'jquery'
import 'survey-vue/defaultV2.min.css'

Survey.StylesManager.applyTheme('defaultV2')

interface SurveyAnswer {
  surveyJson: string
  answer: any
}
const props = withDefaults(defineProps<SurveyAnswer>(), {
  surveyJson: '',
  answer: {},
})

onMounted(() => {
  // 初始化Survey
  const survey = new Survey.Model(props.surveyJson)
  // 只读模式
  survey.data = JSON.parse(props.answer)
  console.log(survey.data, 888)
  survey.mode = 'display'
  $('#surveyElement').Survey({ model: survey })
})
</script>

<template>
  <div class="survey-container container">
    <div id="surveyElement"></div>
  </div>
</template>

<style lang="scss" scoped>
@media (min-width: 1200px) {
  .container {
    width: 100% !important;
    padding: 0 !important;
  }
}
/*   */
.container {
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto;
}
</style>
