<script lang="ts" setup>
import { VxeTableInstance, VxeTableEvents } from 'vxe-table'
import { PropType } from 'vue'
import { IColumns } from './type'
defineProps({
  data: {
    type: Array,
    required: true,
  },
  colomns: {
    type: Array as PropType<IColumns[]>,
    required: true,
  },
  otherOptions: {
    type: Object,
    default: () => ({}),
  },
  needSectColumn: {
    type: Boolean,
    default: true,
  },
})
// const footerMethod: VxeTablePropTypes.FooterMethod = ({ columns }) => {
//   const footerData = [
//     columns.map((column, columnIndex) => {
//       if (columnIndex === 0) {
//         return '合计'
//       }
//       if (['date'].includes(column.field)) {
//         return '说明 xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
//       }
//       if (['rate'].includes(column.field)) {
//         return '不想换行不想换行不想换行不想换行不想换行不想换行不想换行不想换行'
//       }
//       return null
//     }),
//   ]
//   return footerData
// }
const emit = defineEmits(['rowClick'])
const rowClick = (item: any) => {
  emit('rowClick', item)
}
const xTable1 = ref<VxeTableInstance>()

const selectAllChangeEvent1: VxeTableEvents.CheckboxAll = ({ checked }) => {
  const $table = xTable1.value
  const records = $table?.getCheckboxRecords()
  console.log(checked ? '所有勾选事件' : '所有取消事件', records)
}

const selectChangeEvent1: VxeTableEvents.CheckboxChange = ({ checked }) => {
  const $table = xTable1.value
  const records = $table?.getCheckboxRecords()
  console.log(checked ? '勾选事件' : '取消事件', records)
}
// const chck = () => {}
// const getSelectEvent1 = () => {
//   const $table = xTable1.value
//   const selectRecords = $table?.getCheckboxRecords()
//   VXETable.modal.alert(`${selectRecords?.length}条数据`)
// }
</script>
<template>
  <div class="contanier">
    <vxe-table
      ref="xTable1"
      border
      show-header-overflow
      height="300"
      align="center"
      :row-config="{ isHover: true }"
      class="mytable-scrollbar"
      :data="data"
      v-bind="otherOptions"
      @cell-click="rowClick"
    >
      <vxe-column v-if="needSectColumn" type="checkbox" width="60"></vxe-column>
      <vxe-column
        v-for="item in colomns"
        :key="item.field"
        :field="item.field"
        :title="item.title"
        :min-width="item.minwidth ? item.minwidth : '200'"
        v-bind="item.otherOptions"
        @checkbox-all="selectAllChangeEvent1"
        @checkbox-change="selectChangeEvent1"
      >
        <template v-if="item.slotName" #default="scope">
          <slot :name="item.slotName" :row="scope.row" :field="item.field"></slot>
        </template>
      </vxe-column>
    </vxe-table>
  </div>
</template>
<style scoped lang="scss">
.contanier {
  width: 100%;
}
:deep(th) {
  vertical-align: middle;
}
:deep(td) {
  vertical-align: middle;
}
/*滚动条整体部分*/
.mytable-scrollbar ::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}
/*滚动条的轨道*/
.mytable-scrollbar ::-webkit-scrollbar-track {
  background-color: #ffffff;
}
/*滚动条里面的小方块，能向上向下移动*/
.mytable-scrollbar ::-webkit-scrollbar-thumb {
  background-color: #bfbfbf;
  border-radius: 5px;
  border: 1px solid #f1f1f1;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
}
.mytable-scrollbar ::-webkit-scrollbar-thumb:hover {
  background-color: #a8a8a8;
}
.mytable-scrollbar ::-webkit-scrollbar-thumb:active {
  background-color: #787878;
}
/*边角，即两个滚动条的交汇处*/
.mytable-scrollbar ::-webkit-scrollbar-corner {
  background-color: #ffffff;
}
</style>
