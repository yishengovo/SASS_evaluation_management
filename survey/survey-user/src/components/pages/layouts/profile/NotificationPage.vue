<script lang="ts" setup>
import noMessage from '/@src/assets/noMessage.png'
const goBack = () => {
  window.history.go(-1)
}
</script>
<template>
  <div class="timeline-wrapper">
    <div class="timeline-header">
      <h1 class="title is-4">消息通知</h1>
      <i class="lnir lnir-cross-circle" aria-hidden="true" @click="goBack"></i>
    </div>
    <div style="margin: 30px auto; max-width: 900px; text-align: center">
      <img :src="noMessage" alt="" style="width: 45vw; height: 55vh" />
      <div style="text-align: center; margin-top: 20px">暂无消息</div>
    </div>

    <!-- <div class="timeline-wrapper-inner">
      <div class="timeline-container">
        <div class="timeline-item is-unread">
          <div class="date">
            <span>2022-09-23</span>
          </div>
          <div class="dot is-info"></div>
          <div class="content-wrap">
            <div class="content-box">
              <div class="status"></div>
              <VAvatar picture="/demo/avatars/13.jpg" />

              <div class="box-text">
                <div class="meta-text">
                  <p><span>张三.</span> 添加了一个 <a>非常厉害的项目</a>.</p>
                  <span> 上午11:42 </span>
                </div>
              </div>
              <div class="box-end">
                <VAvatar size="small" picture="/images/avatars/svg/vuero-1.svg" />
              </div>
            </div>
          </div>
        </div>

        <div class="timeline-item is-unread">
          <div class="date">
            <span>2022-09-23</span>
          </div>
          <div class="dot is-danger"></div>
          <div class="content-wrap">
            <div class="content-box">
              <div class="status"></div>
              <VAvatar picture="/demo/avatars/23.jpg" />

              <div class="box-text">
                <div class="meta-text">
                  <p><span>张三.</span> 添加了一个 <a>非常厉害的项目</a>.</p>
                  <span> 上午11:42 </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="timeline-item">
          <div class="date">
            <span>2022-09-23</span>
          </div>
          <div class="dot is-danger"></div>
          <div class="content-wrap">
            <div class="content-box">
              <div class="status"></div>
              <VAvatar picture="/demo/avatars/13.jpg" />

              <div class="box-text">
                <div class="meta-text">
                  <p><span>张三.</span> 添加了一个 <a>非常厉害的项目</a>.</p>
                  <span> 上午11:42 </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="load-more-wrap has-text-centered">
        <VButton dark-outlined>加载更多</VButton>
      </div>
    </div> -->
  </div>
</template>

<style lang="scss">
@import '/@src/scss/abstracts/all';

/*
  1. Timeline
  2. Timeline Dark mode
  3. Media Queries
*/

/* ==========================================================================
1. Timeline
========================================================================== */
.timeline-wrapper {
  min-height: 100vh;
  margin: 0 auto;
  .timeline-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 2vw;
    font-size: 36px;
    .title {
      margin-bottom: 0;
    }
    i {
      cursor: pointer;
    }
  }
  .timeline-wrapper-inner {
    max-width: 1200px;
    padding: 30px 2vw 0 2vw;
    margin: 0 auto;

    .timeline-container {
      .timeline-item {
        position: relative;
        display: flex;
        align-items: center;
        margin-bottom: 10px;

        &::before {
          content: '';
          position: absolute;
          top: 46px;
          left: 111px;
          height: 100%;
          width: 2px;
          background: var(--placeholder);
          z-index: 0;
        }

        &:last-child {
          &::before {
            display: none;
          }
        }

        &.is-unread {
          .content-wrap {
            .content-box {
              .status {
                background: var(--red) !important;
              }
            }
          }
        }

        .date {
          width: 80px;
          font-family: var(--font);
          text-align: right;

          span {
            font-size: 0.9rem;
            color: var(--light-text);
          }
        }

        .dot {
          position: relative;
          height: 14px;
          width: 14px;
          border-radius: var(--radius-rounded);
          border: 2.6px solid var(--primary);
          margin: 0 25px;
          z-index: 1;

          &.is-info {
            border-color: var(--info);
          }

          &.is-success {
            border-color: var(--success);
          }

          &.is-warning {
            border-color: var(--warning);
          }

          &.is-danger {
            border-color: var(--danger);
          }

          &.is-purple {
            border-color: var(--purple);
          }
        }

        .content-wrap {
          @include vuero-s-card;

          flex-grow: 2;

          .content-box {
            display: flex;
            align-items: center;

            .status {
              height: 8px;
              width: 8px;
              min-width: 8px;
              border-radius: var(--radius-rounded);
              background: var(--light-text-light-15);
              margin: 0 16px 0 0;
            }

            .box-text {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-left: 12px;
              flex-grow: 2;

              .meta-text {
                line-height: 1.2;

                p {
                  color: var(--light-text-dark-10);

                  span {
                    font-family: var(--font-alt);
                    color: var(--dark-text);
                    font-weight: 600;
                  }

                  a {
                    color: var(--primary);
                  }

                  .tag {
                    position: relative;
                    top: -1px;
                    font-weight: 500;
                    line-height: 1.8;
                    height: 1.8em;
                    margin: 0 2px;
                  }
                }

                > span {
                  color: var(--light-text);
                  font-size: 0.9rem;
                }
              }
            }

            .box-end {
              margin-left: auto;

              .v-avatar {
                margin: 0 2px;
              }
            }
          }

          .meta-content {
            padding-left: 78px;
          }
        }
      }
    }

    .load-more-wrap {
      padding: 40px 0;

      .button {
        min-width: 240px;
        min-height: 50px;
        text-transform: uppercase;
        font-size: 0.85rem;
        font-weight: 500;
        color: var(--light-text);
      }
    }
  }
}

/* ==========================================================================
2. Timeline Dark mode
========================================================================== */

.is-dark {
  .timeline-wrapper {
    .timeline-wrapper-inner {
      .timeline-container {
        .timeline-item {
          &::before {
            background: var(--dark-sidebar-light-20);
          }

          .content-wrap {
            @include vuero-card--dark;

            .content-box {
              .status {
                background: var(--dark-sidebar-light-20);
              }

              .box-text {
                .meta-text {
                  p {
                    span {
                      color: var(--dark-dark-text);
                    }

                    a {
                      color: var(--primary);
                    }
                  }
                }
              }
            }
          }
        }
      }

      .load-more-wrap {
        .button {
          background: var(--dark-sidebar-light-2) !important;
        }
      }
    }
  }
}

/* ==========================================================================
3. Media Queries
========================================================================== */

@media only screen and (max-width: 767px) {
  .timeline-wrapper {
    .timeline-wrapper-inner {
      padding-top: 0;

      .timeline-container {
        .timeline-item {
          flex-direction: column;

          &::before {
            display: none;
          }

          .dot {
            display: none;
          }

          .date {
            align-self: end;
            margin-bottom: 4px;
          }

          .content-wrap {
            .content-box {
              .box-end {
                display: none;
              }
            }
          }
        }
      }
    }
  }
}

@media only screen and (min-width: 768px) and (max-width: 1024px) and (orientation: portrait) {
  .timeline-wrapper {
    .timeline-wrapper-inner {
      padding-top: 0;

      .timeline-container {
        .timeline-item {
          flex-direction: column;

          &::before {
            display: none;
          }

          .dot {
            display: none;
          }

          .date {
            align-self: end;
            margin-bottom: 4px;
          }

          .content-wrap {
            .content-box {
              .box-end {
                display: none;
              }
            }
          }
        }
      }
    }
  }
}
</style>
