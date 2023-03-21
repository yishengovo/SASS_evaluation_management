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
      // 发送保存请求
      const res = await axios({
        url: 'survey/surSurveyLib/saveJson',
        method: 'post',
        data: {
          json: this.creator.text,
          id: this.surveyId
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
