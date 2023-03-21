import { JVXETypes } from '@/components/jeecg/JVxeTable'
import { title } from 'process'
export const tableConfig = {
  surveySelectColumns: [
    {
      title: '问卷名称',
      dataIndex: 'surName',
      key: 'surName'
    },
    {
      title: '问卷描述',
      dataIndex: 'surContent',
      key: 'surContent'
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      fixed: 'right',
      align: 'center',
      scopedSlots: { customRender: 'action' }
    }
  ],
  deparntmentUserColumns: [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: '真实姓名',
      dataIndex: 'realname',
      key: 'realname'
    },
    {
      title: '电话',
      dataIndex: 'phone',
      key: 'phone'
    },
    {
      title: '工号',
      dataIndex: 'workNo',
      key: 'workNo'
    }
  ],
  vxeTableConfig: {
    loading: false,
    dataSource: [],
    columns: [
      {
        title: '姓名',
        key: 'name',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '性别',
        key: 'gender',
        width: '18%',
        type: JVXETypes.select,
        options: [
          // 下拉选项
          { title: '男', value: '1' },
          { title: '女', value: '2' }
        ],
        defaultValue: '',
        placeholder: '请选择${title}'
      },
      {
        title: '年龄',
        key: 'age',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}'
      },
      {
        title: '手机号',
        key: 'phone',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}',
        validateRules: [
          {
            pattern: '^1(3|4|5|7|8)\\d{9}$',
            message: '${title}格式不正确'
          }
        ]
      }
    ]
  },
  questionColumns: [
    {
      title: '题目编号',
      dataIndex: 'qid',
      key: 'qid',
      scopedSlots: { customRender: 'qid' }
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
  ]
}

export const tableConfig360 = {
  vxeTableUserInfoConfig: {
    loading: false,
    dataSource: [],
    columns: [
      {
        title: '姓名',
        key: 'name',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '性别',
        key: 'gender',
        width: '18%',
        type: JVXETypes.select,
        options: [
          // 下拉选项
          { title: '男', value: '1' },
          { title: '女', value: '2' }
        ],
        defaultValue: '',
        placeholder: '请选择${title}'
      },
      {
        title: '年龄',
        key: 'age',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}'
      },
      {
        title: '手机号',
        key: 'phone',
        width: '24%',
        type: JVXETypes.input,
        defaultValue: '',
        placeholder: '请输入${title}',
        validateRules: [
          {
            pattern: '^1(3|4|5|7|8)\\d{9}$',
            message: '${title}格式不正确'
          }
        ]
      }
    ]
  },
  vxeTableUpAndDownConfig: {
    loading: false,
    dataSource: [],
    columns: [
      {
        title: '被评价人',
        key: 'evaluatedName',
        width: '18%',
        type: JVXETypes.select,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '上级',
        key: 'superiorName',
        width: '18%',
        type: JVXETypes.selectMultiple,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '同事',
        key: 'colleagueName',
        width: '18%',
        type: JVXETypes.selectMultiple,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '下级',
        key: 'subordinateName',
        width: '18%',
        type: JVXETypes.selectMultiple,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '其它',
        key: 'otherName',
        width: '18%',
        type: JVXETypes.selectMultiple,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      }
    ]
  },
  vxeTableConfig: {
    loading: false,
    dataSource: [],
    columns: [
      {
        title: '被评价人',
        key: 'evaluatedName',
        width: '18%',
        type: JVXETypes.select,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      },
      {
        title: '评价人',
        key: 'evaluatorName',
        width: '18%',
        type: JVXETypes.selectMultiple,
        options: [
          // 下拉选项
        ],
        defaultValue: '',
        placeholder: '请选择${title}',
        validateRules: [{ required: true, message: '${title}不能为空' }]
      }
    ]
  }
}

export const vxeTableUpAndDownConfig = {
  loading: false,
  dataSource: [],
  columns: [
    {
      title: '被评价人',
      key: 'evaluatedName',
      width: '18%',
      type: JVXETypes.select,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}',
      validateRules: [{ required: true, message: '${title}不能为空' }]
    },
    {
      title: '上级',
      key: 'superiorName',
      width: '18%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '同事',
      key: 'colleagueName',
      width: '18%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '下级',
      key: 'subordinateName',
      width: '18%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '其它',
      key: 'otherName',
      width: '18%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '是否自评',
      key: 'isSelf',
      width: '10%',
      type: JVXETypes.select,
      options: [
        // 下拉选项
        {
          title: '是',
          value: 'true'
        },
        {
          title: '否',
          value: 'false'
        }
      ],
      defaultValue: 'false',
      placeholder: '请选择${title}'
    }
  ]
}
