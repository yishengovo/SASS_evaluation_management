<script setup lang="ts">
import { useHead } from '@vueuse/head'

import { storeUserInfo } from '/@src/utils/storeUserInfo'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { useNotyf } from '/@src/composable/useNotyf'
import { codeLogic } from '/@src/utils/codeLogic'
// import { keRequest } from '/@src/utils/keRequest'
import {
  login,
  getImageCode,
  phoneLogin,
  getEmailCode,
  emailLogin,
  wxPhoneBindApi,
  wxGetPhoneCodeApi,
  wxLoginApi,
} from '/@src/api/auth'
import { getCode } from '/@src/api/auth'
import { codeDecount } from '/@src/utils/codeDecount'
import { checkPhone, checkEmail, checkCode } from '/@src/utils/formCheck'
type StepId = 'login' | 'forgot-password'
const step = ref<StepId>('login')
const isLoading = ref(false)
const router = useRouter()
const route = useRoute()
const notif = useNotyf() as any
const thirdLoginInfo = ref('')
const envAPI = import.meta.env.VITE_API_BASE_URL
const wx_url = envAPI + '/sys/thirdLogin/render/wechat_open'
const redirect = route.query.redirect as string
let currentTime = 0
const sendDisabled = ref(false)
const sendBtnText = ref('发送验证码')
const sendWxDisabled = ref(false)
const sendWxBtnText = ref('发送验证码')
const sendEmailDisabled = ref(false)
const sendEmailBtnText = ref('发送验证码')
const currentLLogin = ref('phone')
const phoneLoginInfo = ref({
  phone: '',
  code: '',
})
const wxPhoneBind = ref({
  phone: '',
  code: '',
  thirdUserUuid: '',
})
const wxPhoneBindModal = ref(false)
const thirdType = ref('')
const thirdLoginState = ref(false)
const thirdUserUuid = ref('')
const emailLoginInfo = ref({
  email: '',
  code: '',
})
const tabChange = (value: string) => {
  currentLLogin.value = value
}
const loginLogic = async (res: any) => {
  if (res.data.code === 200) {
    if (rememberPwd.value) {
      localStorage.setItem('pwd', loginInfo.value.password)
    } else {
      localStorage.removeItem('pwd')
    }
    localStorage.setItem('domainLogin', 'no')
    storeUserInfo(res.data.result)
    if (redirect) {
      router.push(redirect)
    } else {
      router.push({
        name: '/home/all-project',
      })
    }
    if (!!res.data.result.tenantList[0].realmName) {
      location.hostname = res.data.result.tenantList[0].realmName
    }
    notif.dismissAll()
    notif.success(`欢迎回来, 用户${res.data.result.tenantList[0].name}`)
  } else {
    Notice({
      message: `${res.data.message}`,
      notice_type: 'error',
    })
  }
}
const handleLogin = async () => {
  if (!isLoading.value) {
    isLoading.value = true
    const res = await login({
      username: loginInfo.value.userName,
      password: loginInfo.value.password,
      captcha: loginInfo.value.code,
      checkKey: currentTime,
      loginEnd: 1,
    })
    loginLogic(res)
    isLoading.value = false
  }
}
const getWxPhoneCode = async () => {
  if (checkPhone(wxPhoneBind.value.phone)) {
    codeDecount(sendWxDisabled, sendWxBtnText)
    const res = await wxGetPhoneCodeApi({
      mobile: wxPhoneBind.value.phone,
      smsmode: '1',
    })

    codeLogic(res)
  }
}
const bindWxPhone = async () => {
  const res = await wxPhoneBindApi({
    mobile: wxPhoneBind.value.phone,
    captcha: wxPhoneBind.value.code,
    thirdUserUuid: thirdUserUuid.value,
  })
  if (res.data.code !== 0) {
    Notice({
      notice_type: 'error',
      message: res.data.message,
    })
  } else {
    wxLogin(res.data.result)
  }
}
const wxLogin = async (token: string) => {
  const res = await wxLoginApi({ token, thirdType: 'wechat_open', loginEnd: 1 })
  loginLogic(res)
}
const codeImageUrl = ref('')
const rememberPwd = !!localStorage.getItem('pwd') ? ref(true) : ref(false)
const changeImageCode = async () => {
  currentTime = new Date().getTime()
  const res = await getImageCode(`${currentTime}`)
  if (res.data.code !== 0) {
    return Notice({
      notice_type: 'error',
      message: '获取图像验证码失败',
    })
  }
  codeImageUrl.value = res.data.result
}
const openVxModal = () => {
  let url = wx_url
  window.open(
    url,
    `login wechat_open`,
    'height=500, width=500, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no'
  )
  thirdType.value = 'wechat_open'
  thirdLoginInfo.value = ''
  thirdLoginState.value = false
  let receiveMessage = function (event: any) {
    let token = event.data
    if (typeof token === 'string') {
      //如果是字符串类型 说明是token信息
      if (token === '登录失败') {
        Notice({
          notice_type: 'warning',
          message: token,
        })
      } else if (token.includes('绑定手机号')) {
        Notice({
          notice_type: 'warning',
          message: '检测该微信未关联手机号,请关联手机号!',
        })
        wxPhoneBindModal.value = true
        // that.bindingPhoneModal = true
        const strings = token.split(',')
        thirdUserUuid.value = strings[1]
      } else {
        wxLogin(token)
        // that.doThirdLogin(token)
      }
    } else if (typeof token === 'object') {
      //对象类型 说明需要提示是否绑定现有账号
      if (token['isObj'] === true) {
        console.log('对象类型')
        // that.thirdConfirmShow = true
        // that.thirdLoginInfo = { ...token }
      }
    } else {
      Notice({
        notice_type: 'warning',
        message: '不识别的信息传递',
      })
      // that.$message.warning('不识别的信息传递')
    }
  }
  window.addEventListener('message', receiveMessage, false)
  // wxActionsOpen.value = true
}
const loginInfo = ref({
  userName: '',
  password: localStorage.getItem('pwd') ?? '',
  code: '',
})

