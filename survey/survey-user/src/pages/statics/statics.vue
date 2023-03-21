<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-08 20:57:22
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-11 14:42:29
 * @Description: file content
-->
<script lang="ts" setup>
import { keRequest } from '/@src/utils/keRequest'
import { getStatistics } from '/@src/api/projectList'
import { getCAnalylist, getSurAnalytics } from '/@src/api/analytics'
import { getQuestionType } from '/@src/constant/survey'
import noData from '/@src/assets/noData.png'
export interface PropsType {
  id: string
  type: string
}

type choiceListItem = {
  proportion: string
  subtotal: number
  option: string
}
const props = withDefaults(defineProps<PropsType>(), {
  id: '0',
  type: 's',
})
// const router = useRouter()
// const goBack = () => {
//   router.back()
// }
const currentSurIndex = ref(0)
const projectName = ref('')
const projectType = ref('')
const data = ref<any[]>([])
const empty = ref('')
const surveyList = ref<any[]>([])
const getData = async () => {
  if (props.type !== '测评') {
    keRequest(async () => {
      const res = await getStatistics(props.id)
      data.value = res.data.result.data.map((item: any) => {
        return {
          ...item,
          upActive: false,
          downActive: false,
        }
      })
      projectName.value = res.data.result.projectName
      projectType.value = res.data.result.type
      if (data.value.length <= 0) {
        empty.value = '暂无数据分析'
      }
      return [res]
    }, '获取统计数据')
  } else {
    keRequest(async () => {
      const res = await getCAnalylist(props.id as any)
      surveyList.value = res.data.result.surveyList
      projectName.value = res.data.result.projectName
      projectType.value = res.data.result.type
      const res2 = await getSurAnalytics({
        projectId: props.id as any,
        surveyId: surveyList.value[0].id,
      })
      data.value =
        res2.data.result.length > 0
          ? res2.data.result.map((item: any) => {
              return {
                ...item,
                upActive: false,
                downActive: false,
              }
            })
          : []
      if (data.value.length <= 0) {
        empty.value = '暂无数据分析'
      }
      return [res, res2]
    }, '获取统计数据')
  }
}
// const upActive = ref(false)
// const downActive = ref(false)
let defautlChoiceList: choiceListItem[] = []
let isFirst = false
const upBeActive = (index: number) => {
  if (!isFirst) {
    defautlChoiceList = [...data.value[index].choiceList]
    isFirst = true
  }

  data.value[index].upActive = !data.value[index].upActive
  if (data.value[index].upActive) {
    data.value[index].choiceList.sort((a: choiceListItem, b: choiceListItem) => {
      return a.subtotal - b.subtotal
    })
  } else {
    data.value[index].choiceList = [...defautlChoiceList]
  }

  data.value[index].downActive = false
}
const changeSurvey = (id: string, index: number) => {
  if (currentSurIndex.value === index) {
    return
  }
  currentSurIndex.value = index
  keRequest(async () => {
    const res2 = await getSurAnalytics({
      projectId: props.id as any,
      surveyId: id,
    })
    data.value =
      res2.data.result.length > 0
        ? res2.data.result.map((item: any) => {
            return {
              ...item,
              upActive: false,
              downActive: false,
            }
          })
        : []
    if (data.value.length <= 0) {
      empty.value = '暂无数据分析'
    }
    return [res2]
  }, '获取统计数据')
}
const downBeActive = (index: number) => {
  if (!isFirst) {
    defautlChoiceList = [...data.value[index].choiceList]
    isFirst = true
  }
  data.value[index].downActive = !data.value[index].downActive
  if (data.value[index].downActive) {
    data.value[index].choiceList.sort((a: choiceListItem, b: choiceListItem) => {
      return b.subtotal - a.subtotal
    })
  } else {
    data.value[index].choiceList = [...defautlChoiceList]
  }

  data.value[index].upActive = false
}
onMounted(async () => {
  await getData()
})
</script>

