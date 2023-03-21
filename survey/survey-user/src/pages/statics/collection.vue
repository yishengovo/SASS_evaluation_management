<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-09 16:02:00
 * @LastEditTime: 2023-01-30 19:02:30
 * @Description: file content
-->
<script lang="ts" setup>
import { getProject } from '/@src/api/createProject/createProject'
import { getCollect } from '/@src/api/analytics'
import { keRequest } from '/@src/utils/keRequest'
import { ProjectInfo } from '/@src/api/createProject/type'
import noData from '/@src/assets/noData.png'
import keTable from '/@src/components/base/ke-table/ke-table.vue'
import AnswerPreview from '../survey/answer-preview.vue'
import { columns, otherOptions } from './configs/collection'
import { savePdf } from '/@src/utils/survey/pdf/exportPdf'
import { exportExcel } from '/@src/api/excel/exportExcel'
import { handleFileDownload } from '/@src/utils/util'

export interface PropsType {
  id: string
}

const props = withDefaults(defineProps<PropsType>(), {
  id: '0',
})

// const router = useRouter()

const id = props.id

const collectData = ref<any>()

const surveyList = ref<any>([])

const projectData = ref<ProjectInfo>()

const pageInfo = ref({
  pageSize: 10,
  pageNum: 1,
})

const currentPage = ref(1)

let total = ref(0)

const isLoaderActive = ref(false)

const tableColums = ref([...columns])
let isNeedPush = true
const getData = async () => {
  isLoaderActive.value = true

  const res2 = await getProject(id as string)

  projectData.value = res2.data.result

  if (res2.data.result.project.type === '测评') {
    keRequest(async () => {
      const res = await getCollect({
        id: id as string,

        ...pageInfo.value,
      })

      collectData.value = res.data.result

      surveyList.value = res.data.result.surveyList
      if (isNeedPush) {
        surveyList.value.forEach((e: any, index: number) => {
          tableColums.value.push({
            field: 'survey' + (index + 1),

            title: e.surName,

            slotName: 'survey',

            minwidth: '400',
          })
        })
        isNeedPush = false
      }

      total.value = res.data.result.userPage.total

      return [res]
    }, '获取收集情况')
  } else if (res2.data.result.project.type === '调查') {
    keRequest(async () => {
      const res = await getCollect({
        id: id as string,

        ...pageInfo.value,
      })

      collectData.value = res.data.result

      surveyList.value = res.data.result.users.user
      if (isNeedPush) {
        tableColums.value.push({
          field: 'result',

          title: '操作',

          slotName: 'result',

          minwidth: '350',
        })
        isNeedPush = false
      }

      total.value = res.data.result.users.total

      return [res]
    }, '获取收集情况')

    console.log(surveyList.value)
  } else {
    keRequest(async () => {
      const res = await getCollect({
        id: id as string,

        ...pageInfo.value,
      })

      collectData.value = res.data.result

      surveyList.value = res.data.result.users.user
      if (isNeedPush) {
        tableColums.value = []

        tableColums.value.push({
          field: 'evaluatedName',

          title: '被评价者',

          otherOptions: {
            showOverflow: 'tooltip',

            sortable: true,
          },
        })

        tableColums.value.push({
          field: 'phone',

          title: '手机号',

          otherOptions: {
            showOverflow: 'tooltip',

            sortable: true,
          },
        })

        tableColums.value.push({
          field: 'score',

          title: '分数',

          otherOptions: {
            showOverflow: 'tooltip',

            sortable: true,
          },
        })

        const item = surveyList.value.sort((a: any, b: any) => {
          return Object.keys(b).length - Object.keys(a).length
        })

        let index = 1

        for (const key in item[0]) {
          if (key.includes('evaluator')) {
            tableColums.value.push({
              field: 'evaluator' + index,

              title: '评价者' + index,

              slotName: 'evaluator',

              minwidth: '400',
            })

            index++
          }
        }
        isNeedPush = false
      }

      console.log(surveyList.value)

      total.value = res.data.result.users.total

      return [res]
    }, '获取收集情况')
  }

  isLoaderActive.value = false
}

const showDialog = ref(false)

const currentAnswer = ref('')

const jsonPreview = ref('')

// 查看答题详情

const handleAnswerPreview = (item: any, jsonpreview: string) => {
  console.log(item)

  currentAnswer.value = item

  jsonPreview.value = jsonpreview

  showDialog.value = true
}

// 处理导出excel

const handleExportExcel = async (
  row: any,

  fileName: string,

  userId: string,

  evaluatedId?: string
) => {
  const res: any = await exportExcel({
    projectId: id as string,

    surveyId: row.surveyId ?? row.id,

    userId: userId,

    evaluatedId: evaluatedId,
  })

  handleFileDownload([res.data], { type: res.data.type }, fileName)
}