const getPhoneCode = async () => {
  if (checkPhone(phoneLoginInfo.value.phone)) {
    codeDecount(sendDisabled, sendBtnText)
    const res = await getCode({
      mobile: phoneLoginInfo.value.phone,
      smsmode: 3,
    })

    codeLogic(res)
  }
}
const getEmailSms = async () => {
  if (checkEmail(emailLoginInfo.value.email)) {
    codeDecount(sendEmailDisabled, sendEmailBtnText)
    const res = await getEmailCode({
      email: emailLoginInfo.value.email,
    })

    codeLogic(res)
  }
}
const handleOtherLogin = async () => {
  if (currentLLogin.value === 'phone') {
    if (checkCode(phoneLoginInfo.value.code)) {
      const res = await phoneLogin({
        mobile: phoneLoginInfo.value.phone,
        captcha: phoneLoginInfo.value.code,
        loginEnd: 1,
      })
      loginLogic(res)
    }
  } else {
    if (checkCode(emailLoginInfo.value.code)) {
      const res = await emailLogin({
        email: emailLoginInfo.value.email,
        emailCode: emailLoginInfo.value.code,
        loginEnd: 1,
      })
      loginLogic(res)
    }
  }
}
localStorage.setItem('locale', 'zh-CN')
useHead({
  title: '云测 - 登录',
})
onMounted(() => {
  changeImageCode()
})
</script>

