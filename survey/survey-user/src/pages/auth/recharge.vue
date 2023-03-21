<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-12-09 09:37:57
 * @FilePath: \survey\survey-user\src\pages\auth\recharge.vue
 * @Description: 我的项目

 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->

<script setup lang="ts">
import { judegeDevice } from '/@src/utils/judgeDevice'
import { useNotyf } from '/@src/composable/useNotyf'
import {
  queryQrApi,
  queryStatusApi,
  shutDowOrderApi,
  getOrderInfoApi,
  getIntegetMagnificationApi,
} from '/@src/api/pay'
import { useUserSession } from '/@src/stores/userSession'
import QrcodeVue from 'qrcode.vue'
import { CountUp } from 'countup.js'
let cpIntegral: any
const notyf = useNotyf()
const userSession = useUserSession()
const router = useRouter()
const outTrad = ref('')
window.addEventListener(
  'popstate',
  async function () {
    await backLogic()
  },
  false
)
const backLogic = async () => {
  needQrTip.value = false
  const res3 = await shutDowOrderApi(outTrad.value)
  clearInterval(interval)
  if (res3.data.code !== 200) {
    notyf.error('上次一次订单关闭失败,请等候一些时间再支付,避免引起意外错误!')
  }
}
const goBack = async () => {
  await backLogic()
  router.push('/home/all-project')
}
const order: any[] = []
const orderInfo = ref({
  outTradeNo: '',
  amount: 0,
  integral: 0,
})
const needQrTip = ref(true)
const integralMft = ref(100)
const rechargeInfo = [1, 5, 10, 20, 50, 100]
const currentIndex = ref(0)
const otherPrice = ref()
const payWays = ref('wxPay')
const isNeedChangeQr = async () => {
  const orderItem = order.find((item) => {
    return item.price === currentPrice.value
  })
  if (orderItem) {
    const res = await queryOrderStatus(orderItem.outTrad)
    if (res) {
      orderItem.qrStatus = false
    }
    await getQr(orderItem.price)
  } else {
    await getQr(currentPrice.value)
  }
}
const changeMount = async (index: number) => {
  if (currentIndex.value !== index) {
    qrInvalid.value = false
    currentPrice.value = rechargeInfo[index]
    otherPrice.value = currentPrice.value
    currentIndex.value = index
    await isNeedChangeQr()
    qrInvalid.value = false
  }
}
const currentPrice = ref(1)
const qrUrl = ref('')
const isLoaderActive = ref(false)
const successOderModal = ref(false)
const os = judegeDevice()
const deskTop = os.isPc
const confirmPrice = async () => {
  qrInvalid.value = false

  console.log(Number.isInteger(otherPrice.value), !otherPrice.value)

  if (
    !Number.isInteger(otherPrice.value) ||
    !otherPrice.value ||
    parseInt(otherPrice.value) <= 0
  ) {
    otherPrice.value = 1
  }
  if (otherPrice.value.toString().length > 5) {
    otherPrice.value = Number(String(otherPrice.value).slice(0, 5))
  }

  const index = rechargeInfo.findIndex((price) => {
    return price === otherPrice.value
  })
  currentIndex.value = index
  currentPrice.value = otherPrice.value
  await isNeedChangeQr()
  qrInvalid.value = false
}
const qrInvalid = ref(false)
const goPay = () => {
  console.log(qrUrl.value)
  window.open(qrUrl.value, '_self')
}
// const shutDownOder = async () => {
//   const res3 = await shutDowOrderApi(lastOutTrad.value)
//   if (res3.data.code !== 200) {
//     notyf.error('上次一次订单关闭失败,请等候一些时间再支付,避免引起意外错误!')
//   }
// }
const queryOrderStatus = async (pOutTrad: string) => {
  const orderItem = order.find((item) => {
    return item.outTrad === pOutTrad
  })
  const index = order.findIndex((item) => {
    return item.outTrad === pOutTrad
  })
  const res2 = await queryStatusApi(pOutTrad)
  if (res2.data.code === 511) {
    if (needQrTip.value) {
      orderItem.qrStatus = false
      qrInvalid.value = true
      return true
    }
  } else if (res2.data.code === 500) {
    notyf.error('支付失败,如有疑问请联系管理员')
  } else if (res2.data.code === 200) {
    orderItem.status = true
    currentPrice.value = orderItem.price
    cpIntegral.update(userSession.user?.integral + integralMft.value * orderItem.price)
    userSession.setUserInfo(
      'integral',
      userSession.user?.integral + integralMft.value * orderItem.price
    )
    const res5 = await getOrderInfoApi(pOutTrad)
    orderInfo.value = res5.data.result
    successOderModal.value = true
    order.splice(index, 1)
    await getQr(currentPrice.value)
  }
}
const refreshQr = () => {
  getQr(currentPrice.value)
  qrInvalid.value = false
}
const getQr = async (price: number) => {
  isLoaderActive.value = true
  const orderItem = order.find((item) => {
    return item.price === price
  })
  if (!orderItem) {
    const { data: res } = await queryQrApi(price)
    outTrad.value = res.result.out_trade_no
    qrUrl.value = res.result.code_url
    order.push({
      outTrad: outTrad.value,
      price: Number(res.result.total_fee),
      qrUrl: res.result.code_url,
      status: false,
      qrStatus: true,
    })
  } else {
    if (!orderItem.qrStatus) {
      const { data: res2 } = await queryQrApi(price)
      orderItem.qrUrl = res2.result.code_url
      outTrad.value = res2.result.out_trade_no
      orderItem.qrStatus = true
    }
    qrUrl.value = orderItem.qrUrl
  }
  isLoaderActive.value = false
}
const interval = setInterval(async () => {
  if (!!outTrad.value) {
    await queryOrderStatus(outTrad.value)
  }
}, 3000)
const getIntegetMagnification = async () => {
  const res = await getIntegetMagnificationApi()
  integralMft.value = res.data.result.integral
}
await getIntegetMagnification()
onMounted(async () => {
  cpIntegral = new CountUp('integralCount', userSession.user?.integral)
  cpIntegral.start()
  await getQr(1)
})
</script>