const onClose = () => {
  showDialog.value = false
}

watch(currentPage, async (newValue) => {
  //直接监听

  pageInfo.value.pageNum = newValue

  await getData()
})

// const goBack = () => {

//   router.push('/home/all-project')

// }

onMounted(async () => {
  await getData()
})
</script>

<template>
  <div>
    <div v-if="!showDialog" class="collection">
      <!-- <div class="collection-title">
      </div> -->

      <div class="collection-content">
        <div class="collection-center">
          <div class="item-title">数据概况</div>

          <VCard class="card">
            <div class="project-title">{{ projectData?.project.projectName }}</div>

            <!-- <div class="project-desc">项目描述：{{ projectData?.project.content }}</div> -->

            <div class="collect-data">
              <div class="item">
                <div class="collect-title">问卷选择人数</div>

                <div class="collect-content">
                  {{
                    collectData?.selectNumber === 0 ? '不限' : collectData?.selectNumber
                  }}
                </div>
              </div>

              <div class="item">
                <div class="collect-title">已答卷人数</div>

                <div class="collect-content">{{ collectData?.collectNumber }}</div>
              </div>

              <div class="item">
                <div class="collect-title">总完成率</div>

                <div class="collect-content">{{ collectData?.rate }}</div>
              </div>
            </div>
          </VCard>

          <div class="item-title item-title2">
            数据来源

            <Tippy placement="top">
              <span><i class="lnir lnir-question-circle" aria-hidden="true"></i></span>

              <template #content>
                <div class="v-popover-content is-text">
                  360度项目展示的是所有被选择的用户,其他项目为参与答题者。
                </div>
              </template>
            </Tippy>
          </div>

          <VCard class="card">
            <VLoader
              size="large"
              :active="isLoaderActive"
              translucent
              class="list-loader"
            >
              <div
                v-if="projectData?.project.type === '测评' && !!collectData"
                class="answer-users"
              >
                <div v-if="collectData?.userPage.user.length > 0" style="width: 100%">
                  <keTable
                    :data="collectData!.userPage.user"
                    :other-options="otherOptions"
                    :colomns="tableColums"
                    :need-sect-column="false"
                    ><template #survey="{ row, field }">
                      <template v-if="row[field].status === 0"
                        ><span class="unfish">未完成</span></template
                      >

                      <template v-if="row[field].status === 1"
                        ><span class="answering">进行中</span></template
                      >

                      <template v-if="row[field].status === 2">
                        <div class="flexjac">
                          <span class="finish">已完成</span>

                          <div
                            class="infoBtn"
                            @click="
                              handleAnswerPreview(
                                row[field].result,

                                row[field].jsonPreview
                              )
                            "
                          >
                            答题详情
                          </div>

                          <div
                            class="primaryBtn"
                            @click="
                              savePdf(
                                JSON.parse(row[field].result),

                                JSON.parse(row[field].jsonPreview),

                                row.name + '-' + row[field].surName
                              )
                            "
                          >
                            导出为PDF
                          </div>

                          <!-- <div
                            class="excelBtn"
                            @click="
                              handleExportExcel(
                                row[field],

                                row.name + '-' + row[field].surName,

                                row.id
                              )
                            "
                          >
                            导出为Excel
                          </div> -->
                        </div>
                      </template>
                    </template>
                  </keTable>
                </div>

                <div v-else style="width: 100%; padding: 0 1vw">
                  <div style="margin: 30px auto; max-width: 900px; text-align: center">
                    <img :src="noData" alt="" style="width: 20vw; height: 35vh" />

                    <div style="text-align: center; margin-top: 20px">暂无数据</div>
                  </div>
                </div>
              </div>

              <div
                v-else-if="projectData?.project.type === '调查' && !!collectData"
                class="answer-users"
              >
                <div v-if="collectData?.users.user.length > 0" style="width: 100%">
                  <keTable
                    :data="collectData?.users.user"
                    :other-options="otherOptions"
                    :colomns="tableColums"
                    :need-sect-column="false"
                  >
                    <template #result="{ row, field }">
                      <template v-if="row.status === 0"
                        ><span class="unfish">未完成</span></template
                      >

                      <template v-if="row.status === 1"
                        ><span class="answering">进行中</span></template
                      >

                      <template v-if="row.status === 2">
                        <div class="flexjac">
                          <span class="finish">已完成</span>

                          <div
                            class="infoBtn"
                            @click="
                              handleAnswerPreview(
                                row.result,

                                row.jsonPreview
                              )
                            "
                          >
                            答题详情
                          </div>

                          <div
                            class="primaryBtn"
                            @click="
                              savePdf(
                                JSON.parse(row.result),

                                JSON.parse(row.jsonPreview),

                                row.name + '-' + row.surName
                              )
                            "
                          >
                            导出为PDF
                          </div>

                          <!-- <div
                            class="excelBtn"
                            @click="
                              handleExportExcel(
                                row[field],

                                row.name + '-' + row[field].surName,

                                row.id
                              )
                            "
                          >
                            导出为Excel
                          </div> -->
                        </div>
                      </template>
                    </template>
                    <!-- <template #result="{ row }">
                      <div
                        style="
                          display: flex;

                          align-items: center;

                          justify-content: center;
                        "
                      >
                        <div
                          class="infoBtn"
                          @click="handleAnswerPreview(row.result, row.jsonPreview)"
                        >
                          查看答题详情
                        </div>

                        <div
                          class="primaryBtn"
                          @click="
                            savePdf(
                              JSON.parse(row.result),

                              JSON.parse(row.jsonPreview),

                              row.name + '-' + row.surName
                            )
                          "
                        >
                          导出为PDF
                        </div>

                        <div
                          class="excelBtn"
                          @click="
                            handleExportExcel(
                              collectData,

                              row.name + '-' + row.surName,

                              row.id
                            )
                          "
                        >
                          导出为Excel
                        </div>
                      </div>
                    </template> -->
                  </keTable>
                </div>

                <div v-else style="width: 100%">
                  <div style="margin: 30px auto; max-width: 900px; text-align: center">
                    <img :src="noData" alt="" style="width: 20vw; height: 35vh" />

                    <div style="text-align: center; margin-top: 20px">暂无数据</div>
                  </div>
                </div>
              </div>

              <div v-else-if="projectData?.project.type === '360度评估' && !!collectData">
                <div
                  v-if="collectData?.users.user.length > 0"
                  style="width: 100%; padding: 0 1vw"
                >
                  <keTable
                    :data="collectData?.users.user"
                    :other-options="otherOptions"
                    :colomns="tableColums"
                    :need-sect-column="false"
                  >
                    <template #evaluator="{ row, field }">
                      <div class="evaluator-info-item">
                        <span v-if="!!row[field]?.name">
                          姓名: {{ row[field]?.name }}</span
                        >

                        <span v-if="row[field]?.level == 0">级别关系: 自评</span>

                        <span v-else-if="row[field]?.level == 1">级别关系: 上级</span>

                        <span v-else-if="row[field]?.level == 2">级别关系: 同级</span
                        ><span v-else-if="row[field]?.level == 3">级别关系: 下级</span>

                        <span v-else-if="row[field]?.level == 4">级别关系: 其他</span>
                      </div>

                      <div class="evaluator-info-item">
                        <span v-if="!!row[field]?.phone">
                          电话: {{ row[field]?.phone }}</span
                        >
                      </div>

                      <!-- <div class="evaluator-info-item"></div> -->

                      <div class="evaluator-info-item evaluator-info-last-item">
                        <template v-if="row[field]?.status === 0"
                          ><span class="unfish">未完成</span></template
                        >

                        <template v-if="row[field]?.status === 1"
                          ><span class="answering">进行中</span></template
                        >

                        <template v-if="row[field]?.status === 2">
                          <div class="flexjac">
                            <span class="finish">已完成</span>

                            <div
                              class="infoBtn"
                              @click="
                                handleAnswerPreview(row[field].result, row.jsonPreview)
                              "
                            >
                              答题详情
                            </div>

                            <div
                              class="primaryBtn"
                              @click="
                                savePdf(
                                  JSON.parse(row[field].result),

                                  JSON.parse(row.jsonPreview),

                                  row[field].name + '-' + row.surName
                                )
                              "
                            >
                              导出为PDF
                            </div>

                            <!-- <div
                              class="excelBtn"
                              @click="
                                handleExportExcel(
                                  collectData,

                                  row[field].name + '-' + row.surName,

                                  row[field].id,

                                  row.id
                                )
                              "
                            >
                              导出为Excel
                            </div> -->
                          </div>
                        </template>
                      </div>
                    </template>
                  </keTable>
                </div>

                <div v-else style="width: 100%">
                  <div style="margin: 30px auto; max-width: 900px; text-align: center">
                    <img :src="noData" alt="" style="width: 20vw; height: 35vh" />

                    <div style="text-align: center; margin-top: 20px">暂无数据</div>
                  </div>
                </div>
              </div>

              <div class="foot">
                <VFlexPagination
                  v-model:current-page="currentPage"
                  :item-per-page="pageInfo.pageSize"
                  :total-items="total"
                  no-router
                  :max-links-displayed="5"
                />
              </div>
            </VLoader>
          </VCard>
        </div>
      </div>
    </div>

    <AuDialog v-if="showDialog" title="答题详情" :footer="false" @close="onClose">
      <AnswerPreview :answer="currentAnswer" :survey-json="jsonPreview"></AnswerPreview>
    </AuDialog>
  </div>