<template>
  <div class="modern-login">
    <div class="underlay h-hidden-mobile h-hidden-tablet-p"></div>

    <div class="columns is-gapless is-vcentered">
      <div class="column is-relative is-8 h-hidden-mobile h-hidden-tablet-p">
        <div class="hero is-fullheight is-image">
          <div class="hero-body">
            <div class="container">
              <div class="columns">
                <div class="column">
                  <img
                    class="hero-image"
                    src="/@src/assets/illustrations/login/station.svg"
                    alt=""
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="column is-4 is-relative">
        <!-- <RouterLink to="/" class="top-logo">
          <AnimatedLogo width="38px" height="38px" />
        </RouterLink> -->

        <!-- <label
          class="dark-mode ml-auto"
          tabindex="0"
          @keydown.space.prevent="(e) => (e.target as HTMLLabelElement).click()"
        >
          <input
            data-cy="dark-mode-toggle"
            type="checkbox"
            :checked="!darkmode.isDark"
            @change="darkmode.onChange"
          />
          <span></span>
        </label> -->
        <div class="is-form">
          <div class="hero-body">
            <div class="form-text" :class="[step !== 'login' && 'is-hidden']">
              <h2>登录</h2>
              <p>欢迎使用问卷测评系统</p>
            </div>
            <!-- <div class="form-text" :class="[step === 'login' && 'is-hidden']">
              <h2>手机号登录</h2>
            </div> -->
            <form
              data-cy="login-form"
              :class="[step !== 'login' && 'is-hidden']"
              class="login-wrapper"
              @submit.prevent="handleLogin"
            >
              <!-- <VMessage color="primary">
                <div>
                  <strong class="pr-1">email:</strong>
                  <span>john.doe@cssninja.io</span>
                </div>
                <div>
                  <strong class="pr-1">password:</strong>
                  <span>ada.lovelace</span>
                </div>
              </VMessage> -->

              <VField>
                <VControl icon="lnir lnir-user-alt-1 autv-icon">
                  <VLabel class="auth-label">用户名</VLabel>
                  <VInput v-model="loginInfo.userName" autocomplete="username" />
                </VControl>
              </VField>
              <VField>
                <VControl icon="lnil lnil-lock-alt autv-icon">
                  <VLabel class="auth-label">密码</VLabel>
                  <VInput
                    v-model="loginInfo.password"
                    data-cy="password-input"
                    type="password"
                    autocomplete="current-password"
                  />
                </VControl>
              </VField>
              <VField>
                <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                  <VLabel class="auth-label">验证码</VLabel>
                  <VInput v-model="loginInfo.code" />
                  <div style="margin-left: 10px" @click="changeImageCode">
                    <img
                      :src="codeImageUrl"
                      alt=""
                      style="
                        height: 60px;
                        border-radius: 3px;
                        display: block;
                        cursor: pointer;
                      "
                    />
                  </div>
                </VControl>
              </VField>

              <VField>
                <VControl class="is-flex">
                  <VSwitchBlock v-model="rememberPwd" color="primary" label="记住密码" />
                  <a
                    tabindex="0"
                    style="color: var(--link)"
                    @keydown.space.prevent="step = 'forgot-password'"
                    @click="step = 'forgot-password'"
                  >
                    手机号 | 邮箱登录
                  </a>
                </VControl>
              </VField>

              <div class="button-wrap has-help">
                <VButton
                  id="login-button"
                  :loading="isLoading"
                  color="primary"
                  type="submit"
                  size="big"
                  rounded
                  raised
                  bold
                >
                  登录
                </VButton>
                <span>
                  <RouterLink to="/auth/signup">注册</RouterLink>
                  新账号.
                </span>
              </div>

              <div class="vx-login" @click="openVxModal">
                <img src="../../assets/wx.png" alt="" />
                <span>微信登录</span>
              </div>
            </form>

            <form
              :class="[step !== 'forgot-password' && 'is-hidden']"
              class="login-wrapper"
              @submit.prevent
            >
              <VTabs
                :selected="currentLLogin"
                :tabs="[
                  { label: '手机号登录', value: 'phone', icon: 'lnir lnir-phone' },
                  { label: '邮箱登录', value: 'email', icon: 'lnir lnir-message-edit' },
                ]"
                @update:selected="tabChange"
              >
                <template #tab="{ activeValue }">
                  <VField v-if="activeValue === 'phone'">
                    <VControl icon="lnir lnir-phone autv-icon">
                      <VLabel class="auth-label">手机号</VLabel>
                      <VInput
                        id="phone"
                        v-model="phoneLoginInfo.phone"
                        autocomplete="current-password"
                      />
                    </VControl>
                    <VField>
                      <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                        <VLabel class="auth-label">验证码</VLabel>
                        <VInput
                          id="phoneCode"
                          v-model="phoneLoginInfo.code"
                          type="text"
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
                  </VField>
                  <VField v-else-if="activeValue === 'email'">
                    <VControl icon="lnir lnir-message-edit autv-icon">
                      <VLabel class="auth-label">邮箱</VLabel>
                      <VInput
                        id="phone"
                        v-model="emailLoginInfo.email"
                        autocomplete="current-password"
                      />
                    </VControl>
                    <VField>
                      <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                        <VLabel class="auth-label">验证码</VLabel>
                        <VInput
                          id="phoneCode"
                          v-model="emailLoginInfo.code"
                          type="text"
                          autocomplete="phone"
                        />
                        <VButton
                          :disabled="sendEmailDisabled"
                          style="margin-left: 15px"
                          color="primary"
                          @click="getEmailSms"
                        >
                          {{ sendEmailBtnText }}</VButton
                        >
                      </VControl>
                    </VField>
                  </VField>
                </template>
              </VTabs>
              <div class="button-wrap">
                <VButton color="white" size="big" lower rounded @click="step = 'login'">
                  密码登录
                </VButton>
                <VButton
                  color="primary"
                  size="big"
                  type="submit"
                  lower
                  rounded
                  solid
                  @click="handleOtherLogin"
                >
                  登录
                </VButton>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <VModal
      :open="wxPhoneBindModal"
      title="手机号绑定"
      size="small"
      actions="center"
      @close="wxPhoneBindModal = false"
    >
      <template #content>
        <!-- Login Form -->
        <div>
          <form class="login-wrapper" style="padding: 0">
            <VField>
              <VControl icon="lnir lnir-phone autv-icon">
                <VLabel class="auth-label">手机号</VLabel>
                <VInput
                  id="wxphone"
                  v-model="wxPhoneBind.phone"
                  autocomplete="current-password"
                />
              </VControl>
              <VField>
                <VControl class="is-flex" icon="lnir lnir-protection autv-icon">
                  <VLabel class="auth-label">验证码</VLabel>
                  <VInput
                    id="wxphoneCode"
                    v-model="wxPhoneBind.code"
                    type="text"
                    autocomplete="phone"
                  />
                  <VButton
                    :disabled="sendWxDisabled"
                    style="margin-left: 15px"
                    color="primary"
                    @click="getWxPhoneCode"
                  >
                    {{ sendWxBtnText }}</VButton
                  >
                </VControl>
              </VField>
            </VField>
            <VButton color="primary" style="width: 120px" @click="bindWxPhone">
              绑定
            </VButton>
          </form>
        </div>
      </template>
    </VModal>
  </div>
