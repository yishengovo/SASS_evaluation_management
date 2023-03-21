<script setup lang="ts">
import { useDropdown } from '/@src/composable/useDropdown'
import { onceImageErrored } from '/@src/utils/via-placeholder'

const dropdownElement = ref<HTMLElement>()
const dropdown = useDropdown(dropdownElement)
</script>

<template>
  <div class="toolbar-notifications is-hidden-mobile">
    <div
      ref="dropdownElement"
      class="dropdown is-spaced is-dots is-right dropdown-trigger"
    >
      <div
        tabindex="0"
        class="is-trigger"
        aria-haspopup="true"
        @click="dropdown.toggle"
        @keydown.space.prevent="dropdown.toggle"
      >
        <i aria-hidden="true" class="iconify" data-icon="feather:bell"></i>
        <span v-if="false" class="new-indicator pulsate"></span>
      </div>
      <div class="dropdown-menu" role="menu">
        <div class="dropdown-content">
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
          <!-- <ul class="notification-list">
            <li>
              <a class="notification-item">
                <div class="img-left">
                  <img
                    class="user-photo"
                    alt=""
                    src="https://www.baidu.com/link?url=je52pFKmMq-K-pVzjRegP0pPKFXBV_zgMwUxQUp97SVNT4yVydvP1cNqarP-sKpF_YM9JjuxYhp9SCbgYHUuNOa2EXnVjpsY_u4NEPoAut1tpu0sMWitgQacWuF88hJeqWSgufloC7wzi4BZ2ckDnIhEWr5Nk6s0oE2tAQqPpDJw8ggCCC9_EK4NOp9optiGMTp4NHdo0fP5sZgFvZbeu3NkyhByvhGX8bEu3o2pnjc5DxhBIOEH4w4euPmFWweNBJ5apz5mNPfUAvxRMt0JMnZ5ZKALtEtLsldptIysLFDfcVnNbTkkSqhVEvwDANgavtQUjU-kKSu6zBgSAngeLJICOW7BgDFEnr-wPbC4IE-IrsXSOenOxWPew4QMY4mOFwmxECF3ZZfnZPMd3Vwj686ra6l1ki1hRjb79UA2B_7ubjq-BFQYqAKxhNjF2fJ5j1Gz8HcxJhQWkymIXC5VpYf7RQ5gMeF1zDFW-KiaR-3OzCakIH5GBvwkd2LE80kFFm3ORZWu5wfDRwIlmOptE3P0vACtqlFosaBN0C0J5ajno9nD-k5k6O_TMfWgTk6M-EyQcT8UfzxGBFsRplQcjjHhMjU3l8rZ1YQvWO3RVBN7Yc_P_RL8bmHjouc0D4q5nWmG4PQGHztkrQdQs9VDkmAphXfmfUx0kNaglfUiIMCBQ1xIp3S7Qixar8CYa-2a&wd=&eqid=e48c1436000213b0000000066334264e"
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
                    src="https://www.baidu.com/link?url=je52pFKmMq-K-pVzjRegP0pPKFXBV_zgMwUxQUp97SVNT4yVydvP1cNqarP-sKpF_YM9JjuxYhp9SCbgYHUuNOa2EXnVjpsY_u4NEPoAut1tpu0sMWitgQacWuF88hJeqWSgufloC7wzi4BZ2ckDnIhEWr5Nk6s0oE2tAQqPpDJw8ggCCC9_EK4NOp9optiGMTp4NHdo0fP5sZgFvZbeu3NkyhByvhGX8bEu3o2pnjc5DxhBIOEH4w4euPmFWweNBJ5apz5mNPfUAvxRMt0JMnZ5ZKALtEtLsldptIysLFDfcVnNbTkkSqhVEvwDANgavtQUjU-kKSu6zBgSAngeLJICOW7BgDFEnr-wPbC4IE-IrsXSOenOxWPew4QMY4mOFwmxECF3ZZfnZPMd3Vwj686ra6l1ki1hRjb79UA2B_7ubjq-BFQYqAKxhNjF2fJ5j1Gz8HcxJhQWkymIXC5VpYf7RQ5gMeF1zDFW-KiaR-3OzCakIH5GBvwkd2LE80kFFm3ORZWu5wfDRwIlmOptE3P0vACtqlFosaBN0C0J5ajno9nD-k5k6O_TMfWgTk6M-EyQcT8UfzxGBFsRplQcjjHhMjU3l8rZ1YQvWO3RVBN7Yc_P_RL8bmHjouc0D4q5nWmG4PQGHztkrQdQs9VDkmAphXfmfUx0kNaglfUiIMCBQ1xIp3S7Qixar8CYa-2a&wd=&eqid=e48c1436000213b0000000066334264e"
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
                    src="https://www.baidu.com/link?url=je52pFKmMq-K-pVzjRegP0pPKFXBV_zgMwUxQUp97SVNT4yVydvP1cNqarP-sKpF_YM9JjuxYhp9SCbgYHUuNOa2EXnVjpsY_u4NEPoAut1tpu0sMWitgQacWuF88hJeqWSgufloC7wzi4BZ2ckDnIhEWr5Nk6s0oE2tAQqPpDJw8ggCCC9_EK4NOp9optiGMTp4NHdo0fP5sZgFvZbeu3NkyhByvhGX8bEu3o2pnjc5DxhBIOEH4w4euPmFWweNBJ5apz5mNPfUAvxRMt0JMnZ5ZKALtEtLsldptIysLFDfcVnNbTkkSqhVEvwDANgavtQUjU-kKSu6zBgSAngeLJICOW7BgDFEnr-wPbC4IE-IrsXSOenOxWPew4QMY4mOFwmxECF3ZZfnZPMd3Vwj686ra6l1ki1hRjb79UA2B_7ubjq-BFQYqAKxhNjF2fJ5j1Gz8HcxJhQWkymIXC5VpYf7RQ5gMeF1zDFW-KiaR-3OzCakIH5GBvwkd2LE80kFFm3ORZWu5wfDRwIlmOptE3P0vACtqlFosaBN0C0J5ajno9nD-k5k6O_TMfWgTk6M-EyQcT8UfzxGBFsRplQcjjHhMjU3l8rZ1YQvWO3RVBN7Yc_P_RL8bmHjouc0D4q5nWmG4PQGHztkrQdQs9VDkmAphXfmfUx0kNaglfUiIMCBQ1xIp3S7Qixar8CYa-2a&wd=&eqid=e48c1436000213b0000000066334264e"
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
                    src="https://www.baidu.com/link?url=je52pFKmMq-K-pVzjRegP0pPKFXBV_zgMwUxQUp97SVNT4yVydvP1cNqarP-sKpF_YM9JjuxYhp9SCbgYHUuNOa2EXnVjpsY_u4NEPoAut1tpu0sMWitgQacWuF88hJeqWSgufloC7wzi4BZ2ckDnIhEWr5Nk6s0oE2tAQqPpDJw8ggCCC9_EK4NOp9optiGMTp4NHdo0fP5sZgFvZbeu3NkyhByvhGX8bEu3o2pnjc5DxhBIOEH4w4euPmFWweNBJ5apz5mNPfUAvxRMt0JMnZ5ZKALtEtLsldptIysLFDfcVnNbTkkSqhVEvwDANgavtQUjU-kKSu6zBgSAngeLJICOW7BgDFEnr-wPbC4IE-IrsXSOenOxWPew4QMY4mOFwmxECF3ZZfnZPMd3Vwj686ra6l1ki1hRjb79UA2B_7ubjq-BFQYqAKxhNjF2fJ5j1Gz8HcxJhQWkymIXC5VpYf7RQ5gMeF1zDFW-KiaR-3OzCakIH5GBvwkd2LE80kFFm3ORZWu5wfDRwIlmOptE3P0vACtqlFosaBN0C0J5ajno9nD-k5k6O_TMfWgTk6M-EyQcT8UfzxGBFsRplQcjjHhMjU3l8rZ1YQvWO3RVBN7Yc_P_RL8bmHjouc0D4q5nWmG4PQGHztkrQdQs9VDkmAphXfmfUx0kNaglfUiIMCBQ1xIp3S7Qixar8CYa-2a&wd=&eqid=e48c1436000213b0000000066334264e"
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
          </ul> -->
        </div>
      </div>
    </div>
  </div>
</template>
