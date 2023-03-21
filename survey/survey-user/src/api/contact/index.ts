/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-09 20:17:14
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-10 09:54:55
 * @FilePath: \survey-user\src\api\contact\index.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import { IEditGroup, IGroupResult, contactResult, IGetContact } from './type'
const api = useApi()

enum contactApi {
  Group = '/survey/surGroup',
  GetContact = '/survey/SurContact/getContactsByGroupId',
  GetContactCount = '/survey/SurContact/getContactTotal',
  AddContact = '/survey/SurContact/addSurContact',
  DeleteContact = '/survey/SurContact',
  UpdateContact = '/survey/SurContact',
}

export const getGroupApi = (): Promise<IDataType<IGroupResult[]>> => {
  return api.get(contactApi.Group)
}

export const addGroupApi = (name: string): Promise<IDataType<any>> => {
  return api.post(contactApi.Group, {
    name,
  })
}
export const editGroupApi = (data: IEditGroup): Promise<IDataType<any>> => {
  return api.put(contactApi.Group, data)
}
export const deleteGroupApi = (id: string): Promise<IDataType<any>> => {
  return api.delete(contactApi.Group, {
    params: {
      id: id,
    },
  })
}
export const getContactApi = (
  params: IGetContact
): Promise<IDataType<contactResult[]>> => {
  return api.get(contactApi.GetContact, {
    params: params,
  })
}
export const getAllContactApi = (): Promise<IDataType<contactResult[]>> => {
  return api.get(contactApi.GetContact)
}
export const getContactCountApi = (): Promise<IDataType<number>> => {
  return api.get(contactApi.GetContactCount)
}
export const addContactApi = (data: any): Promise<IDataType<any>> => {
  return api.post(contactApi.AddContact, data)
}
export const deleteContactApi = (id: string): Promise<IDataType<any>> => {
  return api.delete(contactApi.DeleteContact, {
    params: {
      id: id,
    },
  })
}

export const updateContactApi = (data: any): Promise<IDataType<any>> => {
  return api.put(contactApi.UpdateContact, data)
}