<template>
  <div class="intergral-container">
    <div class="intergral-title">
      <div class="title-icon" @click="goBack">
        <i class="iconify" data-icon="feather:arrow-left" aria-hidden="true"></i>
      </div>
      <div class="recharge">积分充值</div>
    </div>
    <div class="content">
      <div class="my-integral">
        <div>我的积分余额</div>
        <div id="integralCount" class="integral-count">0</div>
      </div>

      <div class="itg-recharge">
        <div class="itg-recharge-title item">在线充值</div>
        <div class="mount item">
          <span class="mount-title"> 充值金额:</span>

          <span
            v-for="(item, index) in rechargeInfo"
            :key="item"
            :class="['item', currentIndex === index ? 'active' : '']"
            @click="changeMount(index)"
            >{{ item }}元</span
          >
        </div>
        <div class="other-mount item">
          <div class="other-mount-title">其他金额:</div>
          <div class="other-mount-content">
            <VField class="mount-input">
              <VControl>
                <VInput
                  id="other-mount"
                  v-model="otherPrice"
                  autocomplete="off"
                  type="number"
                  min="1"
                  style="width: 170px"
                  placeholder="请输入金额(1 ~ 9999)元"
                />
              </VControl>
            </VField>
            <VButton color="primary" style="margin-left: 10px" @click="confirmPrice">
              确定</VButton
            >
          </div>
        </div>

        <div v-if="deskTop" class="pay-ways">
          <span class="pay-ways-title"> 支付方式:</span>
          <div>
            <VField class="is-flex">
              <VControl raw subcontrol>
                <VRadio
                  v-model="payWays"
                  value="wxPay"
                  label="微信扫码支付"
                  name="outlined_radio"
                  color="primary"
                />
              </VControl>
            </VField>
            <VLoader size="large" :active="isLoaderActive" translucent>
              <div class="qr-code">
                <qrcode-vue
                  v-if="!qrInvalid"
                  :size="150"
                  level="H"
                  :value="qrUrl"
                ></qrcode-vue>
                <div v-else class="qrInvalid" @click="refreshQr">
                  <i class="lnir lnir-reload" aria-hidden="true"></i>
                  <div>二维码过期</div>
                </div>
              </div>
            </VLoader>
          </div>
        </div>
        <div v-else>
          <VButton color="primary" elevated class="pay-now" @click="goPay">
            立即充值
          </VButton>
        </div>
      </div>
    </div>
    <VModal
      :open="successOderModal"
      actions="right"
      size="large"
      title="提示"
      @close="successOderModal = false"
    >
      <template #content>
        <div class="pay-suceess-content">
          <div class="pay-success-title">
            <i class="lnir lnir-checkmark-circle" aria-hidden="true"></i
            ><span class="success-tip">支付成功啦!</span>
            <span>充值到账会有一定延迟,如果超过10分钟未到账,请联系客服。</span>
          </div>
          <div class="pay-succes-content">
            <div>
              <span class="orderInfo-label">订单号:</span>
              <span>{{ orderInfo.outTradeNo }}</span>
            </div>
            <div>
              <span class="orderInfo-label">支付方式: </span><span> 微信支付</span>
            </div>
            <div>
              <span class="orderInfo-label"> 充值账号:</span>
              <span>{{ userSession.user?.name }}</span>
            </div>
            <div>
              <span class="orderInfo-label"> 充值金额:</span>
              <span>{{ orderInfo.amount }}元</span>
            </div>
            <div>
              <span class="orderInfo-label"> 积分数量:</span>
              <span> {{ orderInfo.integral }}</span>
            </div>
          </div>
        </div>
      </template>
      <template #action>
        <VButton color="primary" raised @click="successOderModal = false"> 确定</VButton>
      </template>
    </VModal>
  </div>
