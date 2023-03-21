export const tableConfig = {
  columns: [
    {
      dataIndex: 'option',
      key: 'option',
      title: '选项',
      width: '55%',
      align: 'center',
      sorter: (a, b) => a.option.length - b.option.length
    },
    {
      dataIndex: 'subtotal',
      key: 'subtotal',
      title: '小计',
      width: '10%',
      align: 'center',
      sorter: (a, b) => a.subtotal - b.subtotal
    },
    {
      dataIndex: 'proportion',
      key: 'proportion',
      title: '比例',
      width: '35%',
      align: 'center',
      scopedSlots: {
        customRender: 'proportion'
      }
    }
  ]
}
