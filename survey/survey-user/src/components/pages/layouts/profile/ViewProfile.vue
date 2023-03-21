<script setup lang="ts">
import Clipboard from 'clipboard'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { useUserSession } from '/@src/stores/userSession'
const userSession = useUserSession()
const router = useRouter()
const goBack = () => {
  router.push('/home/all-project')
}
// const goProject = () => {
//   router.push('/home/all-project')
// }
const copyId = async () => {
  console.log(111)
  let clipboard = new Clipboard('.copyID')
  clipboard
    .on('success', () => {
      clipboard.destroy() // 销毁上一次的复制内容
      clipboard = new Clipboard('.copyID')

      Notice({
        notice_type: 'success',
        message: '复制成功！',
      })
    })
    .on('error', () => {
      Notice({
        notice_type: 'error',
        message: '复制失败请稍后再试',
      })
    })
}
const goUserEdit = () => {
  router.push('/sidebar/layouts/profile-edit/settings')
}
</script>

<template>
  <div>
    <div class="profile-title">
      <div class="title-icon" @click="goBack">
        <i class="iconify" data-icon="feather:arrow-left" aria-hidden="true"></i>
      </div>
    </div>
    <div class="profile-wrapper">
      <div class="profile-header has-text-centered">
        <VAvatar
          size="xl"
          :picture="
            userSession.user?.avatar
              ? userSession.user?.avatar
              : '/images/avatars/svg/vuero-1.svg'
          "
        />

        <h3 class="title is-4 is-narrow is-thin">{{ userSession.user?.name }}</h3>
        <!-- <p class="light-text">
          Hey everyone, Iam a product manager from New York and Iam looking for new
          opportunities in the software business.
        </p>
        <div class="profile-stats">
          <div class="profile-stat">
            <i aria-hidden="true" class="lnil lnil-users-alt"></i>
            <span>500+ Relations</span>
          </div>
          <div class="separator"></div>
          <div class="profile-stat">
            <i aria-hidden="true" class="lnil lnil-checkmark-circle"></i>
            <span>78 Projects</span>
          </div>
          <div class="separator"></div>
          <div class="socials">
            <a><i aria-hidden="true" class="fab fa-facebook-f"></i></a>
            <a><i aria-hidden="true" class="fab fa-twitter"></i></a>
            <a><i aria-hidden="true" class="fab fa-linkedin-in"></i></a>
          </div>
        </div> -->
      </div>

      <div class="profile-body">
        <div class="columns">
          <div class="column is-8">
            <div class="profile-card">
              <div class="profile-card-section">
                <div class="section-title">
                  <h4>账户信息</h4>
                  <RouterLink to="/sidebar/layouts/profile-edit">
                    <i aria-hidden="true" class="lnil lnil-pencil"></i>
                  </RouterLink>
                </div>
                <div class="section-content base-info">
                  <div>
                    用户名:
                    <span>{{ userSession.user?.username }}</span>
                  </div>
                  <div>
                    账号ID: <span>{{ userSession.tenantId }}</span>
                    <a
                      class="copyID"
                      :data-clipboard-text="userSession.tenantId"
                      @click="copyId"
                      >复制</a
                    >
                  </div>
                  <div>
                    我的积分:
                    <span>{{ userSession.user?.integral }}</span>
                    <RouterLink to="/auth/recharge">
                      <a>充值</a>
                    </RouterLink>
                  </div>
                  <div>
                    公司名称: <span>{{ userSession.user?.name }}</span>
                    <RouterLink to="/sidebar/layouts/profile-edit">
                      <a> 更改</a>
                    </RouterLink>
                  </div>
                  <div>
                    账号邮箱:
                    <template v-if="userSession.user?.email">
                      <span>{{ userSession.user?.email }}</span>
                      <RouterLink to="/sidebar/layouts/profile-edit/settings">
                        <a>换绑</a>
                      </RouterLink>
                    </template>
                    <template v-else>
                      <a @click="goUserEdit"> 绑定邮箱</a>
                    </template>
                  </div>
                  <div>
                    手机号:
                    <template v-if="userSession.user?.phone">
                      <span>
                        {{
                          userSession.user?.phone.slice(0, 3) +
                          '****' +
                          userSession.user?.phone.slice(-4)
                        }}
                        <a @click="goUserEdit"> 换绑</a>
                      </span>
                    </template>
                    <template v-else>
                      <a @click="goUserEdit"> 绑定手机号</a>
                    </template>
                  </div>
                </div>
                <div>
                  自定义域名:
                  <template v-if="userSession.user?.domain">
                    <span>
                      {{ userSession.user?.domain }}
                      <a @click="goUserEdit"> 换绑</a>
                    </span>
                  </template>
                  <template v-else>
                    <a @click="goUserEdit"> 域名绑定</a>
                  </template>
                </div>
              </div>
              <div class="profile-card-section">
                <div class="section-title">
                  <h4>第三方账号</h4>
                  <RouterLink to="/sidebar/layouts/profile-edit/settings">
                    <i aria-hidden="true" class="lnil lnil-pencil"></i>
                  </RouterLink>
                </div>
                <div class="section-content">
                  <div>
                    微信：
                    <template v-if="!!userSession.user?.wechatNickname">
                      <span>{{ userSession.user?.wechatNickname }}</span>
                      <RouterLink to="/sidebar/layouts/profile-edit/settings">
                        <a>解绑</a>
                      </RouterLink>
                    </template>
                    <template v-else>
                      <a @click="goUserEdit"> 绑定微信</a>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import '/@src/scss/abstracts/all';
