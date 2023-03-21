/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-15 15:21:22
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-15 15:27:15
 * @Description: file content
 */
import { IColumns } from '/@src/components/base/ke-table/type'
import { VxeTablePropTypes } from 'vxe-table'
export const columns: IColumns[] = [
  {
    field: 'projectName',
    title: '项目名称',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'type',
    title: '项目类型',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'selectNumber',
    title: '收集人数',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'createTime',
    title: '创建时间',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'action',
    title: '操作',
    minwidth: '300',
    slotName: 'action',
  },
  // {
  //   field: 'address',
  //   title: '地址',
  //   otherOptions: {
  //     showOverflow: 'tooltip',
  //     sortable: true,
  //   },
  // },
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
