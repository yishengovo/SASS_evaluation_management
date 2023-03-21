<script lang="ts" setup>
// import { IFormData } from './type'
import {
  getGroupApi,
  addGroupApi,
  deleteContactApi,
  updateContactApi,
} from '/@src/api/contact/index'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { keRequest } from '/@src/utils/keRequest'
import { IGroupList } from '/@src/components/contactGroup/type'
import { useGroupSession } from '/@src/stores/group'
import { checkPhone, checkEmail } from '/@src/utils/formCheck'
type propsEle = {
  modelValue: any
  noEdit?: boolean
  needFoot?: boolean
}
const canAddGroup = ref(false)
const groupName = ref('')
const groupSession = useGroupSession()
const currentActiveId = ref('0')
const props = withDefaults(defineProps<propsEle>(), {
  modelValue: {
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
  },
  noEdit: true,
  needFoot: false,
})
const currentSelectGroup = ref()
const confirm = (close: () => void) => {
  if (currentActiveId.value !== '0') {
    const item = formData.value.surGroupContactList.find((item: any) => {
      return item.groupId === currentSelectGroup.value.id
    })
    if (!!item) {
      return Notice({
        notice_type: 'warning',
        message: '已存在该群组请勿重复添加!',
      })
    } else {
      formData.value.surGroupContactList.push({
        groupId: currentSelectGroup.value.id,
        id: null,
      })
      groupNameList.value.push(currentSelectGroup.value.name)
      canAddGroup.value = false
      close()
    }
  } else {
    return Notice({
      notice_type: 'warning',
      message: '请选择群组!',
    })
  }
}
const deleteGroupIn = (index: number) => {
  formData.value.surGroupContactList.splice(index, 1)
  groupNameList.value.splice(index, 1)
}
const closeAddGroup = (close: () => void) => {
  close()
  canAddGroup.value = false
}
const emit = defineEmits(['update:modelValue', 'updateNoEdit', 'updateContact'])
const isNotEdit = computed(() => {
  return !props.noEdit
})
const groupNameList = ref<any>([''])
watch(
  () => props.modelValue,
  () => {
    emit('update:modelValue')
  }
)
const formData = computed(() => {
  return props.modelValue
})

const becomeEdit = () => {
  emit('updateNoEdit')
}
const openAddgroup = () => {
  canAddGroup.value = true
}
const changeGroupSelect = (id: string, item: any) => {
  currentActiveId.value = id
  currentSelectGroup.value = item
}
const groupList = ref<IGroupList[]>([])
const getGroupData = async () => {
  const res = await getGroupApi()
  groupList.value = res.data.result
  groupSession.setGroupList(res.data.result)
  return res
}
const addGroup = async () => {
  if (groupName.value.length > 0) {
    if (groupName.value.length > 15) {
      Notice({
        notice_type: 'error',
        message: '最多只能输入15个字符!',
      })
    } else {
      keRequest(async () => {
        const res = await addGroupApi(groupName.value)
        const res2 = await getGroupData()
        return [res, res2]
      }, '添加群组')
      canAddGroup.value = false
      groupName.value = ''
    }
  } else {
    Notice({
      notice_type: 'error',
      message: '群组不能为空!',
    })
  }
}
const deleteContact = async () => {
  await keRequest(async () => {
    const res = await deleteContactApi(formData.value.surContact.id as string)
    return [res]
  }, '删除联系人')
  groupSession.setTotalCount(groupSession.count - 1)
  emit('updateContact')
}
const updateContact = async () => {
  if (
    formData.value.surContact.phone.length > 0 ||
    formData.value.surContact.sex.length > 0 ||
    formData.value.surContact.name.length > 0
  ) {
    if (
      checkEmail(formData.value.surContact.email) &&
      checkPhone(formData.value.surContact.phone)
    ) {
      await keRequest(async () => {
        const res = await updateContactApi({
          ...formData.value,
        })
        return [res]
      }, '修改联系人')
      emit('updateContact')
    }
  } else {
    Notice({
      notice_type: 'warning',
      message: '姓名，性别，手机号不能为空',
    })
  }
}

