<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useHead } from '@vueuse/head'
import { toFormValidator } from '@vee-validate/zod'
import { useForm } from 'vee-validate'
import { z as zod } from 'zod'
import { getCode, register } from '/@src/api/auth'
import { storeUserInfo } from '/@src/utils/storeUserInfo'
import { useNotyf } from '/@src/composable/useNotyf'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { codeDecount } from '/@src/utils/codeDecount'
import { checkPhone, checkCode } from '/@src/utils/formCheck'
import { codeLogic } from '/@src/utils/codeLogic'
const router = useRouter()
const route = useRoute()
const notif = useNotyf() as any
const redirect = route.query.redirect as string
const { t } = useI18n()
const sendDisabled = ref(false)
const sendBtnText = ref('发送验证码')
// Define a validation schema
const validationSchema = toFormValidator(
  zod
    .object({
      phone: zod
        .string({
          required_error: '手机号是必填项',
        })
        .min(11, '手机号不符合规则'),
      password: zod
        .string({
          required_error: t('auth.errors.password.required'),
        })
        .min(8, t('auth.errors.password.length')),
      passwordCheck: zod.string({
        required_error: t('auth.errors.passwordCheck.required'),
      }),
      promotional: zod.boolean(),
    })
    .refine((data) => data.password === data.passwordCheck, {
      message: t('auth.errors.passwordCheck.match'),
      path: ['passwordCheck'],
    })
)

const { handleSubmit } = useForm({
  validationSchema,
  initialValues: {
    email: '',
    password: '',
    passwordCheck: '',
    promotional: false,
  },
})
const getPhoneCode = async () => {
  if (checkPhone(registerInfo.value.phone)) {
    codeDecount(sendDisabled, sendBtnText)
    const res = await getCode({
      mobile: registerInfo.value.phone,
      smsmode: 1,
    })

    codeLogic(res)
  }
}
const registerInfo = ref({
  phone: '',
  phoneCode: '',
  password: '',
  Rpassword: '',
})
const registerAccount = async () => {
  for (const item in registerInfo.value) {
    if ((registerInfo.value as any)[item].length <= 0) {
      return Notice({
        message: '请将表单填写完整',
        notice_type: 'error',
      })
    }
  }
  if (checkCode(registerInfo.value.phoneCode)) {
    const res = await register({
      phone: registerInfo.value.phone,
      smscode: registerInfo.value.phoneCode,
      password: registerInfo.value.password,
    })
    if (res.data.code === 0 || res.data.code === 200) {
      if (res.data.success) {
        Notice({
          message: '注册成功',
          notice_type: 'success',
        })
        localStorage.setItem('domainLogin', 'no')

        storeUserInfo(res.data.result)
        notif.dismissAll()
        notif.success(`欢迎回来, 用户${res.data.result.tenantList[0].name}`)
        if (redirect) {
          router.push(redirect)
        } else {
          router.push({
            name: '/home/all-project',
          })
        }
      } else {
        Notice({
          message: `注册失败,${res.data.message}`,
          notice_type: 'warning',
        })
      }
    } else {
      Notice({
        message: `注册失败,${res.data.message}`,
        notice_type: 'error',
      })
    }
  }
}
const onSignup = handleSubmit(async (values) => {
  console.log('handleSignup values')
  console.table(values)
})

useHead({
  title: '云测 - 用户注册',
})
</script>

