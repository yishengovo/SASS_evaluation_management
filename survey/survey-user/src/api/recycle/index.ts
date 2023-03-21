/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-15 15:06:12
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-15 15:58:24
 * @Description: file content
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import { recycleData, recycleParams } from './type'

const api = useApi()

enum recycleApi {
  GetRecycle = '/client/userProject/getBin',
  DeleteProject = '/client/userProject/delete',
  RecoveryProject = '/client/userProject/recovery',
  CleanProject = '/client/userProject/clean',
}

export const getRecycle = (data: recycleParams): Promise<IDataType<recycleData>> => {
  return api.post(recycleApi.GetRecycle, data)
}

export const deleteProjectApi = (id: string): Promise<IDataType<any>> => {
  return api.delete(recycleApi.DeleteProject + '/' + id)
}
export const recoveryProjectApi = (id: string): Promise<IDataType<recycleData>> => {
  return api.post(recycleApi.RecoveryProject, {
    ids: [id],
  })
}
export const cleanProjectApi = (): Promise<IDataType<any>> => {
  return api.delete(recycleApi.CleanProject)
}