</template>

<style lang="scss" scoped>
.modern-login {
  position: relative;
  background: var(--white);
  min-height: 100vh;

  .column {
    &.is-relative {
      position: relative;
    }
  }

  .hero {
    &.has-background-image {
      position: relative;

      .hero-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: #5d4298 !important;
        opacity: 0.6;
      }
    }
  }

  .underlay {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 66.6%;
    height: 100%;
    background: #fdfdfd;
    z-index: 0;
  }

  .dark-mode {
    position: absolute;
    top: -25px;
    right: 38px;
    transform: scale(0.6);
    z-index: 2;
  }

  .top-logo {
    position: absolute;
    top: -30px;
    left: 0;
    right: 0;
    margin: 0 auto;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1;

    img {
      display: block;
      width: 100%;
      max-width: 50px;
      margin: 0 auto;
    }

    svg {
      height: 50px;
      width: 50px;
    }
  }

  .is-image {
    position: relative;
    border-right: 1px solid var(--fade-grey);

    .hero-image {
      position: relative;
      z-index: 2;
      display: block;
      margin: -80px auto 0;
      max-width: 60%;
      width: 60%;
    }
  }
}
.is-form {
  position: relative;
  max-width: 420px;
  margin: 0 auto;
  .vx-login {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    cursor: pointer;
    span {
      padding-left: 5px;
      color: var(--link);
    }
  }
  form {
    animation: fadeInLeft 0.5s;
  }

  .form-text {
    padding: 0 20px;
    animation: fadeInLeft 0.5s;

    h2 {
      font-family: var(--font-alt);
      font-weight: 400;
      font-size: 2rem;
      color: var(--primary);
    }

    p {
      color: var(--muted-grey);
      margin-top: 10px;
    }
  }

  .recover-text {
    font-size: 0.9rem;
    color: var(--dark-text);
  }
}
.login-wrapper {
  padding: 30px 20px;

  .control {
    position: relative;
    width: 100%;
    margin-top: 16px;

    .input {
      padding-top: 14px;
      height: 60px;
      border-radius: 10px;
      padding-left: 55px;
      transition: all 0.3s; // transition-all test

      &:focus {
        background: var(--fade-grey-light-6);
        border-color: var(--placeholder);

        ~ .auth-label,
        ~ .autv-icon i {
          color: var(--muted-grey);
        }
      }
    }

    .error-text {
      color: var(--danger);
      font-size: 0.8rem;
      display: none;
      padding: 2px 6px;
    }

    .auth-label {
      position: absolute;
      top: 6px;
      left: 55px;
      font-size: 0.8rem;
      color: var(--dark-text);
      font-weight: 500;
      z-index: 2;
      transition: all 0.3s; // transition-all test
    }

    .autv-icon,
    :deep(.autv-icon) {
      position: absolute;
      top: 0;
      left: 0;
      height: 60px;
      width: 60px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 24px;
      color: var(--placeholder);
      transition: all 0.3s;
    }

    &.has-validation {
      .validation-icon {
        position: absolute;
        top: 0;
        right: 0;
        height: 60px;
        width: 60px;
        display: none;
        justify-content: center;
        align-items: center;

        .icon-wrapper {
          height: 20px;
          width: 20px;
          display: flex;
          justify-content: center;
          align-items: center;
          border-radius: var(--radius-rounded);

          svg {
            height: 10px;
            width: 10px;
            stroke-width: 3px;
            color: var(--white);
          }
        }

        &.is-success {
          .icon-wrapper {
            background: var(--success);
          }
        }

        &.is-error {
          .icon-wrapper {
            background: var(--danger);
          }
        }
      }

      &.has-success {
        .validation-icon {
          &.is-success {
            display: flex;
          }

          &.is-error {
            display: none;
          }
        }
      }

      &.has-error {
        .input {
          border-color: var(--danger);
        }

        .error-text {
          display: block;
        }

        .validation-icon {
          &.is-error {
            display: flex;
          }

          &.is-success {
            display: none;
          }
        }
      }
    }

    &.is-flex {
      display: flex;
      align-items: center;

      a {
        display: block;
        margin-left: auto;
        color: var(--muted-grey);
        font-weight: 500;
        font-size: 0.9rem;
        transition: color 0.3s;

        &:hover,
        &:focus {
          color: var(--primary);
        }
      }

      .remember-me {
        font-size: 0.9rem;
        color: var(--muted-grey);
        font-weight: 500;
      }
    }
  }

  .button-wrap {
    margin: 40px 0;

    &.has-help {
      display: flex;
      align-items: center;

      > span {
        margin-left: 12px;
        font-family: var(--font);

        a {
          color: var(--primary);
          font-weight: 500;
          padding: 0 2px;
        }
      }
    }

    .button {
      height: 46px;
      width: 140px;
      margin-left: 6px;

      &:first-child {
        &:hover {
          opacity: 0.8;
        }
      }
    }
  }
}
.remember-toggle {
  width: 65px;
  display: block;
  position: relative;
  cursor: pointer;
  font-size: 22px;
  user-select: none;
  transform: scale(0.9);

  input {
    position: absolute;
    opacity: 0;
    cursor: pointer;

    &:checked ~ .toggler {
      border-color: var(--primary);

      .active,
      .inactive {
        transform: translateX(100%) rotate(360deg);
      }

      .active {
        opacity: 1;
      }

      .inactive {
        opacity: 0;
      }
    }
  }

  .toggler {
    position: relative;
    display: block;
    height: 34px;
    width: 61px;
    border: 2px solid var(--placeholder);
    border-radius: 100px;
    transition: all 0.3s; // transition-all test

    .active,
    .inactive {
      position: absolute;
      top: 2px;
      left: 2px;
      height: 26px;
      width: 26px;
      border-radius: var(--radius-rounded);
      background: black;
      display: flex;
      justify-content: center;
      align-items: center;
      transform: translateX(0) rotate(0);
      transition: all 0.3s ease;

      svg {
        color: var(--white);
        height: 14px;
        width: 14px;
        stroke-width: 3px;
      }
    }

    .inactive {
      background: var(--placeholder);
      border-color: var(--placeholder);
      opacity: 1;
      z-index: 1;
    }

    .active {
      background: var(--primary);
      border-color: var(--primary);
      opacity: 0;
      z-index: 0;
    }
  }
}

