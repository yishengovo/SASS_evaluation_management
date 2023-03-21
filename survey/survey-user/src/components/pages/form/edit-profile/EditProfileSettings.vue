<script setup lang="ts">
import wx from '/@src/assets/wx.png'
import { codeDecount } from '/@src/utils/codeDecount'
import { checkPhone, checkEmail, checkCode, checkPwd } from '/@src/utils/formCheck'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { codeLogic } from '/@src/utils/codeLogic'
import {
  bindDomain,
  getBindEmailCode,
  bindEmail,
  cancleDomianBind,
  emailBindChange,
  emailBindChangeCode,
  phoneBindChange,
  phoneBindChangeCode,
  resetPassword,
  resetPwdByPhone,
  resetPwdByEmail,
  wxBindApi,
  wxDeBind,
} from '/@src/api/auth'
import { useUserSession } from '/@src/stores/userSession'
const userSession = useUserSession()
const envAPI = import.meta.env.VITE_API_BASE_URL
const wx_url = envAPI + '/sys/thirdLogin/render/wechat_open'
const currentResetPwdWays = ref('phone')
const cancelVxBindModal = ref(false)
const changeEmailModal = ref(false)
const changePhoneModal = ref(false)
const passwordBindModal = ref(false)
const domainBindModal = ref(false)
const cancelDomainBindModal = ref(false)
const phoneCodeDisable = ref(false)
const phoneCodeText = ref('发送验证码')
const emailCodeDisable = ref(false)
const emailCodeText = ref('发送验证码')
const pEmailCodeDisable = ref(false)
const pEmailCodeText = ref('发送验证码')
const cEmailCodeDisable = ref(false)
const cEmailCodeText = ref('发送验证码')
const pPhoneCodeDisable = ref(false)
const pPhoneCodeText = ref('发送验证码')
const cPhoneCodeDisable = ref(false)
const cPhoneCodeText = ref('发送验证码')
const pwdCodeDisable = ref(false)
const pwdCodeText = ref('发送验证码')
const pwdECodeDisable = ref(false)
const pwdECodeText = ref('发送验证码')
const changePwdInfoByP = ref({
  userName: '',
  phone: '',
  code: '',
  newpassword: '',
  passwordCheck: '',
})
const changePwdInfoByE = ref({
  userName: '',
  email: '',
  code: '',
  newpassword: '',
  passwordCheck: '',
})
const currentCEmailProgress = ref(1)
const currentCPhoneProgress = ref(1)
const pEmailInfo = ref({
  email: '',
  code: '',
})
const cEmailInfo = ref({
  email: '',
  code: '',
})
const pPhoneInfo = ref({
  phone: '',
  code: '',
})
const cPhoneInfo = ref({
  phone: '',
  code: '',
})
const passwordInfo = ref({
  userName: '',
  oldPassword: '',
  newPassword: '',
  passwordCheck: '',
})
const phoneInfo = ref({
  phone: '',
  code: '',
})
const emailInfo = ref({
  email: '',
  code: '',
})
const domain = ref('')
const tabChange = (value: string) => {
  currentResetPwdWays.value = value
}
const getPhoneCode = () => {
  if (checkPhone(phoneInfo.value.phone)) {
    codeDecount(phoneCodeDisable, phoneCodeText)
  }
}
const getEmailCode = async () => {
  if (checkEmail(emailInfo.value.email)) {
    codeDecount(emailCodeDisable, emailCodeText)
  }
  const res = await getBindEmailCode(emailInfo.value.email)
  codeLogic(res)
}
const passwordChange = async () => {
  if (passwordInfo.value.userName.length > 0) {
    if (checkPwd(passwordInfo.value.newPassword, passwordInfo.value.passwordCheck)) {
      const res = await resetPassword({
        username: passwordInfo.value.userName,
        oldpassword: passwordInfo.value.oldPassword,
        password: passwordInfo.value.newPassword,
        confirmpassword: passwordInfo.value.passwordCheck,
      })
      if (res.data.code === 200) {
        Notice({
          message: '密码修改成功',
          notice_type: 'success',
        })
        passwordInfo.value = {
          userName: '',
          oldPassword: '',
          newPassword: '',
          passwordCheck: '',
        }
      } else {
        Notice({
          message: res.data.message,
          notice_type: 'error',
        })
      }
    }
  } else {
    Notice({
      notice_type: 'error',
      message: '用户名不允许为空!',
    })
  }
}
const phoneChange = () => {}
const emailChange = async () => {
  if (checkEmail(emailInfo.value.email) && checkCode(emailInfo.value.code)) {
    const { data: res } = await bindEmail({
      email: emailInfo.value.email,
      emailCode: emailInfo.value.code,
    })
    if (res.code !== 200) {
      Notice({
        notice_type: 'error',
        message: res.message,
      })
    } else {
      Notice({
        notice_type: 'success',
        message: '绑定成功',
      })
    }
    userSession.setUserInfo('email', emailInfo.value.email)
    emailInfo.value.email = ''
    emailInfo.value.code = ''
  }
}