</template>
<style lang="scss" scoped>
@media (max-width: 600px) {
  .mount {
    .mount-title {
      display: block !important;
      margin-bottom: 10px !important;
    }
  }
  .other-mount {
    display: block !important;
    .other-mount-title {
      margin-bottom: 10px !important;
    }
    .mount-input {
      margin: 0 !important;
    }
  }
}
.pay-suceess-content {
  padding: 0 10px;
  .pay-success-title {
    display: flex;
    align-items: center;
    padding-bottom: 10px;
    border-bottom: 1px dashed var(--border);
    margin-bottom: 10px;
    font-size: 14px;
    i {
      font-size: 28px;
      color: var(--primary);
    }
    .success-tip {
      font-size: 18px;
      color: #ea1b14;
      font-weight: 700;
      padding: 0 8px;
    }
  }
  .pay-succes-content {
    div {
      margin-bottom: 10px;
      font-size: 14px;
      .orderInfo-label {
        display: inline-block;
        text-align: right;
        width: 5em;
      }
      span {
        padding-left: 10px;
      }
    }
  }
}
.intergral-container {
  min-height: 100vh;
  background-color: #f7f7f7;
  .intergral-title {
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
    .recharge {
      color: #838181;
      font-size: 16px;
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
  .content {
    width: calc(50vw + 10rem);

    margin: 30px auto;
    .my-integral {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30px;
      background: linear-gradient(to right, #52cc95 70%, #41b983);
      border-radius: 4px;
      margin-bottom: 30px;
      box-shadow: 3px 3px 10px #52cc95;
      div {
        font-size: 16px;
        color: white;
      }
      .integral-count {
        font-weight: 700;
        font-size: 24px;
        letter-spacing: 1px;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
      }
    }
    .itg-recharge {
      background-color: white;
      padding: 20px;
      .item {
        margin-bottom: 15px;
      }
      .itg-recharge-title {
        font-size: 16px;
      }
      .mount {
        .mount-title {
          padding-right: 20px;
        }
        .item {
          display: inline-block;
          margin: 10px 15px 10px 0;
          padding: 5px 0;
          width: 80px;
          text-align: center;
          border-radius: 3px;
          border: 1px solid #cdcdcd;
          cursor: pointer;
        }
        .active {
          border: 1px solid var(--primary);
          background-color: #edf4ed;
          color: var(--primary);
        }
      }
      .other-mount {
        display: flex;
        align-items: center;
        .other-mount-content {
          display: flex;
          align-items: center;
        }
        .mount-input {
          margin: 0 6px 0 20px;
        }
      }
      .pay-ways {
        display: flex;
        .pay-ways-title {
          padding-top: 11px;
          padding-right: 5px;
        }
        .qr-code {
          margin-top: 10px;
          width: 150px;
          height: 150px;
          .qrInvalid {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: white;
            cursor: pointer;
            i {
              font-size: 42px;
            }
            div {
              margin-top: 10px;
              color: white;
            }
            background-color: #000;
          }
        }
      }
      .pay-now {
        margin-top: 10px;
        width: 100%;
        height: 40px;
      }
    }
  }
}
</style>
