/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-07 18:13:23
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-16 17:47:23
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
      fixed: 'left',
    },
  },
  {
    field: 'phone',
    title: '手机号',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
      fixed: 'left',
    },
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
