// 表单配置
// type: 表单类型 input: 输入框 select: 下拉框
// label: 标签名称
// field: 表单字段名称
// rules: 表单验证规则
// placeholder: 输入框提示文字
// options: 下拉框选项
// labelCol: 标签布局
// wrapperCol: 输入控件布局
// otherOptions: 其他选项

const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 }
}
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 }
}
export const addFormConfig = {
  formItems: [
    {
      field: 'surName',
      type: 'input',
      label: '问卷名称',
      placeholder: '请输入问卷名称',
      rules: [{ required: true, message: '请输入问卷名称', trigger: 'blur' }],
      otherOptions: {},
      labelCol,
      wrapperCol
    },
    {
      field: 'surContent',
      type: 'input',
      label: '问卷描述',
      placeholder: '请输入问卷描述',
      labelCol,
      wrapperCol
    },
    {
      field: 'type',
      type: 'select',

      label: '问卷类型',
      placeholder: '请选择问卷类型',
      options: [
        {
          label: '测评',
          value: '测评'
        },
        {
          label: '调查',
          value: '调查'
        },
        {
          label: '360度评估',
          value: '360度评估'
        }
      ],
      rules: [{ required: true, message: '请选择问卷类型', trigger: 'blur' }],
      labelCol,
      wrapperCol
    },
    {
      field: 'credit',
      type: 'input',
      label: '问卷积分',
      placeholder: '请输入问卷积分',
      labelCol,
      wrapperCol
    },
    {
      field: 'reportLink',
      type: 'input',
      label: '测评报告链接',
      placeholder: '请输入测评报告链接',
      labelCol,
      wrapperCol
    },
    {
      field: 'tenantIdList',
      mode: 'multiple',
      type: 'select',
      label: '可见租户',
      placeholder: '请选择租户',
      options: [],
      otherOptions: {
        mode: 'multiple'
      },
      labelCol,
      wrapperCol
    },
    {
      field: 'isUse',
      type: 'switch',
      label: '是否启用',
      openText: '启用',
      closeText: '禁用',
      labelCol,
      wrapperCol
    }
  ]
}

export const editFormConfig = {
  formItems: [
    {
      field: 'surName',
      type: 'input',
      label: '问卷名称',
      placeholder: '请输入问卷名称',
      rules: [{ required: true, message: '请输入问卷名称', trigger: 'blur' }],
      labelCol,
      wrapperCol
    },
    {
      field: 'surContent',
      type: 'input',
      label: '问卷描述',
      placeholder: '请输入问卷描述',
      labelCol,
      wrapperCol
    },
    {
      field: 'type',
      type: 'select',

      label: '问卷类型',
      placeholder: '请选择问卷类型',
      options: [
        {
          label: '测评',
          value: '测评'
        },
        {
          label: '调查',
          value: '调查'
        },
        {
          label: '360度评估',
          value: '360度评估'
        }
      ],
      rules: [{ required: true, message: '请选择问卷类型', trigger: 'blur' }],
      labelCol,
      wrapperCol
    },
    {
      field: 'credit',
      type: 'input',
      label: '问卷积分',
      placeholder: '请输入问卷积分',
      labelCol,
      wrapperCol
    },
    {
      field: 'reportLink',
      type: 'input',
      label: '测评报告链接',
      placeholder: '请输入测评报告链接',
      labelCol,
      wrapperCol
    },
    {
      field: 'tenantIdList',
      type: 'select',
      mode: 'multiple',
      label: '可见租户',
      placeholder: '请选择租户',
      options: [],
      otherOptions: {
        mode: 'multiple'
      },
      labelCol,
      wrapperCol
    },
    {
      field: 'isUse',
      type: 'switch',
      label: '是否启用',
      openText: '启用',
      closeText: '禁用',
      labelCol,
      wrapperCol
    }
  ]
}