@import '/@src/scss/components/profile-stats';
@media (max-width: 1100px) {
  .profile-wrapper,
  .profile-card {
    width: 80vw !important;
  }
}
.profile-title {
  display: flex;
  height: 80px;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  background-color: white;
  .title-icon {
    font-size: 24px;
    cursor: pointer;
  }
  .project-btn {
    width: 120px;
    border-radius: 2%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    border: 1px solid var(--primary);
    color: var(--primary);
    cursor: pointer;
  }
}
.base-info {
  div {
    margin-bottom: 5px;
    span {
      padding-left: 3px;
    }
    a {
      padding-left: 5px;
    }
  }
}
.is-navbar {
  .profile-wrapper {
    margin-top: 30px;
  }
}

.profile-wrapper {
  width: 60vw;
  margin: 0 auto;
  padding-top: 40px;
  height: calc(100vh - 80px);
  .profile-header {
    text-align: center;

    > img {
      display: block;
      margin: 0 auto;
      max-width: 300px;
    }

    .v-avatar {
      margin: 0 auto 12px;
    }

    .anim-icon {
      margin-bottom: 12px;
    }

    .title {
      margin-bottom: 6px;
    }

    p {
      font-size: 1rem;
      max-width: 540px;
      margin: 0 auto;
      line-height: 1.3;
    }
  }

  .profile-body {
    padding: 10px 0 20px;

    .profile-card {
      @include vuero-s-card;
      width: 60vw;

      padding: 30px;

      &:not(:last-child) {
        margin-bottom: 20px;
      }

      .profile-card-section {
        padding-bottom: 20px;

        &:not(:last-child) {
          margin-bottom: 20px;
          border-bottom: 1px solid var(--fade-grey-dark-4);
        }

        &.no-padding {
          padding-bottom: 0;
        }

        .section-title {
          display: flex;
          align-items: center;
          margin-bottom: 12px;

          h4 {
            font-family: var(--font-alt);
            font-weight: 600;
            font-size: 0.8rem;
            text-transform: uppercase;
            color: var(--dark-text);
            margin-right: 6px;
          }

          i {
            color: var(--primary);
          }

          .action-link {
            position: relative;
            top: -2px;
            margin-left: auto;
            text-transform: uppercase;
            font-size: 0.8rem;
          }

          .control {
            margin-left: auto;

            .form-switch {
              transform: scale(0.8);
            }
          }
        }

        .section-content {
          .description {
            font-size: 0.95rem;
          }
          a {
            padding-left: 5px;
          }

          .experience-wrapper {
            display: flex;
            flex-wrap: wrap;
            margin-left: -8px;
            margin-right: -8px;

            .experience-item {
              display: flex;
              align-items: center;
              width: calc(50% - 16px);
              margin: 8px;

              img {
                display: block;
                width: 50px;
                min-width: 50px;
                height: 50px;
                border-radius: var(--radius-rounded);
                border: 1px solid var(--fade-grey-dark-4);
              }

              .meta {
                margin-left: 10px;

                > span {
                  font-family: var(--font);
                  display: block;

                  &:first-child {
                    font-family: var(--font-alt);
                    font-weight: 600;
                    color: var(--dark-text);
                    font-size: 0.85rem;
                  }

                  &:nth-child(2),
                  &:nth-child(3) {
                    font-size: 0.85rem;
                    color: var(--light-text);

                    i {
                      position: relative;
                      top: -2px;
                      font-size: 4px;
                      margin: 0 6px;
                    }
                  }

                  &:nth-child(3) {
                    color: var(--primary);
                  }

                  span {
                    display: inline-block;
                  }
                }
              }
            }
          }

          .languages-wrapper {
            display: flex;
            flex-wrap: wrap;
            margin-left: -8px;
            margin-right: -8px;

            .languages-item {
              display: flex;
              align-items: center;
              width: calc(50% - 16px);
              margin: 8px;

              .icon-wrap {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 50px;
                min-width: 50px;
                height: 50px;

                img {
                  position: absolute;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  display: block;
                  width: 32px;
                  min-width: 32px;
                  height: 32px;
                  border-radius: var(--radius-rounded);
                  border: 1px solid var(--fade-grey-dark-4);
                }
              }

              .meta {
                margin-left: 10px;

                > span {
                  display: block;
                  font-family: var(--font);

                  &:first-child {
                    font-family: var(--font-alt);
                    font-weight: 600;
                    color: var(--dark-text);
                    font-size: 0.9rem;
                  }

                  &:nth-child(2) {
                    font-size: 0.85rem;
                    color: var(--light-text);
                  }

                  span {
                    display: inline-block;
                  }
                }
              }
            }
          }

          .skills-wrapper {
            .skills-item {
              display: flex;
              align-items: center;

              &:not(:last-child) {
                margin-bottom: 16px;
              }

              .icon-wrap {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 42px;
                min-width: 42px;
                height: 42px;
                border-radius: var(--radius-rounded);
                border: 1px solid var(--primary);

                &.has-icon {
                  background: var(--fade-grey-light-2);
                  border: 1px solid var(--fade-grey-dark-3);
                  color: var(--light-text);

                  i {
                    font-size: 1.4rem;
                  }
                }

                img {
                  display: block;
                  width: 34px;
                  min-width: 34px;
                  height: 34px;
                  border-radius: var(--radius-rounded);
                  border: 1px solid var(--fade-grey-dark-4);
                }

                .count {
                  position: absolute;
                  bottom: 0;
                  right: -4px;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  width: 22px;
                  height: 22px;
                  border-radius: var(--radius-rounded);
                  background: var(--white);
                  border: 1px solid var(--primary);

                  span {
                    font-family: var(--font);
                    font-weight: 500;
                    font-size: 0.75rem;
                  }
                }
              }

              .skill-info {
                font-family: var(--font);
                margin-left: 12px;
                line-height: 1.3;

                span {
                  display: block;

                  &:first-child {
                    font-family: var(--font-alt);
                    font-weight: 600;
                    color: var(--dark-text);
                    font-size: 0.9rem;
                  }

                  &:nth-child(2) {
                    font-size: 0.9rem;
                    color: var(--light-text);
                  }
                }
              }

              .people {
                margin-left: auto;
                display: flex;
                justify-content: flex-end;

                .v-avatar {
                  margin: 0 4px;
                }
              }
            }
          }

          .recommendations-wrapper {
            display: flex;
            flex-wrap: wrap;
            margin-left: -8px;
            margin-right: -8px;

            .recommendations-item {
              width: calc(50% - 16px);
              margin: 8px;
              background: var(--fade-grey-light-3);
              text-align: center;
              padding: 30px 20px;
              border-radius: var(--radius);

              > .v-avatar {
                display: block;
                margin: 0 auto 8px;
              }

              h3 {
                font-size: 1rem;
                font-family: var(--font-alt);
                font-weight: 600;
                color: var(--dark-text);
                margin-bottom: 8px;
              }

              p {
                font-size: 0.85rem;
                margin-bottom: 16px;
              }

              .meta {
                span {
                  display: block;

                  &:first-child {
                    font-weight: 600;
                    font-family: var(--font-alt);
                    font-size: 0.95rem;
                    color: var(--primary);
                  }

                  &:nth-child(2) {
                    font-size: 0.9rem;
                    color: var(--light-text);
                  }
                }
              }
            }
          }

          .network-notifications {
            h3 {
              font-family: var(--font-alt);
              font-size: 0.9rem;
              margin-bottom: 6px;
            }

            p {
              font-size: 0.9rem;
              max-width: 480px;
            }
          }

          .tools-wrapper {
            padding-top: 12px;

            .tools-item {
              display: flex;
              align-items: center;
              margin-bottom: 16px;

              &:last-child {
                margin-bottom: 0;
              }

              .icon-wrap {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 50px;
                min-width: 50px;
                height: 50px;

                img {
                  position: absolute;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  display: block;
                  width: 32px;
                  min-width: 32px;
                  height: 32px;
                  border-radius: var(--radius-rounded);
                  border: 1px solid transparent;
                }
              }

              .meta {
                margin-left: 10px;

                > span {
                  display: block;
                  font-family: var(--font);

                  &:first-child {
                    font-family: var(--font-alt);
                    font-weight: 600;
                    color: var(--dark-text);
                    font-size: 0.9rem;
                  }

                  &:nth-child(2) {
                    font-size: 0.85rem;
                    color: var(--light-text);
                  }

                  span {
                    display: inline-block;
                  }
                }
              }
            }
          }

          .people-wrapper {
            padding-top: 12px;

            .people-item {
              display: flex;
              align-items: center;
              margin-bottom: 16px;

              &:last-child {
                margin-bottom: 0;
              }

              .meta {
                margin-left: 10px;

                > span {
                  display: block;
                  font-family: var(--font);

                  &:first-child {
                    font-family: var(--font-alt);
                    font-weight: 600;
                    color: var(--dark-text);
                    font-size: 0.9rem;
                  }

                  &:nth-child(2) {
                    font-size: 0.85rem;
                    color: var(--light-text);
                  }

                  span {
                    display: inline-block;
                  }
                }
              }
            }
          }

          .more-button {
            padding-top: 16px;

            .button {
              min-width: 180px;
              font-family: var(--font);
              text-transform: uppercase;
              font-size: 0.8rem;
              font-weight: 500;
              margin: 0 auto;
              color: var(--light-text);
            }
          }
        }
      }
    }
  }
}

