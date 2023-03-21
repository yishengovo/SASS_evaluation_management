<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-09-23 11:21:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-30 19:20:26
 * @Description: file content
-->
<script setup lang="ts">
import { useUserSession } from '/@src/stores/userSession'
import { clearCookieByKey } from '/@src/utils/cookie'
const session = useUserSession()
const router = useRouter()
// const goProject = () => {
//   router.push('/home/all-project')
// }
const loginOut = () => {
  clearCookieByKey('hrtools_token')
  clearCookieByKey('hrtools_tanantId')
  session.logoutUser()
  localStorage.removeItem('hasLogin')

  if (localStorage.getItem('domainLogin') === 'yes') {
    router.push('/auth/login2')
  } else {
    router.push('/auth/login')
  }
}
const goHistoryOrder = () => {
  router.push('/auth/allOrder')
}
const goUserInfo = () => {
  router.push('/sidebar/layouts/profile-view')
}
</script>
<template>
  <VDropdown right spaced class="user-dropdown profile-dropdown">
    <template #button="{ toggle }">
      <a
        tabindex="0"
        class="is-trigger dropdown-trigger"
        aria-haspopup="true"
        @keydown.space.prevent="toggle"
        @click="toggle"
      >
        <VAvatar
          :picture="
            session.user?.avatar
              ? session.user?.avatar
              : '/images/avatars/svg/vuero-1.svg'
          "
        />
      </a>
    </template>

    <template #content>
      <div class="dropdown-head">
        <VAvatar
          size="large"
          :picture="
            session.user?.avatar
              ? session.user?.avatar
              : '/images/avatars/svg/vuero-1.svg'
          "
        />

        <div class="meta">
          <span>{{ session.user?.name }}</span>
          <span>账号ID: {{ session.tenantId }}</span>
        </div>
      </div>

      <!-- <a href="#" role="menuitem" class="dropdown-item is-media">
        <div class="icon">
          <i aria-hidden="true" class="lnil lnil-user-alt"></i>
        </div>
        <div class="meta">
          <span>Profile</span>
          <span>View your profile</span>
        </div>
      </a> -->

      <!-- <a href="#" role="menuitem" class="dropdown-item is-media">
        <div class="icon">
          <i aria-hidden="true" class="lnil lnil-users-alt"></i>
        </div>
        <div class="meta">
          <span>Team</span>
          <span>管理你的团队</span>
        </div>
      </a> -->

      <hr class="dropdown-divider" />

      <a role="menuitem" class="dropdown-item is-media" @click="goUserInfo">
        <div class="icon">
          <i aria-hidden="true" class="lnil lnil-cog"></i>
        </div>
        <div class="meta">
          <span>设置</span>
          <span>账号设置</span>
        </div>
      </a>
      <hr class="dropdown-divider" />
      <a role="menuitem" class="dropdown-item is-media" @click="goHistoryOrder">
        <div class="icon">
          <i class="lnir lnir-ticket-alt-3" aria-hidden="true"></i>
        </div>
        <div class="meta">
          <span>订单</span>
          <span>历史订单</span>
        </div>
      </a>
      <hr class="dropdown-divider" />

      <div class="dropdown-item is-button">
        <VButton
          class="logout-button"
          icon="feather:log-out"
          color="primary"
          role="menuitem"
          raised
          fullwidth
          @click="loginOut"
        >
          登出
        </VButton>
      </div>
    </template>
  </VDropdown>
</template>
