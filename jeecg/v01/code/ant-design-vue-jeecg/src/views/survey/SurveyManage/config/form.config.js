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
      field: 'name',
      type: 'input',
      label: '纬度名称',
      placeholder: '请输入纬度名称',
      labelCol,
      wrapperCol,
      rules: [{ required: true, message: '请输入纬度名称', trigger: 'blur' }]
    }
  ]
}
