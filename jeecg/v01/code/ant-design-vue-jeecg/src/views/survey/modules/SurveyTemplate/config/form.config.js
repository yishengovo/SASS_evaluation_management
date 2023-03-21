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
      label: '模板名称',
      placeholder: '请输入模板名称',
      labelCol,
      wrapperCol,
      rules: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
    },
    {
      field: 'type',
      type: 'select',
      label: '模板类型',
      placeholder: '请选择模板类型',
      options: [
        {
          value: '普通问卷模板',
          label: '普通问卷模板'
        },
        {
          value: '360问卷模板',
          label: '360问卷模板'
        }
      ],
      labelCol,
      wrapperCol,
      rules: [{ required: true, message: '请选择模板类型', trigger: 'blur' }]
    }
  ]
}
