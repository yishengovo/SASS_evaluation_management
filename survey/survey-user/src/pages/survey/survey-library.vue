<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-06 17:40:53
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-31 00:28:42
 * @FilePath: \survey-user\src\pages\survey\survey-library.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import * as Survey from 'survey-jquery'
import $ from 'jquery'
import 'survey-vue/defaultV2.min.css'
import { LocationQueryValue } from 'vue-router'
import { codeDecount } from '/@src/utils/codeDecount'
import { checkPhone } from '/@src/utils/formCheck'
import { getProject, getUserSurvey } from '/@src/api/createProject/createProject'
import { saveAnswer } from '/@src/api/survey/fillSurvey'
import { getSPhoneCode, PhoneLogin } from '/@src/api/survey/360survey'
import type { User } from '/@src/api/survey/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
import noSurvey from '/@src/assets/noSurvey.png'
import type { SurveyList } from '/@src/api/createProject/type'
import { useHead } from '@vueuse/head'
import { codeLogic } from '/@src/utils/codeLogic'
import { queryControlApi } from '/@src/api/projectControl'
import { getControlApi } from '/@src/api/projectControl'
type StepNum = 0 | 1 | 2 | 3 // 0 初始化值,  1 认证身份，2 有多个要评价的人，3 展示问卷
// interface EvaluationOptions {
//   value: string
//   label: string
// }
Survey.StylesManager.applyTheme('defaultV2')

