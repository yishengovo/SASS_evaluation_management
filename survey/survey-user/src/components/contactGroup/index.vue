<script lang="ts" setup>
import { useGroupSession } from '/@src/stores/group'
import {
  getGroupApi,
  addGroupApi,
  editGroupApi,
  deleteGroupApi,
  getContactCountApi,
} from '/@src/api/contact/index'
import { Notice } from '/@src/components/base/au-notice/Notice'
import { keRequest } from '/@src/utils/keRequest'
const groupSession = useGroupSession()
const addGroupModel = ref(false)
const editGroupModel = ref(false)
const deleteGroupModel = ref(false)
const currentGroupId = ref('')
const currentModalGroup = ref('')
const group = computed(() => {
  return groupSession.groupList
})
const openEditModal = (index: number, id: string) => {
  editGroupModel.value = true
  currentGroupId.value = id
}
const openDeleteModal = (index: number, id: string) => {
  deleteGroupModel.value = true
  currentGroupId.value = id
}

const changeGroup = (name: string, count: number, index: number, id: string) => {
  groupSession.setName(name)
  groupSession.setId(id)
  groupSession.setCount(count)
  groupSession.setGroupActiveIndex(index + 1)
}
const addGroup = async () => {
  if (currentModalGroup.value.length > 15) {
    return Notice({
      notice_type: 'error',
      message: '最多只能输入15个字符!',
    })
  }
  keRequest(async () => {
    const res = await addGroupApi(currentModalGroup.value)
    const res2 = await getGroupData()
    groupSession.setGroupActiveIndex(groupSession.curentActiveIndex + 1)
    groupSession.setId(groupSession.groupList[groupSession.curentActiveIndex - 1].id)
    groupSession.setCount(
      groupSession.groupList[groupSession.curentActiveIndex - 1].count
    )
    groupSession.setName(groupSession.groupList[groupSession.curentActiveIndex - 1].name)
    return [res, res2]
  }, '添加群组名称')
  addGroupModel.value = false
  currentModalGroup.value = ''
}
const editGroup = () => {
  if (currentModalGroup.value.length > 0) {
    if (currentModalGroup.value.length > 15) {
      Notice({
        notice_type: 'warning',
        message: '最多只能输入15个字符!',
      })
    }
    keRequest(async () => {
      const res = await editGroupApi({
        name: currentModalGroup.value,
        id: currentGroupId.value,
      })
      const res2 = await getGroupData()
      return [res, res2]
    }, '修改群组名称')
    editGroupModel.value = false
    currentModalGroup.value = ''
  } else {
    Notice({
      notice_type: 'warning',
      message: '群组不能为空!',
    })
  }
}
const deleteGroup = () => {
  keRequest(async () => {
    const res = await deleteGroupApi(currentGroupId.value)
    const res2 = await getGroupData()
    groupSession.setGroupActiveIndex(groupSession.curentActiveIndex - 1)
    if (groupSession.curentActiveIndex === 0) {
      groupSession.setId('')
      groupSession.setCount(groupSession.totolCout)
      groupSession.setName('我的联系人')
    } else {
      groupSession.setId(groupSession.groupList[groupSession.curentActiveIndex - 1].id)
      groupSession.setCount(
        groupSession.groupList[groupSession.curentActiveIndex - 1].count
      )
      groupSession.setName(
        groupSession.groupList[groupSession.curentActiveIndex - 1].name
      )
    }
    return [res, res2]
  }, '删除群组')
  deleteGroupModel.value = false
}
const openAddGroup = () => {
  addGroupModel.value = true
}
const getGroupData = async () => {
  const res = await getGroupApi()
  if (res.data.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '获取群组信息失败,请稍后重试',
    })
  }
  groupSession.setGroupList(res.data.result)
  return res
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
  await getGroupData()
  await getContactCount()
})
</script>
<template>
  <div class="container">
    <div class="left">
      <div class="create-group"><div @click="openAddGroup">+ 新建群组</div></div>
      <div
        :class="['my-contact', groupSession.curentActiveIndex === 0 ? 'active' : '']"
        @click="changeGroup('我的联系人', groupSession.totolCout, -1, '0')"
      >
        <i class="lnir lnir-user-alt-1" aria-hidden="true"></i>我的联系人({{
          groupSession.totolCout
        }})
      </div>
      <div
        v-for="(item, index) in group"
        :key="index"
        :class="[
          'group-item',
          groupSession.curentActiveIndex - 1 === index ? 'active' : '',
        ]"
        @click="changeGroup(item.name, item.count, index, item.id)"
      >
        <div x>{{ item.name }}({{ item.count }})</div>
        <div>
          <Tippy>
            <i
              class="fas fa-edit"
              aria-hidden="true"
              @click="openEditModal(index, item.id)"
            ></i>
            <template #content>编辑群组</template>
          </Tippy>
          <Tippy>
            <i
              class="fas fa-trash-alt"
              style="padding-right: 5px"
              aria-hidden="true"
              @click="openDeleteModal(index, item.id)"
            ></i>
            <template #content>删除群组</template>
          </Tippy>
        </div>
      </div>
    </div>
    <VModal
      :open="addGroupModel"
      actions="right"
      title="新建群组"
      @close="addGroupModel = false"
    >
      <template #content>
        <div>
          <div style="margin-bottom: 10px">群组名称</div>
          <VField>
            <VControl>
              <VInput
                v-model="currentModalGroup"
                type="text"
                placeholder="请输入群组名称"
              />
            </VControl>
          </VField>
        </div>
      </template>
      <template #action>
        <VButton color="primary" raised @click="addGroup()"> 创建</VButton>
      </template>
    </VModal>
    <VModal
      :open="editGroupModel"
      actions="right"
      title="修改群组"
      @close="editGroupModel = false"
    >
      <template #content>
        <div>
          <div style="margin-bottom: 10px">修改群组名称</div>
          <VField>
            <VControl>
              <VInput
                v-model="currentModalGroup"
                type="text"
                placeholder="请输入群组名称"
              />
            </VControl>
          </VField>
        </div>
      </template>
      <template #action>
        <VButton color="primary" raised @click="editGroup()"> 修改</VButton>
      </template>
    </VModal>
    <VModal
      :open="deleteGroupModel"
      actions="right"
      title="删除群组"
      @close="deleteGroupModel = false"
    >
      <template #content>
        <div>确认删除该群组吗？删除后群组内的联系人会释放，不会被删除。</div>
      </template>
      <template #action>
        <VButton color="primary" raised @click="deleteGroup()"> 确定</VButton>
      </template>
    </VModal>
  </div>
</template>
<style scoped lang="scss">
.container {
  width: 100%;
  .left {
    .create-group {
      display: flex;
      justify-content: center;
      padding: 0 30px;
      div {
        width: 100%;
        height: 35px;
        line-height: 35px;
        text-align: center;
        border: 1px solid var(--primary);
        color: var(--primary);
        cursor: pointer;
      }
    }
    .my-contact {
      margin-top: 20px;
      padding: 10px 0;
      padding-left: 30px;
      color: var(--primary);
      cursor: pointer;
      &:hover {
        background-color: #edf4ed;
      }
    }
    .group-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 10px 10px 30px;
      cursor: pointer;
      i {
        display: none;
        color: #707070;
        padding-right: 10px;
      }
      &:hover {
        background-color: #edf4ed;
        i {
          display: inline-block;
          color: var(--primary);
        }
      }
    }
    .active {
      background-color: #edf4ed;
    }
  }
}
</style>
