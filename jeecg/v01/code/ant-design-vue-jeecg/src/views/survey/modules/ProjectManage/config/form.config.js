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
      field: 'projectName',
      label: '项目名称',
      type: 'input',
      rules: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
      placeholder: '请输入项目名称',
      labelCol,
      wrapperCol
    },
    // {
    //   field: 'leader',
    //   label: '项目负责人',
    //   type: 'input',
    //   rules: [{ required: true, message: '请输入项目负责人', trigger: 'blur' }],
    //   placeholder: '请输入项目负责人',
    //   labelCol,
    //   wrapperCol
    // },
    {
      field: 'type',
      type: 'select',
      label: '项目类型',
      placeholder: '请选择项目类型',
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
      rules: [{ required: true, message: '请选择项目类型', trigger: 'blur' }],
      labelCol,
      wrapperCol
    }
  ]
}

export const editFormConfig = {
  formItems: [
    {
      field: 'projectName',
      label: '项目名称',
      type: 'input',
      rules: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
      placeholder: '请输入项目名称',
      labelCol,
      wrapperCol
    },
    {
      field: 'leader',
      label: '项目负责人',
      type: 'input',
      rules: [{ required: true, message: '请输入项目负责人', trigger: 'blur' }],
      placeholder: '请输入项目负责人',
      labelCol,
      wrapperCol
    },
    {
      field: 'type',
      type: 'select',
      label: '项目类型',
      placeholder: '请选择项目类型',
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
      rules: [{ required: true, message: '请选择项目类型', trigger: 'blur' }],
      labelCol,
      wrapperCol
    }
  ]
}
