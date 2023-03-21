<script setup lang="ts">
import { useWizard } from '/@src/stores/wizard'
import { useDarkmode } from '/@src/stores/darkmode'
import { LocationQueryValue } from 'vue-router'
import disableEle from '/@src/components/disableEle.vue'
import { keRequest } from '/@src/utils/keRequest'
import { changeProject, changeProjectName } from '/@src/api/projectList'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { getProject } from '/@src/api/createProject/createProject'
import { getTimeArray } from '/@src/utils/getTimeArray'
import { useNotyf } from '/@src/composable/useNotyf'
import { editControlApi, getControlApi } from '/@src/api/projectControl'
import dayjs from 'dayjs'
// import { consumers } from 'stream'
// import { publishProject } from '/@src/api/projectList'
type timeInfoType = {
  startTime: any
  endTime: any
  designatedHoursStart: string
  designatedMinStart: string
  designatedHoursEnd: string
  designatedMinEnd: string
}
const darkmode = useDarkmode()
const isAddUser = ref(false)
const notyf = useNotyf()
// import { tools } from '/@src/data/wizard'
const isDisable = localStorage.getItem('isView') === 'view'
const wizard = useWizard()
const router = useRouter()
const projectId = ref<string | LocationQueryValue[] | null>(null)
const isLoaderActive = ref(false)
const getControl = async (id: string) => {
  const res = await getControlApi(id)
  if (res.data.code !== 200) {
    Notice({
      notice_type: 'error',
      message: '获取控制设置失败!',
    })
  }
  const result = res.data.result
  if (!!result) {
    timeSetting.value = result.timeEnable
    pwdSetting.value = result.passwordEnable
    controlPassword.value = result.password
    codeSetting.value = result.phoneCaptchaEnable
    if (!!result.startTime) {
      timeCheck.value.startTime = true
      timeInfo.value.startTime = result.startTime
    }
    if (!!result.endTime) {
      timeCheck.value.endTime = true
      timeInfo.value.endTime = result.endTime
    }
    if (!!result.dayStartTime) {
      timeCheck.value.designatedTime = true
      console.log(result.dayStartTime.slice(11, 13))
      timeInfo.value.designatedHoursStart = result.dayStartTime.slice(11, 13)
      timeInfo.value.designatedMinStart = result.dayStartTime.slice(14, 16)
      timeInfo.value.designatedHoursEnd = result.dayEndTime.slice(11, 13)
      timeInfo.value.designatedMinEnd = result.dayEndTime.slice(14, 16)
    }
  }
}
const getProjectInfo = async () => {
  if (router.currentRoute.value.query.id) {
    isLoaderActive.value = true
    projectId.value = router.currentRoute.value.query.id
    const { data: res } = await getProject(router.currentRoute.value.query.id as string)
    if (res.code !== 200) {
      isLoaderActive.value = false
      return Notice({
        notice_type: 'error',
        message: '获取数据失败，请稍后再试',
      })
    }
    isAddUser.value = res.result.project.isAddUser ? true : false
    console.log(isAddUser.value, 888)
    getControl(router.currentRoute.value.query.id as string)
    if (wizard.data.name === '') {
      wizard.data.name = res.result.project.projectName
      wizard.data.description = res.result.project.content
      wizard.data.jsonPreview = res.result.survey?.jsonPreview ?? ''
      wizard.data.id = res.result.project.id
      wizard.data.type = res.result.project.type
    } else {
      wizard.data.jsonPreview = res.result.survey?.jsonPreview ?? ''
      wizard.data.id = res.result.project.id
      wizard.data.type = res.result.project.type
    }
    isLoaderActive.value = false
  }
}
const hoursTime = getTimeArray(24)
const minTime = getTimeArray(60)
const controlPassword = ref('')
const timeSetting = ref(false)
const pwdSetting = ref(false)
const codeSetting = wizard.data.type === '调查' ? ref(false) : ref(true)
// const deviceSetting = ref(false)
// const ipSetting = ref(false)
// const wxSetting = ref(false)
let controlProgress = null
const currentControl = ref(0)
const timeCheck = ref({
  startTime: false,
  endTime: false,
  designatedTime: false,
})
const today = new Date()
const timeInfo = ref({
  startTime: new Date(),
  endTime: new Date(),
  designatedHoursStart: '00',
  designatedMinStart: '00',
  designatedHoursEnd: '00',
  designatedMinEnd: '00',
})
const timeTip = (timeInfo: timeInfoType) => {
  if (!timeSetting.value) {
    return true
  }
  if (
    new Date(timeInfo.endTime).getTime() < new Date(timeInfo.startTime).getTime() &&
    timeCheck.value.startTime &&
    timeCheck.value.endTime
  ) {
    notyf.error('结束时间不应该大于开始时间')
    return false
  }
  if (
    new Date(timeInfo.startTime).getTime() > new Date(timeInfo.endTime).getTime() &&
    timeCheck.value.startTime &&
    timeCheck.value.endTime
  ) {
    notyf.error('开始时间不应该大于结束时间')
    return false
  }
  if (timeInfo.designatedHoursEnd < timeInfo.designatedHoursStart) {
    if (timeInfo.designatedMinEnd < timeInfo.designatedMinStart) {
      notyf.error('指定时间的开始时间不应该大于结束时间')
      return false
    }
    notyf.error('指定时间的开始时间不应该大于结束时间')
    return false
  }
  return true
}
const saveControl = async () => {
  if (timeTip(timeInfo.value)) {
    const res = await editControlApi({
      projectId: projectId.value as string,
      timeEnable: timeSetting.value,
      startTime: timeCheck.value.startTime
        ? dayjs(timeInfo.value.startTime).format('YYYY-MM-DD HH:mm:ss')
        : null,
      endTime: timeCheck.value.endTime
        ? dayjs(timeInfo.value.endTime).format('YYYY-MM-DD HH:mm:ss')
        : null,
      dayStartTime: timeCheck.value.designatedTime
        ? `2022-11-23 ${timeInfo.value.designatedHoursStart}:${timeInfo.value.designatedMinStart}:00`
        : null,
      dayEndTime: timeCheck.value.designatedTime
        ? `2022-11-23 ${timeInfo.value.designatedHoursEnd}:${timeInfo.value.designatedMinEnd}:00`
        : null,
      passwordEnable: pwdSetting.value,
      password: pwdSetting.value ? controlPassword.value : '',
      phoneCaptchaEnable: codeSetting.value,
    })
    if (res.data.code !== 200) {
      Notice({
        notice_type: 'error',
        message: res.data.message,
      })
    } else {
      Notice({
        notice_type: 'success',
        message: '保存设置成功',
      })
    }
  }
}
// const controls = ['基础设置', '答题限制', '提交限制']
// const changeControl = (index: number) => {
//   scroll(index)
//   document.documentElement.scrollTop = index * 60
// }
const scroll = (index: number) => {
  controlProgress = document.querySelector('.control-progress') as HTMLDivElement
  const transformPx = index * 60
  if (controlProgress) {
    controlProgress.style.transform = `translate3d(0, ${transformPx}px, 0)`
  }
  currentControl.value = index
}
const previous = () => {
  if (
    wizard.data.type === '360度评估' ||
    wizard.data.type === '测评' ||
    wizard.data.type === '调查'
  ) {
    if (projectId.value) {
      router.push(`/wizard-v1/project-evaluation?id=${projectId.value}`)
    } else {
      router.push({
        name: '/wizard-v1/project-evaluation',
      })
    }
  } else {
    if (projectId.value) {
      router.push(`/wizard-v1/project-details?id=${projectId.value}`)
    } else {
      router.push({
        name: '/wizard-v1/project-details',
      })
    }
  }
}

