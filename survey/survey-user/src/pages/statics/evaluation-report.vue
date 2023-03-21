<!--
 * @Author: August-Rushme
 * @Date: 2022-11-10 11:23:32
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-02-23 17:40:29
 * @FilePath: \survey-user\src\pages\statics\evaluation-report.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { getAnalytics } from '/@src/api/analytics'
import { SurveyList } from '/@src/api/analytics/type'
import { getProject } from '/@src/api/createProject/createProject'
import { ProjectInfo } from '/@src/api/createProject/type'
import { getUsers } from '/@src/api/survey/fillSurvey'
import { generatorReport } from '/@src/api/survey/report'
import { User } from '/@src/api/survey/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
export interface PropsType {
  id: string
}

const props = withDefaults(defineProps<PropsType>(), {
  id: '0',
})
// const router = useRouter()
const id = props.id

const projectInfo = ref<ProjectInfo>()
// 分页相关
const ipagination = reactive({
  currentPage: 1,
  pageSize: 5,
  total: 0,
})
const isLoaderActive = ref(false)
const userList = ref<User[]>([])
// 报告是否已生成
const showReport = ref(false)
const reportLink = ref('')
const surveyList = ref<SurveyList[]>([])
const currentSurIndex = ref(0)
// 获取项目信息
const getProjectInfo = async () => {
  isLoaderActive.value = true
  const { data: res } = await getProject(id as string)
  if (res.code !== 200) {
    return Notice({ notice_type: 'error', message: '获取数据失败' })
  }
  projectInfo.value = res.result

  const surveyList = res.result.surveyList

  reportLink.value = surveyList[0].reportLink

  await getUserList()
  isLoaderActive.value = false
}
getProjectInfo()

// 获取项目的用户列表
const getUserList = async () => {
  const { data: res } = await getUsers({
    id: projectInfo.value?.project.id as string,
    pageNum: ipagination.currentPage,
    pageSize: ipagination.pageSize,
  })
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({ notice_type: 'error', message: '获取数据失败' })
  }
  userList.value = res.result.records
  let num = 0
  userList.value.forEach((item: any) => {
    num++
    item.num = num
  })
  ipagination.total = res.result.total
}

const columns = {
  num: {
    label: '序号',
  },
  name: {
    label: '用户名',
  },
  phone: {
    label: '手机号',
  },
  actionSlots: {
    label: '操作',
    align: 'end',
  },
}

// 分页变化时
const paginationChange = (value: number) => {
  ipagination.currentPage = value
  getUserList()
}
const handleGenerateReport = async (row: any) => {
  if (row.isFinished === false) {
    return Notice({ notice_type: 'error', message: '用户未完成问卷' })
  }
  Notice({ notice_type: 'default', message: '生成报告中请稍后' })
  const { data: res } = await generatorReport(row.projectId, row.id)
  if (res.code !== 200) {
    return Notice({ notice_type: 'error', message: '生成报告失败请稍后再试!' })
  }
  Notice({ notice_type: 'success', message: '生成报告成功' })
  // 重新获取数据
  getUserList()
}
const userId = ref('')
const projectId = ref('')
const handleViewReport = (row: any) => {
  userId.value = row.id
  projectId.value = row.projectId
  reportLink.value =
    reportLink.value + '&projectId=' + projectId.value + '&userId=' + userId.value
  console.log(reportLink.value)

  showReport.value = true
}
const changeSurvey = (item: any, index: number) => {
  currentSurIndex.value = index
  reportLink.value =
    item.reportLink + '&projectId=' + projectId.value + '&userId=' + userId.value
  console.log(reportLink.value)
}
// changeSurvey(surveyList.value[0], 0)
onMounted(async () => {
  // 获取数据
  const { data: res } = await getAnalytics(id as any)
  if (res.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '数据获取失败',
    })
  }
  if (res.result.surveyList) {
    surveyList.value = res.result.surveyList
    console.log(surveyList.value)
  }
})
// const goBack = () => {
//   router.push('/home/all-project')
// }
</script>

<template>
  <div class="evaluation-report">
    <!-- <div class="report-title">
      <div>项目测评报告</div>
      <div><i class="lnir lnir-close" aria-hidden="true" @click="goBack()"></i></div>
    </div> -->

    <div class="report-content">
      <div class="report-center">
        <div class="proejct-title">{{ projectInfo?.project.projectName }}</div>
        <VLoader size="large" :active="isLoaderActive" translucent class="list-loader">
          <VFlexTable :data="userList" :columns="columns" compact rounded reactive>
            <template #body-cell="{ row, column }">
              <!-- <div v-if="column.key === 'num'">{{}}</div> -->
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
              <div v-if="column.key === 'actionSlots'">
                <VButton
                  v-if="!row.isGenerate"
                  color="primary"
                  @click="handleGenerateReport(row)"
                >
                  生成报告
                </VButton>
                <VButton v-else color="success" @click="handleViewReport(row)">
                  查看报告
                </VButton>
              </div>
            </template>
          </VFlexTable>
        </VLoader>
        <VFlexPagination
          v-model:current-page="ipagination.currentPage"
          :item-per-page="ipagination.pageSize"
          :total-items="ipagination.total"
          :max-links-displayed="5"
          no-router
          @update:current-page="paginationChange"
        />
      </div>
    </div>

    <AuDialog
      v-if="showReport"
      title="测评报告"
      :footer="false"
      @close="showReport = false"
    >
      <div class="content-center">
        <div style="margin: 15px 0; font-size: 16px">问卷列表</div>
        <span
          v-for="(item, index) in surveyList"
          :key="item.id"
          :class="['survey-item', currentSurIndex === index ? 'active' : '']"
          @click="changeSurvey(item, index)"
          >{{ item.surName }}</span
        >
      </div>
      <template #iframe>
        <iframe
          id="iframeReport"
          class="report"
          :src="reportLink"
          frameborder="0"
          title="测评报告"
        >
        </iframe>
      </template>
    </AuDialog>
  </div>
</template>

<style lang="scss" scoped>
@media (max-width: 1100px) {
  .report {
    margin: 0 0 0 2vw !important;
    width: 93vw !important;
    height: 91vh !important;
  }
}

.evaluation-report {
  background-color: var(--keDarkBg);
  .report-title {
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
  .report-content {
    height: calc(100vh - 80px);
    overflow-y: scroll;

    padding-left: 20px;
    .report-center {
      max-width: calc(60vw + 100px);
      margin: 0 auto;
      .proejct-title {
        display: flex;
        justify-content: center;
        margin: 40px 0;
        font-size: 20px;
        font-weight: 700;
      }
    }
  }
  .report {
    margin-left: 28px;
    margin-top: 20px;
    width: 95.8vw;
    height: 90vh;
  }
}
.content-center {
  margin: 5px 0 0 1.8vw;
  padding: 10px;
  max-width: 100%;
  background-color: #f7f7f7;
  .survey-item {
    display: inline-block;
    margin: 5px auto;
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