@media only screen and (max-width: 767px) {
  .modern-login {
    .top-logo {
      top: 30px;
    }

    .dark-mode {
      top: 36px;
      right: 44px;
    }

    .is-form {
      padding-top: 100px;
    }
  }
}

@media only screen and (min-width: 768px) and (max-width: 1024px) and (orientation: portrait) {
  .modern-login {
    .top-logo {
      svg {
        height: 60px;
        width: 60px;
      }
    }

    .dark-mode {
      top: -58px;
      right: 30%;
    }

    .columns {
      display: flex;
      height: 100vh;
    }
  }
}

/* ==========================================================================
  Dark mode
  ========================================================================== */

.is-dark {
  .modern-login {
    background: var(--dark-sidebar);

    .underlay {
      background: var(--dark-sidebar-light-10);
    }

    .is-image {
      border-color: var(--dark-sidebar-light-10);
    }

    .is-form {
      .form-text {
        h2 {
          color: var(--primary);
        }
      }

      .login-wrapper {
        .control {
          &.is-flex {
            a:hover {
              color: var(--primary);
            }
          }

          .input {
            background: var(--dark-sidebar-light-4);

            &:focus {
              border-color: var(--primary);

              ~ .autv-icon {
                i {
                  color: var(--primary);
                }
              }
            }
          }

          .auth-label {
            color: var(--light-text);
          }
        }

        .button-wrap {
          &.has-help {
            span {
              color: var(--light-text);

              a {
                color: var(--primary);
              }
            }
          }
        }
      }
    }
  }

  .remember-toggle {
    input {
      &:checked + .toggler {
        border-color: var(--primary);

        > span {
          background: var(--primary);
        }
      }
    }

    .toggler {
      border-color: var(--dark-sidebar-light-12);

      > span {
        background: var(--dark-sidebar-light-12);
      }
    }
  }
}
</style>
