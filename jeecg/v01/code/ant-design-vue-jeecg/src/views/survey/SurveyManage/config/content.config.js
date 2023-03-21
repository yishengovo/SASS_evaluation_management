/*
 * @Author: August-Rushme
 * @Date: 2022-07-11 11:16:35
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-08 12:29:49
 * @FilePath: \ant-design-vue-jeecg\src\views\survey\SurveyManage\config\content.config.js
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
export const contentConfig = {
  columns: [
    {
      title: '#',
      dataIndex: '',
      key: 'rowIndex',
      width: 60,
      align: 'center',
      customRender: function(t, r, index) {
        return parseInt(index) + 1
      }
    },

    {
      title: '问卷名称',
      align: 'center',
      dataIndex: 'surName'
    },
    {
      title: '描述',
      align: 'center',
      dataIndex: 'surContent'
    },
    { title: '问卷类型', align: 'center', dataIndex: 'type' },
    {
      title: '创建时间',
      align: 'center',
      dataIndex: 'createTime'
    },
    {
      title: '修改时间',
      align: 'center',
      dataIndex: 'updateDate'
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',
      width: 147,
      scopedSlots: { customRender: 'action' }
    }
  ]
}
