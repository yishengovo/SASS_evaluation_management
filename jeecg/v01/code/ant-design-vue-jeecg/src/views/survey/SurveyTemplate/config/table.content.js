export const tableConfig = {
  surveyTemplateColumns: [
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
      title: '模板名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '问卷类型',
      align: 'center',
      dataIndex: 'type'
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',

      scopedSlots: { customRender: 'action' }
    }
  ]
}