const columns = {
  surName: {
    label: '问卷名称',
  },
  surContent: {
    label: '问卷描述',
  },
  status: {
    label: '状态',
  },
  actionSlots: {
    label: '操作',
    align: 'end',
  },
}
const columns360 = {
  name: {
    label: '姓名',
  },
  status: {
    label: '状态',
  },
  actionSlots: {
    label: '操作',
    align: 'end',
  },
}
const router = useRouter()
const registerInfo = ref({
  phone: '',
  phoneCode: '',
  pwd: '',
})
const phoneCodeControl = ref(false)
const pwdControl = ref(false)
const step = ref<StepNum>(1)
const sendDisabled = ref(false)
const sendBtnText = ref('发送验证码')
const projectId = ref<LocationQueryValue[] | string>('')
// const phone = ref('')
const evaluators = ref<any>([]) // 被评价人
// const evaluationOptions = ref<EvaluationOptions[]>([])
const userInfo = ref<User>()
const isAddUser = ref(true)
// const showMask = ref(false)
const emptyText = ref('暂无问卷')
const surveyType = ref('')
const surveyList = ref<SurveyList[]>()
const currentSurveyId = ref('')
const projectName = ref('')
const description = ref('您有以下答卷需要完成')
const projectControl = async () => {
  if (phoneCodeControl.value && !pwdControl.value) {
    const { data: phoneRes } = await PhoneLogin({
      projectId: projectId.value as string,
      phone: registerInfo.value.phone,
      code: registerInfo.value.phoneCode,
    })
    if (phoneRes.code !== 200) {
      return Notice({ message: phoneRes.message, notice_type: 'error' })
    }
    // 判断调查问卷是否已经填写过
    if (phoneRes.result.user.isFinished === true) {
      return Notice({ message: '已填写过该问卷', notice_type: 'warning' })
    }
    if (surveyType.value === '360度评估') {
      userInfo.value = phoneRes.result.user
      evaluators.value = phoneRes.result.evaluator
    } else {
      userInfo.value = phoneRes.result.user
      surveyList.value = phoneRes.result.surveyList
    }
    step.value = 3
  } else if (pwdControl.value && !phoneCodeControl.value) {
    if (isAddUser.value) {
      const { data: phoneRes } = await PhoneLogin({
        projectId: projectId.value as string,
        phone: registerInfo.value.phone,
        password: registerInfo.value.pwd,
      })
      if (phoneRes.code !== 200) {
        return Notice({ message: phoneRes.message, notice_type: 'error' })
      }
      // 判断调查问卷是否已经填写过
      if (phoneRes.result.user.isFinished === true) {
        return Notice({ message: '已填写过该问卷', notice_type: 'warning' })
      }
      if (surveyType.value === '360度评估') {
        userInfo.value = phoneRes.result.user
        evaluators.value = phoneRes.result.evaluator
      } else {
        userInfo.value = phoneRes.result.user
        surveyList.value = phoneRes.result.surveyList
      }
      step.value = 3
    } else {
      const { data: phoneRes } = await PhoneLogin({
        projectId: projectId.value as string,
        password: registerInfo.value.pwd,
      })
      if (phoneRes.code !== 200) {
        return Notice({ message: phoneRes.message, notice_type: 'error' })
      }
      if (surveyType.value === '360度评估') {
        userInfo.value = phoneRes.result.user
        evaluators.value = phoneRes.result.evaluator
      } else {
        userInfo.value = phoneRes.result.user
        surveyList.value = phoneRes.result.surveyList
      }
      step.value = 3
    }
  } else if (pwdControl.value && phoneCodeControl.value) {
    const { data: phoneRes } = await PhoneLogin({
      projectId: projectId.value as string,
      phone: registerInfo.value.phone,
      code: registerInfo.value.phoneCode,
      password: registerInfo.value.pwd,
    })
    if (phoneRes.code !== 200) {
      return Notice({ message: phoneRes.message, notice_type: 'error' })
    }
    // 判断调查问卷是否已经填写过
    if (phoneRes.result.user.isFinished === true) {
      return Notice({ message: '已填写过该问卷', notice_type: 'warning' })
    }
    if (surveyType.value === '360度评估') {
      userInfo.value = phoneRes.result.user
      evaluators.value = phoneRes.result.evaluator
    } else {
      userInfo.value = phoneRes.result.user
      surveyList.value = phoneRes.result.surveyList
    }
    step.value = 3
  } else {
    if (isAddUser.value) {
      const { data: phoneRes } = await PhoneLogin({
        projectId: projectId.value as string,
        phone: registerInfo.value.phone,
      })
      if (phoneRes.code !== 200) {
        return Notice({ message: phoneRes.message, notice_type: 'error' })
      }
      // 判断调查问卷是否已经填写过
      if (phoneRes.result.user.isFinished === true) {
        return Notice({ message: '已填写过该问卷', notice_type: 'warning' })
      }
      if (surveyType.value === '360度评估') {
        userInfo.value = phoneRes.result.user
        evaluators.value = phoneRes.result.evaluator
      } else {
        userInfo.value = phoneRes.result.user
        surveyList.value = phoneRes.result.surveyList
      }
      step.value = 3
    } else {
      const { data: phoneRes } = await PhoneLogin({
        projectId: projectId.value as string,
      })
      if (phoneRes.code !== 200) {
        return Notice({ message: phoneRes.message, notice_type: 'error' })
      }
      if (surveyType.value === '360度评估') {
        userInfo.value = phoneRes.result.user
        evaluators.value = phoneRes.result.evaluator
      } else {
        userInfo.value = phoneRes.result.user
        surveyList.value = phoneRes.result.surveyList
      }
      step.value = 3
    }
  }
  if (surveyType.value === '360度评估' || surveyType.value === '测评') {
    step.value = 2
  }
}
// 验证身份
const handleConfirm = async () => {
  if (surveyType.value === '360度评估') {
    description.value = '您有以下人需要评价'
    projectControl()
  } else {
    projectControl()
  }
}
// 确认选择评价的人
// const handleConfirmSelect = () => {
//   step.value = 3
//   console.log(evaluator.value)
// }
const getPhoneCode = async () => {
  if (checkPhone(registerInfo.value.phone)) {
    codeDecount(sendDisabled, sendBtnText)
    const res = await getSPhoneCode({
      phone: registerInfo.value.phone,
      projectId: projectId.value as string,
    })

    codeLogic(res)
  }
}
// 处理答题

