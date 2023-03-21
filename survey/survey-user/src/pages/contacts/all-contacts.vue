<!--
 * @Author: August-Rushme
 * @Date: 2022-09-23 10:09:10
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-10 17:37:47
 * @FilePath: \survey-user\src\pages\contacts\all-contacts.vue
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
-->
<script setup lang="ts">
import { useGroupSession } from '/@src/stores/group'
import noContact from '/@src/assets/noContact.png'
import drawer from '/@src/components/drawer.vue'
import keTable from '/@src/components/base/ke-table/ke-table.vue'
import { keRequest } from '/@src/utils/keRequest'
import { columns, otherOptions } from './configs/tableconfig'
import creatGroup from '/@src/components/contactGroup/index.vue'
import contactForm from '/@src/components/contactForm/index.vue'
import {
  getAllContactApi,
  getContactApi,
  addContactApi,
  getContactCountApi,
} from '/@src/api/contact/index'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { getGroupApi } from '/@src/api/contact/index'
import dayjs from 'dayjs'
import { debounce } from '/@src/utils/debounce'
import { useHead } from '@vueuse/head'
import { contactResult } from '/@src/api/contact/type'
import { checkPhone, checkEmail } from '/@src/utils/formCheck'
import { judegeDevice } from '/@src/utils/judgeDevice'
const drawerDisplay = ref(false)
const createdrawerDisplay = ref(false)
const groupSession = useGroupSession()
const isLoaderActive = ref(false)
const searchName = ref('')
const tableData = ref<any>([])
const fieldData = ref<any>([])
const groupData = ref<any>([])
const isMobile = ref(false)
const isiPhoneiPad = ref(false)
const editDrawerWidth = ref('35vw')
const creatDrawerWidth = ref('80vw')
const os = judegeDevice()
if (os.isAndroid || os.isPhone) {
  // 手机
  isMobile.value = true
  editDrawerWidth.value = '100vw'
  creatDrawerWidth.value = '80vw'
} else if (os.isTablet) {
  // 平板
  editDrawerWidth.value = '70vw'
  creatDrawerWidth.value = '50vw'
  isiPhoneiPad.value = true
}
const openContactDetail = () => {
  addContactModal.value = true
}
const formData = ref({
  surContact: {
    name: '',
    phone: '',
    email: '',
    birthday: '',
    sex: '男',
    address: '',
    groupNameList: [],
    id: '',
  },
  surContactFieldInformationList: [],
  surGroupContactList: [],
})
const editData = ref({
  surContact: {
    name: '',
    phone: '',
    email: '',
    birthday: '',
    sex: '',
    address: '',
    groupNameList: [],
    id: '',
  },
  surContactFieldInformationList: [
    {
      id: '',
      fieldId: '',
      information: '',
    },
  ],
  surGroupContactList: [
    {
      id: '',
      groupId: '',
    },
  ],
})
const openCreatGroup = () => {
  createdrawerDisplay.value = true
}

