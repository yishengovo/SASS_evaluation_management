export const tableConfig = {
  tableListColumns: [
    {
      title: '项目名称',
      align: 'center',
      dataIndex: 'projectName'
    },
    {
      title: '项目负责人',
      align: 'center',
      dataIndex: 'leader'
    },
    {
      title: '项目类型',
      align: 'center',
      dataIndex: 'type'
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',
      width: 147,
      scopedSlots: { customRender: 'action' }
    }
  ],
  collectColumns: [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      align: 'center',
      width: 140
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
      align: 'center',
      width: 100
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      align: 'center',
      width: 80,
      scopedSlots: { customRender: 'gender' }
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      align: 'center',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      width: 100,

      scopedSlots: { customRender: 'status' }
    },
    {
      title: '操作',
      dataIndex: 'action',
      scopedSlots: { customRender: 'action' },
      width: 180,
      align: 'center',
      fixed: 'right'
    }
  ]
}
