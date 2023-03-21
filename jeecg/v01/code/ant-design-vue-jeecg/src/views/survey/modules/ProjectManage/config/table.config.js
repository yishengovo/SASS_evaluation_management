import { JVXETypes } from '@/components/jeecg/JVxeTable'
// 问卷配置表
export const tableConfig = {
  columns: [
    {
      title: '姓名',
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: '真实姓名',
      dataIndex: 'realname',
      key: 'realname'
    },
    {
      title: '性别',
      dataIndex: 'sex',
      key: 'sex'
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone'
    },
    {
      title: '部门',
      dataIndex: 'orgCode',
      key: 'orgCode'
    }
  ],
  columsSurvey: [
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
      title: '问卷状态',
      dataIndex: 'surStatus',
      key: 'surStatus'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime'
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'action'
    }
  ],
  deparntmentUser: [
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
  ]
}

// 客户配置表
export const VxeTable = {
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
}
