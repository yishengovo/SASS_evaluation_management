export const tableConfig = {
  questionColumns: [
    {
      title: '题目编号',
      dataIndex: 'pid',
      key: 'pid',
      scopedSlots: { customRender: 'pid' }
    },
    {
      title: '问题名称',
      dataIndex: 'content',
      key: 'content'
    },
    {
      title: '题目文本',
      dataIndex: 'title',
      key: 'title'
    },
    {
      title: '问题类型',
      dataIndex: 'typeId',
      key: 'typeId'
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',
      scopedSlots: { customRender: 'action' }
    }
  ],
  answerColumns: [
    {
      title: '选项编号',
      dataIndex: 'pid',
      key: 'pid',
      align: 'center',
      scopedSlots: { customRender: 'pid' }
    },
    {
      title: '选项名称',
      dataIndex: 'choice',
      key: 'choice'
    },
    {
      title: '分值',
      dataIndex: 'basicScore',
      key: 'basicScore',
      scopedSlots: { customRender: 'basicScore' }
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      scopedSlots: { customRender: 'action' }
    }
  ],
  latitudeColumns: [
    {
      title: '序号',
      dataIndex: '',
      key: 'rowIndex',
      width: 60,
      align: 'center',
      customRender: function(t, r, index) {
        return parseInt(index) + 1
      }
    },
    {
      title: '纬度名称',
      dataIndex: 'name',
      key: 'name',
      width: 320,
      align: 'center'
    },
    {
      title: '题目数',
      dataIndex: 'count',
      key: 'count',
      width: 160,
      align: 'center'
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',
      scopedSlots: { customRender: 'action' }
    }
  ],
  bindLatitudeColumn: [
    {
      title: '纬度',
      dataIndex: 'name',
      key: 'name',
      width: 160,
      align: 'center'
    },
    {
      title: '已经绑定的题目',
      dataIndex: 'content',
      key: 'content',
      width: 420,
      align: 'center',
      scopedSlots: { customRender: 'content' }
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      fixed: 'right',
      scopedSlots: { customRender: 'action' }
    }
  ],
  bindLatitudeEditColumns: [
    {
      title: '题目',
      dataIndex: 'content',
      key: 'content',
      width: 420,
      align: 'center',
      scopedSlots: { customRender: 'content' }
    },
    {
      title: '已选择纬度',
      dataIndex: 'name',
      key: 'name',
      width: 160,
      align: 'center'
    }
  ]
}
