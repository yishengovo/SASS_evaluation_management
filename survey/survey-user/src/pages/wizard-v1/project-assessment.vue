<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-11-02 11:51:34
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-12-07 20:44:55
 * @FilePath: \survey-user\src\pages\wizard-v1\project-assessment.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import {
  createEvaluationProject,
  getSelectedSurvey,
  getSurvey,
  getSurveyTemplate,
} from '/@src/api/createProject/createProject'
import { ISurvey, SurveyList, SurveyTemplate } from '/@src/api/createProject/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { useWizard } from '/@src/stores/wizard'

import noData from '/@src/assets/noData.png'

const columns = {
  num: {
    label: '编号',
  },
  surName: {
    label: '问卷名称',
  },
  surContent: {
    label: '问卷描述',
  },
  actionSlots: {
    label: '操作',
    grow: true,
    align: 'end',
  },
}
const wizard = useWizard()
const surveyTemplate = ref<SurveyTemplate[]>([])
const surveyList = ref<SurveyList[] | ISurvey[]>([])
const surveyIdList = ref<string[]>([])
const isEmpty = ref(false)
const isDisable = localStorage.getItem('isView') === 'view'
const isLoaderActive = ref(false)
const emits = defineEmits<{ (e: 'afterCreate', projectId: string): void }>()
// 分页相关
const ipagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})
// 获取已经选择的问卷列表
const getSelectedList = async () => {
  surveyIdList.value = []
  const { data: res } = await getSelectedSurvey(wizard.data.id as string)
  console.log(res)
  res.result.forEach((item: ISurvey) => {
    surveyIdList.value.push(item.id)
  })

  surveyList.value = res.result ?? []
  for (let i = 0; i < surveyList.value.length; i++) {
    surveyList.value[i].num = i + 1
  }
}
if (wizard.data.id) {
  getSelectedList()
}
// 获取问卷模板
const getSurveyTemplates = async () => {
  isLoaderActive.value = true
  const { data: res } = await getSurveyTemplate({
    pageNum: ipagination.currentPage,
    pageSize: ipagination.pageSize,
    type: '测评',
  })
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '获取数据失败，请稍后再试',
    })
  }
  isLoaderActive.value = false
  if (res.result.records.length === 0) {
    isEmpty.value = true
  }
  surveyTemplate.value = res.result.records
  ipagination.total = res.result.total
}
getSurveyTemplates()

// 获取问卷的json
const surveyJson = ref('')
const getSurveyJson = async (id: string) => {
  const { data: res } = await getSurvey(id)
  if (res.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '获取数据失败，请稍后再试',
    })
  }

  surveyJson.value = res.result.jsonPreview
}
// 处理问卷预览
const showSurveyPreview = ref(false)
const currentSurveyId = ref('')
const showFooter = ref(true)
const handleViewSurvey = async (id: string, isShowFooter: boolean) => {
  showFooter.value = isShowFooter
  currentSurveyId.value = id
  await getSurveyJson(id)
  showSurveyPreview.value = true
  console.log(surveyIdList.value)
}

// 创建测评项目
const createProject = async () => {
  const { data: res } = await createEvaluationProject({
    id: (wizard.data.id as string) ?? null,
    name: wizard.data.name,
    content: wizard.data.description,
    type: wizard.data.type as string,
    survey: surveyIdList.value ?? [],
  })
  if (res.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '引用模板失败，请稍后再试试',
    })
  }

  emits('afterCreate', res.result.project.id)
  wizard.data.id = res.result.project.id
  wizard.data.type = res.result.project.type
  surveyList.value = res.result.surveyList
}
const handleOK = async () => {
  showSurveyPreview.value = false
  if (surveyIdList.value.includes(currentSurveyId.value)) {
    return Notice({
      notice_type: 'error',
      message: '已经引用过该问卷',
    })
  }
  surveyIdList.value.push(currentSurveyId.value)
  await createProject()
  await getSelectedList()
}

// 处理取消引用问卷
const cancelActionsOpen = ref(false)
const currentCancelSurveyId = ref('')
const showCancelSurvey = (surveyId: string) => {
  currentCancelSurveyId.value = surveyId
  cancelActionsOpen.value = true
}
const handleCancelSurveyConfirm = async () => {
  console.log(surveyList.value.length)
  if (surveyIdList.value.length === 1) {
    surveyIdList.value = []
  } else {
    surveyIdList.value = surveyIdList.value.filter(
      (item) => item !== currentCancelSurveyId.value
    )
  }
  await createProject()
  await getSelectedList()
  console.log(surveyIdList.value)
  cancelActionsOpen.value = false
}
// 分页变化时
const paginationChange = (value: number) => {
  ipagination.currentPage = value
  getSurveyTemplates()
}
</script>

