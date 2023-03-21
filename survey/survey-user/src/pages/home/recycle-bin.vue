<!--
 * @Author: August-Rushme
 * @Date: 2022-09-22 14:10:53
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-15 17:15:32
 * @FilePath: \survey-user\src\pages\home\recycle-bin.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useHead } from '@vueuse/head'
import {
  getRecycle,
  deleteProjectApi,
  recoveryProjectApi,
  cleanProjectApi,
} from '/@src/api/recycle'
import keTable from '/@src/components/base/ke-table/ke-table.vue'
import { Record } from '/@src/api/recycle/type'
import { columns, otherOptions } from './config/recycleTable'
import { keRequest } from '/@src/utils/keRequest'
useHead({
  title: '云测 - 项目回收站',
})
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
})
const deleteTipModal = ref(false)
const cleanTipModal = ref(false)
const projectId = ref('')
const tableData = ref<Record[]>([])
const getRecycleData = async () => {
  const res = await getRecycle({
    ...pageInfo.value,
  })
  tableData.value = res.data.result.records
}
const openDeleteProject = (id: string) => {
  deleteTipModal.value = true
  projectId.value = id
}
const deleteProject = async () => {
  deleteTipModal.value = false
  await keRequest(async () => {
    const res = await deleteProjectApi(projectId.value)
    return [res]
  }, '删除项目')
  await getRecycleData()
}
const recoveryProject = async (id: string) => {
  await keRequest(async () => {
    const res = await recoveryProjectApi(id)
    return [res]
  }, '恢复项目')
  await getRecycleData()
}
const openCleanProject = () => {
  cleanTipModal.value = true
}
const cleanProject = async () => {
  cleanTipModal.value = false
  await keRequest(async () => {
    const res = await cleanProjectApi()
    return [res]
  }, '清空回收站')
  await getRecycleData()
}
await getRecycleData()
</script>

<template>
  <VCard style="text-align: center">
    <div v-if="tableData.length > 0">
      <div class="recycle-title">
        <div class="tip">
          <i class="lnir lnir-electricity" aria-hidden="true"></i
          >数据清空后将无法恢复，请谨慎操作！
        </div>
        <div>
          <VButton @click="openCleanProject"> 清空回收站 </VButton>
        </div>
      </div>
      <keTable
        :data="tableData"
        :other-options="otherOptions"
        :colomns="columns"
        :need-sect-column="false"
        ><template #action="{ row }">
          <div>
            <VButton
              color="primary"
              icon="lnir lnir-play"
              style="margin-right: 10px"
              @click="recoveryProject(row.id)"
            >
              恢复
            </VButton>
            <VButton
              color="danger"
              icon="fas fa-trash-alt"
              @click="openDeleteProject(row.id)"
            >
              彻底删除
            </VButton>
          </div>
        </template>
      </keTable>
      <VModal
        :open="cleanTipModal"
        actions="center"
        title="提示"
        @close="cleanTipModal = false"
      >
        <template #content> 彻底删除将不可再恢复，确定吗？ </template>
        <template #action>
          <VButton color="primary" raised @click="cleanProject()"> 确定</VButton>
        </template>
      </VModal>
      <VModal
        :open="deleteTipModal"
        actions="center"
        title="提示"
        @close="deleteTipModal = false"
      >
        <template #content> 彻底删除将不可再恢复，确定吗？ </template>
        <template #action>
          <VButton color="primary" raised @click="deleteProject()"> 确定</VButton>
        </template>
      </VModal>
    </div>

    <div v-else class="empty">
      <img src="/@src/assets/project/empty.svg" alt="" />
      <div class="desc">暂无项目</div>
    </div></VCard
  >
</template>

<style lang="scss" scoped>
.recycle-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  .tip {
    display: flex;
    align-items: center;
    color: #1ea0fa;
    i {
      padding-right: 5px;
      font-size: 18px;
    }
  }
}
</style>
