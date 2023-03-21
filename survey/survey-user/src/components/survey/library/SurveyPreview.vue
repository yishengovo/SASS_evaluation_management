<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-06 17:40:53
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-12-11 17:07:18
 * @FilePath: \survey-user\src\components\survey\library\SurveyPreview.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import * as Survey from 'survey-jquery'
import $ from 'jquery'
import 'survey-vue/defaultV2.min.css'
import { LocationQueryValue } from 'vue-router'

Survey.StylesManager.applyTheme('defaultV2')
interface SurveyInfo {
  surveyJson: string
}

const props = withDefaults(defineProps<SurveyInfo>(), {
  surveyJson: '',
})
const router = useRouter()
// 获取问卷项目表的json
const projectId = ref<LocationQueryValue[] | string>('')
onMounted(async () => {
  if (router.currentRoute.value.query.id) {
    projectId.value = router.currentRoute.value.query.id as string
  }

  const survey = new Survey.Model(props.surveyJson)
  // 返回值会暴露给模板和其他的选项式 API 钩子
  $('#surveyElement').Survey({ model: survey })

  // 问卷完成时的回掉
  const surveyComplete = (sender: any) => {
    console.log('完成', sender.data)
    const resultData = []
    for (const key in survey.data) {
      const question = survey.getQuestionByName(key)
      if (!!question) {
        const item = {
          name: key,
          value: question.value,
          title: question.displayValue,
          type: question.getType(),
          displayValue: question.displayValue,
        }
        resultData.push(item)
      }
    }
    console.log(resultData)
  }
  survey.onComplete.add(surveyComplete)
})
</script>

<template>
  <div id="surveyElement" style="display: inline-block; width: 100%"></div>
</template>

<style lang="" scoped></style>
