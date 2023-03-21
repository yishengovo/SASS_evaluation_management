<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-04 20:09:21
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-11-01 23:29:57
 * @FilePath: \ant-design-vue-jeecg\src\views\survey\creator\index.vue
 * @Description: 
 * 
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved. 
-->
<template>
  <div>
    <SurveyCreator />
  </div>
</template>
<script>
import { Survey } from 'survey-vue-ui'
import SurveyCreator from '@/components/survey/creator/SurveyCreator'

export default {
  name: 'MyFirstSurvey',
  components: {
    Survey,
    SurveyCreator
  },
  data() {
    // const survey = new Model(surveyJson)
    // survey.onComplete.add(this.alertResults)
    return {}
  },
  methods: {
    // surveyComplete(sender) {
    //   saveSurveyResults('https://your-web-service.com/' + SURVEY_ID, sender.data)
    // },
    alertResults(sender) {
      const results = JSON.stringify(sender.data)
      alert(results)
    }
  }
}
</script>
<style lang="less" scoped>
body {
  margin: 0;
  padding: 0;
}
</style>
