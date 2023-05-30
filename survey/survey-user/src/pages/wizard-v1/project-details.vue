<script setup lang="ts">
// import type { WizardCustomer } from '/@src/models/wizard'
// import { customers } from '/@src/data/wizard'
import { LocationQueryValue } from 'vue-router'
import type { Survey } from '/@src/api/createProject/type'

import { getProject } from '/@src/api/createProject/createProject'

import { useWizard } from '/@src/stores/wizard'
import { useNotyf } from '/@src/composable/useNotyf'
import QuestionList from './question/question-list.vue'
import { Notice } from '/@src/components/base/au-notice/Notice'
import ProjectAssessment from './project-assessment.vue'

const isDisable = localStorage.getItem('isView') === 'view'
const wizard = useWizard()
const router = useRouter()
const notif = useNotyf() as any
const isLoaderActive = ref(false)
const projectId = ref<string | LocationQueryValue[] | null>(null)
const projectType = ref(wizard.data.type)
if (router.currentRoute.value.query.id) {
  projectId.value = router.currentRoute.value.query.id
}
window.onbeforeunload = function (e) {
  e = e || window.event
  if (e) {
    e.returnValue = '网站可能不会保存您的修改哦~'
  }

  return '网站可能不会保存您的修改哦~'
}

// 项目问卷
const projectSurvey = ref<Survey>()

//手动调用： window.confirm('系统可能不会保存你所做的修改')
// 获取项目信息, 并赋值 防止浏览器刷新数据丢失
const getSurevyInfo = async () => {
  isLoaderActive.value = true
  const { data: res } = await getProject(projectId.value as string)
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({ notice_type: 'error', message: '获取数据失败' })
  }
  projectSurvey.value = res.result.survey
  projectInfo.jsonPreview = projectSurvey.value?.jsonPreview
  // projectInfo.name = wizard.data.name || res.result.project.projectName
  // projectInfo.content = wizard.data.description || res.result.project.content
  wizard.data.id = projectId.value
  wizard.data.name = res.result.project.projectName
  wizard.data.description = res.result.project.content
  wizard.data.type = res.result.project.type

  projectType.value = res.result.project.type
  isLoaderActive.value = false
}
if (projectId.value) {
  getSurevyInfo()
}
// 项目信息
const projectInfo = reactive({
  id: wizard.data.id ?? projectId.value,
  name: wizard.data.name ?? '',
  content: wizard.data.description,
  type: wizard.data.type,
  jsonPreview: wizard.data.jsonPreview || projectSurvey.value?.jsonPreview,
})
// 弹窗相关
const showDialog = ref(false)
const onClose = () => {
  showDialog.value = false
}
// 创建空白问卷
function createEmptySurvey() {
  showDialog.value = true
}
// 编辑问卷
async function editSurvey() {
  await getSurevyInfo()
  isLoaderActive.value = false
  showDialog.value = true
}
// 处理预览问卷
const showPreview = ref(false)
const handlePreview = () => {
  showPreview.value = true
  console.log(1)
}
// 创建问卷时返回的问卷id
const saveSurvey = (id: string) => {
  projectId.value = id
  getSurevyInfo()
  console.log(1111)
}

// 测评项目创建项目时的回调
const handleAfterCreate = (id: string) => {
  console.log(id)
  projectId.value = id
}
// 查看题目列表
const isShowQuestion = ref(false)
const viewQuestionList = () => {
  isShowQuestion.value = true
  console.log('viewQuestionList')
}
const goBack = () => {
  isShowQuestion.value = false
}
const step = ref(3)

wizard.setStep({
  number: step.value,
  canNavigate: true,
  previousStepFn: async () => {
    if (projectId.value) {
      router.push(`/wizard-v1/project-info?id=${projectId.value}`)
    } else {
      router.push({
        name: '/wizard-v1/project-info',
      })
    }
  },
  validateStepFn: async () => {
    if (projectId.value) {
      // if (projectType.value === '调查') {
      //   router.push(`/wizard-v1/project-setting?id=${projectId.value}`)
      // } else {
      router.push(`/wizard-v1/project-evaluation?id=${projectId.value}`)
      // }
    } else {
      notif.dismissAll()
      notif.warning('请先创建问卷或选择问卷')
    }
  },
})
const sort = ['公众调查', '心里调查', '职业调查', '大学生调查']
const currentTemp = ref(0)
const changeTemp = (index: number) => {
  currentTemp.value = index
}
</script>