<template>
  <div class="auth-wrapper-inner columns is-gapless">
    <!-- Form section -->
    <div class="column is-5">
      <div class="hero is-fullheight is-white">
        <div class="hero-heading">
          <!-- <label
            class="dark-mode ml-auto"
            tabindex="0"
            @keydown.space.prevent="(e) => (e.target as HTMLLabelElement).click()"
          >
            <input
              type="checkbox"
              :checked="!darkmode.isDark"
              @change="darkmode.onChange"
            />
            <span></span>
          </label> -->
          <div class="auth-logo">
            <RouterLink to="/">
              <AnimatedLogo class="top-logo" width="36px" height="36px" />
            </RouterLink>
          </div>
        </div>
        <div class="hero-body">
          <div class="container">
            <div class="columns">
              <div class="column is-12">
                <div class="auth-content">
                  <h2>{{ t('auth.title') }}</h2>
                  <p>{{ t('auth.subtitle') }}</p>
                  <RouterLink to="/auth/login">
                    {{ t('auth.action.login') }}
                  </RouterLink>
                </div>
                <div class="auth-form-wrapper">
                  <!-- Login Form -->
                  <form @submit="onSignup">
                    <div id="signin-form" class="login-form">
                      <!-- Input -->
                      <!-- <VField id="name" v-slot="{ field }">
                        <VControl icon="feather:user">
                          <VInput
                            v-model="registerInfo.uname"
                            type="text"
                            placeholder="用户名"
                            autocomplete="name"
                          />
                          <p v-if="field?.errorMessage" class="help is-danger">
                            {{ field.errorMessage }}
                          </p>
                        </VControl>
                      </VField> -->

                      <!-- Input -->
                      <VField id="phone" v-slot="{ field }">
                        <VControl icon="feather:phone">
                          <VInput
                            v-model="registerInfo.phone"
                            type="text"
                            placeholder="手机号"
                            autocomplete="phone"
                          />
                          <p v-if="field?.errorMessage" class="help is-danger">
                            {{ field.errorMessage }}
                          </p>
                        </VControl>
                      </VField>
                      <VField id="code">
                        <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                          <VInput
                            v-model="registerInfo.phoneCode"
                            type="text"
                            placeholder="验证码"
                            autocomplete="phone"
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
                      <!-- Input -->
                      <VField id="password" v-slot="{ field }">
                        <VControl icon="feather:lock">
                          <VInput
                            v-model="registerInfo.password"
                            type="password"
                            :placeholder="t('auth.placeholder.password')"
                            autocomplete="new-password"
                          />
                          <p v-if="field?.errorMessage" class="help is-danger">
                            {{ field.errorMessage }}
                          </p>
                        </VControl>
                      </VField>

                      <!-- Input -->
                      <VField id="passwordCheck" v-slot="{ field }">
                        <VControl icon="feather:lock">
                          <VInput
                            v-model="registerInfo.Rpassword"
                            type="password"
                            autocomplete="new-password"
                            :placeholder="t('auth.placeholder.passwordCheck')"
                          />
                          <p v-if="field?.errorMessage" class="help is-danger">
                            {{ field.errorMessage }}
                          </p>
                        </VControl>
                      </VField>

                      <VField id="promitional">
                        <VControl class="setting-item">
                          <VCheckbox
                            color="primary"
                            :label="t('auth.label.promotional')"
                            paddingless
                          />
                        </VControl>
                      </VField>

                      <!-- Submit -->

                      <div>
                        <VButton
                          type="submit"
                          color="primary"
                          bold
                          fullwidth
                          raised
                          @click="registerAccount"
                        >
                          {{ t('auth.action.signup') }}
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
    </div>

    <!-- Image section (hidden on mobile) -->
    <div class="column login-column is-7 is-hidden-mobile hero-banner">
      <div class="hero login-hero is-fullheight is-app-grey">
        <div class="hero-body">
          <div class="columns">
            <div class="column is-10 is-offset-1">
              <img
                class="light-image has-light-shadow has-light-border"
                src="/@src/assets/illustrations/apps/vuero-banking-light.png"
                alt=""
              />
              <img
                class="dark-image has-light-shadow"
                src="/@src/assets/illustrations/apps/vuero-banking-dark.webp"
                alt=""
              />
            </div>
          </div>
        </div>
        <div class="hero-footer">
          <p class="has-text-centered"></p>
        </div>
      </div>
    </div>
  </div>
</template>
