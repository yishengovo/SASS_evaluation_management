<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-09 19:11:26
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-31 01:30:16
 * @FilePath: \survey-user\src\pages\statics\data-record.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script lang="ts" setup>
import { Model } from 'survey-core'
import { VisualizationPanel } from 'survey-analytics'
// import 'survey-analytics/survey.analytics.css'
import '/@src/utils/survey/analytics/localization/localize'
import { getAnalytics } from '/@src/api/analytics'
import type { SurveyList, SurveyResult } from '/@src/api/analytics/type'
import { Notice } from '/@src/components/base/au-notice/Notice'

export interface PropsType {
  id: string
}

const props = withDefaults(defineProps<PropsType>(), {
  id: '0',
})

const vizPanelOptions = {
  allowHideQuestions: false,

  hideEmptyAnswers: false,

  showPercentages: true,
}

// const router = useRouter()

// const goBack = () => {

//   router.back()

// }

const isLoaderActive = ref(false)

const surveyResults = ref<SurveyResult[]>([])

const surveyJson = ref('')

const collectNum = ref(0)

const lastEditTime = ref('')

const projectName = ref('')

const projectType = ref('')

const currentSurIndex = ref(0)

const surveyList = ref<SurveyList[]>([])

const changeSurvey = (id: string, index: number) => {
  const screenEl = document.getElementById('screen') as HTMLElement

  const surveyEl = document.getElementById('surveyVizPanel2') as HTMLElement

  screenEl.removeChild(surveyEl)

  currentSurIndex.value = index

  surveyList.value.forEach((survey: SurveyList) => {
    if (survey.id === id) {
      surveyJson.value = ''

      surveyJson.value = survey.jsonPreview

      surveyResults.value = []

      surveyResults.value = survey.surveyResults.map((item: any) => {
        return {
          ...JSON.parse(item.answer),
        }
      })
    }
  })

  const divEl = document.createElement('div')

  divEl.id = 'surveyVizPanel2'

  screenEl.appendChild(divEl)

  const survey = new Model(surveyJson.value)

  const vizPanel = new VisualizationPanel(
    survey.getAllQuestions(),

    surveyResults.value,

    vizPanelOptions
  )

  // console.log(vizPanel.getData())

  vizPanel.showHeader = false

  // 渲染数据

  vizPanel.render(document.getElementById('surveyVizPanel2') as HTMLElement)
}

onMounted(async () => {
  isLoaderActive.value = true

  // 获取数据

  const { data: res } = await getAnalytics(props.id as any)

  if (res.code !== 200) {
    isLoaderActive.value = false

    return Notice({
      notice_type: 'error',

      message: '数据获取失败',
    })
  }

  projectName.value = res.result.name

  projectType.value = res.result.type

  collectNum.value = res.result.collectNum

  lastEditTime.value = res.result.lastEditTime

  if (projectType.value !== '测评') {
    surveyResults.value = res.result.surveyResults.map((item: any) => {
      return {
        ...JSON.parse(item.answer),
      }
    })

    isLoaderActive.value = false

    // 先清空数据，避免缓存

    surveyJson.value = ''

    surveyJson.value = res.result.surveyJson

    const survey = new Model(surveyJson.value)

    const vizPanel = new VisualizationPanel(
      survey.getAllQuestions(),

      surveyResults.value,

      vizPanelOptions
    )

    // console.log(vizPanel.getData())

    vizPanel.showHeader = false

    // 渲染数据

    vizPanel.render(document.getElementById('surveyVizPanel') as HTMLElement)
  } else {
    isLoaderActive.value = false

    if (res.result.surveyList) {
      surveyList.value = res.result.surveyList

      surveyJson.value = ''

      surveyJson.value = res.result.surveyList[0].jsonPreview

      surveyResults.value = res.result.surveyList[0].surveyResults.map((item: any) => {
        return {
          ...JSON.parse(item.answer),
        }
      })
    }

    const survey = new Model(surveyJson.value)

    const vizPanel = new VisualizationPanel(
      survey.getAllQuestions(),

      surveyResults.value,

      vizPanelOptions
    )

    // console.log(vizPanel.getData())

    vizPanel.showHeader = false

    // 渲染数据

    vizPanel.render(document.getElementById('surveyVizPanel2') as HTMLElement)
  }
})
</script>

<template>
  <VLoader size="large" :active="isLoaderActive">
    <div id="screen" class="screen">
      <!-- <div class="screen-title">
        <div>数据大屏</div>
        <div><i class="lnir lnir-close" aria-hidden="true" @click="goBack()"></i></div>
      </div> -->

      <div class="screen-content">
        <div class="project-name">{{ projectName }}</div>

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

        <div v-if="projectType !== '测评'" id="surveyVizPanel" />
      </div>

      <template v-if="projectType === '测评'">
        <div class="content-center">
          <div style="margin: 15px 0; font-size: 16px">问卷列表</div>

          <span
            v-for="(item, index) in surveyList"
            :key="item.id"
            :class="['survey-item', currentSurIndex === index ? 'active' : '']"
            @click="changeSurvey(item.id, index)"
            >{{ item.surName }}</span
          >
        </div>
      </template>

      <div id="surveyVizPanel2" />
    </div>
  </VLoader>
</template>

<style lang="scss" scoped>
.screen {
  background-color: var(--keDarkBg);

  padding: 0 10px 10px 10px;

  .screen-title {
    display: flex;

    justify-content: space-between;

    align-items: center;

    padding: 20px;

    height: 80px;

    font-size: 18px;

    font-weight: 700;

    border-bottom: 1px solid var(--border);

    i {
      cursor: pointer;
    }
  }

  .screen-content {
    margin-top: 20px;

    .project-name {
      text-align: center;

      font-size: 24px;

      margin-top: 20px;

      margin-bottom: 20px;

      font-weight: 700;
    }

    .cards {
      display: flex;

      justify-content: space-between;

      margin: 0 0.875rem;

      .time {
        padding: 1.25rem;

        width: 47.8vw;

        background-color: #f7f7f7;

        .desc {
          font-size: 1.14rem;
        }

        .content {
          padding-top: 0.625rem;

          font-size: 2.25rem;

          font-weight: 600;

          color: #0060e6;

          overflow-y: hidden;
        }
      }

      .total {
        padding: 1.25rem;

        width: 47.8vw;

        background-color: #f7f7f7;

        .desc {
          font-size: 1.14rem;
        }

        .content {
          padding-top: 0.625rem;

          font-size: 2.25rem;

          font-weight: 600;

          color: #0060e6;

          overflow-y: hidden;
        }
      }
    }
  }
}

.content-center {
  margin: 0 auto;

  margin-top: 20px;

  padding: 20px 0 40px 10px;

  max-width: 97.1vw;

  background-color: #f7f7f7;

  .survey-item {
    display: inline-block;

    margin: 0 auto;

    padding: 0 15px;

    height: 40px;

    line-height: 40px;

    text-align: center;

    background-color: #bababa;

    color: white;

    border-radius: 3px;

    margin-right: 15px;

    cursor: pointer;
  }

  .active {
    background-color: #0084ff;
  }
}
</style>