const finish = async () => {
  if (!isAddUser.value && codeSetting.value) {
    return Notice({
      notice_type: 'error',
      message: '开启验证码设置时必须导入人员！',
    })
  }
  await saveControl()
  await keRequest(async () => {
    const res = await changeProject({
      id: projectId.value as any,
      isPublish: true,
    })
    return [res]
  }, '发布')
  if (localStorage.getItem('isEdit') === 'true') {
    const projectInfo = JSON.parse(localStorage.getItem('projectInfo') as string)
    const res = await changeProjectName({
      id: projectId.value as any,
      ...projectInfo,
    })
    if (res.data.code !== 200) {
      Notice({
        notice_type: 'error',
        message: '修改项目信息失败!',
      })
    }
  }

  wizard.reset()
  router.push('/home/all-project')
}
const draft = async () => {
  if (!isAddUser.value && codeSetting.value) {
    return Notice({
      notice_type: 'error',
      message: '开启验证码设置时必须导入人员！',
    })
  }
  await saveControl()
  if (localStorage.getItem('isEdit') === 'true') {
    const projectInfo = JSON.parse(localStorage.getItem('projectInfo') as string)
    await keRequest(async () => {
      const res = await changeProjectName({
        id: projectId.value as any,
        ...projectInfo,
      })
      return [res]
    }, '存为草稿')
  }

  wizard.reset()
  router.push('/home/all-project')
}

