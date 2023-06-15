/*
 * @Author: August-Rushme
 * @Date: 2022-09-26 14:49:08
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-17 16:48:22
 * @FilePath: \survey-user\src\api\createProject\createProject.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */

import { useApi } from '../../composable/useApi'
import { IPersonList } from '../survey/type'
import { IDataType } from '../type'
import {
  AddPorject,
  IChoices,
  IEvaluationProject,
  ISaveScore,
  ISurveyPagenation,
  ProjectInfo,
  QuestionResult,
  ISurveyTemplateResult,
  ISurvey,
  IEvaluationProjectResult,
  IEvaluationPersonResult,
  IQueryProjectResult,
} from './type'
const api = useApi()

enum CreateProjectApi {
  AddProject = '/client/userProject/save',
  GetProjectById = '/client/userProject/queryById',
  GetQuestionList = '/client/userProject/question',
  GetChoicesById = '/client/userProject/choice',
  SaveScoreById = '/client/userProject/setScore',
  CreateEvaluationProject = '/client/userProject/createEvaluationProject',
  GetSurveyTemplate = '/client/userProject/getSurveyTemplate',
  GetMySurveyTemplate = '/client/userProject/getMySurveyTemplate',
  GetSurveyJson = '/client/userProject/getSurveyTemplateById/',
  GetSelectedSurvey = '/client/userProject/getEvaluationSurvey/',
  GetEvaluationProjectPerson = '/client/userProject/queryEvaluationUser',
  DeleteEvaluationProjectPerson = '/client/userProject/removeEvaluationUser',
  QueryUserSurvey = '/client/userProject/queryUserSurvey',
  GetDataRecord = '/client/userProject/result/',
  UploadFile = '/client/userProject/importByExcel',
}

/**
 * @description: 创建项目并保存项目问卷
 * @param {object} data
 * @return {*}
 */
export const addProject = (data: AddPorject): Promise<IDataType<any>> => {
  return api.post(CreateProjectApi.AddProject, data)
}

/**
 * @description: 获取项目信息
 * @param {string} id
 * @return {*}
 */
export const getProject = (id: string): Promise<IDataType<ProjectInfo>> => {
  return api.get(CreateProjectApi.GetProjectById, {
    params: { id },
  })
}

/**
 * @description: 获取问题列表
 * @param { string } id
 * @param { number } pageNum
 * @param { number } pageSize
 * @return {*}
 */
export const getQuestionList = (
  id: string,
  pageNum: number,
  pageSize: number
): Promise<IDataType<QuestionResult>> => {
  return api.post(CreateProjectApi.GetQuestionList, { id, pageNum, pageSize })
}

/**
 * @description: 根据问题id获取选项
 * @param {string} questionId
 * @return {*}
 */
export const getChoicesById = (
  questionId: string,
  projectId: string
): Promise<IDataType<IChoices[]>> => {
  return api.post(CreateProjectApi.GetChoicesById, { questionId, projectId })
}

/**
 * @description:  保存选项的分数
 * @param {ISaveScore} data
 * @return {*}
 */
export const saveScoreById = (data: ISaveScore): Promise<IDataType<any>> => {
  return api.post(CreateProjectApi.SaveScoreById, data)
}

/**
 * @description: 创建测评类项目
 * @param {IEvaluationProject} data
 * @return {*}
 */
export const createEvaluationProject = (
  data: IEvaluationProject
): Promise<IDataType<IEvaluationProjectResult>> => {
  return api.post(CreateProjectApi.CreateEvaluationProject, data)
}

/**
 * @description: 获取问卷模板
 * @param {ISurveyPagenation} data
 * @return {*}
 */
export const getSurveyTemplate = (
  data: ISurveyPagenation
): Promise<IDataType<ISurveyTemplateResult>> => {
  return api.post(CreateProjectApi.GetSurveyTemplate, data)
}

/**
 * @description: 获取租户自己购买的问卷模板
 * @param {ISurveyPagenation} data
 * @return {*}
 */
export const getMySurveyTemplate = (
  data: ISurveyPagenation
): Promise<IDataType<ISurveyTemplateResult>> => {
  return api.post(CreateProjectApi.GetMySurveyTemplate, data)
}

/**
 * @description: 获取问卷
 * @param {string} id
 * @return {*}
 */
export const getSurvey = (id: string): Promise<IDataType<ISurvey>> => {
  return api.get(CreateProjectApi.GetSurveyJson + id)
}

/**
 * @description: 获取已选择的问卷
 * @param {string} id
 * @return {*}
 */
export const getSelectedSurvey = (id: string): Promise<IDataType<ISurvey[]>> => {
  return api.get(CreateProjectApi.GetSelectedSurvey + id)
}

/**
 * @description: 获取参与测评问卷的人
 * @param {string} id
 * @return {*}
 */
export const getEvaluationProjectPerson = (
  id: string
): Promise<IDataType<IEvaluationPersonResult[]>> => {
  return api.get(CreateProjectApi.GetEvaluationProjectPerson + '?id=' + id)
}

/**
 *
 * @description: 删除一些测评度项目的评价人员
 * @param {IPersonList} data
 * @return {*}
 */
export const deleteEvaluationProjectPerson = (data: IPersonList): Promise<IDataType> => {
  return api.post(CreateProjectApi.DeleteEvaluationProjectPerson, data)
}

/**
 * @description:  获取用户问卷
 * @return {*}
 */
export const getUserSurvey = (data: {
  projectId: string
  phone: string
}): Promise<IDataType<IQueryProjectResult>> => {
  return api.post(CreateProjectApi.QueryUserSurvey, data)
}

/**
 * @description:  获取数据大屏数据
 * @param {string} id
 * @return {*}
 */
export const getDataRecord = (id: string): Promise<IDataType> => {
  return api.get(CreateProjectApi.GetDataRecord + id)
}

/**
 * @description:  上传excel
 * @param {string} id
 * @param {File} file
 * @return {*}
 */
export const uploadExcel = (id: string, file: File): Promise<IDataType> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('id', id)
  return api.post(CreateProjectApi.UploadFile, formData)
}
