<!--
 * @Author: August-Rushme
 * @Date: 2022-09-22 14:10:33
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2023-01-04 14:44:45
 * @FilePath: \survey-user\src\pages\home\all-project.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'
import { useWizard } from '/@src/stores/wizard'
import { keRequest } from '/@src/utils/keRequest'
import type { WizardType } from '/@src/models/wizard'
import {
  getProjectList,
  deleteProject,
  changeProject,
  copyProject,
} from '/@src/api/projectList'
import { debounce } from '/@src/utils/debounce'
import { useUserSession } from '/@src/stores/userSession'
import { filterConfig } from './config/all-project'
import { PaperListRecord } from '/@src/api/projectList/type'
import ProjectCode from './sidebar/all-project/project-code.vue'
import { Notice } from '/@src/components/base/au-notice/Notice'
import colletionCmp from '/@src/pages/statics/collection.vue'
import dataRecordCmp from '/@src/pages/statics/data-record.vue'
import evaluationReportCmp from '/@src/pages/statics/evaluation-report.vue'
import staticsCmp from '/@src/pages/statics/statics.vue'
const wizard = useWizard()
const session = useUserSession()
const isLoaderActive = ref(false)
const router = useRouter()
const collectionModal = ref(false)
const dataRecordModal = ref(false)
const evaluationModal = ref(false)
const staticsModal = ref(false)
const collectionId = ref('0')
const staticsType = ref('')
const dataRecordId = ref('0')
const evaluationId = ref('0')
const staticsId = ref('0')
const pageInfo = ref({
  pageNum: 1,
  pageSize: 5,
})
const filterObj = reactive({
  type: '',
  order: true,
  name: '',
}) as any
const filter = JSON.parse(JSON.stringify(filterConfig))
const currentPage = ref(1)
const deleteString = ref('')
const centeredActionsOpen = ref(false)
let currentPaperName = ''
const publishModel = ref(false)
const cancleModel = ref(false)
let paperId = ''
const copyModel = ref(false)
const copyType = ref('my')
const copyNanme = ref('')
const otherID = ref('')
let data = ref<PaperListRecord[]>([])
let total = ref(0)
// const collectNum = ref(0)
const tip = ref('')
// 监听分页切换
watch(currentPage, async (newValue) => {
  //直接监听
  pageInfo.value.pageNum = newValue
  await getData()
})
// 监听搜索框值的变化
watch(
  () => filterObj.name,
  () => {
    debounceGetData()
  }
)
// 问卷
// 获取当前过滤状态
const getCurrentFilter = () => {
  filter.forEach((item: any) => {
    const activeItem = (item.items as any).find((item2: any) => {
      return item2.active
    })
    filterObj[item.key] = activeItem.value
  })
}
// 过滤状态改变
const changeState = async (index: number, index2: number) => {
  filter[index].items.forEach((item: any) => {
    item.active = false
  })
  filter[index].items[index2].active = true
  getCurrentFilter()
  becomePageOne()
  await getData()
}
// 获取问卷数据
const getData = async () => {
  isLoaderActive.value = true
  const res = await getProjectList({
    id: session.tenantId,
    ...pageInfo.value,
    ...filterObj,
  })
  data.value = res.data.result.records
  total.value = res.data.result.total
  isLoaderActive.value = false
  return res
}
// 防抖搜索
const debounceGetData = debounce(async () => {
  await getData()
}, 500)
const editPaper = (id: string, isPublish: boolean) => {
  if (isPublish) {
    return Notice({ notice_type: 'error', message: '该项目已发布，不能进行修改' })
  }
  localStorage.setItem('isEdit', 'true')
  router.push(`/wizard-v1/project-info?id=${id}`)
}
const deletePaper = () => {
  centeredActionsOpen.value = false
  keRequest(async () => {
    const res = await deleteProject(deleteString.value)
    if (data.value.length <= 1 && currentPage.value > 1) {
      pageInfo.value.pageNum--
      currentPage.value--
    }
    const res2 = await getData()
    return [res, res2]
  }, '删除')
}
// 分页变为第一页
const becomePageOne = () => {
  pageInfo.value.pageNum = 1
  currentPage.value = 1
}
const confirmPublish = () => {
  publishModel.value = false
  keRequest(async () => {
    const res = await changeProject({ id: paperId, isPublish: true })
    becomePageOne()
    const res2 = await getData()
    return [res, res2]
  }, '发布')
}
const confirmCanclePublish = () => {
  cancleModel.value = false
  keRequest(async () => {
    const res = await changeProject({ id: paperId, isPublish: false })
    becomePageOne()
    const res2 = await getData()
    return [res, res2]
  }, '取消发布')
}
// 发布问卷
const handlePulishProject = (id: string) => {
  paperId = id
  publishModel.value = true
}
const canclePublishProject = (row: any) => {
  paperId = row.id
  tip.value = `您的问卷已收集${row.collectNumber}份数据，将会暂停收集,进行项目内容修改时会同步删除相关数据，不可恢复，请慎重操作。
  您确认要取消发布此问卷吗?`
  console.log(row)
  cancleModel.value = true
}
// 删除确认弹窗
const openModel = (id: string) => {
  centeredActionsOpen.value = true
  deleteString.value = id
}
// 前往设置页
const settingPaper = (id: string, type: WizardType, isPublish: boolean) => {
  if (isPublish) {
    return Notice({ notice_type: 'error', message: '该项目已发布，不能进行修改' })
  }
  wizard.data.type = type
  localStorage.setItem('isEdit', 'true')
  router.push(`/wizard-v1/project-setting?id=${id}`)
}
const viewPaper = (id: string) => {
  localStorage.setItem('isView', 'view')
  router.push(`/wizard-v1/project-info?id=${id}`)
}
// copy
const copyPaper = (id: string, name: string) => {
  copyModel.value = true
  paperId = id
  currentPaperName = name
}
const useCopyName = () => {
  copyNanme.value = currentPaperName
}
// 复制到自己的账户下
const copyMyProject = () => {
  copyModel.value = false
  keRequest(async () => {
    const res = await copyProject({
      id: paperId,
      projectName: copyNanme.value,
      tenantId: localStorage.getItem('tenantId') as string,
    })
    const res2 = await getData()
    return [res, res2]
  }, '复制项目')
  copyNanme.value = ''
}
// 分析与统计
const goStatics = (id: string, type: string) => {
  staticsId.value = id
  staticsType.value = type
  staticsModal.value = true
}
// 去往收集情况页面
const goCollection = (id: string) => {
  collectionId.value = id
  collectionModal.value = true
}