<template>
  <div class="statics">
    <!-- <div class="statics-title">
      <div>问卷数据分析</div>
      <div><i class="lnir lnir-close" aria-hidden="true" @click="goBack()"></i></div>
    </div> -->
    <div class="statics-content">
      <div class="project-name">{{ projectName }}</div>
      <template v-if="projectType === '测评'">
        <div class="content-center">
          <div style="margin: 15px 0">问卷列表</div>
          <span
            v-for="(item, index) in surveyList"
            :key="item.id"
            :class="['survey-item', currentSurIndex === index ? 'active' : '']"
            @click="changeSurvey(item.id, index)"
            >{{ item.surName }}</span
          >
        </div>
      </template>

      <template v-if="data.length > 0">
        <div class="content-center">
          <div v-for="(item, index) in data" :key="item.id">
            <div class="table-title">
              {{ item.content }}
              <span class="title-type">[ {{ getQuestionType(item.type) }} ]</span>
            </div>
            <div v-if="projectType !== '调查'" class="average">
              本题平均分: {{ item.average }}分
            </div>
            <table>
              <!--表头-->
              <thead>
                <tr>
                  <th>选项</th>
                  <th>
                    <div Class="sort">
                      <div>小计</div>
                      <div class="sort-control">
                        <div
                          :class="['sort-up', item.upActive ? 'up-active' : '']"
                          @click="upBeActive(index)"
                        ></div>
                        <div
                          :class="['sort-down', item.downActive ? 'down-active' : '']"
                          @click="downBeActive(index)"
                        ></div>
                      </div>
                    </div>
                  </th>
                  <th>比例</th>
                </tr>
              </thead>
              <!--表内容-->
              <tbody>
                <tr
                  v-for="(item2, index2) in item.choiceList"
                  :key="index2"
                  class="body-content"
                >
                  <td>
                    <Tippy>
                      <div class="text-ellipsis">{{ item2.option }}</div>
                      <template #content>{{ item2.option }}</template>
                    </Tippy>
                  </td>
                  <td>{{ item2.subtotal }}</td>
                  <td class="progress-content">
                    <div class="ke-progress">
                      <div
                        class="ke-progress-color"
                        :style="{
                          width:
                            item2.proportion === 0
                              ? 0
                              : Number(item2.proportion?.split('.')[0]) * 0.12 + 'vw',
                        }"
                      ></div>
                    </div>
                    <!-- <VProgress
                      style="max-width: 250px; margin-top: 7px"
                      size="small"
                      color="info"
                      :value=""
                    /> -->
                    <span>{{ item2.proportion }}</span>
                  </td>
                </tr>
                <tr>
                  <td colspan="3" class="foot">
                    本次有效填写人数：{{ item.userNumber }}人
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
      <template v-else>
        <div style="margin: 30px auto; max-width: 1100px; text-align: center">
          <template v-if="empty !== ''">
            <img :src="noData" alt="" style="width: 35vw; height: 50vh" />
          </template>

          <div style="text-align: center; margin-top: 20px">{{ empty }}</div>
        </div>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@media (max-width: 600px) {
  .text-ellipsis {
    max-width: 80px !important;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}
.text-ellipsis {
  max-width: 200px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.statics {
  background-color: var(--keDarkBg);
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
  .statics-title {
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

  .statics-content {
    height: calc(100vh - 80px);
    overflow-y: scroll;
    .project-name {
      text-align: center;
      font-size: 24px;
      margin-top: 30px;
      font-weight: 700;
    }
    .content-center {
      margin: 0 auto;
      padding: 20px 2vw 40px 2vw;
      max-width: 1100px;
      .table-title {
        font-size: 18px;
        font-weight: 700;
        margin: 30px 0 15px 0;
        .title-type {
          font-weight: 400;
          color: var(--keGreyText);
        }
      }
      .sort {
        display: flex;
        align-items: center;
        justify-content: center;
        .sort-control {
          margin-left: 6px;
          .sort-up {
            width: 0;
            height: 0;
            border: 6px solid transparent;
            border-bottom-color: #7c7c7c;
            cursor: pointer;
          }
          .sort-down {
            width: 0;
            height: 0;
            margin-top: 4px;
            border: 6px solid transparent;
            border-top-color: #7c7c7c;
            cursor: pointer;
          }
          .up-active {
            border-bottom-color: #0084ff;
          }
          .down-active {
            border-top-color: #0084ff;
          }
        }
      }
      .average {
        font-size: 16px;
        color: var(--keGreyText);
        margin: 15px 0;
      }
      table {
        width: 100%;
        text-align: center;

        border-spacing: 0; /**设置相邻单元格的边框间的距离**/
        border-collapse: collapse; /**边框会合并为一个单一的边框**/
        table-layout: fixed; /**固定table表格**/
        td,
        th {
          font-size: 15px;
          padding: 10px;
        }
        .ke-progress {
          position: relative;
          display: inline-block;
          width: 12vw;
          height: 10px;
          border-radius: 5px;
          background-color: #f5f5f5;
          .ke-progress-color {
            position: absolute;
            left: 0;
            height: 10px;
            border-radius: 5px;
            display: inline-block;
            background-color: #0084ff;
          }
        }
        span:not(.text-ellipsis) {
          display: inline-block;
          padding-left: 1em;
          text-align: left;
          // width: 5em;
        }
      }
      table thead {
        background: var(--keTitleBg);

        font-weight: 700;
        th {
          vertical-align: middle;
          color: var(--keWhiteText);
        }
      }
      table td,
      table th {
        border: 1px solid var(--border);
        // overflow-x: hidden; /**溢出隐藏**/
        // white-space: nowrap; /**不换行**/
        text-overflow: ellipsis; /**溢出不可见部分使用...代替**/
      }
      .body-content {
        cursor: pointer;

        &:hover {
          background-color: var(--keHoverColor);
        }
      }
      .foot {
        padding-left: 20px;
        background: var(--keTitleBg);
        font-weight: 700;
        text-align: left;
      }
    }
  }
}
</style>