// const options = ref('Option 1')
onMounted(async () => {
  await getProjectInfo()
  const step = ref(5)

  // if (wizard.data.type === '调查') {
  //   step.value = 4
  // }
  wizard.setStep({
    number: step.value,
    canNavigate: false,
  })
  controlProgress = document.querySelector('.control-progress') as HTMLDivElement
  document.onscroll = function () {
    //为了保证兼容性，这里取两个值，哪个有值取哪一个
    //scrollTop就是触发滚轮事件时滚轮的高度
    let scrollTop = document.documentElement.scrollTop || document.body.scrollTop
    if (scrollTop < 60) {
      scroll(0)
    } else if (scrollTop >= 60 && scrollTop < 100) {
      scroll(1)
    } else {
      scroll(2)
    }
  }
})
</script>

<template>
  <VLoader size="large" :active="isLoaderActive" translucent>
    <div id="wizard-step-5" class="inner-wrapper is-active">
      <div class="step-content">
        <!-- <div class="left-progress">
          <div class="left-text">
            <template v-for="(item, index) in controls" :key="item">
              <div
                :class="currentControl === index ? 'active' : ''"
                @click="changeControl(index)"
              >
                {{ item }}
              </div>
            </template>
          </div>
          <div class="progress-container">
            <div class="control-progress"></div>
          </div>
        </div> -->
        <disableEle :disable="isDisable">
          <div class="right-content">
            <VCard class="card base-setting">
              <div class="card-title">
                <span>项目控制</span>
                <div>
                  <VButton color="primary" @click="saveControl">保存</VButton>
                </div>
              </div>
              <div class="item">
                <div class="item-title">
                  <div>时间控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="timeSetting" color="primary" />
                  </VControl>
                </div>
                <div v-if="timeSetting" class="item-content">
                  <VField class="field">
                    <VControl raw subcontrol>
                      <div class="time-item">
                        <VCheckbox
                          v-model="timeCheck.startTime"
                          label="开始时间"
                          color="primary"
                        />
                        <ClientOnly v-if="timeCheck.startTime">
                          <VDatePicker
                            v-model="timeInfo.startTime"
                            :min-date="today"
                            color="green"
                            mode="dateTime"
                          >
                            <template #default="{ inputValue, inputEvents }">
                              <VField>
                                <VControl icon="feather:calendar">
                                  <VInput
                                    id="startTime"
                                    :value="inputValue"
                                    v-on="inputEvents"
                                  />
                                </VControl>
                              </VField>
                            </template>
                          </VDatePicker>
                        </ClientOnly>
                      </div>
                    </VControl>
                    <VControl raw subcontrol>
                      <div class="time-item">
                        <VCheckbox
                          v-model="timeCheck.endTime"
                          label="结束时间"
                          color="primary"
                        />
                        <ClientOnly v-if="timeCheck.endTime">
                          <VDatePicker
                            v-model="timeInfo.endTime"
                            :min-date="timeInfo.startTime"
                            color="green"
                            mode="dateTime"
                          >
                            <template #default="{ inputValue, inputEvents }">
                              <VField>
                                <VControl icon="feather:calendar">
                                  <VInput
                                    id="endTime"
                                    :value="inputValue"
                                    v-on="inputEvents"
                                  />
                                </VControl>
                              </VField>
                            </template>
                          </VDatePicker>
                        </ClientOnly>
                      </div>
                    </VControl>
                    <VControl raw subcontrol>
                      <div class="time-item">
                        <VCheckbox
                          v-model="timeCheck.designatedTime"
                          label="每天在指定时间段作答"
                          color="primary"
                        />
                        <div v-if="timeCheck.designatedTime" class="time-select">
                          <div class="time">
                            <VField>
                              <VControl>
                                <VSelect
                                  id="selectOneS"
                                  v-model="timeInfo.designatedHoursStart"
                                >
                                  <VOption
                                    v-for="item in hoursTime"
                                    :key="item"
                                    :value="item"
                                    >{{ item }}</VOption
                                  >
                                </VSelect>
                              </VControl>
                            </VField>
                            <VField>
                              <VControl>
                                <VSelect
                                  id="selectTwoS"
                                  v-model="timeInfo.designatedMinStart"
                                >
                                  <VOption
                                    v-for="item in minTime"
                                    :key="item"
                                    :value="item"
                                    >{{ item }}</VOption
                                  >
                                </VSelect>
                              </VControl>
                            </VField>
                          </div>
                          <span>-</span>
                          <div class="time">
                            <VField>
                              <VControl>
                                <VSelect
                                  id="selectOne"
                                  v-model="timeInfo.designatedHoursEnd"
                                >
                                  <VOption
                                    v-for="item in hoursTime"
                                    :key="item"
                                    :value="item"
                                    >{{ item }}</VOption
                                  >
                                </VSelect>
                              </VControl>
                            </VField>
                            <VField>
                              <VControl>
                                <VSelect
                                  id="selectTwo"
                                  v-model="timeInfo.designatedMinEnd"
                                >
                                  <VOption
                                    v-for="item in minTime"
                                    :key="item"
                                    :value="item"
                                    >{{ item }}</VOption
                                  >
                                </VSelect>
                              </VControl>
                            </VField>
                          </div>
                        </div>
                      </div>
                    </VControl>
                  </VField>
                </div>
              </div>
              <div class="item">
                <div class="item-title">
                  <div>密码控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="pwdSetting" color="primary" />
                  </VControl>
                </div>
                <div v-if="pwdSetting" class="item-content">
                  <VField>
                    <VControl>
                      <VInput
                        id="controlpwd"
                        v-model="controlPassword"
                        style="width: 200px; margin-left: 15px"
                        placeholder="请输入密码"
                        data-cy="password-input"
                        autocomplete="current-password"
                      />
                    </VControl>
                  </VField>
                </div>
              </div>
              <!-- v-if="wizard.data.type !== '调查'" -->
              <div class="item">
                <div class="item-title">
                  <div>验证码控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="codeSetting" color="primary" />
                  </VControl>
                </div>
              </div>
            </VCard>
            <!-- <VCard class="card time-setting">
              <div class="card-title">答题设置</div>
              <div class="item">
                <div class="item-title">
                  <div>设备控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="deviceSetting" color="primary" />
                  </VControl>
                </div>
                <div v-if="deviceSetting" class="item-content">333</div>
              </div>
              <div class="item">
                <div class="item-title">
                  <div>IP地址控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="ipSetting" color="primary" />
                  </VControl>
                </div>
                <div v-if="ipSetting" class="item-content">444</div>
              </div>
              <div class="item">
                <div class="item-title">
                  <div>微信控制</div>
                  <VControl subcontrol>
                    <VSwitchBlock v-model="wxSetting" color="primary" />
                  </VControl>
                </div>
                <div v-if="wxSetting" class="item-content">555</div>
              </div>
            </VCard>
            <VCard class="card submit-setting" style="margin-bottom: 40px">
              <div class="card-title">提交限制</div>
              <div class="submit-radios">
                <VControl raw subcontrol>
                  <VCheckbox
                    v-model="options"
                    true-value="Option 1"
                    label="显示感谢信息"
                    color="primary"
                    class="radio"
                    circle
                  />
                </VControl>
                <VControl raw subcontrol>
                  <VCheckbox
                    v-model="options"
                    true-value="Option 2"
                    label="跳转到指定页面"
                    color="primary"
                    class="radio"
                    circle
                  />
                </VControl>
                <VControl raw subcontrol>
                  <VCheckbox
                    v-model="options"
                    true-value="Option 3"
                    label="按条件处理（可发送邮件和短信）"
                    color="primary"
                    class="radio"
                    circle
                  />
                </VControl>
              </div>
            </VCard> -->
          </div>
        </disableEle>
      </div>
      <div class="bottom-content">
        <div :class="['my-buttons', darkmode.isDark ? 'dark' : '']">
          <div class="my-buttons-inner">
            <VButton
              class="my-button-previous"
              bold
              elevated
              color="primary"
              @click="previous"
            >
              上一步
            </VButton>
            <VButton
              :disabled="isDisable"
              type="submit"
              class="my-button-finish"
              color="primary"
              bold
              elevated
              @click="finish"
            >
              发布问卷
            </VButton>
            <VButton
              :disabled="isDisable"
              type="submit"
              class="wizard-button-draft"
              color="primary"
              bold
              elevated
              @click="draft"
            >
              存为草稿
            </VButton>
          </div>
        </div>
      </div>
    </div>
  </VLoader>