<template>
  <VLoader size="large" :active="isLoaderActive" translucent>
    <div id="wizard-step-2" class="inner-wrapper is-active">
      <div v-if="!isShowQuestion && projectType !== '测评'" class="step-content">
        <template v-if="!projectInfo.id && !projectId">
          <h2>请选择创建方式</h2>

          <div class="project">
            <VCard style="margin-right: 50px" class="card" @click="createEmptySurvey">
              <div class="create-project">
                <div class="icon">
                  <i class="lnir lnir-plus" aria-hidden="true"></i>
                </div>
                <div>空白创建</div>
              </div>
            </VCard>
            <VCard class="card">
              <div class="copy-project">
                <div class="icon">
                  <i class="fas fa-copy" aria-hidden="true"></i>
                </div>
                <div>复制项目</div>
              </div>
            </VCard>
          </div>

          <div class="use-temp">使用问卷模板</div>
          <div class="temp-title">
            <div class="sort">
              <template v-for="(item, index) in sort" :key="item"
                ><span
                  :class="currentTemp === index ? 'active' : ''"
                  @click="changeTemp(index)"
                  >{{ item }}</span
                ></template
              >
            </div>
            <div>
              <VField>
                <VControl icon="lnir lnir-search">
                  <VInput type="text" placeholder="搜索模板库" />
                </VControl>
              </VField>
            </div>
          </div>
        </template>
        <template v-if="projectInfo.id && projectId">
          <VCard class="card">
            <div>您的问卷还处于草稿状态,还可以继续编辑</div>
            <div>
              <VButton class="btn" bold elevated color="primary" @click="handlePreview">
                预览
              </VButton>
              <VButton
                v-if="!isDisable"
                class="btn"
                bold
                elevated
                style="margin-left: 6px"
                @click="editSurvey"
              >
                编辑
              </VButton>
            </div>
          </VCard>

          <VCard class="card2">
            <div>
              <a @click="viewQuestionList">查看题目列表></a>
              <div>点击查看现有问卷的所有题目信息</div>
            </div>
            <div>
              <a>设置题目维度></a>
              <div>点击设置问卷维度,绑定题目维度</div>
            </div>
          </VCard>
        </template>
      </div>
      <!-- 测评类项目 -->
      <div v-if="projectType === '测评'" class="step-content">
        <ProjectAssessment @after-create="handleAfterCreate"></ProjectAssessment>
      </div>
      <!-- 问题列表 -->
      <QuestionList
        v-if="isShowQuestion"
        :project-id="(projectId as string)"
        @go-back="goBack"
      ></QuestionList>

      <!-- 编辑问卷 -->
      <AuDialog v-if="showDialog" title="问卷设计" :footer="false" @close="onClose">
        <SurveyCreator
          :project-info="projectInfo"
          @save-survey="saveSurvey"
        ></SurveyCreator>
      </AuDialog>
      <!-- 预览问卷 -->
      <AuDialog
        v-if="showPreview"
        title="问卷预览"
        :footer="false"
        @close="showPreview = false"
      >
        <SurveyPreview :survey-json="projectInfo.jsonPreview"></SurveyPreview>
      </AuDialog>
    </div>
  </VLoader>
</template>

<style scoped lang="scss">
body {
  position: none !important;
}
@media (max-width: 1100px) {
  .project {
    display: block !important;

    div {
      width: 60vw !important;
    }
    .card {
      margin-right: 0 !important;
      margin-bottom: 20px;
    }
  }
  .temp-title {
    padding: 0 !important;
    flex-wrap: wrap !important;
    .sort {
      padding: 0 !important;
      margin-bottom: 20px;
      text-align: left;
    }
  }
}
@media (max-width: 768px) {
  .step-content {
    width: 80vw !important;
  }
}
@media (max-width: 500px) {
  .btn {
    margin-left: 0 !important;
    margin-top: 8px;
  }
}
.step-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 60vw;
  margin: 0 auto;
  text-align: center;
  font-size: 14px;
  h2 {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 30px;
  }
  .project {
    display: flex;
    div {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      width: 400px;
      height: 200px;
      padding: 10px;
      font-size: 18px;
      cursor: pointer;
      .icon {
        margin-top: 40px;
        width: 80px;
        height: 150px;
        font-size: 40px;
        color: #54c090;
      }
    }
  }
  .use-temp {
    font-size: 20px;
    margin: 30px 0;
  }
  .temp-title {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 50px;
    width: 100%;
    .sort {
      text-align: center;
      width: 800px;
      padding-left: 209px;
      span {
        padding: 0 15px;
        font-size: 16px;
        cursor: pointer;
      }
      .active {
        // font-weight: 700;
        color: var(--primary);
      }
    }
  }
  .card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .btn {
      width: 120px;
    }
  }
  .card2 {
    margin-top: 30px;
    div {
      text-align: left;
      margin-bottom: 20px;
    }
    a {
      color: #1890ff;
      font-size: 18px;
    }
  }
}
</style>