const openBindDomain = () => {
  domainBindModal.value = true
}
const confirmBIndDomain = async () => {
  if (domain.value.length > 0) {
    const { data: res } = await bindDomain({
      realmName: (domain.value + '.stalent.net').toLowerCase(),
    })

    if (res.code !== 200) {
      Notice({
        notice_type: 'error',
        message: res.message,
      })
    } else {
      Notice({
        notice_type: 'success',
        message: '绑定成功',
      })
      userSession.setDomain((domain.value + '.stalent.net').toLowerCase())
    }
    domainBindModal.value = false
    domain.value = ''
  } else {
    Notice({
      notice_type: 'error',
      message: '域名不能为空!',
    })
  }
}
const cancleBIndDomain = () => {
  cancelDomainBindModal.value = true
}
const confirmCancleBIndDomain = async () => {
  const { data: res } = await cancleDomianBind()
  if (res.code !== 200) {
    Notice({
      notice_type: 'error',
      message: res.message,
    })
  } else {
    Notice({
      notice_type: 'success',
      message: '取消绑定成功',
    })
    userSession.setDomain('')
  }
  cancelDomainBindModal.value = false
}

const bindPassword = () => {
  passwordBindModal.value = true
}
const phonePwdChange = async () => {
  if (currentResetPwdWays.value === 'phone') {
    if (
      checkPhone(changePwdInfoByP.value.phone) &&
      checkCode(changePwdInfoByP.value.code) &&
      checkPwd(changePwdInfoByP.value.newpassword, changePwdInfoByP.value.passwordCheck)
    ) {
      const res = await resetPwdByPhone({
        username: changePwdInfoByP.value.userName,
        password: changePwdInfoByP.value.newpassword,
        smscode: changePwdInfoByP.value.code,
        phone: changePwdInfoByP.value.phone,
      })
      if (res.data.code === 0) {
        Notice({
          notice_type: 'success',
          message: res.data.message,
        })
        changePwdInfoByP.value = {
          userName: '',
          newpassword: '',
          passwordCheck: '',
          code: '',
          phone: '',
        }
      } else {
        Notice({
          notice_type: 'error',
          message: res.data.message,
        })
      }
    }
  } else {
    if (
      checkEmail(changePwdInfoByE.value.email) &&
      checkCode(changePwdInfoByE.value.code) &&
      checkPwd(changePwdInfoByE.value.newpassword, changePwdInfoByE.value.passwordCheck)
    ) {
      const res = await resetPwdByEmail({
        username: changePwdInfoByE.value.userName,
        password: changePwdInfoByE.value.newpassword,
        emialcode: changePwdInfoByE.value.code,
        email: changePwdInfoByE.value.email,
      })
      if (res.data.code === 0) {
        Notice({
          notice_type: 'success',
          message: res.data.message,
        })
        changePwdInfoByE.value = {
          userName: '',
          newpassword: '',
          passwordCheck: '',
          code: '',
          email: '',
        }
      } else {
        Notice({
          notice_type: 'error',
          message: res.data.message,
        })
      }
    }
  }
  passwordBindModal.value = false
}
// 手机号找回密码code
const getPwdPhoneCode = async () => {
  if (checkPhone(changePwdInfoByP.value.phone)) {
    codeDecount(pwdCodeDisable, pwdCodeText)
    const res = await await phoneBindChangeCode({
      mobile: changePwdInfoByP.value.phone,
      rebindFlag: 3,
    })
    codeLogic(res)
  }
}
// 邮箱找回密码code
const getPwdEmaileCode = async () => {
  if (checkEmail(changePwdInfoByE.value.email)) {
    codeDecount(pwdECodeDisable, pwdECodeText)
    const res = await emailBindChangeCode({
      email: changePwdInfoByE.value.email,
      rebindFlag: 3,
    })
    codeLogic(res)
  }
}
// 换绑邮箱
const openEmailChange = () => {
  changeEmailModal.value = true
}
const confirmEmailBindChange = async () => {
  if (currentCEmailProgress.value === 1) {
    if (checkEmail(pEmailInfo.value.email) && checkCode(pEmailInfo.value.code)) {
      const res = await emailBindChange({
        email: pEmailInfo.value.email,
        emailCode: pEmailInfo.value.code,
        rebindFlag: 1,
      })
      if (res.data.code === 200) {
        currentCEmailProgress.value++
      } else {
        Notice({
          message: res.data.message,
          notice_type: 'error',
        })
      }
    }
  } else {
    if (checkEmail(cEmailInfo.value.email) && checkCode(cEmailInfo.value.code)) {
      const res = await emailBindChange({
        email: cEmailInfo.value.email,
        emailCode: cEmailInfo.value.code,
        rebindFlag: 2,
      })
      if (res.data.code === 200) {
        changeEmailModal.value = false
        currentCEmailProgress.value = 1
        Notice({
          message: '邮箱换绑成功!',
          notice_type: 'success',
        })
        userSession.setUserInfo('email', cEmailInfo.value.email)
        clearEmailModal()
      } else {
        Notice({
          message: res.data.message,
          notice_type: 'error',
        })
      }
    }
  }
}
const getPEmailCode = async () => {
  if (checkEmail(pEmailInfo.value.email)) {
    codeDecount(pEmailCodeDisable, pEmailCodeText)
    const res = await emailBindChangeCode({
      email: pEmailInfo.value.email,
      rebindFlag: 1,
    })
    codeLogic(res)
  }
}
const getCEmailCode = async () => {
  if (checkEmail(cEmailInfo.value.email)) {
    codeDecount(cEmailCodeDisable, cEmailCodeText)
    const res = await emailBindChangeCode({
      email: cEmailInfo.value.email,
      rebindFlag: 2,
    })
    codeLogic(res)
  }
}
const clearEmailModal = () => {
  pEmailInfo.value.code = ''
  pEmailInfo.value.email = ''
  cEmailInfo.value.code = ''
  pEmailInfo.value.email = ''
}
const cancleChangeEmailBind = () => {
  changeEmailModal.value = false
  clearEmailModal()
}
//换绑手机号
const openPhoneChange = () => {
  changePhoneModal.value = true
}
const confirmPhoneBindChange = async () => {
  if (currentCPhoneProgress.value === 1) {
    if (checkPhone(pPhoneInfo.value.phone) && checkCode(pPhoneInfo.value.code)) {
      const res = await phoneBindChange({
        phone: pPhoneInfo.value.phone,
        phoneCode: pPhoneInfo.value.code,
        rebindFlag: 1,
      })
      console.log(res.data)
      if (res.data.code === 200) {
        currentCPhoneProgress.value++
      } else {
        Notice({
          message: res.data.message,
          notice_type: 'error',
        })
      }
    }
  } else {
    if (checkPhone(cPhoneInfo.value.phone) && checkCode(cPhoneInfo.value.code)) {
      const res = await phoneBindChange({
        phone: cPhoneInfo.value.phone,
        phoneCode: cPhoneInfo.value.code,
        rebindFlag: 2,
      })
      if (res.data.code === 200) {
        changePhoneModal.value = false
        currentCPhoneProgress.value = 1
        Notice({
          message: '手机号换绑成功!',
          notice_type: 'success',
        })
        userSession.setUserInfo('phone', cPhoneInfo.value.phone)
        clearPhoneModel()
      } else {
        Notice({
          message: res.data.message,
          notice_type: 'error',
        })
      }
    }
  }
}
const getPPhoneCode = async () => {
  if (checkPhone(pPhoneInfo.value.phone)) {
    codeDecount(pPhoneCodeDisable, pPhoneCodeText)
    const res = await phoneBindChangeCode({
      mobile: pPhoneInfo.value.phone,
      rebindFlag: 1,
    })
    codeLogic(res)
  }
}
const getCPhoneCode = async () => {
  if (checkPhone(cPhoneInfo.value.phone)) {
    codeDecount(cPhoneCodeDisable, cPhoneCodeText)
    const res = await phoneBindChangeCode({
      mobile: cPhoneInfo.value.phone,
      rebindFlag: 2,
    })
    codeLogic(res)
  }
}
const clearPhoneModel = () => {
  pPhoneInfo.value.code = ''
  pPhoneInfo.value.phone = ''
  cPhoneInfo.value.code = ''
  pPhoneInfo.value.phone = ''
}
const deBindWx = async () => {
  cancelVxBindModal.value = true
}
const confirmCancleBIndWx = async () => {
  cancelVxBindModal.value = false
  const res = await wxDeBind()
  if (res.data.code !== 200) {
    Notice({
      notice_type: 'error',
      message: res.data.message,
    })
  } else {
    Notice({
      notice_type: 'success',
      message: '解绑成功',
    })
    userSession.setUserInfo('wechatNickname', '')
  }
}
const bindWx = () => {
  let url = wx_url
  window.open(
    url,
    `login wechat_open`,
    'height=500, width=500, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no'
  )
  let receiveMessage = async function (event: any) {
    let token = event.data
    if (typeof token === 'string') {
      //如果是字符串类型 说明是token信息
      if (token === '登录失败') {
        Notice({
          notice_type: 'warning',
          message: token,
        })
      } else if (token.includes('绑定手机号')) {
        // that.bindingPhoneModal = true
        const strings = token.split(',')
        const thirdUserUuid = strings[1]
        const res = await wxBindApi(thirdUserUuid)
        if (res.data.code !== 200) {
          Notice({
            notice_type: 'warning',
            message: res.data.message,
          })
        } else {
          Notice({
            notice_type: 'success',
            message: '绑定成功',
          })
          userSession.setUserInfo('wechatNickname', res.data.result)
        }
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
}
const cancleChangePhoneBind = () => {
  changePhoneModal.value = false
  clearPhoneModel()
}
</script>

<template>
  <div class="account-box is-form is-footerless">
    <div class="form-head stuck-header">
      <div class="form-head-inner">
        <div class="left">
          <h3>账号安全设置</h3>
          <p>编辑第三账号以及密码</p>
        </div>
        <div class="right">
          <!-- <div class="buttons">
            <VButton
              to="/sidebar/layouts/profile-view"
              icon="lnir lnir-arrow-left rem-100"
              light
              dark-outlined
            >
              Go Back
            </VButton>
            <VButton
              color="primary"
              raised
              :loading="isLoading"
              tabindex="0"
              @keydown.space.prevent="onSave"
              @click="onSave"
            >
              Save Changes
            </VButton>
          </div> -->
        </div>
      </div>
    </div>
    <form class="form-body">
      <!--Fieldset-->
      <div class="fieldset">
        <div class="fieldset-heading">
          <h4>第三方账号设置</h4>
        </div>

        <div class="columns is-multiline">
          <!--域名-->
          <div class="column is-12 bind">
            <div class="domain">
              <i class="lnir lnir-link" aria-hidden="true"></i><span>自定义域名</span>
            </div>
            <div>
              <template v-if="userSession.user?.domain">
                <span>{{ userSession.user?.domain }}</span>
                <a style="padding: 0 10px" @click="cancleBIndDomain">取消绑定</a>
                <a @click="openBindDomain">换绑</a>
              </template>
              <template v-else>
                <a @click="openBindDomain">设置</a>
              </template>
            </div>
          </div>
          <template v-if="userSession.user?.email">
            <!--邮箱换绑-->
            <div class="column is-12 bind">
              <div class="domain">
                <i class="lnir lnir-message-attachement" aria-hidden="true"></i
                ><span>邮箱</span>
              </div>
              <div>
                <span style="padding-right: 5px">{{ userSession.user?.email }}</span>
                <a @click="openEmailChange">换绑</a>
              </div>
            </div>
          </template>
          <!--手机号换绑-->
          <template v-if="userSession.user?.phone">
            <div class="column is-12 bind">
              <div class="domain">
                <i class="lnir lnir-phone-redial" aria-hidden="true"></i
                ><span>手机号</span>
              </div>
              <div>
                <span style="padding-right: 5px">
                  {{
                    userSession.user?.phone.slice(0, 3) +
                    '****' +
                    userSession.user?.phone.slice(-4)
                  }}</span
                >

                <a @click="openPhoneChange">换绑</a>
              </div>
            </div>
          </template>
          <!--微信-->
          <div class="column is-12 bind">
            <div class="wx"><img :src="wx" alt="" /><span>微信</span></div>
            <template v-if="!!userSession.user?.wechatNickname">
              <span>
                <span>{{ userSession.user?.wechatNickname }}</span>
                <a style="padding-left: 5px" @click="deBindWx">解绑</a></span
              >
            </template>
            <template v-else> <a @click="bindWx">绑定微信</a></template>
          </div>
        </div>
      </div>
      <!--邮箱-->
      <template v-if="!userSession.user?.email">
        <div class="fieldset">
          <div class="fieldset-heading">
            <h4>邮箱绑定</h4>
          </div>
          <div class="columns is-multiline">
            <!--Field-->
            <div class="column is-12">
              <VField>
                <VControl icon="lnir lnir-message-edit">
                  <VInput
                    v-model="emailInfo.email"
                    type="text"
                    placeholder="邮箱"
                    autocomplete="tel"
                    inputmode="tel"
                  />
                </VControl>
              </VField>
              <VField class="is-flex">
                <VControl icon="lnir lnir-protection">
                  <VInput
                    v-model="emailInfo.code"
                    type="text"
                    placeholder="验证码"
                    autocomplete="off"
                    inputmode="off"
                    style="width: 120px"
                  />
                </VControl>
                <VButton
                  style="margin-left: 15px"
                  :disabled="emailCodeDisable"
                  @click="getEmailCode"
                  >{{ emailCodeText }}</VButton
                >
              </VField>
            </div>
          </div>
          <VButton color="primary" @click="emailChange">绑定邮箱</VButton>
        </div>
      </template>
      <!--手机号-->
      <template v-if="!userSession.user?.phone">
        <div class="fieldset">
          <div class="fieldset-heading">
            <h4>手机号绑定</h4>
          </div>
          <div class="columns is-multiline">
            <div class="column is-12">
              <VField>
                <VControl icon="feather:smartphone">
                  <VInput
                    v-model="phoneInfo.phone"
                    type="text"
                    placeholder="手机号码"
                    autocomplete="tel"
                    inputmode="tel"
                  />
                </VControl>
              </VField>
              <VField class="is-flex">
                <VControl icon="lnir lnir-protection">
                  <VInput
                    v-model="phoneInfo.code"
                    type="text"
                    placeholder="验证码"
                    autocomplete="off"
                    inputmode="off"
                    style="width: 120px"
                  />
                </VControl>
                <VButton
                  style="margin-left: 15px"
                  :disabled="phoneCodeDisable"
                  @click="getPhoneCode"
                  >{{ phoneCodeText }}</VButton
                >
              </VField>
            </div>
          </div>
          <VButton color="primary" @click="phoneChange">绑定手机号</VButton>
        </div>
      </template>
      <!--密码修改-->
      <div class="fieldset">
        <div class="fieldset-heading">
          <h4>密码修改</h4>
        </div>

        <div class="columns is-multiline">
          <!--Field-->
          <div class="column is-12">
            <VField>
              <VControl icon="lnir lnir-user-alt-1 ">
                <VInput
                  v-model="passwordInfo.userName"
                  type="text"
                  placeholder="用户名"
                  autocomplete="current-password"
                />
              </VControl>
            </VField>
          </div>
          <!--Field-->
          <div class="column is-12">
            <VField>
              <VControl icon="feather:unlock">
                <VInput
                  v-model="passwordInfo.oldPassword"
                  type="password"
                  placeholder="旧密码"
                  autocomplete="current-password"
                />
              </VControl>
            </VField>
          </div>
          <!--Field-->
          <div class="column is-12">
            <VField>
              <VControl icon="feather:lock">
                <VInput
                  v-model="passwordInfo.newPassword"
                  type="password"
                  placeholder="新密码"
                  autocomplete="new-password"
                />
              </VControl>
            </VField>
          </div>
          <!--Field-->
          <div class="column is-12">
            <VField>
              <VControl icon="feather:lock">
                <VInput
                  v-model="passwordInfo.passwordCheck"
                  type="password"
                  placeholder="重复新密码"
                  autocomplete="new-password"
                />
              </VControl>
            </VField>
          </div>
        </div>
        <div class="password-btn">
          <VButton color="primary" @click="passwordChange">保存修改</VButton>
          <span>忘记初始密码? <a @click="bindPassword">找回</a> </span>
        </div>
      </div>
    </form>
    <!-- 域名绑定 -->
    <VModal
      :open="domainBindModal"
      title="绑定域名"
      size="small"
      actions="right"
      @close="domainBindModal = false"
    >
      <template #content>
        <div class="fieldset">
          <div class="columns is-multiline">
            <div class="column is-12">
              <VField class="is-flex" style="align-items: center">
                <span style="padding-right: 10px">https://</span>
                <VControl>
                  <VInput
                    v-model="domain"
                    type="text"
                    placeholder="域名"
                    autocomplete="tel"
                    inputmode="tel"
                  />
                </VControl>
                <span style="padding-left: 10px">.stalent.net</span>
              </VField>
              <VField>
                <div>
                  例如: https://<span style="color: red; padding: 0 5px">abc</span
                  >.stalent.net
                </div>
              </VField>
            </div>
          </div>
        </div>
      </template>
      <template #action>
        <VButton color="primary" @click="confirmBIndDomain">绑定</VButton>
      </template>
    </VModal>
    <!-- 密码找回 -->
    <VModal
      :open="passwordBindModal"
      title="忘记密码"
      size="small"
      actions="right"
      @close="passwordBindModal = false"
    >
      <template #content>
        <VTabs
          :selected="currentResetPwdWays"
          :tabs="[
            { label: '手机号找回', value: 'phone', icon: 'lnir lnir-phone' },
            { label: '邮箱找回', value: 'email', icon: 'lnir lnir-message-edit' },
          ]"
          @update:selected="tabChange"
        >
          <template #tab="{ activeValue }">
            <div v-if="activeValue === 'phone'">
              <div class="fieldset">
                <div class="columns is-multiline">
                  <!--Field-->
                  <div class="column is-12">
                    <VField>
                      <VControl icon="lnir lnir-user-alt-1 ">
                        <VInput
                          v-model="changePwdInfoByP.userName"
                          type="text"
                          placeholder="用户名"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                  </div>
                  <div class="column is-12">
                    <VField>
                      <VControl icon="feather:smartphone">
                        <VInput
                          v-model="changePwdInfoByP.phone"
                          type="text"
                          placeholder="手机号码"
                          autocomplete="tel"
                          inputmode="tel"
                        />
                      </VControl>
                    </VField>
                    <VField class="is-flex">
                      <VControl icon="lnir lnir-protection">
                        <VInput
                          v-model="changePwdInfoByP.code"
                          type="text"
                          placeholder="验证码"
                          autocomplete="off"
                          inputmode="off"
                          style="width: 120px"
                        />
                      </VControl>
                      <VButton
                        style="margin-left: 15px"
                        :disabled="pwdCodeDisable"
                        @click="getPwdPhoneCode"
                        >{{ pwdCodeText }}</VButton
                      >
                    </VField>
                    <VField>
                      <VControl icon="feather:lock">
                        <VInput
                          v-model="changePwdInfoByP.newpassword"
                          type="password"
                          placeholder="新密码"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                    <VField>
                      <VControl icon="feather:lock">
                        <VInput
                          v-model="changePwdInfoByP.passwordCheck"
                          type="password"
                          placeholder="重复新密码"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                  </div>
                </div>
              </div>
            </div>
            <div v-else-if="activeValue === 'email'">
              <div class="fieldset">
                <div class="columns is-multiline">
                  <!--Field-->
                  <div class="column is-12">
                    <VField>
                      <VControl icon="lnir lnir-user-alt-1 ">
                        <VInput
                          v-model="changePwdInfoByE.userName"
                          type="text"
                          placeholder="用户名"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                  </div>
                  <div class="column is-12">
                    <VField>
                      <VControl icon="lnir lnir-message-edit">
                        <VInput
                          v-model="changePwdInfoByE.email"
                          type="text"
                          placeholder="邮箱号"
                          autocomplete="email"
                          inputmode="email"
                        />
                      </VControl>
                    </VField>
                    <VField class="is-flex">
                      <VControl icon="lnir lnir-protection">
                        <VInput
                          v-model="changePwdInfoByE.code"
                          type="text"
                          placeholder="验证码"
                          autocomplete="off"
                          inputmode="off"
                          style="width: 120px"
                        />
                      </VControl>
                      <VButton
                        style="margin-left: 15px"
                        :disabled="pwdECodeDisable"
                        @click="getPwdEmaileCode"
                        >{{ pwdECodeText }}</VButton
                      >
                    </VField>
                    <VField>
                      <VControl icon="feather:lock">
                        <VInput
                          v-model="changePwdInfoByE.newpassword"
                          type="password"
                          placeholder="新密码"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                    <VField>
                      <VControl icon="feather:lock">
                        <VInput
                          v-model="changePwdInfoByE.passwordCheck"
                          type="password"
                          placeholder="重复新密码"
                          autocomplete="current-password"
                        />
                      </VControl>
                    </VField>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </VTabs>
      </template>
      <template #action>
        <VButton color="primary" @click="phonePwdChange">重置</VButton>
      </template>
    </VModal>

    <!-- 邮箱换绑 -->
    <VModal
      :open="changeEmailModal"
      title="邮箱换绑"
      size="small"
      actions="right"
      @close="cancleChangeEmailBind"
    >
      <template #content>
        <template v-if="currentCEmailProgress === 1">
          <div class="fieldset">
            <div class="columns is-multiline">
              <div class="column is-12">
                <VField>
                  <VControl icon="lnir lnir-message-edit">
                    <VInput
                      v-model="pEmailInfo.email"
                      type="text"
                      placeholder="原邮箱"
                      autocomplete="tel"
                      inputmode="email"
                    />
                  </VControl>
                </VField>
                <VField class="is-flex">
                  <VControl icon="lnir lnir-protection">
                    <VInput
                      v-model="pEmailInfo.code"
                      type="text"
                      placeholder="验证码"
                      autocomplete="tel"
                      inputmode="off"
                      style="width: 120px"
                    />
                  </VControl>
                  <VButton
                    style="margin-left: 15px"
                    :disabled="pEmailCodeDisable"
                    @click="getPEmailCode"
                    >{{ pEmailCodeText }}</VButton
                  >
                </VField>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="fieldset">
            <div class="columns is-multiline">
              <div class="column is-12">
                <VField>
                  <VControl icon="lnir lnir-message-edit">
                    <VInput
                      v-model="cEmailInfo.email"
                      type="text"
                      placeholder="换绑邮箱"
                      autocomplete="email"
                      inputmode="email"
                    />
                  </VControl>
                </VField>
                <VField class="is-flex">
                  <VControl icon="lnir lnir-protection">
                    <VInput
                      v-model="cEmailInfo.code"
                      type="text"
                      placeholder="验证码"
                      autocomplete="off"
                      inputmode="off"
                      style="width: 120px"
                    />
                  </VControl>
                  <VButton
                    style="margin-left: 15px"
                    :disabled="cEmailCodeDisable"
                    @click="getCEmailCode"
                    >{{ cEmailCodeText }}</VButton
                  >
                </VField>
              </div>
            </div>
          </div>
        </template>
      </template>
      <template #action>
        <VButton color="primary" @click="confirmEmailBindChange">{{
          currentCPhoneProgress === 1 ? '下一步' : '换绑'
        }}</VButton>
      </template>
    </VModal>
    <!-- 手机号换绑 -->
    <VModal
      :open="changePhoneModal"
      title="手机号换绑"
      size="small"
      actions="right"
      @close="cancleChangePhoneBind"
    >
      <template #content>
        <template v-if="currentCPhoneProgress === 1">
          <div class="fieldset">
            <div class="columns is-multiline">
              <div class="column is-12">
                <VField>
                  <VControl icon="lnir lnir-phone-redial">
                    <VInput
                      v-model="pPhoneInfo.phone"
                      type="text"
                      placeholder="原手机"
                      autocomplete="tel"
                      inputmode="email"
                    />
                  </VControl>
                </VField>
                <VField class="is-flex">
                  <VControl icon="lnir lnir-protection">
                    <VInput
                      v-model="pPhoneInfo.code"
                      type="text"
                      placeholder="验证码"
                      autocomplete="tel"
                      inputmode="off"
                      style="width: 120px"
                    />
                  </VControl>
                  <VButton
                    style="margin-left: 15px"
                    :disabled="pPhoneCodeDisable"
                    @click="getPPhoneCode"
                    >{{ pPhoneCodeText }}</VButton
                  >
                </VField>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="fieldset">
            <div class="columns is-multiline">
              <div class="column is-12">
                <VField>
                  <VControl icon="lnir lnir-phone-redial">
                    <VInput
                      v-model="cPhoneInfo.phone"
                      type="text"
                      placeholder="换绑手机"
                      autocomplete="email"
                      inputmode="email"
                    />
                  </VControl>
                </VField>
                <VField class="is-flex">
                  <VControl icon="lnir lnir-protection">
                    <VInput
                      v-model="cPhoneInfo.code"
                      type="text"
                      placeholder="验证码"
                      autocomplete="off"
                      inputmode="off"
                      style="width: 120px"
                    />
                  </VControl>
                  <VButton
                    style="margin-left: 15px"
                    :disabled="cPhoneCodeDisable"
                    @click="getCPhoneCode"
                    >{{ cPhoneCodeText }}</VButton
                  >
                </VField>
              </div>
            </div>
          </div>
        </template>
      </template>
      <template #action>
        <VButton color="primary" @click="confirmPhoneBindChange">{{
          currentCPhoneProgress === 1 ? '下一步' : '换绑'
        }}</VButton>
      </template>
    </VModal>
    <!--取消绑定确认-->
    <VModal
      :open="cancelDomainBindModal"
      title="解绑域名"
      size="small"
      actions="right"
      @close="cancelDomainBindModal = false"
    >
      <template #content>
        <div>取消绑定后此域名将不能被访问,确定取消吗?</div>
      </template>
      <template #action>
        <VButton color="primary" @click="confirmCancleBIndDomain">确定</VButton>
      </template>
    </VModal>
    <VModal
      :open="cancelVxBindModal"
      title="解绑微信"
      size="small"
      actions="right"
      @close="cancelVxBindModal = false"
    >
      <template #content>
        <div>确定要取消绑定微信吗?</div>
      </template>
      <template #action>
        <VButton color="primary" @click="confirmCancleBIndWx">确定</VButton>
      </template>
    </VModal>
  </div>
</template>
<style lang="scss" scoped>
.password-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.bind {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .wx {
    display: flex;
    span {
      padding-left: 5px;
      font-size: 16px;
      align-self: center;
    }
  }
  .domain {
    display: flex;
    justify-content: center;
    align-items: center;
    i {
      font-size: 18px;
      padding-right: 5px;
      color: var(--link);
    }
    span {
      width: 8em;
    }
  }
}
</style>