const handleAnswerSurvey = async (row: any) => {
  console.log(surveyType.value)
  console.log(row)
  currentSurveyId.value = row.id
  const survey = new Survey.Model(row.jsonPreview)
  await $('#surveyElement').Survey({ model: survey })
  survey.onCurrentPageChanged.add((sender: any) => {
    console.log(sender)
  })
  step.value = 3
  const surveyComplete = async (sender: any) => {
    await saveAnswer({
      projectId: projectId.value as string,
      answer: JSON.stringify(sender.data),
      result: sender.data,
      user: userInfo.value,
      evaluator: row.userId,
      type: surveyType.value,
      status: 2,
      surveyId: currentSurveyId.value,
    }).then((res: any) => {
      if (res.data.code !== 200) {
        return Notice({ message: res.data.message, notice_type: 'error' })
      }

      Notice({
        message: '提交成功,3秒钟后自动跳转',
        notice_type: 'success',
      })
    })
    const { data: res } = await getUserSurvey({
      projectId: projectId.value as string,
      phone: userInfo.value!.phone,
    })

    surveyList.value = res.result.surveyList
    userInfo.value = res.result.user
    evaluators.value = res.result.evaluator

    setTimeout(() => {
      step.value = 2
    }, 3000)
  }
  survey.onComplete.add(surveyComplete)
}
onMounted(async () => {
  if (router.currentRoute.value.query.id) {
    projectId.value = router.currentRoute.value.query.id as string
  }
  Notice({ notice_type: 'default', message: '初始化中' })
  const { data: controlData } = await queryControlApi(projectId.value as string)
  if (controlData.code === 200) {
    // 获取问卷项目表的json
    const { data: res } = await getProject(projectId.value as string)
    if (res.code !== 200) {
      step.value = 0
      return Notice({
        notice_type: 'error',
        message: res.message,
      })
    } else if (res.result == null) {
      step.value = 0
      emptyText.value = '该项目不存在'
      return Notice({ message: '该项目不存在', notice_type: 'error' })
    } else if (!res.result.project.isPublish) {
      emptyText.value = '该项目未发布'
      step.value = 0
      return Notice({ notice_type: 'error', message: '该项目未发布！' })
    }
    if (res.result.project.type) {
      surveyType.value = res.result.project.type
    }
    isAddUser.value = res.result.project.isAddUser ? true : false
    const projectControl = await getControlApi(
      router.currentRoute.value.query.id as string
    )
    phoneCodeControl.value = projectControl.data.result.phoneCaptchaEnable
    pwdControl.value = projectControl.data.result.passwordEnable
    if (!pwdControl.value && !phoneCodeControl.value && isAddUser.value === false) {
      step.value = 3
    } else {
      step.value = 1
    }
    Notice({ notice_type: 'success', message: '初始化成功' })

    const surveyJson = res.result.survey?.jsonPreview ?? ''
    projectName.value = res.result.project.projectName
    const survey = new Survey.Model(surveyJson)

    // 返回值会暴露给模板和其他的选项式 API 钩子
    $('#surveyElement').Survey({ model: survey })

    // 问卷完成时的回掉
    const surveyComplete = (sender: any) => {
      saveAnswer({
        projectId: projectId.value as string,
        answer: JSON.stringify(sender.data),
        result: sender.data,
        user: userInfo.value,
        evaluator: '',
        type: surveyType.value,
        surveyId: currentSurveyId.value,
        status: 2,
      }).then((res: any) => {
        console.log(res)
        if (res.data.code !== 200) {
          return Notice({ message: res.data.message, notice_type: 'error' })
        } else {
          Notice({ message: '提交成功', notice_type: 'success' })
        }
      })
    }
    survey.onComplete.add(surveyComplete)
  } else {
    step.value = 0
    emptyText.value = controlData.message
    Notice({
      notice_type: 'error',
      message: controlData.message,
    })
  }

  // console.log(projectControl.data.result.passwordEnable, 333)
})

useHead({
  title: '云测 - 答卷',
})
watch(step, (newValue) => {
  step.value = newValue
})
</script>

