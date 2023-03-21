/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-09-28 16:37:40
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-15 15:04:39
 * @Description: file content
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import {
  ProjectList,
  PublishProject,
  PaperListData,
  DeleteData,
  PublishData,
  ChangeProjectName,
  GetCollection,
  CollectionData,
  CopyProject,
} from './type'
const api = useApi()

enum ProjectListApi {
  GetProjectListApi = '/client/userProject/getByTenant',
  DeleteProjectApi = '/client/userProject/logicDel',
  ChangeProjectApi = '/client/userProject/changeStatus',
  ChangeProjectNameApi = 'client/userProject/edit',
  GetStatisticsApi = '/client/userProject/statistics',
  GetCollectionApi = '/client/userProject/getAnalysis',
  CopyProjectApi = '/client/userProject/copyProject',
}

/**
 * @description: 获取问卷列表
 * @param {object} data
 * @return {*}
 */
export const getProjectList = (data: ProjectList): Promise<IDataType<PaperListData>> => {
  return api.post(ProjectListApi.GetProjectListApi, data)
}
/**
 * @description: 删除问卷
 * @param {object} data
 * @return {*}
 */
export const deleteProject = (data: string): Promise<IDataType<DeleteData>> => {
  return api.delete(ProjectListApi.DeleteProjectApi + '/' + data)
}
/**
 * @description: 发布问卷
 * @param {object} data
 * @return {*}
 */
export const changeProject = (data: PublishProject): Promise<IDataType<PublishData>> => {
  return api.post(ProjectListApi.ChangeProjectApi, data)
}
/**
 * @description: 修改项目名称
 * @param {object} data
 * @return {*}
 */
export const changeProjectName = (
  data: ChangeProjectName
): Promise<IDataType<string>> => {
  return api.post(ProjectListApi.ChangeProjectNameApi, data)
}
/**
 * @description: 获取数据分析数据
 * @param {object} data
 * @return {*}
 */

export const getStatistics = (data: string): Promise<IDataType<any>> => {
  return api.get(ProjectListApi.GetStatisticsApi, {
    params: {
      id: data,
    },
  })
}
/**
 * @description: 获取收集情况
 * @param {object} data
 * @return {*}
 */

export const getCollection = (
  data: GetCollection
): Promise<IDataType<CollectionData>> => {
  return api.post(ProjectListApi.GetCollectionApi, data)
}

/**
 * @description:  复制项目
 * @param {ISaveScore} data
 * @return {*}
 */
export const copyProject = (data: CopyProject): Promise<IDataType<any>> => {
  return api.post(ProjectListApi.CopyProjectApi, data)
}
// export const getProject = (id: string): Promise<IDataType<any>> => {
//   return api.get(CreateProjectApi.GetProjectById, {
//     params: { id },
//   })
// }
