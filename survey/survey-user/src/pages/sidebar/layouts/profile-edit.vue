<script setup lang="ts">
import { useHead } from '@vueuse/head'
import { useUserSession } from '/@src/stores/userSession'
const userSession = useUserSession()
const router = useRouter()
const goBack = () => {
  router.push('/sidebar/layouts/profile-view')
}
// const goProject = () => {
//   router.push('/home/all-project')
// }
useHead({
  title: '云测-用户信息',
})
</script>
<template>
  <div>
    <div class="profileEdit-title">
      <div class="title-icon" @click="goBack">
        <i class="iconify" data-icon="feather:arrow-left" aria-hidden="true"></i>
      </div>
    </div>
    <div class="page-content-inner edit-page-content-inner">
      <!--Edit Profile-->
      <div class="account-wrapper">
        <div class="columns">
          <!--Navigation-->
          <div class="column is-4">
            <div class="account-box is-navigation">
              <VBlock
                :title="userSession.user?.name"
                :subtitle="'账号ID:' + userSession.tenantId"
                center
              >
                <template #icon>
                  <VAvatar
                    size="large"
                    :picture="
                      userSession.user?.avatar
                        ? userSession.user?.avatar
                        : '/images/avatars/svg/vuero-1.svg'
                    "
                  />
                </template>
              </VBlock>

              <div class="account-menu">
                <RouterLink to="/sidebar/layouts/profile-edit" class="account-menu-item">
                  <i aria-hidden="true" class="lnil lnil-user-alt"></i>
                  <span>用户信息</span>
                  <span class="end">
                    <i aria-hidden="true" class="fas fa-arrow-right"></i>
                  </span>
                </RouterLink>
                <!-- <RouterLink
                  to="/sidebar/layouts/profile-edit-experience"
                  class="account-menu-item"
                >
                  <i aria-hidden="true" class="lnil lnil-crown-alt"></i>
                  <span>Experience</span>
                  <span class="end">
                    <i aria-hidden="true" class="fas fa-arrow-right"></i>
                  </span>
                </RouterLink>
                <RouterLink
                  to="/sidebar/layouts/profile-edit-skills"
                  class="account-menu-item"
                >
                  <i aria-hidden="true" class="lnil lnil-quill"></i>
                  <span>Skills</span>
                  <span class="end">
                    <i aria-hidden="true" class="fas fa-arrow-right"></i>
                  </span>
                </RouterLink> -->
                <RouterLink
                  to="/sidebar/layouts/profile-edit/settings"
                  class="account-menu-item"
                >
                  <i aria-hidden="true" class="lnil lnil-cog"></i>
                  <span>账号安全设置</span>
                  <span class="end">
                    <i aria-hidden="true" class="fas fa-arrow-right"></i>
                  </span>
                </RouterLink>
              </div>
            </div>
          </div>
          <div class="column is-8">
            <RouterView v-slot="{ Component }">
              <component :is="Component" />
            </RouterView>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import '/@src/scss/abstracts/all';
