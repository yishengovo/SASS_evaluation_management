<!--
 * @Author: qiaoqi
 * @Date: 2023-05-29 20:50:24
 * @LastEditors: qiaoqi 2507260744@qq.com
 * @LastEditTime: 2023-05-29 20:50:24
 * @FilePath: \survey-user\src\components\survey\creator\TemplateEdit.vue
 * @Description:
 *
 * Copyright (c) 2023 by qiaoqi booleanchar12@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { SurveyCreator } from 'survey-creator-knockout'
import type { PropType } from 'vue'

import '/@src/utils/survey/creator/localization/localize'

import 'survey-vue/defaultV2.min.css'
import 'survey-creator-core/survey-creator-core.css'

import { addProject } from '/@src/api/createProject/createProject'
import { WizardType } from '/@src/models/wizard'

import { Notice } from '/@src/components/base/au-notice/Notice'
import { LocationQueryValue } from 'vue-router'
import { analysisQuestion } from '/@src/utils/survey/creator/question/analyzeQuestion'
import { saveTemplateApi } from '/@src/api/surTemplate/surTemplate'

interface TemplateInfo {
  name: string
  content?: string
  type: WizardType
  id: string | LocationQueryValue[] | null
  jsonPreview: string | undefined
}

const props = defineProps({
  templateInfo: {
    type: Object as PropType<TemplateInfo>,
    default: () => {},
    require: true,
  },
})

const router = useRouter()

const emit = defineEmits(['saveTemplate'])

const defaultJson = {
  locale: 'zh-cn',
  completedHtml: '<h3>感谢您的填写!</h3>',
  pages: [],
  showQuestionNumbers: 'off',
}
// 配置
const creatorOptions = {
  showLogicTab: true,
  showLogicType: true,
  showJSONEditorTab: true,
  isAtuoSave: false,
  haveCommercialLicense: true,
}
const creator = new SurveyCreator(creatorOptions)
onMounted(() => {
  // console.log("111111111111111111111111111",props);
  creator.text = props.templateInfo.jsonPreview ?? JSON.stringify(defaultJson)
  // 渲染问卷组件
  creator.render('creatorElement')
})

// 保存问卷
creator.saveSurveyFunc = () => {
  const _props = ref(props)
  Notice({
    message: '保存中，请稍后',
  })
  const json = JSON.parse(creator.text)

  // 将json保存到数据库

  const [questions, surveyJson] = analysisQuestion(json)

  if (questions.length > 0) {
    saveTemplateApi({
      surveyId: props.templateInfo.id,
      name: props.templateInfo.name,
      content: props.templateInfo.content,
      type: props.templateInfo.type,
      jsonPreview: JSON.stringify(surveyJson),
      question: questions,
    }).then(async (result) => {
      if (result.data.code !== 200) {
        return Notice({
          message: '保存失败，请稍后再试',
          notice_type: 'error',
        })
      } else {
        emit('saveTemplate')
        // _props.value.templateInfo.id = result.data.result.id
        // router.replace('/wizard-v1/project-details?id=' + result.data.result.id)
        Notice({
          message: '保存成功',
          notice_type: 'success',
        })
      }
    })
  } else {
    Notice({
      message: '保存失败，请稍后再试',
      notice_type: 'error',
    })
  }
}
</script>

<template>
  <div id="creatorElement" style="height: 100vh"></div>
</template>

<style lang="scss" scoped></style>
