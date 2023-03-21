/*
 * @Author: August-Rushme
 * @Date: 2022-07-26 19:04:02
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-04 10:29:21
 * @FilePath: \ant-design-vue-jeecg\src\constant\survey.js
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
// 将问题的英文类型转为中文
export function getQuestionType(type) {
  switch (type) {
    case 'text':
      return '单行文本'
    case 'radiogroup':
      return '单选题'
    case 'checkbox':
      return '多选题'
    case 'dropdown':
      return '下拉题'
    case 'multipletext':
      return '多行文本'
    case 'matrix':
      return '矩阵单选题'
    case 'matrixdropdown':
      return '矩阵下拉题'
    case 'matrixdynamic':
      return '矩阵动态题'
    case 'comment':
      return '意见题'
    case 'panel':
      return '面板'
    case 'page':
      return '页面'
    case 'rating':
      return '评分题'
    case 'expression':
      return '表达式'
    case 'signature':
      return '签名'
    case 'datetime':
      return '日期时间'
    case 'html':
      return 'HTML代码'
    case 'file':
      return '文件'
    case 'ranking':
      return '排序题'
    case 'boolean':
      return '布尔题'
    case 'tagbox':
      return '标签框'
    case 'imagepicker':
      return '图片选择器'
    case 'signaturepad':
      return '签名板'
  }
}
