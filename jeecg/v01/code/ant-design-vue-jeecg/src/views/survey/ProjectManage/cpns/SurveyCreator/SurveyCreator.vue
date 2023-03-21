<template>
  <a-spin :spinning="confirmLoading">
    <div id="creatorElement" style="height: 100vh"></div>
  </a-spin>
</template>
<script>
import { SurveyCreator } from 'survey-creator-knockout'
import { localization } from 'survey-creator-core'
import 'survey-core/survey.i18n.js'
import 'survey-creator-core/survey-creator-core.i18n.js'

import 'survey-core/defaultV2.css'
import 'survey-creator-core/survey-creator-core.css'
import { axios } from '@/utils/request'

localization.currentLocale = 'zh-cn'
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
  showLogicType: true,
  isAtuoSave: false,
  haveCommercialLicense: true
}

export default {
  name: 'survey-creator',
  props: {
    surveyId: {
      type: String,
      default: ''
    },
    surveyJson: {
      type: String
    }
  },

  mounted() {
    // 初始化问卷
    this.creator.text = this.surveyJson || JSON.stringify(defaultJson)
    this.creator.saveSurveyFunc = async (saveNo, callback) => {
      // 加载保存动画
      this.loadingHandle()
      // 从this.creator.text中获取json
      const json = JSON.parse(this.creator.text)
      console.log(json)
      // 将json保存到数据库
      // 循环pages获取name和choices
      const questions = []
      for (let i = 0; i < json.pages.length; i++) {
        for (const question of json.pages[i].elements) {
          const questionObj = {
            name: '',
            names: [],
            choices: []
          }
          questionObj.name = question.name
          // 如果chicos数组里面是对象，则获取value
          if (question.choices && question.choices.length > 0) {
            const choices = []
            question.choices.forEach(item => {
              if (typeof item === 'object') {
                choices.push(item.value)
              } else if (item === '' || item === null) {
                choices = []
              } else {
                choices.push(item)
              }
            })
            questionObj.choices = choices
            questionObj.type = question.type
            // 评分
          } else if (question.rateValues && question.rateValues.length > 0) {
            question.rateValues.forEach(item => {
              questionObj.choices.push(item.value)
            })
            questionObj.type = question.type
            // 矩阵问题
          } else if (question.columns) {
            question.columns.forEach(item => {
              questionObj.choices.push(item.value)
            })
            questionObj.type = question.type
            question.rows.forEach(item => {
              questionObj.names.push(item.value)
            })
          }
          questions.push(questionObj)
        }
      }
      // 比较questions和res之间name的不同，更新不同的问题和选项

      console.log(json)
      console.log(questions)
      // 发送保存请求，并且保存解析度问题
      const res = await axios({
        url: '/survey/surSurveyProject/saveJson',
        method: 'post',
        data: {
          jsonPreview: this.creator.text,
          surveyId: this.surveyId,
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
      creator: new SurveyCreator(creatorOptions),
      loading: true
    }
  },
  methods: {
    loadingHandle() {
      const hide = this.$message.loading('保存中，请耐心等待..', 0)
      return hide
    }
  }
}
</script>
