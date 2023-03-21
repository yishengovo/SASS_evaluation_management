<!--
 * @Author: Augut Rush
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 14:15:13
 * @FilePath: \survey-user\src\layouts\NavbarDropdownLayout.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import type { UserPopover } from '/@src/models/users'
import type { VAvatarColor, VAvatarProps } from '/@src/components/base/avatar/VAvatar.vue'
import { popovers } from '/@src/data/users/userPopovers'
import { useViewWrapper } from '/@src/stores/viewWrapper'
import { usePanels } from '/@src/stores/panels'

type NavbarDropdownTheme = 'default' | 'colored' | 'fade'

const props = withDefaults(
  defineProps<{
    theme?: NavbarDropdownTheme
    nowrap?: boolean
    active?: number
    openOnMounted?: boolean
  }>(),
  {
    theme: 'default',
    active: 1,
  }
)

const viewWrapper = useViewWrapper()
const panels = usePanels()
const route = useRoute()
const router = useRouter()
const displaySearch = ref(false)
const isMobileSidebarOpen = ref(false)
const activeMobileSubsidebar = ref('dashboard')
// const isDesktopSideblockOpen = ref(props.openOnMounted)

const filter = ref('')
const filteredUsers = computed(() => {
  if (!filter.value) {
    return []
  }

  return Object.values(popovers).filter((user) => {
    return (
      user.fullName.match(new RegExp(filter.value, 'i')) ||
      user.position.match(new RegExp(filter.value, 'i'))
    )
  })
})

function getAvatarData(user: UserPopover): VAvatarProps {
  return {
    picture: user.avatar,
    initials: user.initials,
    color: user.color as VAvatarColor,
  }
}

watch(
  () => route.fullPath,
  () => {
    isMobileSidebarOpen.value = false
  }
)
// function toggle() {
//   console.log(111)
// }
// 处理路由跳转
const handleNav = (link: string) => {
  router.push(link)
}
</script>

