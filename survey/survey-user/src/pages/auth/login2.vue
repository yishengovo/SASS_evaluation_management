<script setup lang="ts">
import { storeUserInfo } from '/@src/utils/storeUserInfo'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { useNotyf } from '/@src/composable/useNotyf'
// import { keRequest } from '/@src/utils/keRequest'
import { domainUserLogin, getImageCode, isDomainExist } from '/@src/api/auth'
import { useHead } from '@vueuse/head'
const isLoading = ref(false)
const router = useRouter()
const route = useRoute()
const notif = useNotyf()
const companyName = ref('')
const redirect = route.query.redirect as string
const rememberPwd = !!localStorage.getItem('pwd2') ? ref(true) : ref(false)
const loginLogic = (res: any) => {
  if (rememberPwd.value) {
    localStorage.setItem('pwd2', loginInfo.value.password)
  } else {
    localStorage.removeItem('pwd2')
  }
  if (res.data.code === 200) {
    localStorage.setItem('domainLogin', 'yes')
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
      message: `${res.data.message}`,
      notice_type: 'error',
    })
  }
}
const handleLogin = async () => {
  if (!isLoading.value) {
    isLoading.value = true
    const res = await domainUserLogin({
      username: loginInfo.value.userName,
      password: loginInfo.value.password,
      captcha: loginInfo.value.code,
      checkKey: currentTime,
      loginEnd: 1,
      realmName: location.hostname,
    })
    loginLogic(res)
    isLoading.value = false
  }
}

let currentTime = 0
const loginInfo = ref({
  userName: '',
  password: localStorage.getItem('pwd2') ?? '',
  code: '',
})
const codeImageUrl = ref('')
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
onMounted(async () => {
  const res = await isDomainExist(location.hostname)
  companyName.value = res.data.result
  changeImageCode()
})
useHead({
  title: '云测 - 租户登录',
})
</script>

<template>
  <div class="auth-wrapper-inner columns is-gapless">
    <!-- Image section (hidden on mobile) -->
    <div class="column login-column is-8 h-hidden-mobile h-hidden-tablet-p hero-banner">
      <div class="hero login-hero is-fullheight is-app-grey">
        <div class="hero-body">
          <div class="columns">
            <div class="column is-10 is-offset-1">
              <img
                class="light-image has-light-shadow has-light-border"
                src="/@src/assets/illustrations/apps/vuero-banking-light.webp"
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

    <!-- Form section -->
    <div class="column is-4">
      <div class="hero is-fullheight is-white">
        <div class="hero-heading">
          <div class="auth-logo">
            <RouterLink to="/">
              <AnimatedLogo width="36px" height="36px" />
            </RouterLink>
          </div>
        </div>
        <div class="hero-body">
          <div class="container">
            <div class="columns">
              <div class="column is-12">
                <div class="auth-content">
                  <h2>{{ companyName }}</h2>
                </div>
                <div class="auth-form-wrapper">
                  <!-- Login Form -->
                  <form @submit.prevent="handleLogin">
                    <div class="login-form">
                      <!-- Username -->
                      <VField>
                        <VControl icon="feather:user">
                          <VInput
                            v-model="loginInfo.userName"
                            type="text"
                            placeholder="用户名"
                            autocomplete="username"
                          />
                        </VControl>
                      </VField>

                      <!-- Password -->
                      <VField>
                        <VControl icon="feather:lock">
                          <VInput
                            v-model="loginInfo.password"
                            type="password"
                            placeholder="密码"
                            autocomplete="current-password"
                          />
                        </VControl>
                      </VField>
                      <VField>
                        <VControl class="is-flex" icon="lnir lnir-protection">
                          <VInput
                            id="code2"
                            v-model="loginInfo.code"
                            placeholder="验证码"
                            style="height: 38px"
                          />
                          <div style="margin-left: 10px" @click="changeImageCode">
                            <img
                              :src="codeImageUrl"
                              alt=""
                              style="
                                height: 38px;
                                border-radius: 3px;
                                display: block;
                                cursor: pointer;
                              "
                            />
                          </div>
                        </VControl>
                        <VField>
                          <VControl class="setting-item">
                            <VSwitchBlock
                              v-model="rememberPwd"
                              color="primary"
                              label="记住密码"
                            />
                          </VControl>
                        </VField>
                      </VField>
                      <!-- Submit -->
                      <div class="login">
                        <VButton
                          :loading="isLoading"
                          color="primary"
                          type="submit"
                          bold
                          fullwidth
                          raised
                        >
                          登录
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
  </div>
</template>
