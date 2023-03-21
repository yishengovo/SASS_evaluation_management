/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-07 18:13:23
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-10 12:40:07
 * @Description: file content
 */
import { IColumns } from '/@src/components/base/ke-table/type'
import { VxeTablePropTypes } from 'vxe-table'
export const columns: IColumns[] = [
  {
    field: 'name',
    title: '姓名',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
    minwidth: '120',
  },
  {
    field: 'sex',
    title: '性别',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
    minwidth: '120',
  },
  {
    field: 'phone',
    title: '手机号',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'email',
    title: '邮箱',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'address',
    title: '地址',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'birthday',
    title: '生日',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
    slotName: 'birthday',
  },
  {
    field: 'groupNameList',
    title: '群组',
    slotName: 'groupNameList',
    minwidth: '400',
  },
]

export const otherOptions = {
  tooltipConfig: {
    // contentMethod: ({ type, column, row, items, _columnIndex }) => {
    //   // 重写默认的提示内容
    //   return '自定义提示内容：' + row[column.field]
    // },
    theme: 'light',
    // enterable: true,
  } as VxeTablePropTypes.TooltipConfig,
}
