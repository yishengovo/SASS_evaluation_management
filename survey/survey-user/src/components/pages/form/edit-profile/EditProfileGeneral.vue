<script setup lang="ts">
import { useNotyf } from '/@src/composable/useNotyf'
import sleep from '/@src/utils/sleep'
import { onceImageErrored } from '/@src/utils/via-placeholder'
import { useUserSession } from '/@src/stores/userSession'
import { resetCompanyName } from '/@src/api/auth'
const userSession = useUserSession()
const isUploading = ref(false)
const isLoading = ref(false)

const notyf = useNotyf()
const { y } = useWindowScroll()

const isScrolling = computed(() => {
  return y.value > 30
})

const onAddFile = (error: any, file: any) => {
  if (error) {
    console.error(error)
    return
  }

  console.log('file added', file)
}
const onRemoveFile = (error: any, file: any) => {
  if (error) {
    console.error(error)
    return
  }

  console.log('file removed', file)
}

const userInfo = ref({ name: userSession.user?.name })

const onSave = async () => {
  if (userInfo.value.name.length > 0) {
    isLoading.value = true
    const res = await resetCompanyName({
      companyName: userInfo.value.name,
    })
    if (res.data.code) {
      notyf.success('个人信息保存成功!')
      userSession.setUserInfo('name', userInfo.value.name)
    } else {
      notyf.error(res.data.message)
    }
    isLoading.value = false
  } else {
    notyf.error('公司姓名不允许为空')
  }
}
</script>

<template>
  <div class="account-box is-form is-footerless">
    <div class="form-head stuck-header" :class="[isScrolling && 'is-stuck']">
      <div class="form-head-inner">
        <div class="left">
          <h3>用户信息</h3>
          <p>编辑您的用户基本信息</p>
        </div>
        <div class="right">
          <div class="buttons">
            <!-- <VButton
              to="/sidebar/layouts/profile-view"
              icon="lnir lnir-arrow-left rem-100"
              light
              dark-outlined
            >
              Go Back
            </VButton> -->
            <VButton
              color="primary"
              raised
              :loading="isLoading"
              tabindex="0"
              @keydown.space.prevent="onSave"
              @click="onSave"
            >
              保存修改
            </VButton>
          </div>
        </div>
      </div>
    </div>
    <div class="form-body">
      <!--Fieldset-->
      <div class="fieldset">
        <div class="fieldset-heading">
          <h4>用户头像</h4>
          <p>以下是关于自己的一些信息</p>
        </div>

        <VAvatar size="xl" class="profile-v-avatar">
          <template #avatar>
            <img
              v-if="!isUploading"
              class="avatar"
              :src="
                userSession.user?.avatar
                  ? userSession.user?.avatar
                  : '/images/avatars/svg/vuero-1.svg'
              "
              alt=""
              @error.once="onceImageErrored(150)"
            />
            <VFilePond
              v-else
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
            <VIconButton
              v-if="!isUploading"
              icon="feather:edit-2"
              class="edit-button is-edit"
              circle
              tabindex="0"
              @keydown.space.prevent="isUploading = true"
              @click="isUploading = true"
            />
            <VIconButton
              v-else
              icon="feather:arrow-left"
              class="edit-button is-back"
              circle
              tabindex="0"
              @keydown.space.prevent="isUploading = false"
              @click="isUploading = false"
            />
          </template>
        </VAvatar>
      </div>

      <!--Fieldset-->
      <div class="fieldset">
        <div class="fieldset-heading">
          <h4>个人资料</h4>
        </div>

        <div class="columns is-multiline">
          <!--Field-->
          <!-- <div class="column is-6">
            <VField>
              <VControl icon="feather:user">
                <VInput type="text" placeholder="Last Name" autocomplete="family-name" />
              </VControl>
            </VField>
          </div> -->
          <!--Field-->
          <div class="column is-12">
            <div style="margin-bottom: 10px">公司名称:</div>
            <VField>
              <VControl icon="feather:briefcase">
                <VInput
                  v-model="userInfo.name"
                  type="text"
                  placeholder="公司名称"
                  autocomplete="organization-title"
                />
              </VControl>
            </VField>
          </div>

          <!--Field-->
          <!-- <div class="column is-12">
            <VField>
              <VControl>
                <VTextarea
                  rows="4"
                  placeholder="个人简介"
                  autocomplete="off"
                  autocapitalize="off"
                  spellcheck="true"
                />
              </VControl>
            </VField>
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>
