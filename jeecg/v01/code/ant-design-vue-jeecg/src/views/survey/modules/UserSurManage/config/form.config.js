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
      field: 'surveyUid',
      label: '调查问卷编号',
      type: 'input',
      placeholder: '请输入调查问卷编号',
      labelCol,
      wrapperCol
    },
    {
      field: 'projectUid',
      label: '项目编号',
      type: 'input',
      placeholder: '请输入项目编号',
      labelCol,
      wrapperCol
    },
    {
      field: 'userUid',
      label: '用户编号',
      type: 'input',
      placeholder: '请输入用户编号',
      labelCol,
      wrapperCol
    },
    {
      field: 'project',
      label: '项目名称',
      type: 'input',
      placeholder: '请输入项目名称',
      labelCol,
      wrapperCol
    },
    {
      field: 'survey',
      label: '调查问卷名称',
      type: 'input',
      placeholder: '请输入调查问卷名称',
      labelCol,
      wrapperCol
    },
    {
      field: 'name',
      label: '答卷人',
      type: 'input',
      placeholder: '请输入答卷人',
      labelCol,
      wrapperCol
    },
    {
      field: 'answer',
      label: '答案',
      type: 'textarea',
      placeholder: '请输入答案',
      labelCol,
      wrapperCol
    },
    {
      field: 'age',
      label: '年龄',
      type: 'input',
      placeholder: '请输入年龄',
      labelCol,
      wrapperCol
    },
    {
      field: 'phone',
      label: '手机号',
      type: 'input',
      placeholder: '请输入手机号',
      labelCol,
      wrapperCol
    }
  ]
}
