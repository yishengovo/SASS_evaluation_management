/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-07 18:13:23
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-09 17:03:59
 * @Description: file content
 */
export interface SurContact {
  name?: string
  phone?: string
  email?: string
  source?: string
  company?: string
  sex?: string
  position?: string
  address?: string
  birthday?: string
  remarks?: string
  fixedPhone?: string
  groupNameList?: any[]
  id?: string
}

export interface SurContactFieldInformationList {
  fieldId: string
  information: string
}

export interface SurGroupContactList {
  groupId: string
  id: null | string
}

export interface IFormData {
  surContact: SurContact
  surContactFieldInformationList: SurContactFieldInformationList[]
  surGroupContactList: SurGroupContactList[]
}
