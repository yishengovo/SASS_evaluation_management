<template>
  <a-spin :spinning="confirmLoading">
    <div id="creatorElement" style="height: 100vh"></div>
  </a-spin>
</template>

<script>
import { SurveyCreator } from 'survey-creator-knockout'
import '@/utils/survey/creator/localization/localize'
import 'survey-core/defaultV2.min.css'
import 'survey-creator-core/survey-creator-core.min.css'

import { axios } from '@/utils/request'
import { analysisQuestion } from '@/utils/survey/creator/analyzeQuestion'

const defaultJson = {
  locale: 'zh-cn',
  completedHtml: '<h3>感谢您的填写!</h3>',
  completedHtmlOnCondition: [
    {
      expression: '{nps_score} > 8',
      html:
        '<h3>Thank you for your feedback.</h3><h5>We glad that you love our product. Your ideas and suggestions will help us to make our product even better!</h5>'
    },
    {
      expression: '{nps_score} < 7',
      html:
        '<h3>Thank you for your feedback.</h3><h5> We are glad that you share with us your ideas.We highly value all suggestions from our customers. We do our best to improve the product and reach your expectation.</h5><br />'
    }
  ],
  pages: [],
  showQuestionNumbers: 'off'
}

// 配置
const creatorOptions = {
  showLogicTab: true,
  showLogicType: true,
  showLogicType: true,
  isAtuoSave: false,
  haveCommercialLicense: true
}
// 初始化
let surveyJson = null
export default {
  name: 'surveyjs-creator-component',
  props: {
    surveyId: {
      type: String,
      default: ''
    }
  },
  // 监听props.surveyId变化
  watch: {
    surveyId(val) {
      this.getSurveyJson(val)
    }
  },
  async mounted() {
    this.creatorId = this.surveyId
    await axios({
      url: 'survey/survey/queryById',
      method: 'get',
      params: {
        id: this.creatorId
      }
    }).then(res => {
      surveyJson = res.result.jsonPreview
    })
    this.creator.text = surveyJson || JSON.stringify(defaultJson)

    this.creator.saveSurveyFunc = async (saveNo, callback) => {
      // 加载保存动画
      this.loadingHandle()
      // 从this.creator.text中获取json
      const json = JSON.parse(this.creator.text)
      console.log(json)
      // 将json保存到数据库
      // 循环pages获取name和choices
      const [questions, newSurveyJson] = analysisQuestion(json)

      // 比较questions和res之间name的不同，更新不同的问题和选项

      console.log(newSurveyJson)
      console.log(questions)
      // 发送保存请求，并且保存解析度问题
      if (questions.length === 0) {
        return this.$message.error('保存失败,请稍后再试')
      }
      const res = await axios({
        url: 'survey/survey/saveJson',
        method: 'post',
        data: {
          jsonPreview: JSON.stringify(newSurveyJson),
          surveyId: this.creatorId,
          question: questions
        }
      })

      if (res.code !== 200) {
        callback(saveNo, false)
        return this.$message.error('保存失败,请稍后再试')
      }
      this.$message.destroy()
      callback(saveNo, true)
      this.loading = false
      this.$message.success('保存成功')
    }
    this.creator.render('creatorElement')
    this.confirmLoading = false
  },
  data() {
    return {
      confirmLoading: true,
      creatorId: '',
      creator: new SurveyCreator(creatorOptions),
      loading: true,
      questionAndChoice: []
    }
  },
  methods: {
    async getSurveyJson(val) {
      // let surveyJson = null
      await axios({
        url: 'survey/survey/queryById',
        method: 'get',
        params: {
          id: val
        }
      }).then(res => {
        surveyJson = res.result.jsonPreview
      })
      this.creator.text = surveyJson || JSON.stringify(defaultJson)
    },
    loadingHandle() {
      const hide = this.$message.loading('保存中，请耐心等待..', 0)
      return hide
    }
  }
}
</script>

<style scoped lang="less"></style>