<template>
  <div class="survey">
    <div v-if="step === 0" class="empty">
      <div class="content">
        <img :src="noSurvey" alt="" style="width: 300px; height: 300px" />
        <div style="text-align: center; margin-top: 20px; color: #999">
          {{ emptyText }}
        </div>
      </div>
    </div>
    <div v-show="step === 3">
      <div id="surveyElement"></div>
    </div>
    <div v-if="step === 2" class="surveyContent">
      <div class="userName">{{ userInfo?.name ?? '匿名用户' }}，{{ description }}。</div>
      <VFlexTable
        v-if="surveyType === '360度评估'"
        :data="evaluators"
        :columns="columns360"
        compact
        rounded
      >
        <template #body-cell="{ row, column }">
          <div v-if="column.key === 'status'">
            <VTag v-if="row.status === 0" tag color="danger" label="未评价" />
            <VTag v-if="row.status === 2" tag color="primary" label="已评价" />
          </div>
          <VButton
            v-if="column.key === 'actionSlots'"
            color="primary"
            :disabled="row.status === 2"
            @click="handleAnswerSurvey(row)"
          >
            开始评价
          </VButton>
        </template>
      </VFlexTable>
      <VFlexTable v-else :data="surveyList" :columns="columns" compact rounded>
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
          <div v-if="column.key === 'status'">
            <VTag v-if="row.status === 0" tag color="danger" label="未完成" />
            <VTag v-if="row.status === 2" tag color="primary" label="已完成" />
          </div>
          <VButton
            v-if="column.key === 'actionSlots'"
            color="primary"
            :disabled="row.status === 2"
            @click="handleAnswerSurvey(row)"
          >
            开始答题
          </VButton>
        </template>
      </VFlexTable>
    </div>

    <div v-if="step === 1" class="identify">
      <div class="hero-body">
        <div class="container">
          <div class="columns">
            <div class="column is-12">
              <div class="projectTitle">
                <h2>{{ projectName }}</h2>
              </div>
              <div class="auth-form-wrapper">
                <!-- Login Form -->
                <form class="modal-form" @submit.prevent="handleConfirm">
                  <div class="login-form">
                    <VField v-if="!!projectName && isAddUser" id="phone">
                      <VControl>
                        <div class="field">
                          <label>手机号：</label>
                          <div class="control" style="margin-top: 4px">
                            <VInput
                              v-model="registerInfo.phone"
                              type="text"
                              placeholder="请输入手机号"
                              autocomplete="off"
                            />
                          </div>
                        </div>
                      </VControl>
                    </VField>

                    <VField v-if="pwdControl" id="password">
                      <VControl>
                        <div class="field">
                          <label>密码：</label>
                          <div class="control" style="margin-top: 4px">
                            <VInput
                              v-model="registerInfo.pwd"
                              type="password"
                              placeholder="请输入密码"
                              autocomplete="off"
                            />
                          </div>
                        </div>
                      </VControl>
                    </VField>
                    <VField v-if="phoneCodeControl" id="code">
                      <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                        <VInput
                          v-model="registerInfo.phoneCode"
                          type="text"
                          placeholder="验证码"
                          autocomplete="off"
                        />
                        <VButton
                          :disabled="sendDisabled"
                          style="margin-left: 15px"
                          color="primary"
                          @click="getPhoneCode"
                        >
                          {{ sendBtnText }}</VButton
                        >
                      </VControl>
                    </VField>
                    <!-- Submit -->
                    <div v-if="!!projectName" class="login">
                      <VButton color="primary" type="submit" bold fullwidth raised>
                        确定
                      </VButton>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 认证身份 -->
    <!-- <VModal :open="step === 1" actions="right" title="身份验证" noclose>
      <template #content> </template>
      <template #action>
        <VButton color="primary" raised>确定</VButton>
      </template>
    </VModal> -->
    <!-- <VModal :open="step === 0" actions="right" title="提示" noclose size="medium">
      <template #content>
        <VField v-slot="{ id }" style="height: 100px">
          <VLabel>选择要评价的人: </VLabel>
          <VControl>
            <Multiselect
              v-model="evaluator"
              :attrs="{ id }"
              :options="evaluationOptions"
              placeholder="请选择"
              :can-clear="false"
            />
          </VControl>
        </VField>
      </template>
      <template #action>
        <VButton color="primary" raised @click="handleConfirmSelect">确定</VButton>
      </template>
    </VModal> -->
  </div>
</template>

<style lang="scss" scoped>
@media (max-width: 768px) {
  .surveyContent {
    width: 90vw !important;
  }
  .identify {
    margin: 0 auto;
    width: 90vw !important;
  }
}
.survey {
  height: 100vh;
  background-color: #fff;
  .mask {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1000;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
  }
  .empty {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    color: #d8d8d8;
    .content {
      display: flex;
      align-items: center;
      flex-direction: column;
      margin-top: 120px;
    }
  }
  .surveyContent {
    margin: 0 auto;
    margin-top: 100px;
    width: 50vw;
    .userName {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 30px;
      color: #383838;
      font-size: 20px;
      font-weight: 400;
    }
  }
}
.identify {
  margin: 0 auto;
  margin-top: 8vw;
  width: 30vw;
  .projectTitle {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
    font-size: 22px;
    font-weight: 700;
  }
}
</style>
