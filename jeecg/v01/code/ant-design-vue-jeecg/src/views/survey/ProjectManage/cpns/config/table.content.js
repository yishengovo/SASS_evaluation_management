import { JVXETypes } from '@/components/jeecg/JVxeTable'
export const VxeTableConfig = {
  loading: false,
  dataSource: [],
  columns: [
    {
      title: '被评者',
      key: 'userName',
      width: '18%',
      type: JVXETypes.input,
      defaultValue: '',
      placeholder: '请输入${title}',
      validateRules: [{ required: true, message: '${title}不能为空' }]
    },
    {
      title: '上级',
      key: 'superiorList',
      width: '24%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '同事',
      key: 'colleagueList',
      width: '24%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '下级',
      key: 'subordinateList',
      width: '24%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    },
    {
      title: '其它',
      key: 'otherList',
      width: '24%',
      type: JVXETypes.selectMultiple,
      options: [
        // 下拉选项
      ],
      defaultValue: '',
      placeholder: '请选择${title}'
    }
  ]
}
