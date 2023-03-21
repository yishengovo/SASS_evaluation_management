<!--
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-01 17:34:10
 * @FilePath: \survey-user\src\pages\contacts.vue
 * @Description: 我的项目

 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->

<script setup lang="ts">
import { getHistoryOrderApi } from '/@src/api/pay'
import { Record } from '/@src/api/pay/type'
import { columns, otherOptions } from './configs/allorder'
import keTable from '/@src/components/base/ke-table/ke-table.vue'
import noOrder from '/@src/assets/noOrder.png'
const router = useRouter()
const goBack = async () => {
  router.push('/home/all-project')
}
const isLoaderActive = ref(false)
const currentPage = ref(1)
const pageInfo = ref({
  pageSize: 10,
  pageNo: 1,
})
let total = ref(0)
const tableData = ref<Record[]>([])
watch(currentPage, async (newValue) => {
  //直接监听
  pageInfo.value.pageNo = newValue
  await getData()
})
const getData = async () => {
  isLoaderActive.value = true
  const res = await getHistoryOrderApi({
    ...pageInfo.value,
  })
  tableData.value = res.data.result.records
  total.value = res.data.result.total
  isLoaderActive.value = false
}
await getData()
</script>

<template>
  <div class="order-container">
    <div class="order-title">
      <div class="title-icon" @click="goBack">
        <i class="iconify" data-icon="feather:arrow-left" aria-hidden="true"></i>
      </div>
      <div class="recharge">历史订单</div>
    </div>
    <div class="order-content">
      <div class="order-content-title">
        <div>我的订单</div>
        <div>
          <RouterLink to="/auth/recharge"><a>积分充值</a></RouterLink>
        </div>
      </div>
      <VLoader size="large" :active="isLoaderActive" translucent>
        <VCard>
          <div v-if="tableData.length > 0">
            <keTable
              :data="tableData"
              :other-options="otherOptions"
              :colomns="columns"
              :need-sect-column="false"
            >
              <template #succeeded="{ row }">
                <span v-if="row.succeeded" class="finish"> 成功</span>
                <span v-else class="unfish">失败</span></template
              >
            </keTable>
            <div>
              <VFlexPagination
                v-model:current-page="currentPage"
                :item-per-page="pageInfo.pageSize"
                :total-items="total"
                :max-links-displayed="5"
              />
            </div>
          </div>
          <div v-else style="width: 100%; padding: 0 1vw">
            <div style="margin: 30px auto; max-width: 900px; text-align: center">
              <img :src="noOrder" alt="" style="width: 20vw; height: 35vh" />
              <div style="text-align: center; margin-top: 20px">暂无订单</div>
            </div>
          </div>
        </VCard>
      </VLoader>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.order-container {
  min-height: 100vh;
  background-color: #f7f7f7;
  .unfish {
    padding: 5px 15px;
    border-radius: 3px;
    color: #ba3636;
    background-color: #d49a9a;
  }
  .finish {
    padding: 5px 15px;
    border-radius: 3px;
    color: #3db03d;
    background-color: #c2dfc2;
  }
  .order-title {
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
  .order-content {
    width: calc(50vw + 10rem);
    margin: 30px auto;
    .order-content-title {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      font-size: 16px;
    }
  }
}
</style>