const drawerNoEdit = ref(false)
const addContactModal = ref(false)
const getContactData = async () => {
  if (groupSession.id === '0') {
    await getAllContactData()
  } else {
    await getContact(groupSession.id)
  }
}
const debounceSearch = async () => {
  isLoaderActive.value = true
  if (groupSession.id === '0') {
    const res = await getContactApi({ seachName: searchName.value })
    dataMap(res.data.result)
  } else {
    const res = await getContactApi({
      seachName: searchName.value,
      groupId: groupSession.id,
    })
    dataMap(res.data.result)
  }
  isLoaderActive.value = false
}
const debounceGetData = debounce(async () => {
  await debounceSearch()
}, 500)
watch(searchName, async () => {
  await debounceGetData()
})
const updateContact = async () => {
  drawerDisplay.value = false
  await getContactData()
  await getContactCount()
  const res = await getGroupApi()
  groupSession.setGroupList(res.data.result)
}
const dataMap = (res: contactResult[]) => {
  tableData.value = res.map((item: any) => {
    return item.surContact
  })
  fieldData.value = res.map((item: any) => {
    return item.surContactFieldInformationList
  })
  groupData.value = res.map((item: any) => {
    return item.surGroupContactList
  })
}
const getContact = async (groupId: string) => {
  isLoaderActive.value = true
  const res = await getContactApi({ groupId: groupId })
  dataMap(res.data.result)
  isLoaderActive.value = false
}
watch(
  () => groupSession.id,
  async (newValue: string) => {
    if (newValue !== '0') {
      await getContact(newValue)
    } else {
      await getAllContactData()
    }
  }
)
const getAllContactData = async () => {
  isLoaderActive.value = true
  const res = await getAllContactApi()
  if (res.data.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '获取联系人失败,请稍后重试',
    })
  }
  dataMap(res.data.result)
  isLoaderActive.value = false
  return res
}
const drawercolse = () => {
  resetFormData(editData)
  drawerNoEdit.value = false
}
const updateNoEdit = () => {
  drawerNoEdit.value = true
}
const cancelAddContact = () => {
  addContactModal.value = false
  resetFormData(formData)
}
const rowClick = (tableItem: any) => {
  const index = tableData.value.findIndex((item: any) => {
    return item.id === tableItem.row.id
  })
  editData.value.surContact = { ...tableData.value[index] }
  editData.value.surGroupContactList = [...groupData.value[index]]
  editData.value.surContactFieldInformationList = [...groupData.value[index]]
  drawerDisplay.value = true
}
const resetFormData = (data: any) => {
  data.value.surContact.address = ''
  data.value.surContact.phone = ''
  data.value.surContact.email = ''
  data.value.surContact.name = ''
  data.value.surContact.birthday = ''
  data.value.surContact.sex = ''
  data.value.groupNameList = []
  data.value.surContactFieldInformationList = []
  data.value.surGroupContactList = []
}
const addContact = async () => {
  if (
    formData.value.surContact.phone.length > 0 ||
    formData.value.surContact.sex.length > 0 ||
    formData.value.surContact.name.length > 0
  ) {
    if (
      checkEmail(formData.value.surContact.email) &&
      checkPhone(formData.value.surContact.phone)
    ) {
      keRequest(async () => {
        const res = await addContactApi({
          ...formData.value,
          surContact: {
            ...formData.value.surContact,
            birthday: dayjs(formData.value.surContact.birthday).format(
              'YYYY-MM-DD HH:mm:ss'
            ),
          },
        })
        const res2 = await getAllContactData()
        const res3 = await getGroupApi()
        await getContactCount()
        groupSession.setGroupList(res3.data.result)
        groupSession.setId('')
        groupSession.setGroupActiveIndex(0)
        return [res, res2, res3]
      }, '添加联系人')
      resetFormData(formData)
      addContactModal.value = false
    }
  } else {
    Notice({
      notice_type: 'warning',
      message: '姓名，性别，手机号不能为空',
    })
  }
}
const getContactCount = async () => {
  const res = await getContactCountApi()
  if (res.data.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '获取联系人人数失败,请稍后重试',
    })
  }
  groupSession.setCount(res.data.result)
  groupSession.setTotalCount(res.data.result)
  return res
}
onMounted(async () => {
  groupSession.setName('我的联系人')
  await getAllContactData()
})

useHead({
  title: '云测 - 联系人',
})
</script>