<template>
  <div class="assessmentContent">
    <VLoader size="large" :active="isLoaderActive" translucent class="list-loader">
      <VCard>
        <span class="title">已经选择问卷</span>
        <VFlexTable :data="surveyList" :columns="columns" compact rounded>
          <template #body-cell="{ row, column }">
            <div v-if="column.key === 'surName'">
              <Tippy>
                <div class="text-ellipsis">{{ row.surName }}</div>
                <template #content> {{ row.surName }} </template>
              </Tippy>
              <!-- <small class="tag">{{ row.choice }}</small> -->
              <!-- {{ row.choice }} -->
            </div>
            <div v-if="column.key === 'surContent'">
              <Tippy>
                <div class="text-ellipsis">
                  {{ row.surContent == '' ? '暂无描述' : row.surContent ?? '暂无描述' }}
                </div>
                <template #content>
                  {{ row.surContent == '' ? '暂无描述' : row.surContent ?? '暂无描述' }}
                </template>
              </Tippy>

              <!-- <small class="tag">{{ row.choice }}</small> -->
              <!-- {{ row.choice }} -->
            </div>
            <VButtons v-if="column.key === 'actionSlots'">
              <VButton color="primary" @click="handleViewSurvey(row.id, false)">
                预览问卷
              </VButton>
              <VButton :disabled="isDisable" @click="showCancelSurvey(row.id)">
                取消引用
              </VButton>
            </VButtons>
          </template>
        </VFlexTable>
      </VCard>

      <disableEle :disable="isDisable">
        <VCard style="margin-top: 50px">
          <div class="title">使用测评模板</div>
          <div v-if="surveyTemplate.length > 0" class="survey-templates">
            <div
              v-for="(item, index) in surveyTemplate"
              :key="index"
              class="survey-template"
              @click="handleViewSurvey(item.id, true)"
            >
              <div class="icon">
                <i class="iconify" data-icon="feather:eye" aria-hidden="true"></i>
                <span class="desc">快速预览</span>
              </div>
              <div class="desc" style="width: 100%; height: 100%">
                <div class="row1">
                  <div class="name">
                    {{ item.surName == '' ? '暂无描述' : item.surName ?? '暂无描述' }}
                  </div>
                </div>
                <div class="row2">
                  {{ item.surContent == '' ? '暂无描述' : item.surName ?? '暂无描述' }}
                </div>
              </div>

              <!-- <div class="btn">
                  <VButton color="info" @click="handleAnswerPreview(item)">
                    答题详情
                  </VButton>
                </div> -->
            </div>
          </div>
          <VFlexPagination
            v-model:current-page="ipagination.currentPage"
            :item-per-page="ipagination.pageSize"
            :total-items="ipagination.total"
            :max-links-displayed="5"
            @update:current-page="paginationChange"
          />
          <div v-if="isEmpty">
            <div style="margin: 30px auto; max-width: 900px; text-align: center">
              <img :src="noData" alt="" style="width: 20vw; height: 35vh" />
              <div style="text-align: center; margin-top: 20px">暂无数据</div>
            </div>
          </div>
        </VCard>
      </disableEle>
    </VLoader>
    <AuDialog
      v-if="showSurveyPreview"
      title="问卷预览"
      ok-text="引用"
      cancel-text="取消"
      :footer="showFooter"
      @close="showSurveyPreview = false"
      @cancel="showSurveyPreview = false"
      @confirm="handleOK"
    >
      <SurveyPreview :survey-json="surveyJson"></SurveyPreview>
    </AuDialog>
    <VModal
      :open="cancelActionsOpen"
      actions="center"
      title="提示"
      @close="cancelActionsOpen = false"
    >
      <template #content>
        <VPlaceholderSection title="您确认要取消引用此问卷吗?" />
      </template>
      <template #action>
        <VButton color="primary" raised @click="handleCancelSurveyConfirm"> 确定</VButton>
      </template>
    </VModal>
  </div>
</template>

<style lang="scss" scoped>
.text-tip {
  background-color: #383838;
}
.text-ellipsis {
  max-width: 120px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
@media (max-width: 1100px) {
  .survey-templates {
    justify-content: center !important;
  }
  .assessmentContent {
    width: 90vw !important;
  }
}
@media (max-width: 400px) {
  .survey-template {
    width: 80vw !important;
  }
}
.assessmentContent {
  width: 100%;
  .title {
    display: inline-block;
    margin-bottom: 10px;
    font-weight: 500;
    color: #484848;
    font-size: 20px;
  }
  .survey-templates {
    display: flex;
    flex-wrap: wrap;

    .survey-template {
      position: relative;
      width: 170px;
      height: 130px;
      margin: 20px;
      box-shadow: 0 2px 4px 0 rgb(0 0 0 / 8%), 4px 6px 12px 0 rgb(0 0 0 / 8%);
      border: 1px solid var(--border);
      border-radius: 4px;
      cursor: pointer;
      box-sizing: border-box;
      font-size: 13px;
      padding: 10px;
      transition: transform 0.5s ease;

      &:hover {
        .icon {
          position: fixed;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          margin-top: -10px;
          margin-left: -10px;
          width: 100%;
          height: 100%;
          color: #41b983;
        }
        transform: translateY(-10px);
        background-color: rgba(0, 0, 0, 0.2);
      }

      .icon {
        display: none;

        .iconify {
          font-size: 40px;
        }
        .desc {
          font-size: 14px;
          color: #41b983;
        }
      }

      .row1 {
        justify-content: space-between;
        align-items: center;
        font-weight: 700;
        font-size: 16px;
        color: #484848;
      }
      .row2 {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 10px;
        color: var(--keGreyText);
      }
      .btn {
        position: absolute;
        left: 10px;
        bottom: 10px;
      }
    }
  }
}
</style>