onMounted(async () => {
  await getGroupData()
  groupNameList.value = formData.value.surContact.groupNameList
})
</script>
<template>
  <div class="contact-form">
    <div class="form-title">基础信息</div>
    <div class="is-flex">
      <VField label="姓名:" horizontal style="margin-right: 10px">
        <VControl fullwidth>
          <template v-if="isNotEdit">
            <span class="not-edit" @click="becomeEdit">{{
              formData.surContact.name
            }}</span>
          </template>
          <template v-else>
            <VInput
              v-model="formData.surContact.name"
              type="text"
              placeholder="请输入姓名"
            />
          </template>
        </VControl>
      </VField>
      <VField label="性别:" horizontal>
        <template v-if="isNotEdit">
          <span class="not-edit" @click="becomeEdit">{{ formData.surContact.sex }}</span>
        </template>
        <template v-else>
          <VControl>
            <VSelect v-model="formData.surContact.sex">
              <VOption value="男">男</VOption>
              <VOption value="女">女</VOption>
            </VSelect>
          </VControl>
        </template>
      </VField>
    </div>
    <div class="is-flex">
      <VField label="生日:" horizontal style="margin-right: 10px">
        <VControl fullwidth>
          <template v-if="isNotEdit">
            <span class="not-edit" @click="becomeEdit">{{
              formData.surContact.birthday?.split(' ')[0]
            }}</span>
          </template>
          <template v-else>
            <ClientOnly>
              <VDatePicker
                v-model="formData.surContact.birthday"
                color="green"
                trim-weeks
              >
                <template #default="{ inputValue, inputEvents }">
                  <VField>
                    <VControl icon="feather:calendar">
                      <VInput :value="inputValue" v-on="inputEvents" />
                    </VControl>
                  </VField>
                </template>
              </VDatePicker>
            </ClientOnly>
          </template>
        </VControl>
      </VField>
      <VField label="手机:" horizontal style="margin-right: 10px">
        <VControl fullwidth>
          <template v-if="isNotEdit">
            <span class="not-edit" @click="becomeEdit">{{
              formData.surContact.phone
            }}</span>
          </template>
          <template v-else>
            <VInput
              v-model="formData.surContact.phone"
              type="text"
              placeholder="请输入手机号"
            />
          </template>
        </VControl>
      </VField>
    </div>
    <div class="is-flex">
      <div style="margin-right: 10px">
        <VField label="邮箱:" horizontal>
          <VControl fullwidth>
            <template v-if="isNotEdit">
              <span class="not-edit" @click="becomeEdit">{{
                formData.surContact.email
              }}</span>
            </template>
            <template v-else>
              <VInput
                v-model="formData.surContact.email"
                type="text"
                placeholder="请输入邮箱"
              />
            </template>
          </VControl>
        </VField>
      </div>

      <VField label="地址:" horizontal>
        <VControl fullwidth>
          <template v-if="isNotEdit">
            <span class="not-edit" @click="becomeEdit">{{
              formData.surContact.address
            }}</span>
          </template>
          <template v-else>
            <VTextarea
              v-model="formData.surContact.address"
              rows="3"
              placeholder="请输入地址"
            ></VTextarea>
          </template>
        </VControl>
      </VField>
    </div>
    <div style="margin: 15px 0">群组</div>
    <template v-if="formData.surGroupContactList.length > 0">
      <div class="groupIn">
        所在群组:
        <span v-for="(item, index) in groupNameList" :key="index" class="item">
          {{ item }}
          <a class="delete-group" @click="deleteGroupIn(index)">
            <i class="lnir lnir-cross-circle" aria-hidden="true"></i
          ></a>
        </span>
      </div>
    </template>
    <VDropdown up>
      <template #button="{ toggle }">
        <div class="add" @click="toggle">
          <i class="lnir lnir-circle-plus" aria-hidden="true"></i>添加到群组
        </div>
      </template>

      <template #content="{ close }">
        <div>
          <div class="field">
            <div class="toolptip-title">
              <div>请选择群组</div>
              <div class="add" @click="openAddgroup">+新建群组</div>
            </div>
            <div v-if="canAddGroup" class="addGroupFild">
              <VField>
                <VControl>
                  <VInput
                    v-model="groupName"
                    type="text"
                    placeholder="请输入群组"
                    style="width: 100px"
                  />
                </VControl>
              </VField>

              <a @click="addGroup">新建</a>
            </div>
            <div class="group-content">
              <div
                v-for="item in groupList"
                :key="item.id"
                :class="['item', item.id === currentActiveId ? 'active' : '']"
                @click="changeGroupSelect(item.id, item)"
              >
                {{ item.name }}
              </div>
            </div>
            <div class="bottom">
              <div>
                <span @click="closeAddGroup(close)"><span>取消</span> </span>
                <span class="confirm" @click="confirm(close)"> 确定</span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </VDropdown>

    <template v-if="needFoot">
      <div style="margin-top: 15px">
        <VButton color="primary" @click="updateContact"> 保存修改 </VButton>
        <VButton color="danger" style="margin-left: 15px" @click="deleteContact">
          删除
        </VButton>
      </div></template
    >
  </div>
</template>

<style scoped lang="scss">
@media (max-width: 1100px) {
  .groupCreate {
    width: 30vw !important;
  }
}
@media (max-width: 600px) {
  .not-edit {
    width: 40vw !important;
  }
}
:deep(.dropdown-content) {
  padding: 10px !important;
}
:deep(.field) {
  padding: 0 !important;
}

.contact-form {
  .form-title {
    margin-bottom: 15px;
  }
  .not-edit {
    display: inline-block;
    width: 10vw;
    height: 40px;
    line-height: 40px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    text-align: center;
    cursor: pointer;
    box-sizing: border-box;
    &:hover {
      border: 1px solid var(--primary);
    }
  }
  .groupIn {
    margin: 15px 0;
    span {
      position: relative;
      display: inline-block;
      margin-bottom: 10px;
      margin-left: 15px;
      padding: 6px 15px;
      color: var(--primary);
      background-color: #edf4ed;
      .delete-group {
        position: absolute;
        color: var(--primary);
        right: 1px;
        top: -3px;
        i {
          font-size: 10px;
        }
      }
    }
  }
  .add {
    color: var(--primary);
    cursor: pointer;
    i {
      padding-right: 6px;
    }
  }
  .field {
    padding: 5px;
    .toolptip-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .addGroupFild {
      display: flex;
      max-width: 138px;
      margin-top: 10px;
      a {
        padding-left: 10px;
        padding-top: 8px;
        width: 3em;
        font-size: 14px;
        color: var(--primary);
      }
    }
    .group-content {
      max-height: 120px !important;
      max-width: 138px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow-x: hidden;
      overflow-y: scroll;
      .item {
        padding: 5px !important;
        cursor: pointer;
        &:hover {
          background-color: #edf4ed;
        }
      }
      .active {
        background-color: #edf4ed;
      }
    }
    .bottom {
      margin-top: 10px;
      display: flex;
      justify-content: flex-end;
      span {
        padding-left: 10px;
        cursor: pointer;
      }
      .confirm {
        color: var(--primary);
      }
    }
  }
}
:deep(.field.is-horizontal .field-label .label) {
  color: #000 !important;
  width: 4em !important;
}
</style>