<template>
  <div class="right">
    <div class="group-title">
      <div>
        <span class="name"> {{ groupSession.name }}</span>
        <span class="count">共{{ groupSession.count }}人</span>
      </div>
      <div class="group-control">
        <VField class="search-control">
          <VControl icon="lnir lnir-search">
            <VInput
              v-model="searchName"
              type="text"
              placeholder="搜索联系人"
              style="min-width: 110px"
            />
          </VControl>
        </VField>
        <!-- <VButton icon="lnir lnir-funnel" class="btn"> 高级筛选 </VButton>
        <VButton icon="lnir lnir-layout-alt-2" class="btn"> 联系人去重 </VButton>
        <VButton icon="lnir lnir-cotton-bud" class="btn"> 自定义显示列 </VButton>
        <VButton icon="lnir lnir-cloud-sync" class="btn"> 已同步项目 </VButton> -->
        <VDropdown class="btn">
          <template #button="{ toggle }">
            <VButton
              icon="lnir lnir-plus"
              color="primary"
              class="is-trigger"
              @click="toggle"
            >
              添加联系人
            </VButton>
          </template>

          <template #content="{ close }">
            <div @mouseleave="close">
              <a class="dropdown-item" @click="openContactDetail"> 手动添加 </a>
              <!-- <a class="dropdown-item"> excel导入 </a>
              <a class="dropdown-item"> 从项目中同步 </a>
              <a class="dropdown-item"> 新建表单收集 </a>
              <a class="dropdown-item"> 邀请用户 </a> -->
            </div>
          </template>
        </VDropdown>
        <div class="groupCreate" @click="openCreatGroup">
          <a> <i class="fas fa-layer-group" aria-hidden="true"></i>新建群组</a>
        </div>
      </div>
    </div>
    <VLoader size="large" :active="isLoaderActive" translucent class="list-loader">
      <div class="group-content">
        <VCard v-if="tableData.length > 0">
          <keTable
            :data="tableData"
            :other-options="otherOptions"
            :colomns="columns"
            :need-sect-column="false"
            @row-click="rowClick"
          >
            <template #groupNameList="{ row }"
              ><span
                v-for="item in row.groupNameList"
                :key="item.id"
                class="group-item"
                >{{ item }}</span
              >
            </template>
            <template #birthday="{ row }">{{ row.birthday.split(' ')[0] }}</template>
          </keTable>
        </VCard>
        <div v-if="tableData.length <= 0" class="no-data">
          <img :src="noContact" alt="" style="width: 30%" />
          <div>暂未找到匹配的联系人</div>
        </div>
      </div>
    </VLoader>
    <drawer
      v-model="drawerDisplay"
      :width="editDrawerWidth"
      title="联系人详情"
      @drawerColse="drawercolse"
    >
      <contactForm
        v-if="drawerDisplay"
        v-model="editData"
        :no-edit="drawerNoEdit"
        :need-foot="true"
        @updateNoEdit="updateNoEdit"
        @updateContact="updateContact"
      ></contactForm>
    </drawer>
    <drawer v-model="createdrawerDisplay" :width="creatDrawerWidth" title="创建群组">
      <creatGroup></creatGroup>
    </drawer>
    <VModal
      :open="addContactModal"
      actions="right"
      title="添加联系人"
      size="large"
      @close="cancelAddContact"
    >
      <template #content>
        <contactForm v-model="formData"></contactForm>
      </template>
      <template #action>
        <VButton color="primary" raised @click="addContact"> 添加</VButton>
      </template>
    </VModal>
  </div>
</template>

<style lang="scss" scoped>
@media (max-width: 1100px) and (min-width: 600px) {
  .groupCreate {
    display: block !important;
    background-color: var(--primary);
    text-align: center !important;
    line-height: 38px;
    height: 38px;
    padding: 0 20px;
    border-radius: 3px;
    a {
      color: white !important;
    }
  }
  :deep(.input) {
    width: 20vw !important;
  }
  :deep(.select) {
    width: 20vw !important;
  }
}
@media (max-width: 600px) {
  .group-title {
    display: block !important;

    .group-control {
      margin-top: 15px;
    }
  }
  .groupCreate {
    display: block !important;
    position: absolute;
  }
  :deep(.input) {
    width: 40vw !important;
  }
  :deep(.select) {
    width: 40vw !important;
  }
}
:deep(.input) {
  width: 10vw;
}
:deep(.select) {
  width: 10vw;
}
.right {
  .group-title {
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .name {
      font-size: 16px;
      margin-right: 6px;
    }
    .count {
      font-size: 12px;
      color: #aaa;
    }
  }
  .group-control {
    display: flex;
    .btn {
      margin-left: 10px;
    }
    .groupCreate {
      display: none;
      right: 10px;
      top: 0px;
      margin-left: 5px;
      a {
        color: var(--primary);
        i {
          padding-right: 5px;
        }
      }
    }
  }
  .group-content {
    .no-data {
      margin-top: 50px;
      text-align: center;
    }
    div {
      margin-top: 10px;
    }
  }
  .group-item {
    display: inline-block;
    padding: 5px 10px;
    max-width: 8em;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    margin-right: 10px;
    background-color: #edf4ed;
    border-radius: 3px;
    color: var(--primary);
  }
}
</style>
