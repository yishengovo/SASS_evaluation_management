<template>
  <div class="survey-container container">
    <survey :survey="survey" />
  </div>
</template>

<script>
import { StylesManager, Model } from 'survey-vue'
import 'survey-vue/defaultV2.css'

StylesManager.applyTheme('defaultV2')

import { Survey } from 'survey-vue-ui'

import { axios } from '@/utils/request'

const json = {}
// 获取问卷项目表的json

export default {
  name: 'answer-surveyjs-component',
  components: {
    Survey
  },
  props: {
    jsonAnswer: {
      type: Object,
      default: () => {
        return {}
      }
    },
    surveyId: {
      type: String,
      default: () => {
        return 0
      }
    }
  },
  async mounted() {
    console.log(this.jsonAnswer, 1111)
    const data = await axios({
      url: 'survey/survey/queryById',
      method: 'get',
      params: {
        id: this.surveyId
      }
    })
    let surveyJson = data.result.jsonPreview
    // 初始化Survey
    this.survey = new Model(surveyJson)
    // 只读模式
    this.survey.data = this.jsonAnswer
    this.survey.mode = 'display'
  },
  data() {
    var model = new Model(json)
    return {
      model: {},
      userInfo: {},
      survey: model
    }
  },
  methods: {
    handleOk() {
      this.model.projectId = this.projectId
      console.log(this.model)
      // 校验表单
    }
  }
}
</script>

<style scoped lang="less">
@media (min-width: 1200px) {
  .container {
    width: 1170px;
  }
}
// @media (min-width: 992px) {
//   .container {
//     width: 970px;
//   }
// }
// @media (min-width: 768px) {
//   .container {
//     width: 750px;
//   }
// }
.container {
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto;
}
</style>
