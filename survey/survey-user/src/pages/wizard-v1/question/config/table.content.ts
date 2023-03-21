/*
 * @Author: August-Rushme
 * @Date: 2022-10-03 20:03:19
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-06 10:49:56
 * @FilePath: \survey-user\src\pages\wizard-v1\question\config\table.content.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
export const tableContent = {
  questionColumns: {
    content: {
      label: '题目名称',
    },
    title: {
      label: '题目文本',
    },
    typeId: {
      label: '题目类型',
    },
    actionSlots: {
      label: '操作',
      align: 'end',
    },
  },
  choiceColums: {
    orderNumber: {
      label: '序号',
    },
    choice: {
      align: 'center',
      label: '选项名称',
    },
    basicScore: {
      align: 'center',
      label: '分数',
    },
    actionSlots: {
      label: '操作',
      align: 'end',
    },
  },
}