// 数据大屏页面
const goDataScreen = (id: string) => {
  dataRecordId.value = id
  dataRecordModal.value = true
}
// 测评报告
const goEvaluationReport = (id: string) => {
  evaluationId.value = id
  evaluationModal.value = true
}
// 二维码链接相关
const surveyLink = ref('')
const projectCodeModel = ref(false)
const baseURL = import.meta.env.VITE_WEB_BASE_URL
const showCode = (item: any) => {
  if (item.isPublish) {
    projectCodeModel.value = true
    surveyLink.value = baseURL + `/survey/survey-library?id=${item.id}`
  } else {
    return Notice({ notice_type: 'error', message: '项目暂未发布' })
  }
}
// const resetFilter = () => {
//   filter.forEach(() => {})
// }
onMounted(async () => {
  localStorage.removeItem('isView')
  await getData()
})
useHead({
  title: '云测 - 项目列表',
})
</script>

<template>
  <VLoader size="large" :active="isLoaderActive" translucent class="list-loader">
    <div class="page-content-inner">
      <div class="control">
        <div class="c-title">项目列表</div>
        <div style="display: flex; flex-wrap: wrap">
          <template v-for="(item, index) in filter" :key="index">
            <VDropdown :title="item.name" style="margin-right: 1.2rem" left>
              <template #content>
                <template v-for="(item2, index2) in item.items" :key="index2">
                  <a
                    style="
                      font-family: Helvetica Neue, PingFang SC, Hiragino Sans GB, HeitiSC,
                        Helvetica, Arial, Microsoft YaHei, WenQuanYi Micro Hei, sans-serif;
                      font-size: 12px;
                      color: #707070;
                      font-style: normal;
                      font-weight: normal;
                      outline: none;
                    "
                    href="#"
                    :class="['dropdown-item', item2.active ? 'is-active' : '']"
                    @click="changeState(index, index2)"
                    >{{ item2.name }}
                  </a>
                </template>
              </template>
            </VDropdown>
          </template>
          <VField class="search-control">
            <VControl icon="lnir lnir-search">
              <VInput
                v-model="filterObj.name"
                type="text"
                placeholder="请输入项目名称进行搜索"
              />
            </VControl>
          </VField>
        </div>
      </div>

      <div class="card-content">
        <VCard v-if="data.length == 0">
          <div class="empty">
            <img src="/@src/assets/project/empty.svg" alt="" />
            <div class="desc">目前没有项目</div>
          </div>
        </VCard>
        <div v-else>
          <VCard v-for="(item, index) in data" :key="index" class="paper">
            <div class="project-title">
              <div class="title-name">
                {{ item.projectName }}
              </div>
              <div>
                <span>ID: {{ item.id }}</span>
                <span>
                  <i
                    class="fas fa-circle"
                    aria-hidden="true"
                    :style="{ color: item.isPublish ? '#41B983' : '#bfbfbf' }"
                  ></i>
                  {{ item.isPublish ? '已发布' : '未发布' }}</span
                >
                <span>项目类型: {{ item.type }}</span>
                <span>答卷：{{ item.collectNumber }}</span>
                <span>创建时间：{{ item.createTime }}</span>
              </div>
            </div>
            <div class="project-fc">
              <div>
                <VDropdown class="item design-paper">
                  <template #button="{ toggle }">
                    <VButton
                      icon="fas fa-file-signature"
                      class="is-trigger"
                      @click="toggle"
                    >
                      <span>
                        查看问卷 <i class="lnir lnir-chevron-down" aria-hidden="true"></i>
                      </span>
                    </VButton>
                  </template>

                  <template #content="{ close }">
                    <div @mouseleave="close">
                      <template v-if="!item.isPublish">
                        <a
                          class="dropdown-item"
                          @click="editPaper(item.id, item.isPublish)"
                        >
                          编辑问卷
                        </a>
                        <a
                          class="dropdown-item"
                          @click="settingPaper(item.id, item.type, item.isPublish)"
                        >
                          问卷设置
                        </a>
                      </template>
                      <template v-else>
                        <a class="dropdown-item" @click="viewPaper(item.id)">
                          查看问卷
                        </a>
                      </template>
                    </div>
                  </template>
                </VDropdown>
                <VDropdown class="item share">
                  <template #button="{ toggle }">
                    <VButton icon="fas fas fa-share" class="is-trigger" @click="toggle">
                      <span>
                        发送问卷 <i class="lnir lnir-chevron-down" aria-hidden="true"></i>
                      </span>
                    </VButton>
                  </template>
                  <template #content="{ close }">
                    <div @mouseleave="close">
                      <a class="dropdown-item" @click="showCode(item)"> 链接 & 二维码 </a>
                    </div>
                  </template>
                </VDropdown>
                <VDropdown class="item download">
                  <template #button="{ toggle }">
                    <VButton icon="fas fa-chart-bar" class="is-trigger" @click="toggle">
                      <span>
                        分析&下载
                        <i class="lnir lnir-chevron-down" aria-hidden="true"></i>
                      </span>
                    </VButton>
                  </template>
                  <template #content="{ close }">
                    <div @mouseleave="close">
                      <a
                        class="dropdown-item"
                        @click="goStatics(item.id, item.type as string)"
                      >
                        统计 & 分析
                      </a>
                      <a class="dropdown-item" @click="goCollection(item.id)">
                        收集情况
                      </a>
                      <a class="dropdown-item" @click="goDataScreen(item.id)">
                        数据大屏
                      </a>
                      <a
                        v-if="item.type === '测评'"
                        class="dropdown-item"
                        @click="goEvaluationReport(item.id)"
                      >
                        测评报告
                      </a>
                    </div>
                  </template>
                </VDropdown>
              </div>
              <div class="right">
                <span v-if="!item.isPublish" @click="handlePulishProject(item.id)">
                  <i
                    class="lnir lnir-play"
                    style="color: var(--primary); font-size: 18px; padding-right: 5px"
                    aria-hidden="true"
                  ></i
                  >发布</span
                >
                <span v-else @click="canclePublishProject(item)">
                  <i
                    style="color: var(--primary); font-size: 18px; padding-right: 5px"
                    class="lnir lnir-circle-minus"
                    aria-hidden="true"
                  ></i>
                  取消发布
                </span>
                <!-- <span>
                  <i
                    class="fas fa-eye"
                    style="color: var(--primary); font-size: 18px; padding-right: 5px"
                    aria-hidden="true"
                  ></i>
                  查看详情
                </span> -->
                <span @click="copyPaper(item.id, item.projectName)"
                  ><i
                    class="fas fa-copy"
                    style="color: #41b983; padding-right: 5px"
                    aria-hidden="true"
                  ></i
                  >复制</span
                >
                <span @click="openModel(item.id)"
                  ><i
                    class="fas fa-trash-alt"
                    style="color: #f86538; padding-right: 5px"
                    aria-hidden="true"
                  ></i
                  >删除</span
                >
              </div>
            </div>
          </VCard>
          <VFlexPagination
            v-model:current-page="currentPage"
            :item-per-page="pageInfo.pageSize"
            :total-items="total"
            :max-links-displayed="5"
            no-router
          />
        </div>
      </div>

      <VModal
        :open="cancleModel"
        actions="center"
        title="提示"
        @close="cancleModel = false"
      >
        <template #content>
          <VPlaceholderSection :title="tip" />
        </template>
        <template #action>
          <VButton color="primary" raised @click="confirmCanclePublish()"> 确定</VButton>
        </template>
      </VModal>
      <VModal
        :open="publishModel"
        actions="center"
        title="提示"
        @close="publishModel = false"
      >
        <template #content>
          <VPlaceholderSection title="您确认要发布此问卷吗?" />
        </template>
        <template #action>
          <VButton color="primary" raised @click="confirmPublish()"> 确定</VButton>
        </template>
      </VModal>
      <VModal
        :open="centeredActionsOpen"
        actions="center"
        title="提示"
        @close="centeredActionsOpen = false"
      >
        <template #content>
          <VPlaceholderSection
            title="您确认要删除此问卷吗?(删除的项目将会被移入回收站)"
          />
        </template>
        <template #action>
          <VButton color="primary" raised @click="deletePaper()"> 确定</VButton>
        </template>
      </VModal>
      <VModal
        :open="copyModel"
        actions="center"
        title="复制项目"
        @close="copyModel = false"
      >
        <template #content>
          <div class="copy-content-row">
            <span> 复制到：</span>
            <VField>
              <VControl>
                <VRadio
                  v-model="copyType"
                  value="my"
                  label="自己的账户"
                  name="outlined_radio"
                  color="primary"
                />

                <VRadio
                  v-model="copyType"
                  value="other"
                  label="其他账户"
                  name="outlined_radio"
                  color="primary"
                />
              </VControl>
            </VField>
          </div>
          <div v-if="copyType === 'my'" class="copy-content-row">
            <span>项目标题:</span>
            <div class="content-right">
              <div>
                <VControl>
                  <VInput
                    v-model="copyNanme"
                    type="text"
                    style="width: 100%"
                    placeholder="请输入项目名称"
                  />
                </VControl>
                <a @click="useCopyName"> <span> 使用原项目名称</span></a>
              </div>
            </div>
          </div>
          <div v-else-if="copyType === 'other'">
            <div class="copy-content-row">
              <span>对方账户:</span>
              <div class="content-right">
                <div>
                  <VControl>
                    <VInput
                      v-model="otherID"
                      type="text"
                      style="width: 100%"
                      placeholder="账户ID"
                    />
                  </VControl>
                  <VButton c olor="primary" class="btn">确定</VButton>
                </div>
              </div>
            </div>
            <div class="content-tip">
              <p>
                1、确定后系统将向对方发送一条系统消息，对方在系统消息中确认即可完成复制。
              </p>
              <p>
                2、请确保输入的对方账户ID准确无误，账户ID可在「用户信息」页面查询；<a
                  >如何获取账号ID</a
                >
              </p>
              <p>3、复制的链接的有效期为24小时，过期后也无法复制。</p>
            </div>
          </div>
        </template>
        <template #action>
          <VButton v-if="copyType === 'my'" color="primary" raised @click="copyMyProject">
            确定</VButton
          >
        </template>
      </VModal>

      <!-- 二维码相关 -->
      <VModal
        :open="projectCodeModel"
        actions="center"
        title="二维码"
        @close="projectCodeModel = false"
      >
        <template #content>
          <ProjectCode :size="300" :value="surveyLink"></ProjectCode>
        </template>
        <!-- <template #action>
          <VButton color="primary" raised @click="confirmPublish()"> 确定</VButton>
        </template> -->
      </VModal>
      <AuDialog
        v-if="staticsModal"
        title="统计分析"
        :footer="false"
        @close="staticsModal = false"
      >
        <staticsCmp :id="staticsId" :type="staticsType"></staticsCmp>
      </AuDialog>
      <AuDialog
        v-if="collectionModal"
        title="收集情况"
        :footer="false"
        @close="collectionModal = false"
      >
        <colletionCmp :id="collectionId"></colletionCmp>
      </AuDialog>
      <AuDialog
        v-if="dataRecordModal"
        title="数据大屏"
        :footer="false"
        @close="dataRecordModal = false"
      >
        <dataRecordCmp :id="dataRecordId"></dataRecordCmp>
      </AuDialog>
      <AuDialog
        v-if="evaluationModal"
        title="测评报告"
        :footer="false"
        @close="evaluationModal = false"
      >
        <evaluationReportCmp :id="evaluationId"></evaluationReportCmp>
      </AuDialog>
    </div>
  </VLoader>
