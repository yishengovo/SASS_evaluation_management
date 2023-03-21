<script setup lang="ts">
import { useWizard } from '/@src/stores/wizard'
// import { addPorject } from '/@src/api/createProject'
import { useNotyf } from '/@src/composable/useNotyf'
import { getProject } from '/@src/api/createProject/createProject'
import { LocationQueryValue } from 'vue-router'
import { Notice } from '/@src/components/base/au-notice/Notice'
import disableEle from '/@src/components/disableEle.vue'
const notyf = useNotyf()
const wizard = useWizard()
const router = useRouter()
const projectId = ref<string | LocationQueryValue[] | null>(null)
const isLoaderActive = ref(false)
const isDisable = localStorage.getItem('isView') === 'view'
const tipActionsOpen = ref(false)
const collectNum = ref(0)
const tip = ref('')
// 获取项目信息
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
    if (wizard.data.name === '') {
      wizard.data.name = res.result.project.projectName
      wizard.data.description = res.result.project.content
      wizard.data.jsonPreview = res.result.survey?.jsonPreview ?? ''
      wizard.data.id = res.result.project.id
      wizard.data.type = res.result.project.type
      collectNum.value = res.result.project.collectNumber
      tip.value = `您的问卷已收集${collectNum.value}份数据，现在进行项目内容修改，将会同步删除相关数据，不可恢复，请慎重操作。`

      if (collectNum.value > 0) {
        tipActionsOpen.value = true && !isDisable
      }
    } else {
      wizard.data.jsonPreview = res.result.survey?.jsonPreview ?? ''
      wizard.data.id = res.result.project.id
    }
    isLoaderActive.value = false
  }
}

getProjectInfo()
wizard.setStep({
  number: 2,
  canNavigate: true,
  validateStepFn: async () => {
    if (wizard.data.name === '') {
      notyf.warning('请填写项目名称')
    } else {
      if (wizard.data.description.length > 100)
        return notyf.warning('项目描述在100字以内')
      localStorage.setItem(
        'projectInfo',
        JSON.stringify({
          name: wizard.data.name,
          content: wizard.data.description,
        })
      )
      console.log(wizard.data.name)
      if (projectId.value) {
        router.push(`/wizard-v1/project-details?id=${projectId.value}`)
      } else {
        router.push({
          name: '/wizard-v1/project-details',
        })
      }
    }
  },
})

// const onAddFile = (error: any, fileInfo: any) => {
//   if (error) {
//     notyf.error(`${error.main}: ${error.sub}`)
//     console.error(error)
//     return
//   }

//   const _file = fileInfo.file as File
//   if (_file) {
//     wizard.data.logo = _file
//   }
// }

// const onRemoveFile = (error: any, fileInfo: any) => {
//   if (error) {
//     notyf.error(error)
//     console.error(error)
//     return
//   }

//   console.log(fileInfo)

//   wizard.data.logo = null
// }
</script>

<template>
  <VLoader size="large" :active="isLoaderActive" translucent>
    <div id="wizard-step-1" class="inner-wrapper is-active">
      <div class="step-content">
        <div class="step-title">
          <h2 class="dark-inverted" style="margin-bottom: 20px">描述您的项目</h2>
          <p>完善项目的相关信息</p>
        </div>
        <disableEle :disable="isDisable">
          <div class="project-info">
            <div class="project-info-head">
              <!-- <div class="project-avatar-upload">
            <VField>
              <VControl>
                <VFilePond
                  size="small"
                  class="profile-filepond"
                  name="profile_filepond"
                  :chunk-retry-delays="[500, 1000, 3000]"
                  label-idle="<i class='lnil lnil-cloud-upload'></i>"
                  :accepted-file-types="['image/png', 'image/jpeg', 'image/gif']"
                  :image-preview-height="140"
                  :image-resize-target-width="140"
                  :image-resize-target-height="140"
                  image-crop-aspect-ratio="1:1"
                  style-panel-layout="compact circle"
                  style-load-indicator-position="center bottom"
                  style-progress-indicator-position="right bottom"
                  style-button-remove-item-position="left bottom"
                  style-button-process-item-position="right bottom"
                  @addfile="onAddFile"
                  @removefile="onRemoveFile"
                />
              </VControl>
              <p>
                <span>Upload a project logo</span>
                <span>File size cannot exceed 2MB</span>
              </p>
            </VField>
          </div> -->
              <div class="project-info">
                <div class="project-name">
                  <VField>
                    <VControl>
                      <VInput v-model="wizard.data.name" placeholder="项目名称" />
                    </VControl>
                  </VField>
                </div>
                <div class="project-description p-t-10">
                  <VField>
                    <VControl>
                      <VTextarea
                        v-model="wizard.data.description"
                        class="textarea"
                        rows="8"
                        placeholder="描述项目(100字以内)..."
                      />
                      <!-- <p v-if="wizard.data.description.length === 0" class="help">100个字</p>
                  <p v-else-if="wizard.data.description.length === 99" class="help">
                    剩余 {{ 100 - wizard.data.description.length }} 字
                  </p>
                  <p v-else-if="wizard.data.description.length < 100" class="help">
                    剩余 {{ 100 - wizard.data.description.length }} 字
                  </p> -->
                    </VControl>
                  </VField>
                  <!-- <VField v-slot="{ id }">
                <label>Related Industries</label>
                <VControl>
                  <Multiselect
                    v-model="wizard.data.relatedTo"
                    :attrs="{ id }"
                    label="value"
                    placeholder="Enter something"
                    :options="[
                      {
                        value: 'UI/UX Design',
                      },
                      {
                        value: 'Web Development',
                      },
                      {
                        value: 'Marketing',
                      },
                    ]"
                  >
                  </Multiselect>
                </VControl>
              </VField> -->
                </div>
              </div>
            </div>
          </div>
        </disableEle>
        <VModal
          :open="tipActionsOpen"
          actions="center"
          title="提示"
          noclose
          @close="tipActionsOpen = false"
        >
          <template #content>
            <VPlaceholderSection :title="tip" />
          </template>
          <template #action>
            <VButton color="primary" raised @click="tipActionsOpen = false">
              确定</VButton
            >
          </template>
        </VModal>

        <!-- 您的问卷已收集 1 份数据，现在进行评价关系或者问卷内容修改，将会暂停收集，如增删题目或评价关系。会同步删除相关数据，不可恢复，请慎重操作。
确定后系统会自动备份当前数据到“我的下载”，如需请前往查看。 -->
      </div>
    </div>
  </VLoader>
</template>