</template>

<style lang="scss" scoped>
@media (max-width: 600px) {
  .time-item {
    display: block !important;
  }
}
#wizard-step-5 {
  position: relative;

  .step-content {
    display: flex;
    width: calc(50vw + 10rem);
    margin: 0 auto;
    .left-progress {
      position: fixed;
      display: flex;
      align-items: center;
      height: 180px;

      .left-text {
        margin-right: 20px;
        margin-top: 10px;
        .active {
          color: var(--primary);
        }
        div {
          cursor: pointer;
          margin-bottom: 30px;
        }
      }
      .progress-container {
        height: 180px;
        width: 6px;
        border-radius: 3px;
        background-color: #dfdfdf;
        .control-progress {
          width: 6px;
          border-radius: 3px;
          height: 60px;
          background-color: #6ac29b;
          transform: translate3d(0, 0, 0);
          transition: all 0.3s ease;
        }
      }
    }
    .right-content {
      flex: 1;
      // margin-left: 8rem;

      .time-item {
        display: flex;
        align-items: center;
        :deep(.control) {
          margin-left: 20px;
          margin-top: 10px;
        }
      }
      .time-select {
        display: flex;
        .time {
          display: flex;
        }
        span {
          display: inline-block;
          padding: 0 5px;
          font-size: 18px;
          height: 55px;
          line-height: 55px;
        }
      }

      // cursor: not-allowed;
      .card {
        margin-bottom: 20px;
        padding-bottom: 40px;
        // pointer-events: none;
        .card-title {
          display: flex;
          align-items: center;
          justify-content: space-between;
          font-weight: 700;
          font-size: 16px;
          padding: 15px 0;
          border-bottom: 1px solid var(--border);
        }
        .item {
          padding: 15px 0;
          border-bottom: 1px solid var(--border);
          .item-title {
            display: flex;
            justify-content: space-between;
          }
          .item-content {
            margin-top: 15px;
            .field {
              margin-left: -15px;
              .checkbox {
                color: var(--dark-text);
              }
            }
          }
        }
      }
    }
  }
  .submit-radios {
    margin: 10px 0 10px -15px;
    border-bottom: 1px solid var(--border);
    .radio {
      color: var(--dark-text);
    }
  }
  .bottom-content {
    width: 100%;
    margin: 0 auto;
    position: fixed;
    bottom: 20px;
    .my-buttons {
      margin: 0 auto;
      max-width: 350px;
      border-radius: 14px;
      background: white;
      border: 1px solid #e5e5e5;
      // transform: translateY(0px);
      // transition: transform 0.3s; // transition-all test
      z-index: 5;
      .my-buttons-inner {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px;

        .button {
          min-width: 100px;
          margin: 0 4px;
          border-radius: 8px;
        }
      }
    }
  }
  .dark {
    background-color: #252528 !important;
    border-color: #3b3b40 !important;
  }
}
</style>