</template>
<style lang="scss" scoped>
@media (max-width: 1100px) {
  .list-loader {
    .page-content-inner {
      .control {
        display: block;
        .c-title {
          margin-bottom: 10px;
        }
      }
      .card-content {
        .paper {
          .project-title {
            display: block;
            .title-name {
              margin-bottom: 10px;
            }
          }
        }
      }
    }
  }
}
@media (max-width: 600px) {
  .search-control {
    margin-top: 10px;
  }
}
.page-content-inner {
  .control {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .c-title {
      width: 7em;
      font-size: 1.5em;
    }
  }
  .card-content {
    margin-top: 1.125rem;
    padding: 0;
    .empty {
      text-align: center;
      padding: 6.25rem 0;
      .desc {
        margin-top: 1.125rem;
        font-size: 1.375rem;
        color: #5b5858;
      }
    }
    .paper {
      margin-bottom: 20px;
    }
    .project-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      padding-bottom: 20px;
      border-bottom: 1px solid var(--border);
      .title-name {
        max-width: 600px;
      }
      span {
        padding-right: 15px;
      }
    }
    .project-fc {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 20px 0 0 -15px;
      .design-paper {
        :deep(.fas) {
          color: #4b97ce;
        }
      }
      .share {
        :deep(.fas) {
          color: #f59917;
        }
      }
      .download {
        :deep(.fas) {
          color: #627ce5;
        }
      }

      .is-trigger {
        border: none;
        background: none !important;
      }

      .right {
        display: flex;
        align-items: center;
        span {
          display: flex;
          align-items: center;
          margin-right: 15px;
          cursor: pointer;
        }
      }
    }
  }
}
.copy-content-row {
  display: flex;
  align-items: center;
  .content-right {
    flex: 1;
    div {
      display: flex;
      align-items: center;
      width: 100%;
      padding-left: 0.5em;
      a {
        width: 11em;
        padding-left: 0.5em;
      }
      .btn {
        margin-left: 0.5em;
      }
    }
  }
}
.content-tip {
  margin-left: 5.5em;
  margin-top: 10px;
  p {
    color: var(--dark-text);
    font-weight: 400;
    margin: 10px 0;
    a {
      color: #0095ff;
    }
  }
}
</style>
