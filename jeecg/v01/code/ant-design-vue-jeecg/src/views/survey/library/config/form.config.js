const labelCol = {
  xs: { span: 24 },
  sm: { span: 5 }
}
const wrapperCol = {
  xs: { span: 24 },
  sm: { span: 16 }
}
export const formConfig = {
  formItems: [
    {
      field: 'phoneSuffix',
      type: 'input',
      label: '手机号',
      placeholder: '请输入手机号码',
      rules: [
        {
          required: true,
          message: '请输入手机号码',
          trigger: 'blur'
        }
      ],
      labelCol,
      wrapperCol
    }
  ]
}

export const formConfig360 = {
  formItems: [
    {
      field: 'selectEvaluator',
      type: 'select',
      label: '请选择被评价人',
      placeholder: '请选择',
      options: [],
      rules: [{ required: true, message: '请选择问卷类型', trigger: 'blur' }],
      labelCol,
      wrapperCol
    }
  ]
}
