/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-23 11:34:54
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-23 13:08:15
 * @Description: file content
 *
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import { IEditControl } from './type'

const api = useApi()

enum projectControlApi {
  EditControl = '/survey/surProjectEnable',
  QueryControl = '/survey/surProjectEnable/timeControll',
}

export const editControlApi = (data: IEditControl): Promise<IDataType<any>> => {
  return api.post(projectControlApi.EditControl, data)
}

export const getControlApi = (projectId: string): Promise<IDataType<any>> => {
  return api.get(projectControlApi.EditControl, {
    params: {
      projectId,
    },
  })
}
export const queryControlApi = (projectId: string): Promise<IDataType<any>> => {
  return api.get(projectControlApi.QueryControl, {
    params: {
      projectId,
    },
  })
}
