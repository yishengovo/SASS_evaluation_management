export const filterConfig = [
  {
    name: '项目类型',
    key: 'type',
    items: [
      {
        name: '所有项目',
        value: '',
        active: true,
      },
      {
        name: '调查项目',
        value: '调查',
        active: false,
      },
      {
        name: '测评项目',
        value: '测评',
        active: false,
      },
      {
        name: '360度评估项目',
        value: '360度评估',
        active: false,
      },
    ],
  },
  {
    name: '更新时间',
    key: 'order',
    items: [
      {
        name: '降序',
        value: true,
        active: true,
      },
      {
        name: '升序',
        value: false,
        active: false,
      },
    ],
  },
  {
    name: '项目状态',
    key: 'isPublish',
    items: [
      {
        name: '所有状态',
        value: '',
        active: true,
      },
      {
        name: '未发布',
        value: false,
        active: false,
      },
      {
        name: '已发布',
        value: true,
        active: false,
      },
    ],
  },
]