<template>
  <div class="navbar-layout">
    <div class="app-overlay"></div>

    <!-- Mobile navigation -->
    <MobileNavbar
      :is-open="isMobileSidebarOpen"
      @toggle="isMobileSidebarOpen = !isMobileSidebarOpen"
    >
      <template #brand>
        <RouterLink to="/" class="navbar-item is-brand">
          <AnimatedLogo width="80px" />
        </RouterLink>

        <div class="brand-end">
          <NotificationsMobileDropdown />
          <UserProfileDropdown />
        </div>
      </template>
    </MobileNavbar>

    <!-- Mobile sidebar links -->
    <MobileSidebar
      :is-open="isMobileSidebarOpen"
      @toggle="isMobileSidebarOpen = !isMobileSidebarOpen"
    >
      <template #links>
        <!-- <li>
          <a
            :class="[activeMobileSubsidebar === 'layout' && 'is-active']"
            tabindex="0"
            @keydown.space.prevent="activeMobileSubsidebar = 'layout'"
            @click="activeMobileSubsidebar = 'layout'"
          >
            <i aria-hidden="true" class="iconify" data-icon="feather:grid"></i>
          </a>
        </li> -->
        <li
          :class="[activeMobileSubsidebar === 'elements' && 'is-active']"
          tabindex="0"
          @keydown.space.prevent="activeMobileSubsidebar = 'elements'"
          @click="activeMobileSubsidebar = 'elements'"
        >
          <a>
            <i aria-hidden="true" class="iconify" data-icon="feather:box"></i>
          </a>
        </li>
        <li>
          <a
            :class="[activeMobileSubsidebar === 'dashboard' && 'is-active']"
            tabindex="0"
            @keydown.space.prevent="activeMobileSubsidebar = 'dashboard'"
            @click="activeMobileSubsidebar = 'dashboard'"
          >
            <i class="iconify" data-icon="feather:server" aria-hidden="true"></i>
          </a>
        </li>
        <li
          :class="[activeMobileSubsidebar === 'components' && 'is-active']"
          tabindex="0"
          @keydown.space.prevent="activeMobileSubsidebar = 'components'"
          @click="activeMobileSubsidebar = 'components'"
        >
          <a>
            <i class="iconify" data-icon="feather:users" aria-hidden="true"></i>
          </a>
        </li>

        <!-- <li>
          <RouterLink to="/messaging-v1">
            <i aria-hidden="true" class="iconify" data-icon="feather:message-circle"></i>
          </RouterLink>
        </li> -->
      </template>

      <template #bottom-links>
        <li>
          <a
            tabindex="0"
            @keydown.space.prevent="panels.setActive('search')"
            @click="panels.setActive('search')"
          >
            <i aria-hidden="true" class="iconify" data-icon="feather:search"></i>
          </a>
        </li>
        <li>
          <a href="#">
            <i aria-hidden="true" class="iconify" data-icon="feather:settings"></i>
          </a>
        </li>
      </template>
    </MobileSidebar>

    <!-- Mobile subsidebar links -->
    <Transition name="slide-x">
      <LayoutsMobileSubsidebar
        v-if="isMobileSidebarOpen && activeMobileSubsidebar === 'layout'"
      />
      <DashboardsMobileSubsidebar
        v-else-if="isMobileSidebarOpen && activeMobileSubsidebar === 'dashboard'"
      />
      <ComponentsMobileSubsidebar
        v-else-if="isMobileSidebarOpen && activeMobileSubsidebar === 'components'"
      />
      <ElementsMobileSubsidebar
        v-else-if="isMobileSidebarOpen && activeMobileSubsidebar === 'elements'"
      />
    </Transition>
    <!-- 顶部菜单 -->

    <Navbar :theme="props.theme">
      <!-- Custom navbar title -->
      <template #title>
        <RouterLink to="/" class="brand">
          <AnimatedLogo width="80px" />
        </RouterLink>

        <div class="separator"></div>

        <ProjectsQuickDropdown />
        <h1 class="title is-5">{{ viewWrapper.pageTitle }}</h1>
      </template>

      <!-- Custom navbar toolbar -->
      <template #toolbar>
        <Toolbar class="desktop-toolbar" style="margin-right: 15px">
          <ToolbarNotification />

          <!-- <a
            class="toolbar-link right-panel-trigger"
            tabindex="0"
            @keydown.space.prevent="panels.setActive('activity')"
            @click="panels.setActive('activity')"
          >
            <i aria-hidden="true" class="iconify" data-icon="feather:grid"></i>
          </a> -->
        </Toolbar>
        <LayoutSwitcher />

        <UserProfileDropdown />
      </template>

      <!-- Custom navbar links -->
      <template #links>
        <div class="centered-drops center">
          <div
            class="link-item"
            :class="[props.active === 1 && 'active-link']"
            @click="handleNav('/home/all-project')"
          >
            <!-- <VButton
              class="btn"
              to="/home/all-project"
              :color="props.active === 1 ? 'primary' : undefined"
              rounded
            >
              我的项目
            </VButton> -->
            <i class="fas fa-briefcase" aria-hidden="true" style="font-size: 18px"></i>
            <div class="link" :class="[props.active === 1 && 'active-link']">
              我的项目
            </div>
            <!-- <RouterLink
              class="link"
              :class="[props.active === 1 && 'active-link']"
              to="/home/all-project"
              >我的项目</RouterLink
            > -->
          </div>
          <div
            class="link-item"
            :class="[props.active === 2 && 'active-link']"
            @click="handleNav('/survey-template/evaluation')"
          >
            <i
              class="fas fa-clipboard-list"
              aria-hidden="true"
              style="font-size: 18px"
            ></i>
            <div class="link" :class="[props.active === 2 && 'active-link']">
              问卷市场
            </div>
          </div>
          <div
            class="link-item"
            :class="[props.active === 3 && 'active-link']"
            @click="handleNav('/contacts/all-contacts')"
          >
            <i class="fas fa-address-book" aria-hidden="true" style="font-size: 18px"></i>
            <div class="link" :class="[props.active === 3 && 'active-link']">联系人</div>

            <!-- <VButton
              class="btn"
              to="/contacts/all-contacts"
              :color="props.active === 3 ? 'primary' : undefined"
              rounded
            >
              联系人
            </VButton> -->
          </div>
        </div>
        <div class="centered-search" :class="[!displaySearch && 'is-hidden']">
          <div class="field">
            <div class="control has-icon">
              <input
                v-model="filter"
                type="text"
                class="input is-rounded search-input"
                placeholder="Search records..."
              />
              <div class="form-icon">
                <i aria-hidden="true" class="iconify" data-icon="feather:search"></i>
              </div>
              <div
                id="navbar-navbar-search-close"
                class="form-icon is-right"
                tabindex="0"
                @keydown.space.prevent="displaySearch = false"
                @click="displaySearch = false"
              >
                <i aria-hidden="true" class="iconify" data-icon="feather:x"></i>
              </div>
              <div
                v-if="filteredUsers.length > 0"
                class="search-results has-slimscroll is-active"
              >
                <div v-for="user in filteredUsers" :key="user.id" class="search-result">
                  <VAvatar v-bind="getAvatarData(user)" />
                  <div class="meta">
                    <span>{{ user.username }}</span>
                    <span>{{ user.position }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </Navbar>

    <!-- 侧边栏 -->
    <Transition name="slide-x">
      <slot name="sidebar"></slot>
    </Transition>

    <LanguagesPanel />
    <ActivityPanel />
    <TaskPanel />

    <VViewWrapper top-nav>
      <VPageContentWrapper>
        <template v-if="props.nowrap">
          <slot></slot>
        </template>
        <VPageContent v-else class="is-relative">
          <div class="is-navbar-lg">
            <div class="page-title has-text-centered">
              <!-- Mobile Page Title -->
              <div class="title-wrap">
                <h1 class="title is-4">{{ viewWrapper.pageTitle }}</h1>
              </div>

              <Toolbar class="mobile-toolbar">
                <ToolbarNotification />

                <!-- <a
                  class="toolbar-link right-panel-trigger"
                  tabindex="0"
                  @keydown.space.prevent="panels.setActive('activity')"
                  @click="panels.setActive('activity')"
                >
                  <i aria-hidden="true" class="iconify" data-icon="feather:grid"></i>
                </a> -->
              </Toolbar>
            </div>

            <slot></slot>
          </div>
        </VPageContent>
      </VPageContentWrapper>
    </VViewWrapper>
  </div>
</template>

<style lang="scss" scoped>
.centered-drops {
  .link-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 12px;
    height: 96px;
    line-height: 96px;
    cursor: pointer;
    .link {
      margin-left: 8px;
      font-size: 16px;
      color: #333;
    }
    .active-link {
      font-size: 16px;
      color: #41b983;
    }
    &.active-link {
      color: #41b983;
    }
  }
}
@media only screen and (max-width: 1026px) {
  .sideblock {
    // max-width: 1380px;
    display: none;
  }
}
.sideblock {
  .create-project {
    display: flex;
    justify-content: center;
    .create-btn {
      // padding-right: 60px;
      margin-bottom: 1.875rem;
    }
  }
}
</style>
