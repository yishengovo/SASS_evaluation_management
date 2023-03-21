<script setup lang="ts">
import { useDropdown } from '/@src/composable/useDropdown'
import { onceImageErrored } from '/@src/utils/via-placeholder'

const dropdownElement = ref<HTMLElement>()
const dropdown = useDropdown(dropdownElement)
</script>

<template>
  <div
    ref="dropdownElement"
    class="navbar-item has-dropdown is-notification is-hidden-tablet is-hidden-desktop"
  >
    <a
      class="navbar-link is-arrowless"
      tabindex="0"
      @keydown.space.prevent="dropdown.toggle"
      @click="dropdown.toggle"
    >
      <i aria-hidden="true" class="iconify" data-icon="feather:bell"></i>
      <span v-if="false" class="new-indicator pulsate"></span>
    </a>
    <div class="navbar-dropdown is-boxed is-right">
      <div class="heading">
        <div class="heading-left">
          <h6 class="heading-title">通知</h6>
        </div>
        <div class="heading-right">
          <RouterLink
            class="notification-link"
            to="/sidebar/layouts/profile-notifications"
          >
            查看所有
          </RouterLink>
        </div>
      </div>

      <div style="text-align: center; margin: 10px 0">暂无消息</div>
      <!-- <div class="inner has-slimscroll">
        <ul class="notification-list">
          <li>
            <a class="notification-item">
              <div class="img-left">
                <img
                  class="user-photo"
                  alt=""
                  src="/images/avatars/svg/vuero-4.svg"
                  @error.once="onceImageErrored(150)"
                />
              </div>
              <div class="user-content">
                <p class="user-info">
                  <span class="name">张三.</span> 给您分享了一个项目.
                </p>
                <p class="time">1小时前</p>
              </div>
            </a>
          </li>
          <li>
            <a class="notification-item">
              <div class="img-left">
                <img
                  class="user-photo"
                  alt=""
                  src="/images/avatars/svg/vuero-2.svg"
                  @error.once="onceImageErrored(150)"
                />
              </div>
              <div class="user-content">
                <p class="user-info">
                  <span class="name">张三.</span> 给您分享了一个项目.
                </p>
                <p class="time">1小时前</p>
              </div>
            </a>
          </li>
          <li>
            <a class="notification-item">
              <div class="img-left">
                <img
                  class="user-photo"
                  alt=""
                  src="/images/avatars/svg/vuero-5.svg"
                  @error.once="onceImageErrored(150)"
                />
              </div>
              <div class="user-content">
                <p class="user-info">
                  <span class="name">张三.</span> 给您分享了一个项目.
                </p>
                <p class="time">1小时前</p>
              </div>
            </a>
          </li>
          <li>
            <a class="notification-item">
              <div class="img-left">
                <img
                  class="user-photo"
                  alt=""
                  src="/images/avatars/svg/vuero-9.svg"
                  @error.once="onceImageErrored(150)"
                />
              </div>
              <div class="user-content">
                <p class="user-info">
                  <span class="name">张三.</span> 给您分享了一个项目.
                </p>
                <p class="time">1小时前</p>
              </div>
            </a>
          </li>
        </ul>
      </div> -->
    </div>
  </div>
</template>