</template>

<style lang="scss" scoped>
.flexjac {
  display: flex;

  align-items: center;

  justify-content: center;
}

.collection {
  background-color: var(--keDarkBg);

  .collection-title {
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

  .collection-content {
    height: calc(100vh - 80px);

    overflow-y: scroll;

    padding-left: 20px;

    .evaluator-info-item {
      margin-bottom: 8px;

      display: flex;

      justify-content: space-between;

      padding: 0 20px;
    }

    .evaluator-info-last-item {
      margin-bottom: 0;
    }

    .collection-center {
      max-width: calc(60vw + 100px);

      margin: 0 auto;
    }

    .item-title {
      position: relative;

      margin: 10px 0 10px 0;

      color: #484848;

      height: 30px;

      line-height: 30px;

      font-size: 16px;

      font-weight: 400;

      padding-left: 10px;

      color: var(--keWhiteText);

      span {
        vertical-align: middle;

        i {
          margin-left: 4px;

          font-size: 18px;

          color: var(--keGreyText);

          cursor: pointer;
        }
      }

      &::before {
        content: '';

        position: absolute;

        left: 0;

        width: 4px;

        border-radius: 2px;

        margin-top: 5px;

        height: 20px;

        background-color: var(--info);
      }
    }
    .item-title2 {
      margin-top: 20px;
    }

    .card {
      text-align: center;

      padding: 20px 0;

      .unfish {
        padding: 5px 15px;

        border-radius: 2px;

        color: #ba3636;

        background-color: #d49a9a;
      }

      .finish {
        padding: 5px 15px;

        border-radius: 2px;

        color: #3db03d;

        background-color: #c2dfc2;
      }

      .answering {
        padding: 5px 15px;

        border-radius: 2px;

        color: #888823;

        background-color: #e7e7d6;
      }

      .infoBtn {
        padding: 5px 15px;

        border-radius: 2px;

        background-color: var(--info);

        color: white;

        margin-left: 10px;

        cursor: pointer;
      }

      .primaryBtn {
        padding: 5px 15px;

        border-radius: 2px;

        background-color: var(--primary);

        color: white;

        margin-left: 10px;

        cursor: pointer;
      }

      .excelBtn {
        padding: 5px 15px;

        border-radius: 2px;

        background-color: #148147;

        color: white;

        margin-left: 10px;

        cursor: pointer;
      }

      .project-title {
        font-size: 18px;

        font-weight: 700;
      }

      .project-desc {
        color: var(--keGreyText);

        padding: 10px 0 20px 0;

        border-bottom: 1px solid var(--border);
      }

      .collect-data {
        display: flex;

        justify-content: space-between;

        padding: 40px 20px 20px;

        .item {
          display: flex;

          height: 44px;

          width: 100%;

          flex-direction: column;

          justify-content: center;

          align-items: center;

          .collect-title {
            color: var(--keGreyText);

            font-size: 16px;

            line-height: 24px;
          }

          .collect-content {
            color: #484848;

            font-size: 20px;

            font-family: PingFangSC-Medium;

            line-height: 28px;

            color: var(--keWhiteText);

            margin: 2px 0 0;
          }
        }

        .item:not(:last-child) {
          border-right: 1px dashed var(--border);
        }
      }

      .answer-users {
        padding: 0 2vw;

        display: flex;

        flex-wrap: wrap;

        .answer-user {
          position: relative;

          width: 200px;

          height: 130px;

          margin: 20px;

          box-shadow: -2px 4px 5px 0 rgb(0 0 0 / 6%);

          border: 1px solid var(--border);

          border-radius: 4px;

          cursor: pointer;

          box-sizing: border-box;

          font-size: 13px;

          padding: 10px;

          transition: transform 0.5s ease;

          text-align: left;

          &:hover {
            transform: translateY(-10px);
          }

          .row1 {
            display: flex;

            justify-content: space-between;

            align-items: center;

            font-weight: 700;

            font-size: 15px;
          }

          .row2 {
            padding: 10px 0 20px 0;

            color: var(--keGreyText);
          }

          .btn {
            position: absolute;

            left: 10px;

            bottom: 10px;
          }
        }
      }

      .foot {
        padding: 0 20px;

        :deep(.is-current) {
          background-color: var(--info) !important;

          border-color: var(--info) !important;
        }

        :deep(.flex-pagination) {
          padding: 12px 0 0 0 !important;
        }
        :deep(svg) {
          color: var(--info) !important;
        }
      }
    }
  }
}
</style>