.is-dark {
  .profile-wrapper {
    .profile-header {
      .v-avatar {
        .badge {
          border-color: var(--dark-sidebar-light-6);
        }
      }
    }

    .profile-body {
      .profile-card {
        @include vuero-card--dark;

        .profile-card-section {
          border-color: var(--dark-sidebar-light-12);

          .section-title {
            h4 {
              color: var(--dark-dark-text);
            }

            i {
              color: var(--primary);
            }
          }

          .section-content {
            .icon-wrap {
              > img {
                border-color: var(--dark-sidebar-light-12) !important;
              }
            }

            .experience-wrapper {
              .experience-item {
                > img {
                  border-color: var(--dark-sidebar-light-12);
                }

                .meta {
                  > span {
                    &:nth-child(3) {
                      color: var(--primary);
                    }
                  }
                }
              }
            }

            .skills-wrapper {
              .skills-item {
                .icon-wrap {
                  border-color: var(--primary) !important;

                  &.has-icon,
                  &.has-img {
                    background: var(--dark-sidebar-light-2) !important;
                    border-color: var(--dark-sidebar-light-12) !important;
                  }
                }

                .people {
                  .v-avatar {
                    &:last-child {
                      .is-fake {
                        background: var(--dark-sidebar-light-2);
                        border: 1px solid var(--dark-sidebar-light-12);
                      }
                    }
                  }
                }
              }
            }

            .recommendations-wrapper {
              .recommendations-item {
                background: var(--dark-sidebar-light-2);
                border-color: var(--dark-sidebar-light-12);

                .meta {
                  span {
                    &:first-child {
                      color: var(--primary);
                    }
                  }
                }
              }
            }

            .more-button {
              .button {
                background: var(--dark-sidebar-light-2);
                border-color: var(--dark-sidebar-light-12);
              }
            }
          }
        }
      }
    }
  }

  .icon-wrap,
  .icon-wrap.is-placeholder {
    background: var(--dark-sidebar-light-2) !important;
    border-color: var(--dark-sidebar-light-12) !important;
  }
}

@media only screen and (max-width: 767px) {
  .profile-wrapper {
    .profile-body {
      .profile-card {
        padding: 20px;

        .profile-card-section {
          .section-content {
            .experience-wrapper,
            .languages-wrapper,
            .recommendations-wrapper {
              .experience-item,
              .languages-item,
              .recommendations-item {
                width: calc(100% - 16px);
              }
            }

            .skills-wrapper {
              .skills-item {
                .people {
                  .v-avatar {
                    &:not(:last-child) {
                      display: none !important;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
