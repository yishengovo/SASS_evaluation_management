<!--
 * @Author: August-Rushme
 * @Date: 2022-10-03 19:46:12
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-25 14:34:46
 * @FilePath: \survey-user\src\pages\wizard-v1\question\question-list.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import disableEle from '/@src/components/disableEle.vue'

import {
  getChoicesById,
  getQuestionList,
  saveScoreById,
} from '/@src/api/createProject/createProject'
import { getQuestionType } from '/@src/constant/survey'

import { tableContent } from './config/table.content'

import type { IChoices, Question } from '/@src/api/createProject/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { LocationQueryValue } from 'vue-router'
const isDisable = localStorage.getItem('isView') === 'view'
const props = defineProps({
  projectId: {
    type: String,
    require: true,
    default: '',
  },
})
const router = useRouter()
const emit = defineEmits(['goBack'])

const isLoaderActive = ref(false)
const projectId = ref<string | LocationQueryValue[] | null>(null)
if (router.currentRoute.value.query.id) {
  projectId.value = router.currentRoute.value.query.id
}
// 当前tab
const currentTab = ref('questions')
// 当前问题id
const currentQuestionId = ref('')
// 分页相关
const ipagination = reactive({
  currentPage: 1,
  pageSize: 5,
  total: 0,
})
const data = ref<any[]>([])
// 获取问题列表
const getQuestions = async () => {
  isLoaderActive.value = true
  const { data: res } = await getQuestionList(
    props.projectId,
    ipagination.currentPage,
    ipagination.pageSize
  )
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '获取数据失败，请稍后再试',
    })
  }
  ipagination.total = res.result.total
  data.value = res.result.records.map((item: Question) => {
    return {
      ...item,
      typeId: getQuestionType(item.typeId),
      isDisabled:
        getQuestionType(item.typeId) === '单行文本' ||
        getQuestionType(item.typeId) === '多行文本' ||
        getQuestionType(item.typeId) === '图片选择器' ||
        getQuestionType(item.typeId) === '布尔题' ||
        getQuestionType(item.typeId) === '意见题' ||
        getQuestionType(item.typeId) === '文件' ||
        getQuestionType(item.typeId) === 'HTML代码' ||
        getQuestionType(item.typeId) === '签名板'
          ? true
          : false,
    }
  })
  isLoaderActive.value = false
}
getQuestions()

const handleSetting = (row: Question) => {
  currentQuestionId.value = row.id

  nextTick(async () => {
    await getChoices()
  })
  currentTab.value = 'choices'
  console.log(row)
}
const tabChange = (tab: string) => {
  currentTab.value = tab
  if (tab === 'choices') {
    getChoices()
  }
}
// 选项相关
const choices = ref<any>([])
const getChoices = async () => {
  const { data: res } = await getChoicesById(
    currentQuestionId.value,
    projectId.value as string
  )
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '获取数据失败，请稍后再试',
    })
  }
  choices.value = res.result.map((item: IChoices, index: number) => {
    return {
      ...item,
      edit: false,
      orderNumber: index + 1,
    }
  })
}

const handleEdit = (row: any) => {
  row.edit = true
}
const handleSave = async (row: any) => {
  Notice({
    message: '保存中...',
    notice_type: 'default',
  })
  const score: string[] = []
  choices.value.forEach((item: IChoices) => {
    score.push(item.basicScore)
  })
  const { data: res } = await saveScoreById({
    projectId: projectId.value as string,
    id: row.id,
    score,
  })

  if (res.code !== 200) {
    return Notice({
      message: '保存失败，请稍后再试',
      notice_type: 'error',
    })
  }
  Notice({
    message: '保存成功',
    notice_type: 'success',
  })
  row.edit = false
}
const handleCancel = (row: any) => {
  row.edit = false
}
// 返回问卷编辑
const goBack = () => {
  emit('goBack')
}
// 分页变化时
const paginationChange = (value: number) => {
  ipagination.currentPage = value
  getQuestions()
}
</script>

