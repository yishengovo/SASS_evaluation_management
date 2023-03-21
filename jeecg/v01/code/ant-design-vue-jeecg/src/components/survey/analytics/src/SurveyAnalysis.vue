<template>
  <a-spin :spinning="confirmLoading">
    <div class="cards">
      <div class="time">
        <div class="desc">最近更新日期</div>
        <div class="content">{{ lastEditTime }}</div>
      </div>
      <div class="total">
        <div class="desc">答卷总数</div>
        <div class="content">{{ collectNum }}</div>
      </div>
    </div>
    <div id="surveyVizPanel" />
  </a-spin>
</template>

<script>
import { Model } from 'survey-core'
import { VisualizationPanel, localization, VisualizerBase } from 'survey-analytics'
import * as SurveyAnalytics from 'survey-analytics'
import { axios } from '@/utils/request'

localization.locales['en']['chartType_bar'] = '条形图'
localization.locales['en']['chartType_pie'] = '饼状图'
localization.locales['en']['chartType_doughnut'] = '扇形图'
localization.locales['en']['chartType_scatter'] = '散点图'
localization.locales['en']['defaultOrder'] = '默认排序'
localization.locales['en']['descOrder'] = '降序'
localization.locales['en']['ascOrder'] = '升序'

const vizPanelOptions = {
  allowHideQuestions: false,
  hideEmptyAnswers: false,
  showPercentages: true
}
// 注册自定义组件
export default {
  name: 'survey-analytics',
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      surveyResults: [],
      surveyJson: {},
      collectNum: 0,
      lastEditTime: '',
      confirmLoading: false,
      url: {
        getSurveyJsonAndResult: '/survey/surProject/result/'
      }
    }
  },
  created() {},
  async mounted() {
    this.confirmLoading = true
    const { result: res } = await axios({
      method: 'get',
      url: this.url.getSurveyJsonAndResult + this.projectId
    })
    this.surveyJson = res.surveyJson
    this.collectNum = res.collectNum
    this.lastEditTime = res.lastEditTime

    this.surveyResults = res.surveyResults.map(item => {
      return {
        ...JSON.parse(item.answer)
      }
    })

    const survey = new Model(this.surveyJson)

    const vizPanel = new VisualizationPanel(survey.getAllQuestions(), this.surveyResults, vizPanelOptions)
    // console.log(vizPanel.getData())
    vizPanel.showHeader = false
    vizPanel.render(document.getElementById('surveyVizPanel'))
    this.confirmLoading = false
    // console.log(new VisualizerBase.getData())
  },
  methods: {
    async getSurveyJsonAndResult() {}
  }
}
</script>
<style lang="less" scoped>
.desc() {
  font-size: 1.14rem;
  color: #262626;
}
.content() {
  padding-top: 0.625rem;
  font-size: 2.25rem;
  font-weight: 600;
  color: #0060e6;
  overflow-y: hidden;
}
.cards {
  display: flex;
  justify-content: space-between;
  margin: 0 0.875rem;
  .time {
    padding: 1.25rem;
    width: 56.2rem;
    background-color: #f7f7f7;
    .desc {
      .desc();
    }
    .content {
      .content();
    }
  }
  .total {
    padding: 1.25rem;
    width: 56.2rem;
    background-color: #f7f7f7;
    .desc {
      .desc();
    }
    .content {
      .content();
    }
  }
}
</style>
