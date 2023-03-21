/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-07 18:13:23
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-01 17:13:08
 * @Description: file content
 */
import { IColumns } from '/@src/components/base/ke-table/type'
import { VxeTablePropTypes } from 'vxe-table'
export const columns: IColumns[] = [
  {
    field: 'outTradeNo',
    title: '订单号',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'productDescription',
    title: '描述',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },

  {
    field: 'amount',
    title: '充值金额(元)',
    minwidth: '120',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'integral',
    title: '所得积分',
    minwidth: '100',
    otherOptions: {
      showOverflow: 'tooltip',
      sortable: true,
    },
  },
  {
    field: 'succeeded',
    title: '充值状态',
    slotName: 'succeeded',
    minwidth: '120',
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
