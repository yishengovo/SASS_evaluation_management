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
      field: 'surveyName',
      label: '已选问卷',
      type: 'input',
      rules: [{ required: true, message: '请选择问卷', trigger: 'blur' }],
      placeholder: '请选择问卷',
      labelCol,
      wrapperCol
    }
  ]
}