.profileEdit-title {
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
.is-navbar {
  .account-wrapper {
    margin-top: 30px;
  }
}
.edit-page-content-inner {
  height: calc(100vh - 80px);
  padding: 30px 10vw 0 10vw;
  overflow-y: scroll;
}
.account-wrapper {
  padding-bottom: 60px;

  .account-box {
    @include vuero-s-card;

    &.is-navigation {
      .media-flex-center {
        padding-bottom: 20px;

        .flex-meta {
          span {
            &:first-child {
              font-size: 1.3rem;
            }
          }
        }
      }

      .account-menu {
        .account-menu-item {
          display: flex;
          align-items: center;
          padding: 12px 16px;
          border: 1px solid transparent;
          border-radius: 8px;
          margin-bottom: 5px;
          transition: all 0.3s; // transition-all test

          &.router-link-exact-active {
            box-shadow: var(--light-box-shadow);
            border-color: var(--fade-grey-dark-3);

            span,
            i {
              color: var(--primary);
            }

            .end {
              display: block;
            }
          }

          &:not(.router-link-exact-active) {
            &:hover {
              background: var(--fade-grey-light-3);
            }
          }

          i {
            margin-right: 8px;
            font-size: 1.1rem;
            color: var(--light-text);

            &.fas,
            .fal,
            .far {
              font-size: 0.9rem;
            }
          }

          span {
            font-family: var(--font-alt);
            font-size: 0.95rem;
            color: var(--dark-text);
          }

          .end {
            margin-left: auto;
            display: none;
          }
        }
      }
    }

    &.is-form {
      padding: 0;

      &.is-footerless {
        padding-bottom: 20px;
      }

      .form-head,
      .form-foot {
        padding: 12px 20px;

        .form-head-inner,
        .form-foot-inner {
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
      }

      .form-head {
        border-bottom: 1px solid var(--fade-grey-dark-3);
        transition: all 0.3s; // transition-all test

        &.is-stuck {
          background: var(--white);
          padding-right: 80px;
          border-left: 1px solid var(--fade-grey-dark-3);
        }

        .left {
          h3 {
            font-family: var(--font-alt);
            font-size: 1.2rem;
            line-height: 1.3;
          }

          p {
            font-size: 0.95rem;
          }
        }
      }

      .form-foot {
        border-top: 1px solid var(--fade-grey-dark-3);
      }

      .form-body {
        padding: 20px;

        .fieldset {
          padding: 20px 0;
          max-width: 480px;
          margin: 0 auto;

          .fieldset-heading {
            margin-bottom: 20px;

            h4 {
              font-family: var(--font-alt);
              font-weight: 600;
              font-size: 1rem;
            }

            p {
              font-size: 0.9rem;
            }
          }

          .v-avatar {
            position: relative;
            display: block;
            margin: 0 auto;

            .edit-button {
              position: absolute;
              bottom: 0;
              right: 0;
            }
          }

          .setting-list {
            .setting-form {
              text-align: center;

              .filepond-profile-wrap {
                margin: 0 auto 10px !important;
              }
            }

            .setting-item {
              display: flex;
              align-items: center;
              margin-bottom: 24px;

              .icon-wrap {
                position: relative;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 50px;
                min-width: 50px;
                height: 50px;
                border-radius: var(--radius-rounded);
                background: var(--fade-grey-light-2);
                border: 1px solid var(--fade-grey-dark-3);
                color: var(--light-text);

                &.has-img {
                  border-color: var(--primary);

                  img {
                    width: 36px;
                    min-width: 36px;
                    height: 36px;
                  }
                }

                i {
                  font-size: 1.4rem;
                }
              }

              img {
                display: block;
                width: 50px;
                min-width: 50px;
                height: 50px;
                border-radius: var(--radius-rounded);
                border: 1px solid transparent;
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
                    font-size: 0.9rem;
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

              .end {
                margin-left: auto;
              }
            }
          }
        }
      }
    }
  }
}

.is-dark {
  .account-wrapper {
    .account-box {
      @include vuero-card--dark;

      &.is-navigation {
        .account-menu {
          .account-menu-item {
            &.router-link-exact-active {
              background: var(--dark-sidebar-light-8);
              border-color: var(--dark-sidebar-light-12);

              i,
              span {
                color: var(--primary);
              }
            }

            &:not(.router-link-exact-active) {
              &:hover {
                background: var(--dark-sidebar-light-10);
              }
            }

            span {
              color: var(--dark-dark-text);
            }
          }
        }
      }

      &.is-form {
        .form-head,
        .form-foot {
          border-color: var(--dark-sidebar-light-12);
        }

        .form-head {
          &.is-stuck {
            background: var(--dark-sidebar);
            border-color: var(--dark-sidebar-light-6);
          }

          .left {
            h3 {
              color: var(--dark-dark-text);
            }
          }
        }

        .form-body {
          .fieldset {
            .fieldset-heading {
              h4 {
                color: var(--dark-dark-text);
              }
            }

            .setting-list {
              .setting-item {
                > img,
                > .icon-wrap,
                > .icon-wrap img {
                  border-color: var(--dark-sidebar-light-12);
                }

                > .icon-wrap {
                  background: var(--dark-sidebar-light-2);
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
          }
        }
      }
    }
  }
}
</style>