<template>
  <VLoader size="large" :active="isLoaderActive" translucent>
    <div class="content">
      <VCard>
        <div class="backBtnContent">
          <VIconButton
            color="danger"
            light
            raised
            circle
            icon="feather:x"
            class="backBtn"
            @click="goBack"
          />
        </div>

        <VTabs
          type="boxed"
          slide
          :selected="currentTab"
          :tabs="[
            {
              label: '题目列表',
              value: 'questions',
              to: `/wizard-v1/project-details?id=${projectId}`,
            },
            {
              label: '设置选项',
              value: 'choices',
              to: `/wizard-v1/project-details?id=${projectId}`,
            },
          ]"
          @update:selected="tabChange"
        >
          <template #tab="{ activeValue }">
            <div>
              <div v-if="activeValue === 'questions'">
                <disableEle :disable="isDisable">
                  <VFlexTable
                    :data="data"
                    :columns="tableContent.questionColumns"
                    compact
                    rounded
                  >
                    <template #body-cell="{ row, column }">
                      <div v-if="column.key === 'title'">
                        <Tippy>
                          <div class="text-ellipsis">{{ row.title }}</div>
                          <template #content> {{ row.title }} </template>
                        </Tippy>
                        <!-- <VTag v-tooltip="row.title" color="solid" :label="row.title" /> -->

                        <!-- <small class="tag">{{ row.choice }}</small> -->
                        <!-- {{ row.choice }} -->
                      </div>
                      <VButton
                        v-if="column.key === 'actionSlots'"
                        color="primary"
                        :disabled="row.isDisabled"
                        @click="handleSetting(row)"
                      >
                        设置选项
                      </VButton>
                    </template>
                  </VFlexTable>
                </disableEle>
                <VFlexPagination
                  v-model:current-page="ipagination.currentPage"
                  :item-per-page="ipagination.pageSize"
                  :total-items="ipagination.total"
                  :max-links-displayed="5"
                  no-router
                  @update:current-page="paginationChange"
                />
              </div>
              <disableEle :disable="isDisable">
                <VFlexTable
                  v-if="activeValue === 'choices'"
                  ref="choicesTable"
                  :data="choices"
                  :columns="tableContent.choiceColums"
                  reactive
                  compact
                  rounded
                >
                  <template #body-cell="{ row, column }">
                    <div v-if="column.key === 'choice'">
                      <Tippy>
                        <div class="text-ellipsis">{{ row.choice }}</div>
                        <template #content> {{ row.choice }} </template>
                      </Tippy>
                      <!-- <VTag v-tooltip="row.choice" color="solid" :label="row.choice" /> -->

                      <!-- <small class="tag">{{ row.choice }}</small> -->
                      <!-- {{ row.choice }} -->
                    </div>
                    <div v-if="column.key === 'basicScore'">
                      <VField>
                        <VInput v-if="row.edit" v-model="row.basicScore" />
                        <span v-else> {{ row.basicScore }}</span>
                      </VField>
                    </div>
                    <div v-if="column.key === 'actionSlots'" class="btnGroup">
                      <VButton
                        v-if="row.edit"
                        color="primary"
                        class="saveBtn"
                        @click="handleSave(row)"
                      >
                        保存
                      </VButton>
                      <VButton v-else color="success" @click="handleEdit(row)">
                        编辑
                      </VButton>
                      <VButton v-if="row.edit" @click="handleCancel(row)"> 取消 </VButton>
                    </div>
                  </template>
                </VFlexTable>
              </disableEle>
            </div>
          </template>
        </VTabs>
      </VCard>
    </div>
  </VLoader>
</template>

<style lang="scss" scoped>
.content {
  margin: 0 auto;
  width: calc(50vw + 10rem);
  .backBtnContent {
    display: flex;
    justify-content: end;
  }

  .saveBtn {
    margin-right: 10px;
  }
}
</style>
